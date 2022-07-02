package de.regnis.b.node;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class FuncDeclaration extends Declaration {

	// Fields =================================================================

	public final Type type;
	public final String name;
	public final FuncDeclarationParameters parameters;
	public final StatementList statementList;

	// Setup ==================================================================

	public FuncDeclaration(@NotNull Type type, @NotNull String name, @NotNull FuncDeclarationParameters parameters, @NotNull StatementList statementList) {
		this.type = type;
		this.name = name;
		this.parameters = parameters;
		this.statementList = statementList;
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(DeclarationVisitor<O> visitor) {
		return visitor.visitFunctionDeclaration(this);
	}
}
