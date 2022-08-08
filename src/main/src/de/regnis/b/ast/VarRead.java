package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class VarRead extends Expression {

	// Fields =================================================================

	public final String name;
	public final int line;
	public final int column;

	// Setup ==================================================================

	public VarRead(@NotNull String name) {
		this(name, -1, -1);
	}

	public VarRead(@NotNull String name, int line, int column) {
		this.name = name;
		this.line = line;
		this.column = column;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return name;
	}

	@Override
	public <O> O visit(ExpressionVisitor<O> visitor) {
		return visitor.visitVarRead(this);
	}
}
