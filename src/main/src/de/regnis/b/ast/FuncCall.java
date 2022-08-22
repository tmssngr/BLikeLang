package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class FuncCall extends Expression {

	// Fields =================================================================

	private final List<Expression> parameters;
	public final String name;
	public final Position position;

	// Setup ==================================================================

	public FuncCall(@NotNull String name, @NotNull FuncCallParameters parameters) {
		this(name, parameters, Position.DUMMY);
	}

	public FuncCall(@NotNull String name, @NotNull FuncCallParameters parameters, @NotNull Position position) {
		this.name = name;
		this.position = position;
		this.parameters = Collections.unmodifiableList(new ArrayList<>(parameters.getExpressions()));
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(ExpressionVisitor<O> visitor) {
		return visitor.visitFunctionCall(this);
	}

	@Override
	public String toString() {
		return "func call " + name;
	}

	// Accessing ==============================================================

	public List<Expression> getParameters() {
		return parameters;
	}
}
