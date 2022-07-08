package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class CallStatement extends Statement {

	// Fields =================================================================

	public final String name;
	public final int line;
	public final int column;
	private final List<Expression> parameters;

	// Setup ==================================================================

	public CallStatement(@NotNull String name, @NotNull FuncCallParameters parameters) {
		this(name, parameters, -1, -1);
	}

	public CallStatement(@NotNull String name, @NotNull FuncCallParameters parameters, int line, int column) {
		this.name = name;
		this.line = line;
		this.column = column;
		this.parameters = Collections.unmodifiableList(new ArrayList<>(parameters.getExpressions()));
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(StatementVisitor<O> visitor) {
		return visitor.visitCall(this);
	}

	// Accessing ==============================================================

	public List<Expression> getParameters() {
		return parameters;
	}
}
