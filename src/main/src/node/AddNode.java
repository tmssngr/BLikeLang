package node;

/**
 * @author Thomas Singer
 */
public final class AddNode extends BinaryExpressionNode {

	// Setup ==================================================================

	public AddNode(ExpressionNode left, ExpressionNode right) {
		super(left, "+", right);
	}
}
