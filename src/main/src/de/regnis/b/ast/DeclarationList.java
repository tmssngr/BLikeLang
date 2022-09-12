package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author Thomas Singer
 */
public final class DeclarationList {

	// Static =================================================================

	@NotNull
	public static DeclarationList of(@NotNull Declaration declaration) {
		return new DeclarationList(List.of(declaration));
	}

	@NotNull
	public static DeclarationList of(@NotNull List<Declaration> declarations) {
		return new DeclarationList(new ArrayList<>(declarations));
	}

	// Fields =================================================================

	private final List<Declaration> declarations;

	// Setup ==================================================================

	private DeclarationList(@NotNull List<Declaration> declarations) {
		this.declarations = declarations;
	}

	// Accessing ==============================================================

	@NotNull
	public DeclarationList transform(@NotNull Function<Declaration, Declaration> function) {
		final List<Declaration> newDeclarations = new ArrayList<>();
		for (Declaration declaration : declarations) {
			final Declaration newDeclaration = function.apply(declaration);
			if (newDeclaration != null) {
				newDeclarations.add(newDeclaration);
			}
		}
		return new DeclarationList(newDeclarations);
	}

	@NotNull
	public List<Declaration> getDeclarations() {
		return Collections.unmodifiableList(declarations);
	}

	@Nullable
	public FuncDeclaration getFunction(String name) {
		for (Declaration declaration : declarations) {
			final FuncDeclaration found = declaration.visit(new DeclarationVisitor<>() {
				@Nullable
				@Override
				public FuncDeclaration visitConst(ConstDeclaration node) {
					return null;
				}

				@Nullable
				@Override
				public FuncDeclaration visitFunctionDeclaration(FuncDeclaration node) {
					if (node.name().equals(name)) {
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
