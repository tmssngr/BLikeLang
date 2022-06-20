package node;

/**
 * @author Thomas Singer
 */
public final class VarReadNode extends ExpressionNode {

	// Fields =================================================================

	private final String var;
	private final int line;
	private final int column;

	// Setup ==================================================================

	public VarReadNode(String var, int line, int column) {
		this.var = var;
		this.line = line;
		this.column = column;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return "read(" + var + ")";
	}

	@Override
	public void visit(NodeVisitor visitor) {
		visitor.visitVarRead(var, line, column);
	}
}
