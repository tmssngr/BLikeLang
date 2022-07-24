package de.regnis.b.ast;

import de.regnis.b.type.Type;
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
	public final Type type;

	// Setup ==================================================================

	public Assignment(@NotNull String name, @NotNull Expression expression) {
		this(name, expression, -1, -1);
	}

	public Assignment(@NotNull String name, @NotNull Expression expression, int line, int column) {
		this.name = name;
		this.expression = expression;
		this.line = line;
		this.column = column;
		type = null;
	}

	public Assignment(@NotNull String name, @NotNull Expression expression, @NotNull Type type) {
		this.name = name;
		this.expression = expression;
		this.type = type;
		line = -1;
		column = -1;
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
