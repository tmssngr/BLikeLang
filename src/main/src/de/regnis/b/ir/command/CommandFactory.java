package de.regnis.b.ir.command;

import de.regnis.b.ast.*;
import de.regnis.b.ir.*;
import de.regnis.b.type.BasicTypes;
import de.regnis.b.type.Type;
import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
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

	public static final int RP = 0xFD;

	static final int SP_H = 0xFE;
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

	private List<Integer> pops;
	private String labelPrefix = "_";
	private int labelIndex;

	// Setup ==================================================================

	public CommandFactory(@NotNull StackPositionProvider stackPositionProvider, @NotNull Function<String, Type> functionNameToReturnType, @NotNull BuiltInFunctions builtInFunctions, @NotNull CommandList commandList) {
		this.stackPositionProvider    = stackPositionProvider;
		this.functionNameToReturnType = functionNameToReturnType;
		this.builtInFunctions         = builtInFunctions;
		this.commandList              = commandList;
	}

	// Accessing ==============================================================

	public void addPrelude(@NotNull FuncDeclaration declaration) {
		commandList.add(new Label(declaration.name()));

		pops = new ArrayList<>();
		final StackPositionProvider.RegistersToPush registersToPush = stackPositionProvider.getRegistersToPush();
		for (int i = registersToPush.count(), reg = registersToPush.startRegister(); i-- > 0; reg += 2) {
			push(reg);
			pops.add(reg);
		}
		// reserve space for local variables
		for (int i = registersToPush.localVarsStoredOnStack(); i-- > 0; ) {
			push(0);
			pops.add(0);
		}
		Collections.reverse(pops);

		labelPrefix = "_" + declaration.name();
	}

	public void add(@NotNull AbstractBlock block) {
		commandList.add(new Label(block.label));

		block.visit(new BlockVisitor() {
			@Override
			public void visitBasic(BasicBlock block) {
				addStatements(block);
				addCommand(new JumpCommand(block.getSingleNext().label));
			}

			@Override
			public void visitIf(IfBlock block) {
				addStatements(block);
				addIf(block.getExpression(), block.getTrueBlock().label, block.getFalseBlock().label);
			}

			@Override
			public void visitWhile(WhileBlock block) {
				addStatements(block);
				addIf(block.getExpression(), block.getInnerBlock().label, block.getLeaveBlock().label);
			}

			@Override
			public void visitExit(ExitBlock block) {
				for (Integer register : pops) {
					pop(register);
				}

				addCommand(NoArgCommand.Ret);
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
				addCommand(new JumpCommand(node.value() != 0 ? trueLabel : falseLabel));
				return node;
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
					case add -> handleAssignment(ArithmeticOp.add, node);
					case sub -> handleAssignment(ArithmeticOp.sub, node);
					case bitAnd -> handleAssignment(ArithmeticOp.and, node);
					case bitOr -> handleAssignment(ArithmeticOp.or, node);
					case bitXor -> handleAssignment(ArithmeticOp.xor, node);
					case shiftL -> handleShift(node, true);
					case shiftR -> handleShift(node, false);
					case divide -> handleDivide(node);
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

	private void addStatements(StatementsBlock block) {
		for (SimpleStatement statement : block.getStatements()) {
			add(statement);
		}
	}

	private void handleIf(String leftVar, SimpleExpression right, BinaryExpression.Op operator, String trueLabel, String falseLabel) {
		loadA(leftVar);

		literalOrVar(right,
		             literal -> {
			             addCommand(new ArithmeticLiteral(ArithmeticOp.cp, workingRegister(REG_A), literal >> 8));
			             addMsbJumps(operator, trueLabel, falseLabel);
			             addCommand(new ArithmeticLiteral(ArithmeticOp.cp, workingRegister(REG_A + 1), literal));
			             addLsbJump(operator, falseLabel);
		             },
		             rightVar -> {
			             loadB(rightVar);
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
				if (!(node.left() instanceof VarRead leftVar)
						|| !(node.right() instanceof SimpleExpression right)) {
					throw new IllegalStateException();
				}

				final BinaryExpression.Op operator = node.operator();
				switch (operator) {
					case lessThan, lessEqual, equal, notEqual, greaterEqual, greaterThan -> {
						labelIndex++;
						final String trueLabel = labelPrefix + "_relation_" + labelIndex + "_true";
						final String falseLabel = labelPrefix + "_relation_" + labelIndex + "_false";
						final String nextLabel = labelPrefix + "_relation_" + labelIndex + "_next";

						handleIf(leftVar.name(), right, operator, trueLabel, falseLabel);
						addCommand(new Label(trueLabel));
						ldALiteral(-1);
						addCommand(new JumpCommand(nextLabel));
						addCommand(new Label(falseLabel));
						ldALiteral(0);
						addCommand(new Label(nextLabel));
						storeA(name);
					}
					default -> throw new UnsupportedOperationException(operator.text);
				}
				return node;
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
				final int varRegister = stackPositionProvider.getRegister(name);
				if (varRegister >= 0) {
					addCommand(new TempLdLiteral(workingRegister(varRegister), node.value()));
				}
				else {
					ldALiteral(node.value());
					store(name, REG_A);
				}

				return node;
			}

			@Override
			public Object visitVarRead(VarRead node) {
				loadA(node.name());
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
				             name -> loadA(name));
				pushA();
			}
		}
		else if (nonVoidReturnType) {
			// to reserve space for the result
			pushA();
		}

		addCommand(new CallCommand(functionName));

		if (parameters.size() > 0) {
			for (int i = parameters.size(); i-- > 0; ) {
				popA();
			}
		}
		else if (nonVoidReturnType) {
			popA();
		}

		if (storeName != null) {
			storeA(storeName);
		}
	}

	private void pushA() {
		push(REG_A);
	}

	private void push(int register) {
		addCommand(new RegisterCommand(RegisterCommand.Op.push, workingRegister(register)));
		addCommand(new RegisterCommand(RegisterCommand.Op.push, workingRegister(register + 1)));
	}

	private void popA() {
		pop(REG_A);
	}

	private void pop(int register) {
		addCommand(new RegisterCommand(RegisterCommand.Op.pop, workingRegister(register + 1)));
		addCommand(new RegisterCommand(RegisterCommand.Op.pop, workingRegister(register)));
	}

	private void ldALiteral(int literal) {
		ldLiteral(REG_A, literal);
	}

	private void ldLiteral(int register, int literal) {
		addCommand(new TempLdLiteral(workingRegister(register), literal));
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

	private void handleAssignment(ArithmeticOp op, Assignment node) {
		literalOrVar(node.expression(),
		             literal -> {
			             final int register = stackPositionProvider.getRegister(node.name());
			             if (register >= 0) {
				             addCommand(new TempArithmeticLiteral(op, workingRegister(register), literal));
			             }
			             else {
				             loadA(node.name());
				             addCommand(new TempArithmeticLiteral(op, workingRegister(REG_A), literal));
				             storeA(node.name());
			             }
		             },
		             var -> {
			             loadA(node.name());
			             loadB(var);
			             addCommand(new TempArithmetic(op, workingRegister(REG_A), workingRegister(REG_B)));
			             storeA(node.name());
		             });
	}

	private void handleShift(Assignment node, boolean left) {
		literalOrVar(node.expression(),
		             literal -> {
			             if (literal == 0) {
				             return;
			             }

			             loadA(node.name());

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
					             addCommand(new RegisterCommand(RegisterCommand.Op.sra, workingRegister(REG_A)));
					             addCommand(new RegisterCommand(RegisterCommand.Op.rrc, workingRegister(REG_A + 1)));
				             }
			             }

			             storeA(node.name());
		             },
		             var -> {
			             throw new UnsupportedOperationException();
		             });
	}

	private void handleDivide(Assignment node) {
		literalOrVar(node.expression(),
		             literal -> {
			             throw new UnsupportedOperationException();
		             },
		             var -> {
			             loadA(node.name());
			             loadB(var);
			             addCommand(new TempLd(0x12, workingRegister(REG_A)));
			             addCommand(new TempLd(0x14, workingRegister(REG_B)));
			             addCommand(new RegisterCommand(RegisterCommand.Op.push, RP));
			             addCommand(new LdLiteral(RP, 0x10));
			             addCommand(new CallCommand("%00E0"));
			             addCommand(new RegisterCommand(RegisterCommand.Op.pop, RP));
			             addCommand(new TempLd(workingRegister(REG_A), 0x12));
			             storeA(node.name());
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

	private void loadA(String name) {
		load(REG_A, name);
	}

	private void loadB(String var) {
		load(REG_B, var);
	}

	private void load(int register, @NotNull String varName) {
		final int stackPosition = stackPositionProvider.getStackPosition(varName);
		if (stackPosition >= 0) {
			loadVarAddress(stackPosition);

			addCommand(new LdFromMem(register, VAR_ACCESS_REGISTER_H));
			addCommand(new RegisterCommand(RegisterCommand.Op.incw, workingRegister(VAR_ACCESS_REGISTER_H)));
			addCommand(new LdFromMem(register + 1, VAR_ACCESS_REGISTER_H));
			return;
		}

		final int varRegister = stackPositionProvider.getRegister(varName);
		Utils.assertTrue(varRegister >= 0);

		addCommand(new TempLd(workingRegister(register), workingRegister(varRegister)));
	}

	private void storeA(@NotNull String varName) {
		store(varName, REG_A);
	}

	private void store(@NotNull String varName, int register) {
		final int stackPosition = stackPositionProvider.getStackPosition(varName);
		if (stackPosition >= 0) {
			loadVarAddress(stackPosition);

			addCommand(new LdToMem(VAR_ACCESS_REGISTER_H, register));
			addCommand(new RegisterCommand(RegisterCommand.Op.incw, workingRegister(VAR_ACCESS_REGISTER_H)));
			addCommand(new LdToMem(VAR_ACCESS_REGISTER_H, register + 1));
			return;
		}

		final int varRegister = stackPositionProvider.getRegister(varName);
		Utils.assertTrue(varRegister >= 0);

		addCommand(new TempLd(workingRegister(varRegister), workingRegister(register)));
	}

	private void loadVarAddress(int stackPosition) {
		Utils.assertTrue(stackPosition >= 0);

		addCommand(new TempLd(workingRegister(VAR_ACCESS_REGISTER_H), SP_H));
		if (stackPosition != 0) {
			addCommand(new TempArithmeticLiteral(ArithmeticOp.add, workingRegister(VAR_ACCESS_REGISTER_H), stackPosition));
		}
	}

	private void addCommand(@NotNull Command command) {
		commandList.add(command);
	}
}
