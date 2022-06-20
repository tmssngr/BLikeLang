package node;

/**
 * @author Thomas Singer
 */
public final class AssignmentNode extends StatementNode {

	public final String var;
	public final ExpressionNode expression;

	public AssignmentNode(String var, ExpressionNode expression) {
		this.var = var;
		this.expression = expression;
	}
}
