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
public abstract class CommandFactory {

	// Abstract ===============================================================

	protected abstract void addCommand(@NotNull Command command);

	// Fields =================================================================

	private final Function<String, Type> functionNameToReturnType;

	private int variableCount;

	// Setup ==================================================================

	protected CommandFactory(@NotNull Function<String, Type> functionNameToReturnType) {
		this.functionNameToReturnType = functionNameToReturnType;
	}

	// Accessing ==============================================================

	public final void addPrelude(@NotNull ControlFlowGraph graph, Set<String> parameterVars) {
		variableCount = determineVariableCount(graph, parameterVars);
		// reserve space for local variables
		for (int i = 0; i < variableCount; i++) {
			addCommand(new RegisterCommand(RegisterCommand.Op.push, 0));
		}
	}

	public final void add(@NotNull AbstractBlock block) {
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
					addCommand(new RegisterCommand(RegisterCommand.Op.pop, 0));
				}

				addCommand(NoArgCommand.Return);
			}
		});
	}

	public final void addIf(@NotNull Expression expression, @NotNull String trueLabel, @NotNull String falseLabel) {
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
				addCommand(new Load(0, node.name()));
				addCommand(new CmpCJump(0, 0,
				                        JumpCondition.nz, trueLabel,
				                        JumpCondition.z, falseLabel));
				return node;
			}
		});
	}

	public final void add(@NotNull SimpleStatement statement) {
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
		addCommand(new Load(0, leftVar));

		literalOrVar(right,
		             literal -> addCommand(new CmpCJump(0, literal, trueCondition, trueLabel, falseCondition, falseLabel)),
		             rightVar -> {
			             addCommand(new Load(1, rightVar));
			             addCommand(new CmpJump(0, 1, trueCondition, trueLabel, falseCondition, falseLabel));
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
				addCommand(new LoadC(0, node.value()));
				addCommand(new Store(name, 0));
				return node;
			}

			@Override
			public Object visitVarRead(VarRead node) {
				addCommand(new Load(0, node.name()));
				addCommand(new Store(name, 0));
				return node;
			}
		});
	}

	private void handleCall(String functionName, FuncCallParameters callParameters, boolean nonVoidReturnType, @Nullable String storeName) {
		final List<Expression> parameters = callParameters.getExpressions();
		if (parameters.size() > 0) {
			for (Expression parameter : parameters) {
				literalOrVar(parameter,
				             literal -> addCommand(new LoadC(0, literal)),
				             name -> addCommand(new Load(0, name)));
				addCommand(new RegisterCommand(RegisterCommand.Op.push, 0));
			}
		}
		else if (nonVoidReturnType) {
			// to reserve space for the result
			addCommand(new RegisterCommand(RegisterCommand.Op.push, 0));
		}

		addCommand(new CallCommand(functionName));
		if (storeName != null) {
			addCommand(new Store(storeName, 0));
		}

		if (parameters.size() > 0) {
			for (int i = parameters.size(); i-- > 0; ) {
				addCommand(new RegisterCommand(RegisterCommand.Op.pop, 0));
			}
		}
		else if (nonVoidReturnType) {
			addCommand(new RegisterCommand(RegisterCommand.Op.pop, 0));
		}
	}

	private void handleAssignment(Assignment node, ArithmeticOp op) {
		addCommand(new Load(0, node.name()));
		literalOrVar(node.expression(),
		             literal -> addCommand(new ArithmeticC(op, 0, literal)),
		             var -> {
			             addCommand(new Load(1, var));
			             addCommand(new Arithmetic(op, 0, 1));
		             });
		addCommand(new Store(node.name(), 0));
	}

	private void handleShift(Assignment node, RegisterCommand.Op op) {
		addCommand(new Load(0, node.name()));
		literalOrVar(node.expression(),
		             literal -> {
			             for (int i = 0; i < literal; i++) {
				             addCommand(new RegisterCommand(op, 0));
			             }
		             },
		             var -> {
			             throw new UnsupportedOperationException();
		             });
		addCommand(new Store(node.name(), 0));
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

	private static int determineVariableCount(@NotNull ControlFlowGraph graph, Set<String> parameterVars) {
		final Set<String> variables = new HashSet<>(parameterVars);
		if (graph.getType() != BasicTypes.VOID) {
			variables.add(ControlFlowGraph.RESULT);
		}
		final Set<String> parametersAndReturnValue = new HashSet<>(variables);

		final SimpleStatementVisitor<Object> statementVisitor = new SimpleStatementVisitor<>() {
			@Override
			public Object visitAssignment(Assignment node) {
				variables.add(node.name());
				return node;
			}

			@Override
			public Object visitLocalVarDeclaration(VarDeclaration node) {
				variables.add(node.name());
				return node;
			}

			@Override
			public Object visitCall(CallStatement node) {
				return node;
			}
		};

		graph.iterate(new BlockVisitor() {
			@Override
			public void visitBasic(BasicBlock block) {
				for (SimpleStatement statement : block.getStatements()) {
					statement.visit(statementVisitor);
				}
			}

			@Override
			public void visitIf(IfBlock block) {
			}

			@Override
			public void visitWhile(WhileBlock block) {
			}

			@Override
			public void visitExit(ExitBlock block) {
			}
		});

		variables.removeAll(parametersAndReturnValue);
		return variables.size();
	}
}
