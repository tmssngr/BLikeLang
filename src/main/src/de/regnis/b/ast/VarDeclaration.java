package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record VarDeclaration(@NotNull String name, @NotNull Expression expression,
                             @NotNull Position position) implements SimpleStatement {

	// Static =================================================================

	@NotNull
	public static VarDeclaration createTempVarDeclaration(@NotNull String name, @NotNull Expression expression) {
		return new VarDeclaration(name, expression);
	}

	// Setup ==================================================================

	public VarDeclaration(@NotNull String name, @NotNull Expression expression) {
		this(name, expression, Position.DUMMY);
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return "declare(" + name + ":= " + expression + ")";
	}

	@Override
	public <O> O visit(@NotNull StatementVisitor<O> visitor) {
		return visitor.visitLocalVarDeclaration(this);
	}

	@Override
	public <O> O visit(@NotNull SimpleStatementVisitor<O> visitor) {
		return visitor.visitLocalVarDeclaration(this);
	}

	// Accessing ==============================================================

	public VarDeclaration derive(@NotNull Expression expression) {
		return new VarDeclaration(name, expression, position);
	}
}
