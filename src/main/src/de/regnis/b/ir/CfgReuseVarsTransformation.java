package de.regnis.b.ir;

import de.regnis.b.ast.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Singer
 */
public final class CfgReuseVarsTransformation {

	// Static =================================================================

	public static void transform(@NotNull ControlFlowGraph graph, @NotNull RegisterAllocation.Result registers) {
		final CfgReuseVarsTransformation optimization = new CfgReuseVarsTransformation(graph, registers);
		optimization.replaceWithRegisterVars();
	}

	// Fields =================================================================

	private final Map<String, String> oldToNewVarName = new HashMap<>();
	private final ControlFlowGraph graph;

	// Setup ==================================================================

	private CfgReuseVarsTransformation(@NotNull ControlFlowGraph graph, @NotNull RegisterAllocation.Result registers) {
		this.graph = graph;

		final Map<Integer, String> registerToVar = new HashMap<>();
		for (String varName : registers.getVarNames()) {
			final int register = registers.get(varName);
			final String registerVar = registerToVar.get(register);
			if (registerVar != null) {
				oldToNewVarName.put(varName, registerVar);
				continue;
			}

			registerToVar.put(register, varName);
			oldToNewVarName.put(varName, varName);
		}
	}

	// Utils ==================================================================

	private void replaceWithRegisterVars() {
		graph.iterate(new BlockVisitor() {
			@Override
			public void visitBasic(BasicBlock block) {
				processStatements(block);
			}

			@Override
			public void visitIf(IfBlock block) {
				processStatements(block);
			}

			@Override
			public void visitWhile(WhileBlock block) {
				processStatements(block);
			}

			@Override
			public void visitExit(ExitBlock block) {
			}
		});
	}

	private void processStatements(StatementsBlock block) {
		block.replace(((statement, consumer) -> statement.visit(new SimpleStatementVisitor<>() {
			@Override
			public Object visitAssignment(Assignment node) {
				addAssignment(node.operation(), node.name(), node.expression());
				return node;
			}

			@Override
			public Object visitLocalVarDeclaration(VarDeclaration node) {
				addAssignment(Assignment.Op.assign, node.name(), node.expression());
				return node;
			}

			@Override
			public Object visitCall(CallStatement node) {
				consumer.accept(node);
				return node;
			}

			private void addAssignment(Assignment.Op operation, @NotNull String name, @NotNull Expression expression) {
				final var newExpression = handleExpression(expression);
				final String newVar = replace(name);

				final boolean isNoOp = newExpression instanceof VarRead varRead && varRead.name().equals(newVar);
				if (isNoOp) {
					return;
				}

				consumer.accept(new Assignment(operation, newVar, newExpression));
			}
		})));
	}

	private Expression handleExpression(Expression expression) {
		return expression.visit(new ExpressionVisitor<>() {
			@Override
			public Expression visitBinary(BinaryExpression node) {
				final var left = handleExpression(node.left());
				final var right = handleExpression(node.right());
				return new BinaryExpression(left, node.operator(), right);
			}

			@Override
			public Expression visitFunctionCall(FuncCall node) {
				final FuncCallParameters parameters = node.parameters().transform(expression -> handleExpression(expression));
				return new FuncCall(node.name(), parameters);
			}

			@Override
			public Expression visitNumber(NumberLiteral node) {
				return node;
			}

			@Override
			public Expression visitVarRead(VarRead node) {
				return new VarRead(replace(node.name()));
			}
		});
	}

	private String replace(String var) {
		return oldToNewVarName.get(var);
	}
}
