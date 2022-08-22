package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author Thomas Singer
 */
public final class FuncCallParameters {

	// Static =================================================================

	public static FuncCallParameters empty() {
		return new FuncCallParameters(List.of());
	}

	public static FuncCallParameters of(@NotNull Expression expression) {
		return new FuncCallParameters(List.of(expression));
	}

	public static FuncCallParameters of(@NotNull Expression expr1, @NotNull Expression expr2) {
		return new FuncCallParameters(List.of(expr1, expr2));
	}

	public static FuncCallParameters of(@NotNull List<Expression> expressions) {
		return new FuncCallParameters(new ArrayList<>(expressions));
	}

	// Fields =================================================================

	private final List<Expression> expressions;

	// Setup ==================================================================

	private FuncCallParameters(@NotNull List<Expression> expressions) {
		this.expressions = expressions;
	}

	// Accessing ==============================================================

	@NotNull
	public List<Expression> getExpressions() {
		return Collections.unmodifiableList(expressions);
	}

	@NotNull
	public FuncCallParameters transform(@NotNull Function<Expression, Expression> function) {
		final List<Expression> newExpressions = new ArrayList<>(expressions.size());
		for (Expression expression : expressions) {
			final Expression newExpression = function.apply(expression);
			newExpressions.add(newExpression);
		}
		return new FuncCallParameters(newExpressions);
	}
}
