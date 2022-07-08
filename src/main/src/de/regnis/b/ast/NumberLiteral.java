package de.regnis.b.ast;

import de.regnis.b.type.BasicTypes;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class NumberLiteral extends Expression {

	// Fields =================================================================

	public final int value;

	// Setup ==================================================================

	public NumberLiteral(int value) {
		this(value, BasicTypes.determineType(value));
	}

	public NumberLiteral(int value, @NotNull BasicTypes.NumericType type) {
		this.value = value;
		setType(type);
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public <O> O visit(ExpressionVisitor<O> visitor) {
		return visitor.visitNumber(this);
	}
}
