package de.regnis.b.node;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class VarDeclaration extends Statement {

	// Fields =================================================================

	public final String var;
	public final Expression expression;
	public final int line;
	public final int column;

	// Setup ==================================================================

	public VarDeclaration(@NotNull String var, @NotNull Expression expression) {
		this(var, expression, -1, -1);
	}

	public VarDeclaration(@NotNull String var, @NotNull Expression expression, int line, int column) {
		this.var = var;
		this.expression = expression;
		this.line = line;
		this.column = column;
	}

	public VarDeclaration(@NotNull String var, Type type, Expression expression) {
		this(var, expression);
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return "declare(" + var + ", " + expression + ")";
	}

	@Override
	public <O> O visit(StatementVisitor<O> visitor) {
		return visitor.visitLocalVarDeclaration(this);
	}
}
