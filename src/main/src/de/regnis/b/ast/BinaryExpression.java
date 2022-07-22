package de.regnis.b.ast;

import de.regnis.b.type.Type;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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

	public static BinaryExpression createAdd(Expression left, Expression right) {
		return new BinaryExpression(left, PLUS, right);
	}

	public static BinaryExpression createSub(Expression left, Expression right) {
		return new BinaryExpression(left, MINUS, right);
	}

	public static BinaryExpression createMultiply(Expression left, Expression right) {
		return new BinaryExpression(left, MULTIPLY, right);
	}

	public static BinaryExpression createDivide(Expression left, Expression right) {
		return new BinaryExpression(left, DIVIDE, right);
	}

	public static BinaryExpression createModulo(Expression left, Expression right) {
		return new BinaryExpression(left, MODULO, right);
	}

	public static BinaryExpression createShiftL(Expression left, Expression right) {
		return new BinaryExpression(left, SHIFT_L, right);
	}

	public static BinaryExpression createShiftR(Expression left, Expression right) {
		return new BinaryExpression(left, SHIFT_R, right);
	}

	public static BinaryExpression createLt(Expression left, Expression right) {
		return new BinaryExpression(left, LT, right);
	}

	public static BinaryExpression createLe(Expression left, Expression right) {
		return new BinaryExpression(left, LE, right);
	}

	public static BinaryExpression createEq(Expression left, Expression right) {
		return new BinaryExpression(left, EQ, right);
	}

	public static BinaryExpression createGe(Expression left, Expression right) {
		return new BinaryExpression(left, GE, right);
	}

	public static BinaryExpression createGt(Expression left, Expression right) {
		return new BinaryExpression(left, GT, right);
	}

	public static BinaryExpression createNe(Expression left, Expression right) {
		return new BinaryExpression(left, NE, right);
	}

	public static BinaryExpression createBitAnd(Expression left, Expression right) {
		return new BinaryExpression(left, BIT_AND, right);
	}
	public static BinaryExpression createBitOr(Expression left, Expression right) {
		return new BinaryExpression(left, BIT_OR, right);
	}
	public static BinaryExpression createBitXor(Expression left, Expression right) {
		return new BinaryExpression(left, BIT_XOR, right);
	}


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

	private BinaryExpression(Expression left, String operator, Expression right) {
		Objects.requireNonNull(left);
		Objects.requireNonNull(operator);
		Objects.requireNonNull(right);

		this.left = left;
		this.operator = operator;
		this.right = right;
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

	// Accessing ==============================================================

	@NotNull
	public BinaryExpression createNew(@NotNull Expression left, @NotNull Expression right) {
		return new BinaryExpression(left, operator, right);
	}

	@NotNull
	public BinaryExpression createNew(@NotNull Type type, @NotNull Expression left, @NotNull Expression right) {
		final BinaryExpression binaryExpression = new BinaryExpression(left, operator, right);
		binaryExpression.setType(type);
		return binaryExpression;
	}
}
