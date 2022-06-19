package node;

import java.util.Objects;

/**
 * @author Thomas Singer
 */
public final class AssignmentNode extends StatementNode {

	// Fields =================================================================

	public final String var;
	public final ExpressionNode expression;

	// Setup ==================================================================

	public AssignmentNode(String var, ExpressionNode expression) {
		Objects.requireNonNull(var);
		Objects.requireNonNull(expression);

		this.var = var;
		this.expression = expression;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return "set(" + var + ", " + expression + ")";
	}

	@Override
	public void visit(NodeVisitor visitor) {
		expression.visit(visitor);
		visitor.visitAssignment(var);
	}
}
