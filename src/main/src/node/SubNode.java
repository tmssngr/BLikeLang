package node;

/**
 * @author Thomas Singer
 */
public final class SubNode extends BinaryExpressionNode {

	// Setup ==================================================================

	public SubNode(ExpressionNode left, ExpressionNode right) {
		super(left, right);
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return left + " - " + right;
	}
}
