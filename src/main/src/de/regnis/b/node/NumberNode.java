package de.regnis.b.node;

/**
 * @author Thomas Singer
 */
public final class NumberNode extends ExpressionNode {

	// Fields =================================================================

	public final int value;

	// Setup ==================================================================

	public NumberNode(int value) {
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
