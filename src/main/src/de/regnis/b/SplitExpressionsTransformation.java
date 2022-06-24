package de.regnis.b;

import de.regnis.b.node.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Thomas Singer
 */
public final class SplitExpressionsTransformation {

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

	@NotNull
	private StatementListNode handleStatementList(@NotNull StatementListNode statementList) {
		final StatementListNode newStatementList = new StatementListNode();

		final List<? extends StatementNode> statements = statementList.getStatements();
		for (StatementNode statement : statements) {
			if (statement instanceof VarDeclarationNode) {
				final VarDeclarationNode varDeclarationNode = (VarDeclarationNode) statement;
				final ExpressionNode simplifiedExpression = splitOutMostExpression(varDeclarationNode.expression, newStatementList);
				newStatementList.add(new VarDeclarationNode(varDeclarationNode.var, simplifiedExpression, varDeclarationNode.line, varDeclarationNode.column));
			}
			else if (statement instanceof AssignmentNode) {
				final AssignmentNode assignmentNode = (AssignmentNode) statement;
				final ExpressionNode simplifiedExpression = splitOutMostExpression(assignmentNode.expression, newStatementList);
				newStatementList.add(new AssignmentNode(assignmentNode.var, simplifiedExpression, assignmentNode.line, assignmentNode.column));
			}
			else if (statement instanceof StatementListNode) {
				newStatementList.add(handleStatementList((StatementListNode) statement));
			}
		}
		return newStatementList;
	}

	@NotNull
	private ExpressionNode splitOutMostExpression(ExpressionNode expression, StatementListNode list) {
		if (expression instanceof BinaryExpressionNode) {
			final BinaryExpressionNode ben = (BinaryExpressionNode) expression;
			return createSimplifiedBinaryExpression(ben, list);
		}

		if (expression instanceof FunctionCallNode) {
			final FunctionCallNode fcn = (FunctionCallNode) expression;
			return createSimplifiedFunctionCall(fcn, list);
		}

		return expression;
	}

	@NotNull
	private BinaryExpressionNode createSimplifiedBinaryExpression(BinaryExpressionNode ben, StatementListNode list) {
		final ExpressionNode left = splitInnerExpression(ben.left, list);
		final ExpressionNode right = splitInnerExpression(ben.right, list);
		return ben.createNew(left, right);
	}

	@NotNull
	private FunctionCallNode createSimplifiedFunctionCall(FunctionCallNode node, StatementListNode list) {
		final FunctionParametersNode parameters = new FunctionParametersNode();
		for (ExpressionNode parameter : node.getParameters()) {
			final ExpressionNode simplifiedParameter = splitInnerExpression(parameter, list);
			parameters.add(simplifiedParameter);
		}
		return new FunctionCallNode(node.name, parameters, node.line, node.column);
	}

	@NotNull
	private ExpressionNode splitInnerExpression(ExpressionNode expressionNode, StatementListNode list) {
		if (expressionNode instanceof BinaryExpressionNode) {
			final BinaryExpressionNode ben = createSimplifiedBinaryExpression((BinaryExpressionNode) expressionNode, list);
			return createTempVar(ben, list);
		}
		if (expressionNode instanceof FunctionCallNode) {
			final FunctionCallNode fcn = createSimplifiedFunctionCall((FunctionCallNode) expressionNode, list);
			return createTempVar(fcn, list);
		}
		return expressionNode;
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
		return "t " + tempVarIndex;
	}
}
