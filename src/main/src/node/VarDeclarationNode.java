package node;

import java.util.Objects;

/**
 * @author Thomas Singer
 */
public final class VarDeclarationNode extends StatementNode {

	// Fields =================================================================

	public final String type;
	public final String var;
	public final ExpressionNode expression;
	private final int line;
	private final int column;

	// Setup ==================================================================

	public VarDeclarationNode(String type, String var, ExpressionNode expression, int line, int column) {
		Objects.requireNonNull(type);
		Objects.requireNonNull(var);
		Objects.requireNonNull(expression);

		this.type = type;
		this.var = var;
		this.expression = expression;
		this.line = line;
		this.column = column;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return "declare(" + type + " " + var + ", " + expression + ")";
	}

	@Override
	public void visit(NodeVisitor visitor) {
		expression.visit(visitor);
		visitor.visitDeclaration(var, line, column);
	}
}
