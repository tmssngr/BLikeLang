package de.regnis.b.ir.command;

import de.regnis.b.ast.*;
import de.regnis.b.ir.*;
import de.regnis.b.type.BasicTypes;
import de.regnis.b.type.Type;
import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;

/**
 * @author Thomas Singer
 */
@SuppressWarnings("PointlessArithmeticExpression")
public final class CommandFactory {

	// Constants ==============================================================

	static final int SP_L = 0xFF;
	static final int SP_H = 0xFE;
	static final int VAR_ACCESS_REGISTER_L = 15;
	static final int VAR_ACCESS_REGISTER_H = 14;

	static final int REG_A = 0;
	static final int REG_B = 2;

	// Static =================================================================

	public static boolean isWorkingRegister(int register) {
		return (register & 0xF0) == 0xE0;
	}

	public static int getWorkingRegister(int register) {
		return register & 0x0F;
	}

	public static int workingRegister(int register) {
		Utils.assertTrue(register >= 0 && register <= 15);

		return 0xE0 + register;
	}

	// Fields =================================================================

	private final StackPositionProvider stackPositionProvider;
	private final Function<String, Type> functionNameToReturnType;
	private final BuiltInFunctions builtInFunctions;
	private final CommandList commandList;

	private int variableCount;

	// Setup ==================================================================

	public CommandFactory(@NotNull StackPositionProvider stackPositionProvider, @NotNull Function<String, Type> functionNameToReturnType, @NotNull BuiltInFunctions builtInFunctions, @NotNull CommandList commandList) {
		this.stackPositionProvider    = stackPositionProvider;
		this.functionNameToReturnType = functionNameToReturnType;
		this.builtInFunctions         = builtInFunctions;
		this.commandList              = commandList;
	}

	// Accessing ==============================================================

	public void addPrelude(@NotNull FuncDeclaration declaration, int variableCount) {
		commandList.add(new Label(declaration.name()));

		this.variableCount = variableCount;
		// reserve space for local variables
		for (int i = 0; i < variableCount; i++) {
			pushA();
		}
	}

	public void add(@NotNull AbstractBlock block) {
		commandList.add(new Label(block.label));

		block.visit(new BlockVisitor() {
			@Override
			public void visitBasic(BasicBlock block) {
				for (SimpleStatement statement : block.getStatements()) {
					add(statement);
				}
				addCommand(new JumpCommand(block.getSingleNext().label));
			}

			@Override
			public void visitIf(IfBlock block) {
				addIf(block.getExpression(), block.getTrueBlock().label, block.getFalseBlock().label);
			}

			@Override
			public void visitWhile(WhileBlock block) {
			}

			@Override
			public void visitExit(ExitBlock block) {
				for (int i = 0; i < variableCount; i++) {
					popA();
				}

				addCommand(NoArgCommand.Return);
			}
		});
	}

	public void addIf(@NotNull Expression expression, @NotNull String trueLabel, @NotNull String falseLabel) {
		expression.visit(new ExpressionVisitor<>() {
			@Override
			public Object visitBinary(BinaryExpression node) {
				if (!(node.left() instanceof VarRead leftVar)
						|| !(node.right() instanceof SimpleExpression right)) {
					throw new IllegalStateException();
				}

				final BinaryExpression.Op operator = node.operator();
				switch (operator) {
					case lessThan, lessEqual, equal, notEqual, greaterEqual, greaterThan -> handleIf(leftVar.name(), right, operator, trueLabel, falseLabel);
					default -> throw new UnsupportedOperationException(operator.text);
				}
				return node;
			}

			@Override
			public Object visitFunctionCall(FuncCall node) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Object visitNumber(NumberLiteral node) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Object visitVarRead(VarRead node) {
				handleIf(node.name(), new NumberLiteral(0), BinaryExpression.Op.notEqual, trueLabel, falseLabel);
				return node;
			}
		});
	}

