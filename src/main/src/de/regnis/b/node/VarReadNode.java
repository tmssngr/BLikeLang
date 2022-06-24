package de.regnis.b.node;

import de.regnis.b.ExpressionVisitor;

/**
 * @author Thomas Singer
 */
public final class VarReadNode extends ExpressionNode {

	// Fields =================================================================

	public final String var;
	public final int line;
	public final int column;

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
	public <O> O visit(ExpressionVisitor<O> visitor) {
		return visitor.visitVarRead(this);
	}
}
