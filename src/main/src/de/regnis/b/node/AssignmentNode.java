package de.regnis.b.node;

import node.NodeVisitor;

import java.util.Objects;

/**
 * @author Thomas Singer
 */
public final class AssignmentNode extends StatementNode {

	// Fields =================================================================

	public final String var;
	public final ExpressionNode expression;
	public final int line;
	public final int column;

	// Setup ==================================================================

	public AssignmentNode(String var, ExpressionNode expression) {
		this(var, expression, -1, -1);
	}

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
