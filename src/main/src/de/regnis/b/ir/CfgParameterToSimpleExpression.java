package de.regnis.b.ir;

import de.regnis.b.ast.*;
import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author Thomas Singer
 */
public final class CfgParameterToSimpleExpression implements BlockVisitor {

	// Static =================================================================

	public static void transform(@NotNull ControlFlowGraph graph) {
		graph.iterate(new CfgParameterToSimpleExpression());
	}

	// Fields =================================================================

	private int nextTempVarIndex;

	// Setup ==================================================================

	private CfgParameterToSimpleExpression() {
	}

	// Implemented ============================================================

	@Override
	public void visitBasic(BasicBlock block) {
		block.replace((statement, consumer) -> statement.visit(new SimpleStatementVisitor<>() {
			@Override
			public Object visitAssignment(Assignment node) {
				final Expression expression = toSimpleExpression(node.expression, false, consumer);
				consumer.accept(new Assignment(node.operation, node.name, expression));
				return node;
			}

			@Override
			public Object visitLocalVarDeclaration(VarDeclaration node) {
				final Expression expression = toSimpleExpression(node.expression, false, consumer);
				consumer.accept(new VarDeclaration(node.name, expression));
				return node;
			}

			@Override
			public Object visitCall(CallStatement node) {
				final FuncCallParameters parameters = new FuncCallParameters();
				for (Expression parameter : node.getParameters()) {
					final Expression expression = toSimpleExpression(parameter, true, consumer);
					parameters.add(expression);
				}
				consumer.accept(new CallStatement(node.name, parameters));
				return node;
			}
		}));
	}

	@Override
	public void visitIf(IfBlock block) {
		Utils.assertTrue(block.getStatements().isEmpty());
	}

	@Override
	public void visitWhile(WhileBlock block) {
		Utils.assertTrue(block.getStatements().isEmpty());
	}

	@Override
	public void visitExit(ExitBlock block) {
	}

	// Utils ==================================================================

	private Expression toSimpleExpression(Expression expression, boolean createTempVar, Consumer<SimpleStatement> consumer) {
		return expression.visit(new ExpressionVisitor<>() {
			@Override
			public Expression visitBinary(BinaryExpression node) {
				final Expression left = toSimpleExpression(node.left, createTempVar, consumer);
				final Expression right = toSimpleExpression(node.right, createTempVar, consumer);
				return maybeWrapInTempVar(new BinaryExpression(left, node.operator, right));
			}

			@Override
			public Expression visitFunctionCall(FuncCall node) {
				final FuncCallParameters parameters = new FuncCallParameters();
				for (Expression parameter : node.getParameters()) {
					final Expression expression = toSimpleExpression(parameter, true, consumer);
					parameters.add(expression);
				}
				return maybeWrapInTempVar(new FuncCall(node.name, parameters));
			}

			@Override
			public Expression visitNumber(NumberLiteral node) {
				return node;
			}

			@Override
			public Expression visitVarRead(VarRead node) {
				return node;
			}

			@NotNull
			private Expression maybeWrapInTempVar(Expression expression) {
				if (createTempVar) {
					final String tempVar = "t" + nextTempVarIndex;
					nextTempVarIndex++;
					consumer.accept(new VarDeclaration(tempVar, expression));
					return new VarRead(tempVar);
				}
				return expression;
			}
		});
	}
}
