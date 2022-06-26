package de.regnis.b.node;

/**
 * @author Thomas Singer
 */
public final class FuncDeclaration extends Declaration {

	// Fields =================================================================

	public final String type;
	public final String name;
	public final FuncDeclarationParameters parameters;
	public final Statement statement;

	// Setup ==================================================================

	public FuncDeclaration(String type, String name, FuncDeclarationParameters parameters, Statement statement) {
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
