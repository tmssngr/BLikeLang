package de.regnis.b;

import de.regnis.b.ast.*;
import de.regnis.b.type.BasicTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class SplitExpressionsTransformation {

	// Constants ==============================================================

	private static final String FUNCTION_INIT_GLOBALS = "__init_globals__";

	// Static =================================================================

	public static DeclarationList transform(DeclarationList root) {
		final SplitExpressionsTransformation transformation = new SplitExpressionsTransformation();
		root = transformation.moveGlobalVarInitializationToInit(root);
		return transformation.splitExpressions(root);
	}

	// Fields =================================================================


	private int tempVarIndex;

	// Setup ==================================================================

	private SplitExpressionsTransformation() {
	}

	// Utils ==================================================================

	private DeclarationList moveGlobalVarInitializationToInit(@NotNull DeclarationList declarationList) {
		final List<Assignment> initializationAssignments = new ArrayList<>();

		final DeclarationVisitor<Declaration> visitor = new DeclarationVisitor<>() {
			@Override
			public Declaration visitGlobalVarDeclaration(GlobalVarDeclaration node) {
				if (node.node.expression instanceof NumberLiteral) {
					return node;
				}

				initializationAssignments.add(new Assignment(Assignment.Op.assign, node.node.name, node.node.expression));
				return new GlobalVarDeclaration(new VarDeclaration(node.node.name, new NumberLiteral(0)));
			}

			@Override
			public Declaration visitFunctionDeclaration(FuncDeclaration node) {
				return node;
			}
		};

		final DeclarationList newDeclarationList = processDeclarations(declarationList, visitor);

		if (initializationAssignments.size() > 0) {
			addInitializationFunction(initializationAssignments, newDeclarationList);
		}
		return newDeclarationList;
	}

	@NotNull
	private DeclarationList processDeclarations(@NotNull DeclarationList declarationList, DeclarationVisitor<Declaration> visitor) {
		final DeclarationList newDeclarationList = new DeclarationList();
		for (Declaration declaration : declarationList.getDeclarations()) {
			final Declaration newDeclaration = declaration.visit(visitor);
			newDeclarationList.add(newDeclaration);
		}
		return newDeclarationList;
	}

	private void addInitializationFunction(List<Assignment> initializationAssignments, DeclarationList newDeclarationList) {
		if (newDeclarationList.getFunction(FUNCTION_INIT_GLOBALS) != null) {
			throw new TransformationFailedException("Function " + FUNCTION_INIT_GLOBALS + " must not exist.");
		}

		final StatementList statementList = new StatementList();
		for (Assignment assignment : initializationAssignments) {
			statementList.add(assignment);
		}
		newDeclarationList.add(new FuncDeclaration(BasicTypes.VOID, FUNCTION_INIT_GLOBALS, new FuncDeclarationParameters(), statementList));
	}

	private DeclarationList splitExpressions(@NotNull DeclarationList declarationList) {
		final DeclarationVisitor<Declaration> visitor = new DeclarationVisitor<>() {
			@Override
			public Declaration visitGlobalVarDeclaration(GlobalVarDeclaration node) {
				return node;
			}

			@Override
			public Declaration visitFunctionDeclaration(FuncDeclaration node) {
				final StatementList newStatementList = handleStatementList(node.statementList);
				return new FuncDeclaration(node.type, node.name, node.parameters, newStatementList);
			}
		};

		return processDeclarations(declarationList, visitor);
	}

	private Statement handleAssignment(Assignment node, TempVarFactory tempVarFactory) {
		final Expression expression = handleExpression(node.expression, tempVarFactory);
		return new Assignment(node.operation, node.name, expression, node.line, node.column);
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
		final StatementList ifStatements = handleStatementList(node.trueStatements);
		final StatementList elseStatements = handleStatementList(node.falseStatements);
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

				@Override
				public Statement visitBreak(BreakStatement node) {
					return node;
				}
			});
			newStatementList.add(newStatement);
		}
		return newStatementList;
	}

	private Expression handleExpression(Expression expression, TempVarFactory tempVarFactory) {
		return expression.visit(new ExpressionVisitor<>() {
			@Override
			public Expression visitBinary(BinaryExpression node) {
				return handleBinary(node, tempVarFactory);
			}

			@Override
			public Expression visitFunctionCall(FuncCall node) {
				return handleFunctionCall(node, tempVarFactory);
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

	private BinaryExpression handleBinary(BinaryExpression node, TempVarFactory tempVarFactory) {
		final Expression left = splitInnerExpression(node.left, tempVarFactory);
		final Expression right = splitInnerExpression(node.right, tempVarFactory);
		return new BinaryExpression(left, node.operator, right);
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
				final BinaryExpression tempExpression = handleBinary(node, tempVarFactory);
				return tempVarFactory.createTempVarDeclaration(tempExpression);
			}

			@Override
			public Expression visitFunctionCall(FuncCall node) {
				final FuncCall fcn = handleFunctionCall(node, tempVarFactory);
				return tempVarFactory.createTempVarDeclaration(fcn);
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

	private TempVarFactory createTempVarFactory(StatementList newStatementList) {
		return expression -> {
			final String tempVar = getNextTempVarName();
			newStatementList.add(VarDeclaration.createTempVarDeclaration(tempVar, expression));
			return new VarRead(tempVar);
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
