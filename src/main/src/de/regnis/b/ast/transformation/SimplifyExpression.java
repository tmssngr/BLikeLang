package de.regnis.b.ast.transformation;

import de.regnis.b.ast.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public class SimplifyExpression implements ExpressionVisitor<Expression> {

	// Static =================================================================

	@NotNull
	public static Expression toSimpleExpression(@NotNull Expression expression) {
		return expression.visit(new SimplifyExpression());
	}

	// Implemented ============================================================

	@Override
	public Expression visitBinary(BinaryExpression node) {
		final Expression left = node.left().visit(this);
		final Expression right = node.right().visit(this);
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

	// Utils ==================================================================

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
