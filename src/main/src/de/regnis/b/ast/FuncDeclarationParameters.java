package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class FuncDeclarationParameters {

	// Static =================================================================

	public static FuncDeclarationParameters empty() {
		return new FuncDeclarationParameters(List.of());
	}

	public static FuncDeclarationParameters of(@NotNull FuncDeclarationParameter parameter) {
		return new FuncDeclarationParameters(List.of(parameter));
	}

	public static FuncDeclarationParameters of(@NotNull List<FuncDeclarationParameter> parameters) {
		return new FuncDeclarationParameters(new ArrayList<>(parameters));
	}

	// Fields =================================================================

	private final List<FuncDeclarationParameter> parameters;

	// Setup ==================================================================

	private FuncDeclarationParameters(@NotNull List<FuncDeclarationParameter> parameters) {
		this.parameters = parameters;
	}

	// Accessing ==============================================================

	@NotNull
	public List<FuncDeclarationParameter> getParameters() {
		return Collections.unmodifiableList(parameters);
	}
}
