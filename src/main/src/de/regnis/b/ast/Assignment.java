package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class Assignment extends SimpleStatement {

	// Fields =================================================================

	public final String name;
	public final Expression expression;
	public final int line;
	public final int column;

	// Setup ==================================================================

	public Assignment(@NotNull String name, @NotNull Expression expression) {
		this(name, expression, -1, -1);
	}

	public Assignment(@NotNull String name, @NotNull Expression expression, int line, int column) {
		this.name = name;
		this.expression = expression;
		this.line = line;
		this.column = column;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return "set(" + name + ", " + expression + ")";
	}

	@Override
	public <O> O visit(StatementVisitor<O> visitor) {
		return visitor.visitAssignment(this);
	}
}
