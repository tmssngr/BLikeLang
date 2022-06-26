package de.regnis.b.node;

/**
 * @author Thomas Singer
 */
public final class FunctionDeclarationParameter extends Node {

	// Fields =================================================================

	public final String type;
	public final String name;

	// Setup ==================================================================

	public FunctionDeclarationParameter(String type, String name) {
		this.type = type;
		this.name = name;
	}
}
