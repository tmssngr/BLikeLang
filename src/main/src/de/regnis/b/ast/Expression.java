package de.regnis.b.ast;

import de.regnis.b.type.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

	@Nullable
	public Type getTypeNullable() {
		return type;
	}

	public boolean hasType() {
		return type != null;
	}

	public void setType(@NotNull Type type) {
		this.type = type;
	}
}
