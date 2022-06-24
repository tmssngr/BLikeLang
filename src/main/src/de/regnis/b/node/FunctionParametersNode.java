package de.regnis.b.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class FunctionParametersNode extends Node {

	// Fields =================================================================

	private final List<ExpressionNode> expressions = new ArrayList<>();

	// Setup ==================================================================

	public FunctionParametersNode() {
	}

	// Accessing ==============================================================

	public FunctionParametersNode add(ExpressionNode node) {
		expressions.add(node);
		return this;
	}

	public List<ExpressionNode> getExpressions() {
		return Collections.unmodifiableList(expressions);
	}
}
