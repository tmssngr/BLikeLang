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

	protected DeclarationList handleDeclarationList(@NotNull DeclarationList declarationList) {
		final DeclarationList newDeclarationList = new DeclarationList();

		for (Declaration declaration : declarationList.getDeclarations()) {
			newDeclarationList.add(declaration.visit(new DeclarationVisitor<>() {
				@Override
				public Declaration visitGlobalVarDeclaration(GlobalVarDeclaration node) {
					return node;
				}

				@Override
				public Declaration visitFunctionDeclaration(FunctionDeclaration node) {
					final StatementNode statement = node.statement;
					final StatementListNode statementList;
					if (statement instanceof StatementListNode) {
						statementList = (StatementListNode) statement;
					}
					else {
						statementList = new StatementListNode();
						statementList.add(statement);
					}
					final StatementListNode newStatementList = handleStatementList(statementList);
					return new FunctionDeclaration(node.type, node.name, node.parameters, newStatementList);
				}
			}));
		}
		return newDeclarationList;
	}

	@NotNull
	private StatementListNode handleStatementList(@NotNull StatementListNode statementList) {
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

				@Override
				public StatementNode visitReturn(ReturnStatement node) {
					return handleReturn(node, newStatementList);
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

	protected StatementNode handleReturn(ReturnStatement node, StatementListNode newStatementList) {
		final ExpressionNode expression = handleExpression(node.expression, newStatementList);
		return new ReturnStatement(expression);
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
