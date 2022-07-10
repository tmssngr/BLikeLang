package de.regnis.b.ast;

/**
 * @author Thomas Singer
 */
public final class IfStatement extends ControlFlowStatement {

	// Fields =================================================================

	public final Expression expression;
	public final StatementList trueStatements;
	public final StatementList falseStatements;
	public final int line;
	public final int column;

	// Setup ==================================================================

	public IfStatement(Expression expression, StatementList trueStatements, StatementList falseStatements) {
		this(expression, trueStatements, falseStatements, -1, -1);
	}

	public IfStatement(Expression expression, StatementList trueStatements, StatementList falseStatements, int line, int column) {
		this.expression = expression;
		this.trueStatements = trueStatements;
		this.falseStatements = falseStatements;
		this.line = line;
		this.column = column;
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(StatementVisitor<O> visitor) {
		return visitor.visitIf(this);
	}
}
