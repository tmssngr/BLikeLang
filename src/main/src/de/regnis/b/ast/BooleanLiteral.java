package de.regnis.b.ast;

import de.regnis.b.type.BasicTypes;

/**
 * @author Thomas Singer
 */
public final class BooleanLiteral extends Expression {

	// Constants ==============================================================

	public static final BooleanLiteral TRUE = new BooleanLiteral(true);
	public static final BooleanLiteral FALSE = new BooleanLiteral(false);

	// Static =================================================================

	public static BooleanLiteral get(boolean value) {
		return value ? TRUE : FALSE;
	}

	// Fields =================================================================

	public final boolean value;

	// Setup ==================================================================

	private BooleanLiteral(boolean value) {
		this.value = value;
		setType(BasicTypes.BOOLEAN);
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public <O> O visit(ExpressionVisitor<O> visitor) {
		return visitor.visitBoolean(this);
	}
}
