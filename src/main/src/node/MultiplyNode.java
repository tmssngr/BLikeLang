package node;

/**
 * @author Thomas Singer
 */
public final class MultiplyNode extends BinaryExpressionNode {

	// Setup ==================================================================

	public MultiplyNode(ExpressionNode left, ExpressionNode right) {
		super(left, "*", right);
	}
}
