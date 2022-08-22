package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record FuncDeclarationParameter(@NotNull String name, @NotNull Position position) {

	// Setup ==================================================================

	public FuncDeclarationParameter(@NotNull String name) {
		this(name, Position.DUMMY);
	}
}
