package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class CallStatement extends SimpleStatement {

	// Fields =================================================================

	public final String name;
	public final Position position;
	private final List<Expression> parameters;

	// Setup ==================================================================

	public CallStatement(@NotNull String name, @NotNull FuncCallParameters parameters) {
		this(name, parameters, Position.DUMMY);
	}

	public CallStatement(@NotNull String name, @NotNull FuncCallParameters parameters, @NotNull Position position) {
		this.name = name;
		this.position = position;
		this.parameters = Collections.unmodifiableList(new ArrayList<>(parameters.getExpressions()));
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(StatementVisitor<O> visitor) {
		return visitor.visitCall(this);
	}

	@Override
	public <O> O visit(SimpleStatementVisitor<O> visitor) {
		return visitor.visitCall(this);
	}

	// Accessing ==============================================================

	public List<Expression> getParameters() {
		return parameters;
	}
}