	public void add(@NotNull SimpleStatement statement) {
		statement.visit(new SimpleStatementVisitor<>() {
			@Override
			public Object visitAssignment(Assignment node) {
				switch (node.operation()) {
					case assign -> handleAssignOrDeclareVar(node.name(), node.expression());
					case add -> handleAssignment(node, ArithmeticOp.adc, ArithmeticOp.add);
					case sub -> handleAssignment(node, ArithmeticOp.sbc, ArithmeticOp.sub);
					case bitAnd -> handleAssignment(node, ArithmeticOp.and, ArithmeticOp.and);
					case bitOr -> handleAssignment(node, ArithmeticOp.or, ArithmeticOp.or);
					case bitXor -> handleAssignment(node, ArithmeticOp.xor, ArithmeticOp.xor);
					case shiftL -> handleShift(node, true);
					case shiftR -> handleShift(node, false);
					default -> throw new UnsupportedOperationException(node.operation().toString());
				}
				return node;
			}

			@Override
			public Object visitLocalVarDeclaration(VarDeclaration node) {
				handleAssignOrDeclareVar(node.name(), node.expression());
				return node;
			}

			@Override
			public Object visitCall(CallStatement node) {
				final String name = node.name();
				final Type returnType = functionNameToReturnType.apply(name);
				if (returnType != null) {
					handleCall(name, node.parameters(), returnType != BasicTypes.VOID, null);
				}
				else {
					handleBuiltInFunctionCall(name, node.parameters(), null);
				}
				return node;
			}
		});
	}

	// Utils ==================================================================

	private void handleIf(String leftVar, SimpleExpression right, BinaryExpression.Op operator, String trueLabel, String falseLabel) {
		load(REG_A, leftVar);

		literalOrVar(right,
		             literal -> {
			             addCommand(new ArithmeticC(ArithmeticOp.cp, workingRegister(REG_A), literal >> 8));
			             addMsbJumps(operator, trueLabel, falseLabel);
			             addCommand(new ArithmeticC(ArithmeticOp.cp, workingRegister(REG_A + 1), literal));
			             addLsbJump(operator, falseLabel);
		             },
		             rightVar -> {
			             load(REG_B, rightVar);
			             addCommand(new Arithmetic(ArithmeticOp.cp, workingRegister(REG_A), workingRegister(REG_B)));
			             addMsbJumps(operator, trueLabel, falseLabel);
			             addCommand(new Arithmetic(ArithmeticOp.cp, workingRegister(REG_A + 1), workingRegister(REG_B + 1)));
			             addLsbJump(operator, falseLabel);
		             });
	}

	private void addMsbJumps(BinaryExpression.Op operator, String trueLabel, String falseLabel) {
		if (operator == BinaryExpression.Op.lessThan || operator == BinaryExpression.Op.lessEqual) {
			addCommand(new JumpCommand(JumpCondition.lt, trueLabel));
			addCommand(new JumpCommand(JumpCondition.nz, falseLabel));
		}
		else if (operator == BinaryExpression.Op.greaterThan || operator == BinaryExpression.Op.greaterEqual) {
			addCommand(new JumpCommand(JumpCondition.gt, trueLabel));
			addCommand(new JumpCommand(JumpCondition.nz, falseLabel));
		}
		else if (operator == BinaryExpression.Op.equal) {
			addCommand(new JumpCommand(JumpCondition.nz, falseLabel));
		}
		else if (operator == BinaryExpression.Op.notEqual) {
			addCommand(new JumpCommand(JumpCondition.nz, trueLabel));
		}
		else {
			throw new UnsupportedOperationException(operator.text);
		}
	}

	private void addLsbJump(BinaryExpression.Op operator, String falseLabel) {
		if (operator == BinaryExpression.Op.lessThan) {
			addCommand(new JumpCommand(JumpCondition.uge, falseLabel));
		}
		else if (operator == BinaryExpression.Op.lessEqual) {
			addCommand(new JumpCommand(JumpCondition.ugt, falseLabel));
		}
		else if (operator == BinaryExpression.Op.greaterThan) {
			addCommand(new JumpCommand(JumpCondition.ule, falseLabel));
		}
		else if (operator == BinaryExpression.Op.greaterEqual) {
			addCommand(new JumpCommand(JumpCondition.ult, falseLabel));
		}
		else if (operator == BinaryExpression.Op.equal) {
			addCommand(new JumpCommand(JumpCondition.nz, falseLabel));
		}
		else if (operator == BinaryExpression.Op.notEqual) {
			addCommand(new JumpCommand(JumpCondition.z, falseLabel));
		}
		else {
			throw new UnsupportedOperationException(operator.text);
		}
	}

