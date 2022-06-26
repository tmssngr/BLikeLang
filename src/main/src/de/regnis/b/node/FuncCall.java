package de.regnis.b.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class FuncCall extends Expression {

	// Fields =================================================================

	public final String name;
	public final int line;
	public final int column;
	private final List<Expression> parameters;

	// Setup ==================================================================

	public FuncCall(String name, FuncCallParameters parameters) {
		this(name, parameters, -1, -1);
	}

	public FuncCall(String name, FuncCallParameters parameters, int line, int column) {
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

	public List<Expression> getParameters() {
		return parameters;
	}
}
