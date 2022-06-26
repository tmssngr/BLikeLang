package de.regnis.b.node;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class GlobalVarDeclaration extends Declaration {

	// Fields =================================================================

	public final VarDeclaration node;

	// Setup ==================================================================

	public GlobalVarDeclaration(@NotNull VarDeclaration node) {
		this.node = node;
	}

	@Override
	public <O> O visit(DeclarationVisitor<O> visitor) {
		return visitor.visitGlobalVarDeclaration(this);
	}
}
