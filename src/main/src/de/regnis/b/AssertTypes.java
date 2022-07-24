package de.regnis.b;

import de.regnis.b.ast.*;

/**
 * @author Thomas Singer
 */
public final class AssertTypes {

	// Static =================================================================

	public static void assertAllExpressionsHaveType(DeclarationList root) {
		for (Declaration declaration : root.getDeclarations()) {
			declaration.visit(new DeclarationVisitor<>() {
				@Override
				public Object visitGlobalVarDeclaration(GlobalVarDeclaration node) {
					if (node.node.type == null) {
						throw new IllegalStateException("var declaration without type: " + node.node);
					}
					assertAllExpressionsHaveType(node.node.expression);
					if (!(node.node.expression instanceof NumberLiteral) && node.node.type != node.node.expression.getType()) {
						throw new IllegalStateException("var declaration type differs: " + node.node);
					}
					return node;
				}

				@Override
				public Object visitFunctionDeclaration(FuncDeclaration node) {
					assertAllExpressionsHaveType(node);
					return node;
				}
			});
		}
	}

	public static void assertAllExpressionsHaveType(FuncDeclaration node) {
		assertAllExpressionsHaveType(node.statementList);
	}

	// Utils ==================================================================

	private static void assertAllExpressionsHaveType(StatementList statementList) {
		for (Statement statement : statementList.getStatements()) {
			statement.visit(new StatementVisitor<>() {
				@Override
				public Object visitAssignment(Assignment node) {
					if (node.type == null) {
						throw new IllegalStateException("assignment without type: " + node);
					}
					assertAllExpressionsHaveType(node.expression);
					if (!(node.expression instanceof NumberLiteral) && node.type != node.expression.getType()) {
						throw new IllegalStateException("assignment type differs: " + node);
					}
					return node;
				}

				@Override
				public Object visitMemAssignment(MemAssignment node) {
					assertAllExpressionsHaveType(node.expression);
					return node;
				}

				@Override
				public Object visitStatementList(StatementList node) {
					assertAllExpressionsHaveType(node);
					return node;
				}

				@Override
				public Object visitLocalVarDeclaration(VarDeclaration node) {
					if (node.type == null) {
						throw new IllegalStateException("var declaration without type: " + node);
					}
					assertAllExpressionsHaveType(node.expression);
					if (!(node.expression instanceof NumberLiteral) && node.type != node.expression.getType()) {
						throw new IllegalStateException("var declaration type differs: " + node);
					}
					return node;
				}

				@Override
				public Object visitCall(CallStatement node) {
					for (Expression parameter : node.getParameters()) {
						assertAllExpressionsHaveType(parameter);
					}
					return node;
				}

				@Override
				public Object visitReturn(ReturnStatement node) {
					if (node.expression != null) {
						assertAllExpressionsHaveType(node.expression);
					}
					return node;
				}

				@Override
				public Object visitIf(IfStatement node) {
					assertAllExpressionsHaveType(node.expression);
					return node;
				}

				@Override
				public Object visitWhile(WhileStatement node) {
					assertAllExpressionsHaveType(node.expression);
					return node;
				}

				@Override
				public Object visitBreak(BreakStatement node) {
					return node;
				}
			});
		}
	}

	private static void assertAllExpressionsHaveType(Expression expression) {
		if (!expression.hasType()) {
			throw new IllegalStateException("expression without type: " + expression);
		}

		expression.visit(new ExpressionVisitor<>() {
			@Override
			public Object visitBinary(BinaryExpression node) {
				assertAllExpressionsHaveType(node.left);
				assertAllExpressionsHaveType(node.right);
				if (!(node.right instanceof NumberLiteral) && node.left.getType() != node.right.getType()) {
					throw new IllegalStateException("not the same type left and right for expression " + node);
				}
				return node;
			}

			@Override
			public Object visitFunctionCall(FuncCall node) {
				for (Expression parameter : node.getParameters()) {
					assertAllExpressionsHaveType(parameter);
				}
				return node;
			}

			@Override
			public Object visitNumber(NumberLiteral node) {
				return node;
			}

			@Override
			public Object visitBoolean(BooleanLiteral node) {
				return node;
			}

			@Override
			public Object visitVarRead(VarRead node) {
				return node;
			}

			@Override
			public Object visitMemRead(MemRead node) {
				return node;
			}

			@Override
			public Object visitTypeCast(TypeCast node) {
				assertAllExpressionsHaveType(node.expression);
				return node;
			}
		});
	}
}
