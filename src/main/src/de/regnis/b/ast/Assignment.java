package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class Assignment extends SimpleStatement {

	// Fields =================================================================

	public final Op operation;
	public final String name;
	public final Expression expression;
	public final Position position;

	// Setup ==================================================================

	public Assignment(@NotNull Op operation, @NotNull String name, @NotNull Expression expression) {
		this(operation, name, expression, Position.DUMMY);
	}

	public Assignment(@NotNull Op operation, @NotNull String name, @NotNull Expression expression, @NotNull Position position) {
		this.operation = operation;
		this.name = name;
		this.expression = expression;
		this.position = position;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return name + " " + operation.text + " " + expression;
	}

	@Override
	public <O> O visit(StatementVisitor<O> visitor) {
		return visitor.visitAssignment(this);
	}

	@Override
	public <O> O visit(SimpleStatementVisitor<O> visitor) {
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
