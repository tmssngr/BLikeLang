package de.regnis.b.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class FuncDeclarationParameters extends Node {

	// Fields =================================================================

	private final List<FuncDeclarationParameter> parameters = new ArrayList<>();

	// Setup ==================================================================

	public FuncDeclarationParameters() {
	}

	// Accessing ==============================================================

	public FuncDeclarationParameters add(FuncDeclarationParameter parameter) {
		parameters.add(parameter);
		return this;
	}

	public List<FuncDeclarationParameter> getParameters() {
		return Collections.unmodifiableList(parameters);
	}
}
