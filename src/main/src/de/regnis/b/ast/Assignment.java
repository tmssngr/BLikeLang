package de.regnis.b.ast;

import java.util.Objects;

/**
 * @author Thomas Singer
 */
public final class Assignment extends Statement {

	// Fields =================================================================

	public final String var;
	public final Expression expression;
	public final int line;
	public final int column;

	// Setup ==================================================================

	public Assignment(String var, Expression expression) {
		this(var, expression, -1, -1);
	}

	public Assignment(String var, Expression expression, int line, int column) {
		Objects.requireNonNull(var);
		Objects.requireNonNull(expression);

		this.var = var;
		this.expression = expression;
		this.line = line;
		this.column = column;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return "set(" + var + ", " + expression + ")";
	}

	@Override
	public <O> O visit(StatementVisitor<O> visitor) {
		return visitor.visitAssignment(this);
	}
}
