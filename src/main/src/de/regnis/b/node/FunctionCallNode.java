package de.regnis.b.node;

import java.util.List;

/**
 * @author Thomas Singer
 */
public final class FunctionCallNode extends ExpressionNode {

	// Fields =================================================================

	public final String name;
	private final FunctionParametersNode parameters;
	public final int line;
	public final int column;

	// Setup ==================================================================

	public FunctionCallNode(String name, FunctionParametersNode parameters) {
		this(name, parameters, -1, -1);
	}

	public FunctionCallNode(String name, FunctionParametersNode parameters, int line, int column) {
		this.name = name;
		this.parameters = parameters;
		this.line = line;
		this.column = column;
	}

	// Accessing ==============================================================

	public List<ExpressionNode> getExpressions() {
		return parameters.getExpressions();
	}
}
