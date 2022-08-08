package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

	@Nullable
	public FuncDeclaration getFunction(String name) {
		for (Declaration declaration : declarations) {
			final FuncDeclaration found = declaration.visit(new DeclarationVisitor<>() {
				@Nullable
				@Override
				public FuncDeclaration visitFunctionDeclaration(FuncDeclaration node) {
					if (node.name.equals(name)) {
						return node;
					}
					return null;
				}
			});
			if (found != null) {
				return found;
			}
		}
		return null;
	}
}
