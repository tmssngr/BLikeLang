package de.regnis.b.node;

/**
 * @author Thomas Singer
 */
public final class FuncDeclarationParameter extends Node {

	// Fields =================================================================

	public final String type;
	public final String name;

	// Setup ==================================================================

	public FuncDeclarationParameter(String type, String name) {
		this.type = type;
		this.name = name;
	}
}
