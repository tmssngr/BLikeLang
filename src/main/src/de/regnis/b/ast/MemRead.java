package de.regnis.b.ast;

import de.regnis.b.type.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class MemRead extends Expression {

	// Fields =================================================================

	public final String name;
	public final int line;
	public final int column;

	// Setup ==================================================================

	public MemRead(@NotNull String name) {
		this(name, -1, -1);
	}

	public MemRead(@NotNull String name, int line, int column) {
		this.name = name;
		this.line = line;
		this.column = column;
	}

	public MemRead(@NotNull Type type, @NotNull String name) {
		this.name = name;
		setType(type);
		line = -1;
		column = -1;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return name;
	}

	@Override
	public <O> O visit(ExpressionVisitor<O> visitor) {
		return visitor.visitMemRead(this);
	}
}
