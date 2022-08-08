package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class VarDeclaration extends SimpleStatement {

	// Static =================================================================

	@NotNull
	public static VarDeclaration createTempVarDeclaration(@NotNull String name, @NotNull Expression expression) {
		return new VarDeclaration(name, expression);
	}

	// Fields =================================================================

	public final String name;
	public final Expression expression;
	public final int line;
	public final int column;

	// Setup ==================================================================

	public VarDeclaration(@NotNull String name, @NotNull Expression expression) {
		this(name, expression, -1, -1);
	}

	public VarDeclaration(@NotNull String name, @NotNull Expression expression, int line, int column) {
		this.name = name;
		this.expression = expression;
		this.line = line;
		this.column = column;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return "declare(" + name + ":= " + expression + ")";
	}

	@Override
	public <O> O visit(StatementVisitor<O> visitor) {
		return visitor.visitLocalVarDeclaration(this);
	}

	@Override
	public <O> O visit(SimpleStatementVisitor<O> visitor) {
		return visitor.visitLocalVarDeclaration(this);
	}

	// Accessing ==============================================================

	public VarDeclaration derive(@NotNull Expression expression) {
		return new VarDeclaration(name, expression, line, column);
	}
}
