package de.regnis.b.node;

import java.util.Objects;

/**
 * @author Thomas Singer
 */
public final class VarDeclarationNode extends StatementNode {

	// Fields =================================================================

	public final String var;
	public final ExpressionNode expression;
	public final int line;
	public final int column;

	// Setup ==================================================================

	public VarDeclarationNode(String var, ExpressionNode expression) {
		this(var, expression, -1, -1);
	}

	public VarDeclarationNode(String var, ExpressionNode expression, int line, int column) {
		Objects.requireNonNull(var);
		Objects.requireNonNull(expression);

		this.var = var;
		this.expression = expression;
		this.line = line;
		this.column = column;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return "declare(" + var + ", " + expression + ")";
	}

	@Override
	public <O> O visit(StatementVisitor<O> visitor) {
		return visitor.visitVarDeclaration(this);
	}
}
