package de.regnis.b.ast;

/**
 * @author Thomas Singer
 */
public final class IfStatement extends ControlFlowStatement {

	// Fields =================================================================

	public final Expression expression;
	public final StatementList ifStatements;
	public final StatementList elseStatements;
	public final int line;
	public final int column;

	// Setup ==================================================================

	public IfStatement(Expression expression, StatementList ifStatements, StatementList elseStatements) {
		this(expression, ifStatements, elseStatements, -1, -1);
	}

	public IfStatement(Expression expression, StatementList ifStatements, StatementList elseStatements, int line, int column) {
		this.expression = expression;
		this.ifStatements = ifStatements;
		this.elseStatements = elseStatements;
		this.line = line;
		this.column = column;
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(StatementVisitor<O> visitor) {
		return visitor.visitIf(this);
	}
}
