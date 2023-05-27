package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record BinaryExpression(@NotNull Expression left, @NotNull Op operator,
                               @NotNull Expression right) implements Expression {

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
		add("+", true), sub("-", false), multiply("*", true), divide("/", false), modulo("%", false), shiftL("<<", false), shiftR(">>", false),
		lessThan("<", false), lessEqual("<=", false), equal("==", true), greaterEqual(">=", false), greaterThan(">", false), notEqual("!=", true),
		bitAnd("&", true), bitOr("|", true), bitXor("^", true);

		public final String text;
		public final boolean commutative;

		Op(String text, boolean commutative) {
			this.text = text;
			this.commutative = commutative;
		}
	}
}
