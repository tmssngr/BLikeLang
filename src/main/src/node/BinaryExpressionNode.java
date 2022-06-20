package node;

import java.util.Objects;

/**
 * @author Thomas Singer
 */
public abstract class BinaryExpressionNode extends ExpressionNode {

	// Fields =================================================================

	public final ExpressionNode left;
	public final ExpressionNode right;

	// Setup ==================================================================

	protected BinaryExpressionNode(ExpressionNode left, ExpressionNode right) {
		Objects.requireNonNull(left);
		Objects.requireNonNull(right);

		this.left = left;
		this.right = right;
	}

	// Implemented ============================================================

	@Override
	public final void visit(NodeVisitor visitor) {
		left.visit(visitor);
		right.visit(visitor);
	}
}
