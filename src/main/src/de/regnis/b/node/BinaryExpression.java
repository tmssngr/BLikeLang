package de.regnis.b.node;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Thomas Singer
 */
public final class BinaryExpression extends Expression {

	// Constants ==============================================================

	public static final String PLUS = "+";
	public static final String MINUS = "-";
	public static final String MULTIPLY = "*";

	// Static =================================================================

	public static BinaryExpression createAdd(Expression left, Expression right) {
		return new BinaryExpression(left, PLUS, right);
	}

	public static BinaryExpression createSub(Expression left, Expression right) {
		return new BinaryExpression(left, MINUS, right);
	}

	public static BinaryExpression createMultiply(Expression left, Expression right) {
		return new BinaryExpression(left, MULTIPLY, right);
	}

	// Fields =================================================================

	public final Expression left;
	public final String operator;
	public final Expression right;

	// Setup ==================================================================

	private BinaryExpression(Expression left, String operator, Expression right) {
		Objects.requireNonNull(left);
		Objects.requireNonNull(operator);
		Objects.requireNonNull(right);

		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return left + " " + operator + " " + right;
	}

	@Override
	public <O> O visit(ExpressionVisitor<O> visitor) {
		return visitor.visitBinary(this);
	}

	// Accessing ==============================================================

	@NotNull
	public BinaryExpression createNew(Expression left, Expression right) {
		return new BinaryExpression(left, operator, right);
	}
}
