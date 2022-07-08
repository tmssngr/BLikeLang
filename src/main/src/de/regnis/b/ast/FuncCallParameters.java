package de.regnis.b.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class FuncCallParameters extends Node {

	// Fields =================================================================

	private final List<Expression> expressions = new ArrayList<>();

	// Setup ==================================================================

	public FuncCallParameters() {
	}

	// Accessing ==============================================================

	public FuncCallParameters add(Expression node) {
		expressions.add(node);
		return this;
	}

	public List<Expression> getExpressions() {
		return Collections.unmodifiableList(expressions);
	}
}
