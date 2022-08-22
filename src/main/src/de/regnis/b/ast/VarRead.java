package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record VarRead(@NotNull String name, @NotNull Position position) implements SimpleExpression {

	// Setup ==================================================================

	public VarRead(@NotNull String name) {
		this(name, Position.DUMMY);
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return name;
	}

	@Override
	public <O> O visit(@NotNull ExpressionVisitor<O> visitor) {
		return visitor.visitVarRead(this);
	}
}
