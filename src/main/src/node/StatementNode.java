package node;

/**
 * @author Thomas Singer
 */
public abstract class StatementNode extends Node {
	public abstract void visit(NodeVisitor visitor);
}
