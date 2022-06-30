package de.regnis.b.node;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class FuncDeclarationParameter extends Node {

	// Fields =================================================================

	public final Type type;
	public final String name;
	public final int line;
	public final int column;

	// Setup ==================================================================

	public FuncDeclarationParameter(@NotNull Type type, @NotNull String name) {
		this(type, name, -1, -1);
	}

	public FuncDeclarationParameter(@NotNull Type type, @NotNull String name, int line, int column) {
		this.type = type;
		this.name = name;
		this.line = line;
		this.column = column;
	}
}
