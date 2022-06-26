package de.regnis.b.node;

/**
 * @author Thomas Singer
 */
public final class FunctionDeclaration extends Declaration {

	// Fields =================================================================

	public final String type;
	public final String name;
	public final FunctionDeclarationParameters parameters;
	public final StatementNode statement;

	// Setup ==================================================================

	public FunctionDeclaration(String type, String name, FunctionDeclarationParameters parameters, StatementNode statement) {
		this.type = type;
		this.name = name;
		this.parameters = parameters;
		this.statement = statement;
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(DeclarationVisitor<O> visitor) {
		return visitor.visitFunctionDeclaration(this);
	}
}
