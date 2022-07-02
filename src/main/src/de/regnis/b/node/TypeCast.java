package de.regnis.b.node;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class TypeCast extends Expression {

	// Fields =================================================================

	public final String typeName;
	public final Expression expression;

	// Setup ==================================================================

	public TypeCast(@NotNull String typeName, @NotNull Expression expression) {
		this.typeName = typeName;
		this.expression = expression;
	}

	public TypeCast(@NotNull Type type, @NotNull Expression expression) {
		typeName = type.toString();
		this.expression = expression;
		setType(type);
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(ExpressionVisitor<O> visitor) {
		return visitor.visitTypeCast(this);
	}
}
