package de.regnis.b.node;

/**
 * @author Thomas Singer
 */
public final class VarRead extends Expression {

	// Fields =================================================================

	public final String var;
	public final int line;
	public final int column;

	// Setup ==================================================================

	public VarRead(String var) {
		this(var, -1, -1);
	}

	public VarRead(String var, int line, int column) {
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
