package de.regnis.b.node;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Thomas Singer
 */
public final class BinaryExpressionNode extends ExpressionNode {

	// Constants ==============================================================

	public static final String PLUS = "+";
	public static final String MINUS = "-";
	public static final String MULTIPLY = "*";

	// Static =================================================================

	public static BinaryExpressionNode createAdd(ExpressionNode left, ExpressionNode right) {
		return new BinaryExpressionNode(left, PLUS, right);
	}

	public static BinaryExpressionNode createSub(ExpressionNode left, ExpressionNode right) {
		return new BinaryExpressionNode(left, MINUS, right);
	}

	public static BinaryExpressionNode createMultiply(ExpressionNode left, ExpressionNode right) {
		return new BinaryExpressionNode(left, MULTIPLY, right);
	}

	// Fields =================================================================

	public final ExpressionNode left;
	public final String operator;
	public final ExpressionNode right;

	// Setup ==================================================================

	private BinaryExpressionNode(ExpressionNode left, String operator, ExpressionNode right) {
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
	public BinaryExpressionNode createNew(ExpressionNode left, ExpressionNode right) {
		return new BinaryExpressionNode(left, operator, right);
	}
}
