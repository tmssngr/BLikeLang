package de.regnis.b;

import de.regnis.b.ast.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Thomas Singer
 */
public final class SplitExpressionsTransformation {

	// Static =================================================================

	public static DeclarationList transform(DeclarationList root) {
		final SplitExpressionsTransformation transformation = new SplitExpressionsTransformation();
		return transformation.handleDeclarationList(root);
	}

	// Fields =================================================================

	private int tempVarIndex;

	// Setup ==================================================================

	private SplitExpressionsTransformation() {
	}

	// Utils ==================================================================

	private DeclarationList handleDeclarationList(@NotNull DeclarationList declarationList) {
		final DeclarationList newDeclarationList = new DeclarationList();

		final TempVarFactory tempVarFactory = createTempVarFactory(newDeclarationList);
		for (Declaration declaration : declarationList.getDeclarations()) {
			final Declaration newDeclaration = declaration.visit(new DeclarationVisitor<>() {
				@Override
				public Declaration visitGlobalVarDeclaration(GlobalVarDeclaration node) {
					return new GlobalVarDeclaration(handleVarDeclaration(node.node, tempVarFactory));
				}

				@Override
				public Declaration visitFunctionDeclaration(FuncDeclaration node) {
					final StatementList newStatementList = handleStatementList(node.statementList);
					return new FuncDeclaration(node.type, node.name, node.parameters, newStatementList);
				}
			});
			newDeclarationList.add(newDeclaration);
		}
		return newDeclarationList;
	}

	private Statement handleAssignment(Assignment node, TempVarFactory tempVarFactory) {
		final Expression expression = handleExpression(node.expression, tempVarFactory);
		return new Assignment(node.var, expression, node.line, node.column);
	}

	private VarDeclaration handleVarDeclaration(VarDeclaration node, TempVarFactory tempVarFactory) {
		final Expression expression = handleExpression(node.expression, tempVarFactory);
		return node.derive(expression);
	}

	private Statement handleCall(CallStatement node, TempVarFactory tempVarFactory) {
		final FuncCallParameters parameters = handleFuncCallParameters(node.getParameters(), tempVarFactory);
		return new CallStatement(node.name, parameters);
	}

	private Statement handleReturn(ReturnStatement node, TempVarFactory tempVarFactory) {
		if (node.expression == null) {
			return node;
		}

		final Expression expression = handleExpression(node.expression, tempVarFactory);
		return new ReturnStatement(expression);
	}

	private Statement handleIfStatement(IfStatement node, TempVarFactory tempVarFactory) {
		final Expression expression = handleExpression(node.expression, tempVarFactory);
		final StatementList ifStatements = handleStatementList(node.ifStatements);
		final StatementList elseStatements = handleStatementList(node.elseStatements);
		return new IfStatement(expression, ifStatements, elseStatements);
	}

	private Statement handleWhileStatement(WhileStatement node, TempVarFactory tempVarFactory) {
		final Expression expression = handleExpression(node.expression, tempVarFactory);
		final StatementList statements = handleStatementList(node.statements);
		return new WhileStatement(expression, statements);
	}

	@NotNull
	private StatementList handleStatementList(@NotNull StatementList statementList) {
		final StatementList newStatementList = new StatementList();

		final TempVarFactory tempVarFactory = createTempVarFactory(newStatementList);
		for (Statement statement : statementList.getStatements()) {
			final Statement newStatement = statement.visit(new StatementVisitor<>() {
				@Override
				public Statement visitAssignment(Assignment node) {
					return handleAssignment(node, tempVarFactory);
				}

				@Override
				public Statement visitStatementList(StatementList node) {
					return handleStatementList(node);
				}

				@Override
				public Statement visitLocalVarDeclaration(VarDeclaration node) {
					return handleVarDeclaration(node, tempVarFactory);
				}

				@Override
				public Statement visitCall(CallStatement node) {
					return handleCall(node, tempVarFactory);
				}

				@Override
				public Statement visitReturn(ReturnStatement node) {
					return handleReturn(node, tempVarFactory);
				}

				@Override
				public Statement visitIf(IfStatement node) {
					return handleIfStatement(node, tempVarFactory);
				}

				@Override
				public Statement visitWhile(WhileStatement node) {
					return handleWhileStatement(node, tempVarFactory);
				}
			});
			newStatementList.add(newStatement);
		}
		return newStatementList;
	}

	private Expression handleExpression(Expression expression, TempVarFactory parameter) {
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

	private BinaryExpression handleBinary(BinaryExpression node, TempVarFactory tempVarFactory) {
		final Expression left = splitInnerExpression(node.left, tempVarFactory);
		final Expression right = splitInnerExpression(node.right, tempVarFactory);
		return node.createNew(left, right);
	}

	private FuncCall handleFunctionCall(FuncCall node, TempVarFactory tempVarFactory) {
		final FuncCallParameters parameters = handleFuncCallParameters(node.getParameters(), tempVarFactory);
		return new FuncCall(node.name, parameters, node.line, node.column);
	}

	@NotNull
	private FuncCallParameters handleFuncCallParameters(List<Expression> parameters, TempVarFactory tempVarFactory) {
		final FuncCallParameters newParameters = new FuncCallParameters();
		for (Expression parameter : parameters) {
			final Expression simplifiedParameter = splitInnerExpression(parameter, tempVarFactory);
			newParameters.add(simplifiedParameter);
		}
		return newParameters;
	}

	@NotNull
	private Expression splitInnerExpression(Expression expressionNode, TempVarFactory tempVarFactory) {
		return expressionNode.visit(new ExpressionVisitor<>() {
			@Override
			public Expression visitBinary(BinaryExpression node) {
				final BinaryExpression ben = handleBinary(node, tempVarFactory);
				if (node.hasType()) {
					ben.setType(node.getType());
				}
				return tempVarFactory.createTempVarDeclaration(ben);
			}

			@Override
			public Expression visitFunctionCall(FuncCall node) {
				final FuncCall fcn = handleFunctionCall(node, tempVarFactory);
				if (node.hasType()) {
					fcn.setType(node.getType());
				}
				return tempVarFactory.createTempVarDeclaration(fcn);
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

	private TempVarFactory createTempVarFactory(DeclarationList newDeclarationList) {
		return expression -> {
			final String tempVar = getNextTempVarName();
			newDeclarationList.add(new GlobalVarDeclaration(VarDeclaration.createTempVarDeclaration(tempVar, expression)));
			return new VarRead(tempVar, -1, -1);
		};
	}

	private TempVarFactory createTempVarFactory(StatementList newStatementList) {
		return expression -> {
			final String tempVar = getNextTempVarName();
			newStatementList.add(VarDeclaration.createTempVarDeclaration(tempVar, expression));
			return new VarRead(tempVar, -1, -1);
		};
	}

	@NotNull
	private String getNextTempVarName() {
		tempVarIndex++;
		return "$" + tempVarIndex;
	}

	// Inner Classes ==========================================================

	private interface TempVarFactory {
		Expression createTempVarDeclaration(Expression expression);
	}
}
