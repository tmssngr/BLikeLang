package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class DeclarationList extends Node {

	// Fields =================================================================

	private final List<Declaration> declarations = new ArrayList<>();

	// Setup ==================================================================

	public DeclarationList() {
	}

	// Accessing ==============================================================

	public DeclarationList add(@NotNull Declaration declaration) {
		declarations.add(declaration);
		return this;
	}

	public List<Declaration> getDeclarations() {
		return Collections.unmodifiableList(declarations);
	}
}
