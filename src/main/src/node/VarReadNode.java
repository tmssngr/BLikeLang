package node;

/**
 * @author Thomas Singer
 */
public class VarReadNode extends ExpressionNode {

	public final String varName;

	public VarReadNode(String varName) {
		this.varName = varName;
	}
}
