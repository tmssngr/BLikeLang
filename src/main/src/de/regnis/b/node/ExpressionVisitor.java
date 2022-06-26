package de.regnis.b.node;

import de.regnis.b.node.BinaryExpressionNode;
import de.regnis.b.node.FunctionCallNode;
import de.regnis.b.node.NumberNode;
import de.regnis.b.node.VarReadNode;

/**
 * @author Thomas Singer
 */
public interface ExpressionVisitor<O> {
	O visitBinary(BinaryExpressionNode node);

	O visitFunctionCall(FunctionCallNode node);

	O visitNumber(NumberNode node);

	O visitVarRead(VarReadNode node);
}
