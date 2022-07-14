package de.regnis.b.ast;

import java.util.Objects;

/**
 * @author Thomas Singer
 */
public final class MemAssignment extends SimpleStatement {

	// Fields =================================================================

	public final String name;
	public final Expression expression;
	public final int line;
	public final int column;

	// Setup ==================================================================

	public MemAssignment(String name, Expression expression) {
		this(name, expression, -1, -1);
	}

	public MemAssignment(String name, Expression expression, int line, int column) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(expression);

		this.name = name;
		this.expression = expression;
		this.line = line;
		this.column = column;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return "setMem(" + name + ", " + expression + ")";
	}

	@Override
	public <O> O visit(StatementVisitor<O> visitor) {
		return visitor.visitMemAssignment(this);
	}
}
