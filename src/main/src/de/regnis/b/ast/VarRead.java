package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class VarRead extends SimpleExpression {

	// Fields =================================================================

	public final String name;
	public final Position position;

	// Setup ==================================================================

	public VarRead(@NotNull String name) {
		this(name, Position.DUMMY);
	}

	public VarRead(@NotNull String name, @NotNull Position position) {
		this.name = name;
		this.position = position;
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
