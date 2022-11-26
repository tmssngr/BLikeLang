package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record CallStatement(@NotNull String name, @NotNull FuncCallParameters parameters,
                            @NotNull Position position) implements SimpleStatement {

	// Setup ==================================================================

	public CallStatement(@NotNull String name, @NotNull FuncCallParameters parameters) {
		this(name, parameters, Position.DUMMY);
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
