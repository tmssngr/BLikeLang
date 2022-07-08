package de.regnis.b.ast;

import de.regnis.b.type.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class VarRead extends Expression {

	// Fields =================================================================

	public final String var;
	public final int line;
	public final int column;

	// Setup ==================================================================

	public VarRead(@NotNull String var) {
		this(var, -1, -1);
	}

	public VarRead(@NotNull String var, int line, int column) {
		this.var = var;
		this.line = line;
		this.column = column;
	}

	public VarRead(@NotNull Type type, @NotNull String var) {
		this.var = var;
		setType(type);
		line = -1;
		column = -1;
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
