package de.regnis.b.node;

/**
 * @author Thomas Singer
 */
public final class NumberLiteral extends Expression {

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
