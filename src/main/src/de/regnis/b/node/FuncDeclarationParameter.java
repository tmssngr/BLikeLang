package de.regnis.b.node;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class FuncDeclarationParameter extends Node {

	// Fields =================================================================

	public final Type type;
	public final String name;

	// Setup ==================================================================

	public FuncDeclarationParameter(@NotNull Type type, @NotNull String name) {
		this.type = type;
		this.name = name;
	}
}
