package node;

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
}
