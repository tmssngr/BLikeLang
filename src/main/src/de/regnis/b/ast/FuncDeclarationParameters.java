package de.regnis.b.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class FuncDeclarationParameters {

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
