package de.regnis.b.ast;

/**
 * @author Thomas Singer
 */
public final class NumberLiteral extends SimpleExpression {

	// Constants ==============================================================

	public static final NumberLiteral FALSE = new NumberLiteral(0);
	public static final NumberLiteral TRUE = new NumberLiteral(1);

	// Static =================================================================

	public static NumberLiteral get(boolean value) {
		return value ? TRUE : FALSE;
	}

	// Fields =================================================================

	public final int value;

	// Setup ==================================================================

	public NumberLiteral(int value) {
		this.value = value;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public <O> O visit(ExpressionVisitor<O> visitor) {
		return visitor.visitNumber(this);
	}
}
