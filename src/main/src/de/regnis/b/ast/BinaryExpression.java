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
		add("+"), sub("-"), multiply("*"), divide("/"), modulo("%"), shiftL("<<"), shiftR(">>"),
		lessThan("<"), lessEqual("<="), equal("=="), greaterEqual(">="), greaterThan(">"), notEqual("!="),
		bitAnd("&"), bitOr("|"), bitXor("^");

		public final String text;

		Op(String text) {
			this.text = text;
		}
	}
}
