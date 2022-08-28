package de.regnis.b.ast;

import de.regnis.b.type.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record FuncDeclaration(@NotNull Type type, @NotNull String name, @NotNull FuncDeclarationParameters parameters,
                              @NotNull StatementList statementList, @NotNull Position position) implements Declaration {

	// Setup ==================================================================

	public FuncDeclaration(@NotNull Type type, @NotNull String name, @NotNull FuncDeclarationParameters parameters, @NotNull StatementList statementList) {
		this(type, name, parameters, statementList, Position.DUMMY);
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return name + "(" + parameters.getParameters().size() + ") -> " + type;
	}

	@Override
	public <O> O visit(DeclarationVisitor<O> visitor) {
		return visitor.visitFunctionDeclaration(this);
	}
}
