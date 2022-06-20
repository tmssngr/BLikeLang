package node;

import java.util.Objects;

import org.antlr.v4.runtime.Token;

/**
 * @author Thomas Singer
 */
public final class VarDeclarationNode extends StatementNode {

	// Fields =================================================================

	private final String type;
	private final Token var;
	private final ExpressionNode expression;

	// Setup ==================================================================

	public VarDeclarationNode(String type, Token var, ExpressionNode expression) {
		Objects.requireNonNull(type);
		Objects.requireNonNull(var);
		Objects.requireNonNull(expression);

		this.type = type;
		this.var = var;
		this.expression = expression;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return "declare(" + type + " " + var.getText() + ", " + expression + ")";
	}

	@Override
	public void visit(NodeVisitor visitor) {
		expression.visit(visitor);
		visitor.visitDeclaration(var.getText(), var);
	}
}
