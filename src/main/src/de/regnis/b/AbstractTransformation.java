package de.regnis.b;

import de.regnis.b.node.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public abstract class AbstractTransformation {

	// Setup ==================================================================

	protected AbstractTransformation() {
	}

	// Accessing ==============================================================

	@NotNull
	protected StatementListNode handleStatementList(@NotNull StatementListNode statementList) {
		final StatementListNode newStatementList = new StatementListNode();

		for (StatementNode statement : statementList.getStatements()) {
			newStatementList.add(statement.visit(new StatementVisitor<>() {
				@Override
				public StatementNode visitAssignment(AssignmentNode node) {
					return handleAssignment(node, newStatementList);
				}

				@Override
				public StatementNode visitStatementList(StatementListNode node) {
					return handleStatementList(node);
				}

				@Override
				public StatementNode visitLocalVarDeclaration(VarDeclarationNode node) {
					return handleVarDeclaration(node, newStatementList);
				}
			}));
		}
		return newStatementList;
	}

	protected StatementNode handleAssignment(AssignmentNode node, StatementListNode newStatementList) {
		final ExpressionNode expression = handleExpression(node.expression, newStatementList);
		return new AssignmentNode(node.var, expression, node.line, node.column);
	}

	protected StatementNode handleVarDeclaration(VarDeclarationNode node, StatementListNode newStatementList) {
		final ExpressionNode expression = handleExpression(node.expression, newStatementList);
		return new VarDeclarationNode(node.var, expression, node.line, node.column);
	}

	protected ExpressionNode handleBinary(BinaryExpressionNode node, StatementListNode newStatementList) {
		return node;
	}

	protected ExpressionNode handleFunctionCall(FunctionCallNode node, StatementListNode newStatementList) {
		return node;
	}

	// Utils ==================================================================

	private ExpressionNode handleExpression(ExpressionNode expression, StatementListNode newStatementList) {
		return expression.visit(new ExpressionVisitor<>() {
			@Override
			public ExpressionNode visitBinary(BinaryExpressionNode node) {
				return handleBinary(node, newStatementList);
			}

			@Override
			public ExpressionNode visitFunctionCall(FunctionCallNode node) {
				return handleFunctionCall(node, newStatementList);
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
}
