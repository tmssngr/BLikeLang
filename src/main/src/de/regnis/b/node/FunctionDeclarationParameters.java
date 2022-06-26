package de.regnis.b.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class FunctionDeclarationParameters extends Node {

	// Fields =================================================================

	private final List<FunctionDeclarationParameter> parameters = new ArrayList<>();

	// Setup ==================================================================

	public FunctionDeclarationParameters() {
	}

	// Accessing ==============================================================

	public FunctionDeclarationParameters add(FunctionDeclarationParameter parameter) {
		parameters.add(parameter);
		return this;
	}

	public List<FunctionDeclarationParameter> getParameters() {
		return Collections.unmodifiableList(parameters);
	}
}
