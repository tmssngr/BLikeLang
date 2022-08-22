package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class FuncDeclarationParameter {

	// Fields =================================================================

	public final String name;
	public final Position position;

	// Setup ==================================================================

	public FuncDeclarationParameter(@NotNull String name) {
		this(name, Position.DUMMY);
	}

	public FuncDeclarationParameter(@NotNull String name, @NotNull Position position) {
		this.name = name;
		this.position = position;
	}
}
