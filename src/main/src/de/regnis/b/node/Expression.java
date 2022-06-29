package de.regnis.b.node;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public abstract class Expression extends Node {

	// Abstract ===============================================================

	public abstract <O> O visit(ExpressionVisitor<O> visitor);

	// Fields =================================================================

	private Type type;

	// Setup ==================================================================

	protected Expression() {
	}

	// Accessing ==============================================================

	@NotNull
	public Type getType() {
		return type;
	}

	public void setType(@NotNull Type type) {
		this.type = type;
	}
}
