package de.regnis.b.ast;

import de.regnis.b.type.Type;
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
	public final Position position;

	// Setup ==================================================================

	public FuncDeclaration(@NotNull Type type, @NotNull String name, @NotNull FuncDeclarationParameters parameters, @NotNull StatementList statementList) {
		this(type, name, parameters, statementList, Position.DUMMY);
	}

	public FuncDeclaration(@NotNull Type type, @NotNull String name, @NotNull FuncDeclarationParameters parameters, @NotNull StatementList statementList, @NotNull Position position) {
		this.type = type;
		this.name = name;
		this.parameters = parameters;
		this.statementList = statementList;
		this.position = position;
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(DeclarationVisitor<O> visitor) {
		return visitor.visitFunctionDeclaration(this);
	}
}