	private void handleAssignOrDeclareVar(String name, Expression expression) {
		expression.visit(new ExpressionVisitor<>() {
			@Override
			public Object visitBinary(BinaryExpression node) {
				throw new UnsupportedOperationException(node.toString());
			}

			@Override
			public Object visitFunctionCall(FuncCall node) {
				final Type returnType = functionNameToReturnType.apply(node.name());
				if (returnType != null) {
					handleCall(node.name(), node.parameters(), true, name);
				}
				else {
					handleBuiltInFunctionCall(node.name(), node.parameters(), name);
				}
				return node;
			}

			@Override
			public Object visitNumber(NumberLiteral node) {
				ldALiteral(node.value());
				storeA(name);
				return node;
			}

			@Override
			public Object visitVarRead(VarRead node) {
				load(REG_A, node.name());
				storeA(name);
				return node;
			}
		});
	}

	private void handleCall(String functionName, FuncCallParameters callParameters, boolean nonVoidReturnType, @Nullable String storeName) {
		final List<Expression> parameters = callParameters.getExpressions();
		if (parameters.size() > 0) {
			for (Expression parameter : parameters) {
				literalOrVar(parameter,
				             literal -> ldALiteral(literal),
				             name -> load(REG_A, name));
				pushA();
			}
		}
		else if (nonVoidReturnType) {
			// to reserve space for the result
			addCommand(new RegisterCommand(RegisterCommand.Op.push, workingRegister(REG_A)));
			addCommand(new RegisterCommand(RegisterCommand.Op.push, workingRegister(REG_A + 1)));
		}

		addCommand(new CallCommand(functionName));
		if (storeName != null) {
			storeA(storeName);
		}

		if (parameters.size() > 0) {
			for (int i = parameters.size(); i-- > 0; ) {
				popA();
			}
		}
		else if (nonVoidReturnType) {
			popA();
		}
	}

	private void pushA() {
		addCommand(new RegisterCommand(RegisterCommand.Op.push, workingRegister(REG_A)));
		addCommand(new RegisterCommand(RegisterCommand.Op.push, workingRegister(REG_A + 1)));
	}

	private void popA() {
		addCommand(new RegisterCommand(RegisterCommand.Op.pop, workingRegister(REG_A + 1)));
		addCommand(new RegisterCommand(RegisterCommand.Op.pop, workingRegister(REG_A)));
	}

	private void ldALiteral(int literal) {
		ldLiteral(REG_A, literal);
	}

	private void ldLiteral(int register, int literal) {
		addCommand(new LdLiteral(workingRegister(register + 1), literal));
		addCommand(new LdLiteral(workingRegister(register), literal >> 8));
	}

	private void handleBuiltInFunctionCall(@NotNull String name, @NotNull FuncCallParameters parameters, @Nullable String assignReturnToVar) {
		final BuiltInFunctions.FunctionCommandFactory factory = builtInFunctions.getFactory(name);
		if (factory == null) {
			throw new IllegalStateException("function " + name + " not found");
		}

		factory.handleCall(parameters.getExpressions(), assignReturnToVar, new BuiltInFunctions.CommandFactory() {
			@Override
			public void loadToRegister(@NotNull Expression parameterExpression, int register) {
				literalOrVar(parameterExpression,
				             literal -> ldLiteral(register, literal),
				             name -> load(register, name));
			}

			@Override
			public void saveToVar(@NotNull String var, int register) {
				store(var, register);
			}

			@Override
			public void addCommand(@NotNull Command command) {
				CommandFactory.this.addCommand(command);
			}
		});
	}

