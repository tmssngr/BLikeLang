package node;

import java.util.List;

/**
 * @author Thomas Singer
 */
public final class FunctionCallNode extends ExpressionNode {

	// Fields =================================================================

	private final String name;
	private final FunctionParametersNode parameters;
	private final int line;
	private final int column;

	// Setup ==================================================================

	public FunctionCallNode(String name, FunctionParametersNode parameters, int line, int column) {
		this.name = name;
		this.parameters = parameters;
		this.line = line;
		this.column = column;
	}

	// Accessing ==============================================================

	public String getName() {
		return name;
	}

	public List<ExpressionNode> getExpressions() {
		return parameters.getExpressions();
	}
}
