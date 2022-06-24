package de.regnis.b.node;

import node.NodeVisitor;

/**
 * @author Thomas Singer
 */
public final class VarReadNode extends ExpressionNode {

	// Fields =================================================================

	public final String var;
	private final int line;
	private final int column;

	// Setup ==================================================================

	public VarReadNode(String var) {
		this(var, -1, -1);
	}

	public VarReadNode(String var, int line, int column) {
		this.var = var;
		this.line = line;
		this.column = column;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return var;
	}

	@Override
	public void visit(NodeVisitor visitor) {
		visitor.visitVarRead(var, line, column);
	}
}
