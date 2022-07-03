package de.regnis.b;

import de.regnis.b.node.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public abstract class AbstractTransformation<H> {

	// Setup ==================================================================

	protected AbstractTransformation() {
	}

	// Accessing ==============================================================

	protected DeclarationList handleDeclarationList(@NotNull DeclarationList declarationList) {
		final DeclarationList newDeclarationList = new DeclarationList();

		final H helper = createHelper(newDeclarationList);
		for (Declaration declaration : declarationList.getDeclarations()) {
			newDeclarationList.add(declaration.visit(new DeclarationVisitor<>() {
				@Override
				public Declaration visitGlobalVarDeclaration(GlobalVarDeclaration node) {
					return new GlobalVarDeclaration(handleVarDeclaration(node.node, helper));
				}

				@Override
				public Declaration visitFunctionDeclaration(FuncDeclaration node) {
					final StatementList newStatementList = handleStatementList(node.statementList);
					return new FuncDeclaration(node.type, node.name, node.parameters, newStatementList);
				}
			}));
		}
		return newDeclarationList;
	}

	protected H createHelper(DeclarationList newDeclarationList) {
		return null;
	}

	protected H createHelper(StatementList newStatementList) {
		return null;
	}

	protected Statement handleAssignment(Assignment node, H helper) {
		final Expression expression = handleExpression(node.expression, helper);
		return new Assignment(node.var, expression, node.line, node.column);
	}

	protected VarDeclaration handleVarDeclaration(VarDeclaration node, H helper) {
		final Expression expression = handleExpression(node.expression, helper);
		return node.derive(expression);
	}

	protected Statement handleReturn(ReturnStatement node, H helper) {
		if (node.expression == null) {
			return node;
		}

		final Expression expression = handleExpression(node.expression, helper);
		return new ReturnStatement(expression);
	}

	protected Expression handleBinary(BinaryExpression node, H helper) {
		return node;
	}

	protected Expression handleFunctionCall(FuncCall node, H helper) {
		return node;
	}

	// Utils ==================================================================

	@NotNull
	private StatementList handleStatementList(@NotNull StatementList statementList) {
		final StatementList newStatementList = new StatementList();

		final H helper = createHelper(newStatementList);
		for (Statement statement : statementList.getStatements()) {
			newStatementList.add(statement.visit(new StatementVisitor<>() {
				@Override
				public Statement visitAssignment(Assignment node) {
					return handleAssignment(node, helper);
				}

				@Override
				public Statement visitStatementList(StatementList node) {
					return handleStatementList(node);
				}

				@Override
				public Statement visitLocalVarDeclaration(VarDeclaration node) {
					return handleVarDeclaration(node, helper);
				}

				@Override
				public Statement visitReturn(ReturnStatement node) {
					return handleReturn(node, helper);
				}
			}));
		}
		return newStatementList;
	}

	private Expression handleExpression(Expression expression, H parameter) {
		return expression.visit(new ExpressionVisitor<>() {
			@Override
			public Expression visitBinary(BinaryExpression node) {
				return handleBinary(node, parameter);
			}

			@Override
			public Expression visitFunctionCall(FuncCall node) {
				return handleFunctionCall(node, parameter);
			}

			@Override
			public Expression visitNumber(NumberLiteral node) {
				return node;
			}

			@Override
			public Expression visitBoolean(BooleanLiteral node) {
				return node;
			}

			@Override
			public Expression visitVarRead(VarRead node) {
				return node;
			}

			@Override
			public Expression visitTypeCast(TypeCast node) {
				return node;
			}
		});
	}
}
