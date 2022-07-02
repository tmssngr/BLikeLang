package de.regnis.b.node;

/**
 * @author Thomas Singer
 */
public interface ExpressionVisitor<O> {
	O visitBinary(BinaryExpression node);

	O visitFunctionCall(FuncCall node);

	O visitNumber(NumberLiteral node);

	O visitBoolean(BooleanLiteral node);

	O visitVarRead(VarRead node);

	O visitTypeCast(TypeCast node);
}
