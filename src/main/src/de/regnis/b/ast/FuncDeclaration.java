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
	public final int line;
	public final int column;

	// Setup ==================================================================

	public FuncDeclaration(@NotNull Type type, @NotNull String name, @NotNull FuncDeclarationParameters parameters, @NotNull StatementList statementList) {
		this(type, name, parameters, statementList, -1, -1);
	}

	public FuncDeclaration(@NotNull Type type, @NotNull String name, @NotNull FuncDeclarationParameters parameters, @NotNull StatementList statementList, int line, int column) {
		this.type = type;
		this.name = name;
		this.parameters = parameters;
		this.statementList = statementList;
		this.line = line;
		this.column = column;
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(DeclarationVisitor<O> visitor) {
		return visitor.visitFunctionDeclaration(this);
	}
}
