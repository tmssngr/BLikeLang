package de.regnis.b.ast;

import de.regnis.b.type.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class BinaryExpression extends Expression {

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

	public BinaryExpression(@NotNull Expression left, @NotNull Op operator, @NotNull Expression right, @NotNull Type type) {
		this(left, operator, right);
		setType(type);
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

	// Inner Classes ==========================================================

	public enum Op {
		plus("+"), minus("-"), multiply("*"), divide("/"), modulo("%"), shiftL("<<"), shiftR(">>"),
		lessThan("<"), lessEqual("<="), equal("=="), greaterEqual(">="), greaterThan(">"), notEqual("!="),
		bitAnd("&"), bitOr("|"), bitXor("^");

		public final String text;

		Op(String text) {
			this.text = text;
		}
	}
}
