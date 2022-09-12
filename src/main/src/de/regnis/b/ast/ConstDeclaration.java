package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record ConstDeclaration(@NotNull String name, @NotNull Expression expression, @NotNull Position position) implements Declaration {

	// Implemented ============================================================

	@Override
	public String toString() {
		return "const " + name + "=" + expression;
	}

	@Override
	public <O> O visit(DeclarationVisitor<O> visitor) {
		return visitor.visitConst(this);
	}
}
