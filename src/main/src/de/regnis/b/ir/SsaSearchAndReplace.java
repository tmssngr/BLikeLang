package de.regnis.b.ir;

import de.regnis.b.ast.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Thomas Singer
 */
public abstract class SsaSearchAndReplace implements BlockVisitor, ExpressionVisitor<Expression> {

	// Abstract ===============================================================

	protected abstract void processLocalVarDeclaration(VarDeclaration node, Consumer<SimpleStatement> consumer);

	// Static =================================================================

	public static void remove(ControlFlowGraph graph, Set<String> vars) {
		final Map<String, SimpleExpression> fromTo = new HashMap<>();
		for (String unusedVariable : vars) {
			fromTo.put(unusedVariable, null);
		}
		replace(graph, fromTo);
	}

	public static void replace(@NotNull ControlFlowGraph graph, @NotNull Map<String, ? extends SimpleExpression> fromTo) {
		final SsaSearchAndReplace searchAndReplace = new SsaSearchAndReplace() {
			@Override
			protected void processLocalVarDeclaration(VarDeclaration node, Consumer<SimpleStatement> consumer) {
				if (fromTo.containsKey(node.name)) {
					return;
				}

				final Expression newExpression = node.expression.visit(this);
				consumer.accept(new VarDeclaration(node.name, newExpression));
			}

			@Override
			public Expression visitVarRead(VarRead node) {
				final SimpleExpression replace = fromTo.get(node.name);
				return replace != null ? replace : node;
			}
		};
		searchAndReplace.run(graph);
	}

	public static void rename(@NotNull ControlFlowGraph graph, @NotNull Map<String, String> fromTo) {
		final SsaSearchAndReplace searchAndReplace = new SsaSearchAndReplace() {
			@Override
			protected void processLocalVarDeclaration(VarDeclaration node, Consumer<SimpleStatement> consumer) {
				final Expression newExpression = node.expression.visit(this);
				final String name = fromTo.getOrDefault(node.name, node.name);
				consumer.accept(new VarDeclaration(name, newExpression));
			}

			@Override
			protected void processAssignment(Assignment node, Consumer<SimpleStatement> consumer) {
				final Expression newExpression = node.expression.visit(this);
				final String name = fromTo.getOrDefault(node.name, node.name);
				consumer.accept(new Assignment(node.operation, name, newExpression));
			}

			@Override
			public Expression visitVarRead(VarRead node) {
				final String name = fromTo.getOrDefault(node.name, node.name);
				return new VarRead(name);
			}
		};
		searchAndReplace.run(graph);
	}

	// Setup ==================================================================

	private SsaSearchAndReplace() {
	}

	// Implemented ============================================================

	@Override
	public void visitBasic(BasicBlock block) {
		processStatements(block);
	}

	@Override
	public void visitIf(IfBlock block) {
		processStatements(block);
		block.setExpression(block.getExpression().visit(this));
	}

	@Override
	public void visitWhile(WhileBlock block) {
		processStatements(block);
		block.setExpression(block.getExpression().visit(this));
	}

	@Override
	public void visitExit(ExitBlock block) {
	}

	@Override
	public Expression visitBinary(BinaryExpression node) {
		final Expression left = node.left.visit(this);
		final Expression right = node.right.visit(this);
		return new BinaryExpression(left, node.operator, right);
	}

	@Override
	public Expression visitFunctionCall(FuncCall node) {
		final FuncCallParameters parameters = new FuncCallParameters();
		for (Expression parameter : node.getParameters()) {
			parameters.add(parameter.visit(this));
		}
		return new FuncCall(node.name, parameters);
	}

	@Override
	public Expression visitNumber(NumberLiteral node) {
		return node;
	}

	// Accessing ==============================================================

	protected void processAssignment(Assignment node, Consumer<SimpleStatement> consumer) {
		throw new UnsupportedOperationException();
	}

	// Utils ==================================================================

	private void run(@NotNull ControlFlowGraph graph) {
		graph.iterate(block -> block.visit(this));
	}

	private void processStatements(StatementsBlock block) {
		block.replace((statement, consumer) -> statement.visit(new SimpleStatementVisitor<>() {
			@Override
			public SimpleStatement visitLocalVarDeclaration(VarDeclaration node) {
				processLocalVarDeclaration(node, consumer);
				return node;
			}

			@Override
			public SimpleStatement visitAssignment(Assignment node) {
				processAssignment(node, consumer);
				return node;
			}

			@Override
			public SimpleStatement visitCall(CallStatement node) {
				final FuncCallParameters parameters = new FuncCallParameters();
				for (Expression parameter : node.getParameters()) {
					parameters.add(parameter.visit(SsaSearchAndReplace.this));
				}
				consumer.accept(new CallStatement(node.name, parameters));
				return node;
			}
		}));
	}
}
