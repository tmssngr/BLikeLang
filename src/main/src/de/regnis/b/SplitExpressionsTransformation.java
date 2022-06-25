package de.regnis.b;

import de.regnis.b.node.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class SplitExpressionsTransformation extends AbstractTransformation{

	// Static =================================================================

	public static StatementListNode createTempVars(StatementListNode root) {
		final SplitExpressionsTransformation transformation = new SplitExpressionsTransformation();
		return transformation.handleStatementList(root);
	}

	// Fields =================================================================

	private int tempVarIndex;

	// Setup ==================================================================

	private SplitExpressionsTransformation() {
	}

	// Utils ==================================================================

	@Override
	protected BinaryExpressionNode handleBinary(BinaryExpressionNode node, StatementListNode newStatementList) {
		final ExpressionNode left = splitInnerExpression(node.left, newStatementList);
		final ExpressionNode right = splitInnerExpression(node.right, newStatementList);
		return node.createNew(left, right);
	}

	@Override
	protected FunctionCallNode handleFunctionCall(FunctionCallNode node, StatementListNode newStatementList) {
		final FunctionParametersNode parameters = new FunctionParametersNode();
		for (ExpressionNode parameter : node.getParameters()) {
			final ExpressionNode simplifiedParameter = splitInnerExpression(parameter, newStatementList);
			parameters.add(simplifiedParameter);
		}
		return new FunctionCallNode(node.name, parameters, node.line, node.column);
	}

	@NotNull
	private ExpressionNode splitInnerExpression(ExpressionNode expressionNode, StatementListNode list) {
		return expressionNode.visit(new ExpressionVisitor<>() {
			@Override
			public ExpressionNode visitBinary(BinaryExpressionNode node) {
				final BinaryExpressionNode ben = handleBinary(node, list);
				return createTempVar(ben, list);
			}

			@Override
			public ExpressionNode visitFunctionCall(FunctionCallNode node) {
				final FunctionCallNode fcn = handleFunctionCall(node, list);
				return createTempVar(fcn, list);
			}

			@Override
			public ExpressionNode visitNumber(NumberNode node) {
				return node;
			}

			@Override
			public ExpressionNode visitVarRead(VarReadNode node) {
				return node;
			}
		});
	}

	@NotNull
	private VarReadNode createTempVar(ExpressionNode node, StatementListNode list) {
		final String tempVar = getNextTempVarName();
		list.add(new VarDeclarationNode(tempVar, node, -1, -1));
		return new VarReadNode(tempVar, -1, -1);
	}

	@NotNull
	private String getNextTempVarName() {
		tempVarIndex++;
		return "$" + tempVarIndex;
	}
}
