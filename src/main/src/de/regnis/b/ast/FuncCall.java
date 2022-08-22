package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class FuncCall extends Expression {

	// Fields =================================================================

	public final FuncCallParameters parameters;
	public final String name;
	public final Position position;

	// Setup ==================================================================

	public FuncCall(@NotNull String name, @NotNull FuncCallParameters parameters) {
		this(name, parameters, Position.DUMMY);
	}

	public FuncCall(@NotNull String name, @NotNull FuncCallParameters parameters, @NotNull Position position) {
		this.name       = name;
		this.parameters = parameters;
		this.position   = position;
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(ExpressionVisitor<O> visitor) {
		return visitor.visitFunctionCall(this);
	}

	@Override
	public String toString() {
		return "func call " + name;
	}
}
