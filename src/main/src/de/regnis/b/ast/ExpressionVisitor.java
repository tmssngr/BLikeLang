package de.regnis.b.ast;

/**
 * @author Thomas Singer
 */
public interface ExpressionVisitor<O> {
	O visitBinary(BinaryExpression node);

	O visitFunctionCall(FuncCall node);

	O visitNumber(NumberLiteral node);

	O visitVarRead(VarRead node);
}
