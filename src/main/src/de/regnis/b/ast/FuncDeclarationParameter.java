package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class FuncDeclarationParameter {

	// Fields =================================================================

	public final String name;
	public final int line;
	public final int column;

	// Setup ==================================================================

	public FuncDeclarationParameter(@NotNull String name) {
		this(name, -1, -1);
	}

	public FuncDeclarationParameter(@NotNull String name, int line, int column) {
		this.name = name;
		this.line = line;
		this.column = column;
	}
}
