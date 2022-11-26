package de.regnis.b.ast.transformation;

import de.regnis.b.Messages;
import de.regnis.b.ast.*;
import de.regnis.b.out.StringOutput;
import de.regnis.b.type.BasicTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Thomas Singer
 */
public final class RemoveUnusedFunctionsTransformation {

	// Static =================================================================

	@NotNull
	public static DeclarationList transform(@NotNull DeclarationList root, @NotNull BuiltInFunctions builtInFunctions, @NotNull StringOutput warningOutput) {
		final String entryPoint = "main";
		final FuncDeclaration entryFunction = root.getFunction(entryPoint);
		if (entryFunction == null) {
			throw new TransformationFailedException(Messages.errorMissingMain());
		}
		if (entryFunction.type() != BasicTypes.VOID
				|| entryFunction.parameters().getParameters().size() > 0) {
			throw new TransformationFailedException(Messages.errorMainHasWrongSignature(entryFunction.position().line(), entryFunction.position().column()));
		}

		final Set<String> usedFunctions = determineUsedFunctions(entryFunction, root, builtInFunctions);
		return reportAndRemoveUnusedFunctions(root, usedFunctions, warningOutput);
	}

	// Setup ==================================================================

	private RemoveUnusedFunctionsTransformation() {
	}

	// Utils ==================================================================

	@NotNull
	private static Set<String> determineUsedFunctions(@NotNull FuncDeclaration entryFunction, @NotNull DeclarationList root, @NotNull BuiltInFunctions builtInFunctions) {
		final List<FuncDeclaration> pendingFunctions = new LinkedList<>();
		pendingFunctions.add(entryFunction);
		final AstWalker functionVisitor = new AstWalker() {
			@Override
			protected void visitCall(@NotNull String name, FuncCallParameters node) {
				final FuncDeclaration function = root.getFunction(name);
				if (function == null) {
					if (builtInFunctions.get(name) == null) {
						throw new TransformationFailedException("Function '" + name + "' not found");
					}
				}
				else {
					pendingFunctions.add(function);
				}
				super.visitCall(name, node);
			}
		};
		final Set<String> usedFunctions = new HashSet<>();
		while (pendingFunctions.size() > 0) {
			final FuncDeclaration usedFunction = pendingFunctions.remove(0);
			if (!usedFunctions.add(usedFunction.name())) {
				continue;
			}

			functionVisitor.visitFunctionDeclaration(usedFunction);
		}
		return usedFunctions;
	}

	private static DeclarationList reportAndRemoveUnusedFunctions(DeclarationList root, Set<String> used, StringOutput warningOutput) {
		return root.transform(declaration -> declaration.visit(new DeclarationVisitor<>() {
			@Nullable
			@Override
			public Declaration visitFunctionDeclaration(FuncDeclaration node) {
				if (!used.contains(node.name())) {
					warningOutput.print(Messages.warningUnusedFunction(node.position().line(), node.position().column(), node.name()));
					warningOutput.println();
					return null;
				}

				return node;
			}
		}));
	}
}
