package de.regnis.b.node;

import de.regnis.b.ExpressionVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class FunctionCallNode extends ExpressionNode {

	// Fields =================================================================

	public final String name;
	public final int line;
	public final int column;
	private final List<ExpressionNode> parameters;

	// Setup ==================================================================

	public FunctionCallNode(String name, FunctionCallParameters parameters) {
		this(name, parameters, -1, -1);
	}

	public FunctionCallNode(String name, FunctionCallParameters parameters, int line, int column) {
		this.name = name;
		this.line = line;
		this.column = column;
		this.parameters = Collections.unmodifiableList(new ArrayList<>(parameters.getExpressions()));
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(ExpressionVisitor<O> visitor) {
		return visitor.visitFunctionCall(this);
	}

	// Accessing ==============================================================

	public List<ExpressionNode> getParameters() {
		return parameters;
	}
}
