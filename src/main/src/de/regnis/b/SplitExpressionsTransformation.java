package de.regnis.b;

import de.regnis.b.node.*;
import org.jetbrains.annotations.NotNull;

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

		for (StatementNode statement : statementList.getStatements()) {
			newStatementList.add(statement.visit(new StatementVisitor<>() {
				@Override
				public StatementNode visitAssignment(AssignmentNode node) {
					final ExpressionNode simplifiedExpression = splitOutMostExpression(node.expression, newStatementList);
					return new AssignmentNode(node.var, simplifiedExpression, node.line, node.column);
				}

				@Override
				public StatementNode visitStatementList(StatementListNode node) {
					return handleStatementList(node);
				}

				@Override
				public StatementNode visitVarDeclaration(VarDeclarationNode node) {
					final ExpressionNode simplifiedExpression = splitOutMostExpression(node.expression, newStatementList);
					return new VarDeclarationNode(node.var, simplifiedExpression, node.line, node.column);
				}
			}));
		}
		return newStatementList;
	}

	@NotNull
	private ExpressionNode splitOutMostExpression(ExpressionNode expression, StatementListNode list) {
		return expression.visit(new ExpressionVisitor<>() {
			@Override
			public ExpressionNode visitBinary(BinaryExpressionNode node) {
				return createSimplifiedBinaryExpression(node, list);
			}

			@Override
			public ExpressionNode visitFunctionCall(FunctionCallNode node) {
				return createSimplifiedFunctionCall(node, list);
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
		return expressionNode.visit(new ExpressionVisitor<>() {
			@Override
			public ExpressionNode visitBinary(BinaryExpressionNode node) {
				final BinaryExpressionNode ben = createSimplifiedBinaryExpression(node, list);
				return createTempVar(ben, list);
			}

			@Override
			public ExpressionNode visitFunctionCall(FunctionCallNode node) {
				final FunctionCallNode fcn = createSimplifiedFunctionCall(node, list);
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
		return "t " + tempVarIndex;
	}
}
