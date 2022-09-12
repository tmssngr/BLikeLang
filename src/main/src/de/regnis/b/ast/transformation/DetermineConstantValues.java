package de.regnis.b.ast.transformation;

import de.regnis.b.Messages;
import de.regnis.b.ast.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Singer
 */
public final class DetermineConstantValues {

	// Static =================================================================

	@NotNull
	public static Map<String, NumberLiteral> determineConstants(@NotNull DeclarationList root) {
		final DetermineConstantValues transformation = new DetermineConstantValues();
		transformation.determineConstantValues(root);
		return Collections.unmodifiableMap(transformation.constantToLiteral);
	}

	// Fields =================================================================

	private final Map<String, NumberLiteral> constantToLiteral = new HashMap<>();

	// Setup ==================================================================

	private DetermineConstantValues() {
	}

	// Utils ==================================================================

	private void determineConstantValues(DeclarationList root) {
		final ExpressionVisitor<Expression> expressionVisitor = new SimplifyExpression() {
			@Override
			public NumberLiteral visitFunctionCall(FuncCall node) {
				throw new TransformationFailedException(Messages.errorFunctionCallsInConstantsNotAllowed(node.position().line(), node.position().column()));
			}

			@Override
			public NumberLiteral visitNumber(NumberLiteral node) {
				return node;
			}

			@Override
			public NumberLiteral visitVarRead(VarRead node) {
				final NumberLiteral literal = constantToLiteral.get(node.name());
				if (literal == null) {
					throw new TransformationFailedException(Messages.errorUndeclaredVariable(node.position().line(), node.position().column(), node.name()));
				}
				return literal;
			}
		};
		final DeclarationVisitor<Object> declarationVisitor = new DeclarationVisitor<>() {
			@Override
			public Object visitConst(ConstDeclaration node) {
				final NumberLiteral expression = (NumberLiteral) node.expression().visit(expressionVisitor);
				constantToLiteral.put(node.name(), expression);
				return node;
			}

			@Override
			public Object visitFunctionDeclaration(FuncDeclaration node) {
				return node;
			}
		};
		for (Declaration declaration : root.getDeclarations()) {
			declaration.visit(declarationVisitor);
		}
	}
}
