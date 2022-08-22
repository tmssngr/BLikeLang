package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class CallStatement implements SimpleStatement {

	// Fields =================================================================

	public final String name;
	public final FuncCallParameters parameters;
	public final Position position;

	// Setup ==================================================================

	public CallStatement(@NotNull String name, @NotNull FuncCallParameters parameters) {
		this(name, parameters, Position.DUMMY);
	}

	public CallStatement(@NotNull String name, @NotNull FuncCallParameters parameters, @NotNull Position position) {
		this.name       = name;
		this.parameters = parameters;
		this.position   = position;
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(@NotNull StatementVisitor<O> visitor) {
		return visitor.visitCall(this);
	}

	@Override
	public <O> O visit(@NotNull SimpleStatementVisitor<O> visitor) {
		return visitor.visitCall(this);
	}
}
