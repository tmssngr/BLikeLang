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
				public Declaration visitFunctionDeclaration(FuncDeclaration node) {
					final Statement statement = node.statement;
					final StatementList statementList;
					if (statement instanceof StatementList) {
						statementList = (StatementList) statement;
					}
					else {
						statementList = new StatementList();
						statementList.add(statement);
					}
					final StatementList newStatementList = handleStatementList(statementList);
					return new FuncDeclaration(node.type, node.name, node.parameters, newStatementList);
				}
			}));
		}
		return newDeclarationList;
	}

	@NotNull
	private StatementList handleStatementList(@NotNull StatementList statementList) {
		final StatementList newStatementList = new StatementList();

		for (Statement statement : statementList.getStatements()) {
			newStatementList.add(statement.visit(new StatementVisitor<>() {
				@Override
				public Statement visitAssignment(Assignment node) {
					return handleAssignment(node, newStatementList);
				}

				@Override
				public Statement visitStatementList(StatementList node) {
					return handleStatementList(node);
				}

				@Override
				public Statement visitLocalVarDeclaration(VarDeclaration node) {
					return handleVarDeclaration(node, newStatementList);
				}

				@Override
				public Statement visitReturn(ReturnStatement node) {
					return handleReturn(node, newStatementList);
				}
			}));
		}
		return newStatementList;
	}

	protected Statement handleAssignment(Assignment node, StatementList newStatementList) {
		final Expression expression = handleExpression(node.expression, newStatementList);
		return new Assignment(node.var, expression, node.line, node.column);
	}

	protected Statement handleVarDeclaration(VarDeclaration node, StatementList newStatementList) {
		final Expression expression = handleExpression(node.expression, newStatementList);
		return new VarDeclaration(node.var, expression, node.line, node.column);
	}

	protected Statement handleReturn(ReturnStatement node, StatementList newStatementList) {
		final Expression expression = handleExpression(node.expression, newStatementList);
		return new ReturnStatement(expression);
	}

	protected Expression handleBinary(BinaryExpression node, StatementList newStatementList) {
		return node;
	}

	protected Expression handleFunctionCall(FuncCall node, StatementList newStatementList) {
		return node;
	}

	// Utils ==================================================================

	private Expression handleExpression(Expression expression, StatementList newStatementList) {
		return expression.visit(new ExpressionVisitor<>() {
			@Override
			public Expression visitBinary(BinaryExpression node) {
				return handleBinary(node, newStatementList);
			}

			@Override
			public Expression visitFunctionCall(FuncCall node) {
				return handleFunctionCall(node, newStatementList);
			}

			@Override
			public Expression visitNumber(NumberLiteral node) {
				return node;
			}

			@Override
			public Expression visitVarRead(VarRead node) {
				return node;
			}
		});
	}
}