	private void handleAssignment(Assignment node, ArithmeticOp msbOp, ArithmeticOp lsbOp) {
		load(REG_A, node.name());
		literalOrVar(node.expression(),
		             literal -> {
			             addCommand(new ArithmeticC(lsbOp, workingRegister(REG_A + 1), literal));
			             addCommand(new ArithmeticC(msbOp, workingRegister(REG_A), literal >> 8));
		             },
		             var -> {
			             load(REG_B, var);
			             addCommand(new Arithmetic(lsbOp, workingRegister(REG_A + 1), workingRegister(REG_B + 1)));
			             addCommand(new Arithmetic(msbOp, workingRegister(REG_A), workingRegister(REG_B)));
		             });
		storeA(node.name());
	}

	private void handleShift(Assignment node, boolean left) {
		literalOrVar(node.expression(),
		             literal -> {
			             if (literal == 0) {
				             return;
			             }

			             load(REG_A, node.name());

			             if (left) {
				             if (literal >= 8) {
								 addCommand(new Ld(workingRegister(REG_A), workingRegister(REG_A + 1)));
								 addCommand(new LdLiteral(workingRegister(REG_A + 1), 0));
					             literal -= 8;
				             }
				             for (int i = 0; i < literal; i++) {
								 addCommand(NoArgCommand.Ccf);
					             addCommand(new RegisterCommand(RegisterCommand.Op.rlc, workingRegister(REG_A + 1)));
					             addCommand(new RegisterCommand(RegisterCommand.Op.rlc, workingRegister(REG_A)));
				             }
			             }
			             else {
				             if (literal >= 8) {
								 addCommand(new Ld(workingRegister(REG_A + 1), workingRegister(REG_A)));
								 addCommand(new LdLiteral(workingRegister(REG_A), 0));
					             literal -= 8;
				             }
				             for (int i = 0; i < literal; i++) {
					             addCommand(NoArgCommand.Ccf);
					             addCommand(new RegisterCommand(RegisterCommand.Op.rrc, workingRegister(REG_A)));
					             addCommand(new RegisterCommand(RegisterCommand.Op.rrc, workingRegister(REG_A + 1)));
				             }
			             }

			             storeA(node.name());
		             },
		             var -> {
			             throw new UnsupportedOperationException();
		             });
	}

	private void literalOrVar(Expression expression, IntConsumer literalConsumer, Consumer<String> varConsumer) {
		if (expression instanceof final NumberLiteral literal) {
			literalConsumer.accept(literal.value());
		}
		else if (expression instanceof VarRead varRead) {
			varConsumer.accept(varRead.name());
		}
		else {
			throw new UnsupportedOperationException(expression.toString());
		}
	}

	private void load(int register, @NotNull String varName) {
		loadVarAddress(varName);

		addCommand(new LdFromMem(register, VAR_ACCESS_REGISTER_H));
		addCommand(new RegisterCommand(RegisterCommand.Op.incw, workingRegister(VAR_ACCESS_REGISTER_H)));
		addCommand(new LdFromMem(register + 1, VAR_ACCESS_REGISTER_H));
	}

	private void storeA(@NotNull String varName) {
		store(varName, REG_A);
	}

	private void store(@NotNull String varName, int register) {
		loadVarAddress(varName);

		addCommand(new LdToMem(VAR_ACCESS_REGISTER_H, register));
		addCommand(new RegisterCommand(RegisterCommand.Op.incw, workingRegister(VAR_ACCESS_REGISTER_H)));
		addCommand(new LdToMem(VAR_ACCESS_REGISTER_H, register + 1));
	}

	private void loadVarAddress(@NotNull String varName) {
		final int stackPosition = stackPositionProvider.getStackPosition(varName);
		addCommand(new Ld(workingRegister(VAR_ACCESS_REGISTER_L), SP_L));
		addCommand(new Ld(workingRegister(VAR_ACCESS_REGISTER_H), SP_H));
		addCommand(new ArithmeticC(ArithmeticOp.add, workingRegister(VAR_ACCESS_REGISTER_L), stackPosition));
		addCommand(new ArithmeticC(ArithmeticOp.adc, workingRegister(VAR_ACCESS_REGISTER_H), stackPosition >> 8));
	}

	private void addCommand(@NotNull Command command) {
		commandList.add(command);
	}
}
