package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class BinaryExpression implements Expression {

	// Static =================================================================

	public static boolean isComparison(Op operator) {
		return operator == Op.lessThan
				|| operator == Op.lessEqual
				|| operator == Op.equal
				|| operator == Op.greaterEqual
				|| operator == Op.greaterThan
				|| operator == Op.notEqual
				;
	}

	// Fields =================================================================

	public final Expression left;
	public final Op operator;
	public final Expression right;

	// Setup ==================================================================

	public BinaryExpression(@NotNull Expression left, @NotNull Op operator, @NotNull Expression right) {
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
	public <O> O visit(@NotNull ExpressionVisitor<O> visitor) {
		return visitor.visitBinary(this);
	}

	// Inner Classes ==========================================================

	public enum Op {
		add("+"), sub("-"), multiply("*"), divide("/"), modulo("%"), shiftL("<<"), shiftR(">>"),
		lessThan("<"), lessEqual("<="), equal("=="), greaterEqual(">="), greaterThan(">"), notEqual("!="),
		bitAnd("&"), bitOr("|"), bitXor("^");

		public final String text;

		Op(String text) {
			this.text = text;
		}
	}
}
