package de.regnis.b.ast;

/**
 * @author Thomas Singer
 */
public final class BreakStatement extends ControlFlowStatement {

	// Fields =================================================================

	public final int line;
	public final int column;

	// Setup ==================================================================

	public BreakStatement(int line, int column) {
		this.line = line;
		this.column = column;
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(StatementVisitor<O> visitor) {
		return visitor.visitBreak(this);
	}
}
