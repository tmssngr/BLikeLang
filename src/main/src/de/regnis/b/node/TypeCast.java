package de.regnis.b.node;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class TypeCast extends Expression {

	// Fields =================================================================

	public final String typeName;
	public final Expression expression;
	public final int line;
	public final int column;

	// Setup ==================================================================

	public TypeCast(@NotNull String typeName, @NotNull Expression expression) {
		this(typeName, expression, -1, -1);
	}

	public TypeCast(@NotNull String typeName, @NotNull Expression expression, int line, int column) {
		this.typeName = typeName;
		this.expression = expression;
		this.line = line;
		this.column = column;
	}

	public TypeCast(@NotNull Type type, @NotNull Expression expression) {
		typeName = type.toString();
		this.expression = expression;
		line = -1;
		column = -1;
		setType(type);
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(ExpressionVisitor<O> visitor) {
		return visitor.visitTypeCast(this);
	}
}
