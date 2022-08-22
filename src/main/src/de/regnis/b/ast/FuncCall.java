package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record FuncCall(@NotNull String name, @NotNull FuncCallParameters parameters,
                       @NotNull Position position) implements Expression {

	// Setup ==================================================================

	public FuncCall(@NotNull String name, @NotNull FuncCallParameters parameters) {
		this(name, parameters, Position.DUMMY);
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(@NotNull ExpressionVisitor<O> visitor) {
		return visitor.visitFunctionCall(this);
	}

	@Override
	public String toString() {
		return "func call " + name;
	}
}
