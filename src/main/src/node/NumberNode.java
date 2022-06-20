package node;

/**
 * @author Thomas Singer
 */
public final class NumberNode extends ExpressionNode {

	public final int value;

	public NumberNode(int value) {
		this.value = value;
	}
}
