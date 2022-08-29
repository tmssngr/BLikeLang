package de.regnis.b.ir.command;

import de.regnis.b.ast.*;
import de.regnis.b.ir.*;
import de.regnis.b.type.BasicTypes;
import de.regnis.b.type.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;

/**
 * @author Thomas Singer
 */
public final class CommandFactory {

	// Constants ==============================================================

	static final String SP = "SP";
	static final int VAR_ACCESS_REGISTER = 14;
	static final String VAR_ACCESS_REGISTER_NAME = "@rr" + VAR_ACCESS_REGISTER;

	static final int REG_A = 0;
	static final int REG_B = 2;

	// Fields =================================================================

	private final StackPositionProvider stackPositionProvider;
	private final Function<String, Type> functionNameToReturnType;
	private final CommandList commandList;

	private int variableCount;

	// Setup ==================================================================

	public CommandFactory(@NotNull StackPositionProvider stackPositionProvider, @NotNull Function<String, Type> functionNameToReturnType, @NotNull CommandList commandList) {
		this.stackPositionProvider    = stackPositionProvider;
		this.functionNameToReturnType = functionNameToReturnType;
		this.commandList              = commandList;
	}

	// Accessing ==============================================================

	public void addPrelude(@NotNull FuncDeclaration declaration, int variableCount) {
		commandList.add(new Label(declaration.name()));

		this.variableCount = variableCount;
		// reserve space for local variables
		for (int i = 0; i < variableCount; i++) {
			addCommand(new RegisterCommand(RegisterCommand.Op.push, REG_A));
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
					addCommand(new RegisterCommand(RegisterCommand.Op.pop, REG_A));
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

				switch (node.operator()) {
					case lessThan -> handleIf(leftVar.name(), right, JumpCondition.lt, JumpCondition.ge, trueLabel, falseLabel);
					case lessEqual -> handleIf(leftVar.name(), right, JumpCondition.le, JumpCondition.gt, trueLabel, falseLabel);
					case equal -> handleIf(leftVar.name(), right, JumpCondition.z, JumpCondition.nz, trueLabel, falseLabel);
					case notEqual -> handleIf(leftVar.name(), right, JumpCondition.nz, JumpCondition.z, trueLabel, falseLabel);
					case greaterEqual -> handleIf(leftVar.name(), right, JumpCondition.ge, JumpCondition.lt, trueLabel, falseLabel);
					case greaterThan -> handleIf(leftVar.name(), right, JumpCondition.gt, JumpCondition.le, trueLabel, falseLabel);
					default -> throw new UnsupportedOperationException(node.operator().text);
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
				load(REG_A, node.name());
				addCommand(new ArithmeticC(ArithmeticOp.cmp, REG_A, 0));
				addCommand(new JumpCommand(JumpCondition.nz, falseLabel));
				addCommand(new JumpCommand(trueLabel));
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
					case add -> handleAssignment(node, ArithmeticOp.add);
					case sub -> handleAssignment(node, ArithmeticOp.sub);
					case bitAnd -> handleAssignment(node, ArithmeticOp.and);
					case bitOr -> handleAssignment(node, ArithmeticOp.or);
					case bitXor -> handleAssignment(node, ArithmeticOp.xor);
					case shiftL -> handleShift(node, RegisterCommand.Op.shiftL);
					case shiftR -> handleShift(node, RegisterCommand.Op.shiftR);
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
				final Type returnType = functionNameToReturnType.apply(node.name());
				handleCall(node.name(), node.parameters(), returnType != BasicTypes.VOID, null);
				return node;
			}
		});
	}

	// Utils ==================================================================

	private void handleIf(String leftVar, SimpleExpression right, JumpCondition trueCondition, JumpCondition falseCondition, String trueLabel, String falseLabel) {
		load(REG_A, leftVar);

		literalOrVar(right,
		             literal -> {
			             addCommand(new ArithmeticC(ArithmeticOp.cmp, REG_A, literal));
						 addCommand(new JumpCommand(falseCondition, falseLabel));
						 addCommand(new JumpCommand(trueLabel));
		             },
		             rightVar -> {
			             load(REG_B, rightVar);
			             addCommand(new Arithmetic(ArithmeticOp.cmp, REG_A, REG_B));
						 addCommand(new JumpCommand(falseCondition, falseLabel));
						 addCommand(new JumpCommand(trueLabel));
		             });
	}

	private void handleAssignOrDeclareVar(String name, Expression expression) {
		expression.visit(new ExpressionVisitor<>() {
			@Override
			public Object visitBinary(BinaryExpression node) {
				throw new UnsupportedOperationException(node.toString());
			}

			@Override
			public Object visitFunctionCall(FuncCall node) {
				handleCall(node.name(), node.parameters(), true, name);
				return node;
			}

			@Override
			public Object visitNumber(NumberLiteral node) {
				addCommand(new LoadC(REG_A, node.value()));
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
				             literal -> addCommand(new LoadC(REG_A, literal)),
				             name -> load(REG_A, name));
				addCommand(new RegisterCommand(RegisterCommand.Op.push, REG_A));
			}
		}
		else if (nonVoidReturnType) {
			// to reserve space for the result
			addCommand(new RegisterCommand(RegisterCommand.Op.push, REG_A));
		}

		addCommand(new CallCommand(functionName));
		if (storeName != null) {
			storeA(storeName);
		}

		if (parameters.size() > 0) {
			for (int i = parameters.size(); i-- > 0; ) {
				addCommand(new RegisterCommand(RegisterCommand.Op.pop, REG_A));
			}
		}
		else if (nonVoidReturnType) {
			addCommand(new RegisterCommand(RegisterCommand.Op.pop, REG_A));
		}
	}

	private void handleAssignment(Assignment node, ArithmeticOp op) {
		load(REG_A, node.name());
		literalOrVar(node.expression(),
		             literal -> addCommand(new ArithmeticC(op, REG_A, literal)),
		             var -> {
			             load(REG_B, var);
			             addCommand(new Arithmetic(op, REG_A, REG_B));
		             });
		storeA(node.name());
	}

	private void handleShift(Assignment node, RegisterCommand.Op op) {
		load(REG_A, node.name());
		literalOrVar(node.expression(),
		             literal -> {
			             for (int i = 0; i < literal; i++) {
				             addCommand(new RegisterCommand(op, REG_A));
			             }
		             },
		             var -> {
			             throw new UnsupportedOperationException();
		             });
		storeA(node.name());
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
		final int stackPosition = stackPositionProvider.getStackPosition(varName);
		addCommand(new Load(VAR_ACCESS_REGISTER, SP));
		addCommand(new ArithmeticC(ArithmeticOp.add, VAR_ACCESS_REGISTER, stackPosition));

		addCommand(new Load(register, VAR_ACCESS_REGISTER_NAME));
	}

	private void storeA(@NotNull String varName) {
		final int stackPosition = stackPositionProvider.getStackPosition(varName);
		addCommand(new Load(VAR_ACCESS_REGISTER, SP));
		addCommand(new ArithmeticC(ArithmeticOp.add, VAR_ACCESS_REGISTER, stackPosition));

		addCommand(new Store(VAR_ACCESS_REGISTER_NAME, REG_A));
	}

	private void addCommand(@NotNull Command command) {
		commandList.add(command);
	}
}
