package node;

import java.util.Objects;

/**
 * @author Thomas Singer
 */
public final class AssignmentNode extends StatementNode {

	// Fields =================================================================

	private final String var;
	private final ExpressionNode expression;
	private final int line;
	private final int column;

	// Setup ==================================================================

	public AssignmentNode(String var, ExpressionNode expression, int line, int column) {
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
		return "set(" + var + ", " + expression + ")";
	}

	@Override
	public void visit(NodeVisitor visitor) {
		expression.visit(visitor);
		visitor.visitAssignment(var, line, column);
	}
}
