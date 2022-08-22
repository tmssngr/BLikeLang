package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record NumberLiteral(int value) implements SimpleExpression {

	// Constants ==============================================================

	public static final NumberLiteral FALSE = new NumberLiteral(0);
	public static final NumberLiteral TRUE = new NumberLiteral(1);

	// Static =================================================================

	public static NumberLiteral get(boolean value) {
		return value ? TRUE : FALSE;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public <O> O visit(@NotNull ExpressionVisitor<O> visitor) {
		return visitor.visitNumber(this);
	}
}
