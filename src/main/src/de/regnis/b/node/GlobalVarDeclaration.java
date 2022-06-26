package de.regnis.b.node;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class GlobalVarDeclaration extends Declaration {

	// Fields =================================================================

	public final VarDeclarationNode node;

	// Setup ==================================================================

	public GlobalVarDeclaration(@NotNull VarDeclarationNode node) {
		this.node = node;
	}

	@Override
	public <O> O visit(DeclarationVisitor<O> visitor) {
		return visitor.visitGlobalVarDeclaration(this);
	}
}
