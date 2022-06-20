package node;

import java.util.Objects;

/**
 * @author Thomas Singer
 */
public abstract class BinaryExpressionNode extends ExpressionNode {

	// Fields =================================================================

	public final ExpressionNode left;
	public final String operator;
	public final ExpressionNode right;

	// Setup ==================================================================

	protected BinaryExpressionNode(ExpressionNode left, String operator, ExpressionNode right) {
		Objects.requireNonNull(left);
		Objects.requireNonNull(operator);
		Objects.requireNonNull(right);

		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	// Implemented ============================================================

	@Override
	public final String toString() {
		return left + " " + operator + " " + right;
	}

	@Override
	public final void visit(NodeVisitor visitor) {
		left.visit(visitor);
		right.visit(visitor);
	}
}
