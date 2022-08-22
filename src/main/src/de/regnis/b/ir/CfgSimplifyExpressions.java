package de.regnis.b.ir;

import de.regnis.b.ast.*;
import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class CfgSimplifyExpressions implements BlockVisitor {

	// Static =================================================================

	public static void transform(@NotNull ControlFlowGraph graph) {
		graph.iterate(new CfgSimplifyExpressions());
	}

	// Setup ==================================================================

	private CfgSimplifyExpressions() {
	}

	// Implemented ============================================================

	@Override
	public void visitBasic(BasicBlock block) {
		block.replace((statement, consumer) -> statement.visit(new SimpleStatementVisitor<>() {
			@Override
			public Object visitAssignment(Assignment node) {
				final Expression expression = toSimpleExpression(node.expression());
				consumer.accept(new Assignment(node.operation(), node.name(), expression));
				return node;
			}

			@Override
			public Object visitLocalVarDeclaration(VarDeclaration node) {
				final Expression expression = toSimpleExpression(node.expression());
				consumer.accept(new VarDeclaration(node.name(), expression));
				return node;
			}

			@Override
			public Object visitCall(CallStatement node) {
				consumer.accept(node);
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

	private Expression toSimpleExpression(Expression expression) {
		return expression.visit(new ExpressionVisitor<>() {
			@Override
			public Expression visitBinary(BinaryExpression node) {
				final Expression left = toSimpleExpression(node.left());
				final Expression right = toSimpleExpression(node.right());
				return simplify(left, node.operator(), right);
			}

			@Override
			public Expression visitFunctionCall(FuncCall node) {
				return node;
			}

			@Override
			public Expression visitNumber(NumberLiteral node) {
				return node;
			}

			@Override
			public Expression visitVarRead(VarRead node) {
				return node;
			}
		});
	}

	@NotNull
	private Expression simplify(Expression left, BinaryExpression.Op operator, Expression right) {
		if (left instanceof NumberLiteral leftNumber && right instanceof NumberLiteral rightNumber) {
			switch (operator) {
				case add:
					return new NumberLiteral(leftNumber.value() + rightNumber.value());
				case sub:
					return new NumberLiteral(leftNumber.value() - rightNumber.value());
				case multiply:
					return new NumberLiteral(leftNumber.value() * rightNumber.value());
				case divide:
					return new NumberLiteral(leftNumber.value() / rightNumber.value());
				case modulo:
					return new NumberLiteral(leftNumber.value() % rightNumber.value());
				case bitAnd:
					return new NumberLiteral(leftNumber.value() & rightNumber.value());
				case bitOr:
					return new NumberLiteral(leftNumber.value() | rightNumber.value());
				case bitXor:
					return new NumberLiteral(leftNumber.value() ^ rightNumber.value());
			}
		}
		if (left instanceof BinaryExpression leftBin
				&& right instanceof NumberLiteral) {
			if (operator == BinaryExpression.Op.add) {
				if (leftBin.operator() == BinaryExpression.Op.add) {
					return new BinaryExpression(leftBin.left(), BinaryExpression.Op.add,
					                            simplify(leftBin.right(), BinaryExpression.Op.add, right));
				}
				if (leftBin.operator() == BinaryExpression.Op.sub) {
					return new BinaryExpression(leftBin.left(), BinaryExpression.Op.add,
					                            simplify(right, BinaryExpression.Op.sub, leftBin.right()));
				}
			}
			if (operator == BinaryExpression.Op.sub) {
				if (leftBin.operator() == BinaryExpression.Op.add) {
					return new BinaryExpression(leftBin.left(), BinaryExpression.Op.sub,
					                            simplify(leftBin.right(), BinaryExpression.Op.sub, right));
				}
				if (leftBin.operator() == BinaryExpression.Op.sub) {
					return new BinaryExpression(leftBin.left(), BinaryExpression.Op.sub,
					                            simplify(leftBin.right(), BinaryExpression.Op.add, right));
				}
			}
		}
		if (operator == BinaryExpression.Op.add && left instanceof NumberLiteral) {
			return new BinaryExpression(right, operator, left);
		}
		return new BinaryExpression(left, operator, right);
	}
}
