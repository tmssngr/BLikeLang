package de.regnis.b.ast;

import de.regnis.b.type.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class BinaryExpression extends Expression {

	// Constants ==============================================================

	public static final String PLUS = "+";
	public static final String MINUS = "-";
	public static final String MULTIPLY = "*";
	public static final String DIVIDE = "/";
	public static final String MODULO = "%";
	public static final String SHIFT_L = "<<";
	public static final String SHIFT_R = ">>";
	public static final String LT = "<";
	public static final String LE = "<=";
	public static final String EQ = "==";
	public static final String GE = ">=";
	public static final String GT = ">";
	public static final String NE = "!=";
	public static final String BIT_AND = "&";
	public static final String BIT_OR = "|";
	public static final String BIT_XOR = "^";

	// Static =================================================================

	public static boolean isComparison(String operator) {
		return operator.equals(LT)
				|| operator.equals(LE)
				|| operator.equals(EQ)
				|| operator.equals(GE)
				|| operator.equals(GT)
				|| operator.equals(NE)
				;
	}

	// Fields =================================================================

	public final Expression left;
	public final String operator;
	public final Expression right;

	// Setup ==================================================================

	public BinaryExpression(@NotNull Expression left, @NotNull String operator, @NotNull Expression right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	public BinaryExpression(@NotNull Expression left, @NotNull String operator, @NotNull Expression right, @NotNull Type type) {
		this(left, operator, right);
		setType(type);
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return left + " " + operator + " " + right;
	}

	@Override
	public <O> O visit(ExpressionVisitor<O> visitor) {
		return visitor.visitBinary(this);
	}
}
