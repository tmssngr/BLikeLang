package node;

import java.util.Objects;

import org.antlr.v4.runtime.Token;

/**
 * @author Thomas Singer
 */
public final class AssignmentNode extends StatementNode {

	// Fields =================================================================

	private final Token var;
	private final ExpressionNode expression;

	// Setup ==================================================================

	public AssignmentNode(Token var, ExpressionNode expression) {
		Objects.requireNonNull(var);
		Objects.requireNonNull(expression);

		this.var = var;
		this.expression = expression;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return "set(" + var.getText() + ", " + expression + ")";
	}

	@Override
	public void visit(NodeVisitor visitor) {
		expression.visit(visitor);
		visitor.visitAssignment(var.getText(), var);
	}
}
