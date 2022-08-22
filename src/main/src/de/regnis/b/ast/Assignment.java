package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record Assignment(@NotNull Op operation, @NotNull String name, @NotNull Expression expression, @NotNull Position position) implements SimpleStatement {

	// Setup ==================================================================

	public Assignment(@NotNull Op operation, @NotNull String name, @NotNull Expression expression) {
		this(operation, name, expression, Position.DUMMY);
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return name + " " + operation.text + " " + expression;
	}

	@Override
	public <O> O visit(@NotNull StatementVisitor<O> visitor) {
		return visitor.visitAssignment(this);
	}

	@Override
	public <O> O visit(@NotNull SimpleStatementVisitor<O> visitor) {
		return visitor.visitAssignment(this);
	}

	// Inner Classes ==========================================================

	public enum Op {
		assign("="),
		add("+="), sub("-="), multiply("*="), divide("/="), modulo("%="), shiftL("<<="), shiftR(">>="),
		bitAnd("&="), bitOr("|="), bitXor("^=");

		public final String text;

		Op(String text) {
			this.text = text;
		}
	}
}
