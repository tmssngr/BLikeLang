package de.regnis.b;

import de.regnis.b.ast.*;
import de.regnis.b.type.BasicTypes;
import de.regnis.b.type.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class ReplaceBinaryExpressionsWithModifyAssignmentsTransformation {

	// Constants ==============================================================

	private static final String FUNCTION_INIT_GLOBALS = "__init_globals__";

	// Static =================================================================

	public static DeclarationList transform(DeclarationList root) {
		final ReplaceBinaryExpressionsWithModifyAssignmentsTransformation transformation = new ReplaceBinaryExpressionsWithModifyAssignmentsTransformation();
		root = transformation.moveGlobalVarInitializationToInit(root);
		return transformation.splitExpressions(root);
	}

	// Fields =================================================================


	private int tempVarIndex;

	// Setup ==================================================================

	private ReplaceBinaryExpressionsWithModifyAssignmentsTransformation() {
	}

	// Utils ==================================================================

	private DeclarationList moveGlobalVarInitializationToInit(@NotNull DeclarationList declarationList) {
		final List<Assignment> initializationAssignments = new ArrayList<>();

		final DeclarationVisitor<Declaration> visitor = new DeclarationVisitor<>() {
			@Override
			public Declaration visitGlobalVarDeclaration(GlobalVarDeclaration node) {
				if (node.node.expression instanceof BooleanLiteral
						|| node.node.expression instanceof NumberLiteral) {
					return node;
				}

				initializationAssignments.add(new Assignment(Assignment.Op.assign, node.node.name, node.node.expression));
				return new GlobalVarDeclaration(new VarDeclaration(node.node.typeName, node.node.name, node.node.type == BasicTypes.BOOLEAN ? BooleanLiteral.FALSE : new NumberLiteral(0), -1, -1));
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

	@Nullable
	private Assignment.Op toAssignmentOperator(BinaryExpression binEx) {
		return switch (binEx.operator) {
			case add -> Assignment.Op.add;
			case sub -> Assignment.Op.sub;
			case multiply -> Assignment.Op.multiply;
			case divide -> Assignment.Op.divide;
			case modulo -> Assignment.Op.modulo;
			case shiftL -> Assignment.Op.shiftL;
			case shiftR -> Assignment.Op.shiftR;
			case bitAnd -> Assignment.Op.bitAnd;
			case bitOr -> Assignment.Op.bitOr;
			case bitXor -> Assignment.Op.bitXor;
			default -> null;
		};
	}

	private Expression splitExpression(Expression expression, boolean assignToTempVar, TempVarFactory tempVarFactory) {
		return expression.visit(new ExpressionVisitor<>() {
			@Override
			public Expression visitBinary(BinaryExpression node) {
				final Expression left = splitExpression(node.left, true, tempVarFactory);
				final Expression right = splitExpression(node.right, true, tempVarFactory);
				final Assignment.Op op = toAssignmentOperator(node);
				if (op == null) {
					final BinaryExpression expression = new BinaryExpression(left, node.operator, right);
					copyType(node, expression);
					if (assignToTempVar) {
						final String tempVar = tempVarFactory.createTempVarDeclaration(expression);
						return createVarRead(tempVar, expression);
					}

					return expression;
				}

				final String tempVar = left != node.left && left instanceof VarRead leftTempVar
						? leftTempVar.name
						: tempVarFactory.createTempVarDeclaration(left);
				tempVarFactory.createAssignment(op, tempVar, right, left.getTypeNullable());
				return createVarRead(tempVar, left);
			}

			@Override
			public Expression visitFunctionCall(FuncCall node) {
				final FuncCall fcn = handleFunctionCall(node, tempVarFactory);
				copyType(node, fcn);
				if (assignToTempVar) {
					final String tempVar = tempVarFactory.createTempVarDeclaration(fcn);
					return createVarRead(tempVar, fcn);
				}
				return fcn;
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
				final TypeCast expression = handleTypeCast(node, tempVarFactory);
				if (assignToTempVar) {
					final String tempVar = tempVarFactory.createTempVarDeclaration(expression);
					return createVarRead(tempVar, expression);
				}
				return expression;
			}
		});
	}

	private Statement handleAssignment(Assignment node, TempVarFactory tempVarFactory) {
		if (node.operation == Assignment.Op.assign && node.expression instanceof BinaryExpression binEx) {
			final Assignment.Op op = toAssignmentOperator(binEx);
			if (op != null) {
				final Expression left = splitExpression(binEx.left, true, tempVarFactory);
				final Expression right = splitExpression(binEx.right, true, tempVarFactory);
				String tempVar = null;
				if (left instanceof VarRead leftVar) {
					if (leftVar.name.equals(node.name)) {
						return createAssignment(op, node.name, node.type, right);
					}

					if (left != binEx.left) {
						tempVar = leftVar.name;
					}
				}
				if (tempVar == null) {
					tempVar = tempVarFactory.createTempVarDeclaration(left);
				}
				tempVarFactory.createAssignment(op, tempVar, right, left.getTypeNullable());
				return createAssignment(Assignment.Op.assign, node.name, node.type, createVarRead(tempVar, left));
			}

			// boolean
			final Expression expression = splitExpression(node.expression, true, tempVarFactory);
			return createAssignment(node.operation, node.name, node.type, expression);
		}

		final Expression expression = splitExpression(node.expression, false, tempVarFactory);
		return createAssignment(node.operation, node.name, node.type, expression);
	}

	@NotNull
	private Assignment createAssignment(Assignment.Op operation, String name, @Nullable Type type, Expression expression) {
		if (type != null) {
			return new Assignment(operation, name, expression, type);
		}
		return new Assignment(operation, name, expression);
	}

	private VarDeclaration handleVarDeclaration(VarDeclaration node, TempVarFactory tempVarFactory) {
		final Expression expression = splitExpression(node.expression, false, tempVarFactory);
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

		final Expression expression = splitExpression(node.expression, false, tempVarFactory);
		return new ReturnStatement(expression);
	}

	private Statement handleIfStatement(IfStatement node, TempVarFactory tempVarFactory) {
		final Expression expression = splitExpression(node.expression, false, tempVarFactory);
		final StatementList ifStatements = handleStatementList(node.trueStatements);
		final StatementList elseStatements = handleStatementList(node.falseStatements);
		return new IfStatement(expression, ifStatements, elseStatements);
	}

	private Statement handleWhileStatement(WhileStatement node, TempVarFactory tempVarFactory) {
		final Expression expression = splitExpression(node.expression, false, tempVarFactory);
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

	private FuncCall handleFunctionCall(FuncCall node, TempVarFactory tempVarFactory) {
		final FuncCallParameters parameters = handleFuncCallParameters(node.getParameters(), tempVarFactory);
		final FuncCall funcCall = new FuncCall(node.name, parameters, node.line, node.column);
		copyType(node, funcCall);
		return funcCall;
	}

	private void copyType(Expression from, Expression to) {
		if (from.hasType()) {
			to.setType(from.getType());
		}
	}

	@NotNull
	private FuncCallParameters handleFuncCallParameters(List<Expression> parameters, TempVarFactory tempVarFactory) {
		final FuncCallParameters newParameters = new FuncCallParameters();
		for (Expression parameter : parameters) {
			final Expression simplifiedParameter = splitExpression(parameter, true, tempVarFactory);
			newParameters.add(simplifiedParameter);
		}
		return newParameters;
	}

	@NotNull
	private TypeCast handleTypeCast(TypeCast node, TempVarFactory tempVarFactory) {
		final Expression simplifiedExpression = splitExpression(node.expression, true, tempVarFactory);
		final TypeCast typeCast = new TypeCast(node.typeName, simplifiedExpression);
		copyType(node, typeCast);
		return typeCast;
	}

	private TempVarFactory createTempVarFactory(StatementList statementList) {
		return new TempVarFactory() {
			@Override
			public String createTempVarDeclaration(Expression expression) {
				final String tempVar = getNextTempVarName();
				statementList.add(VarDeclaration.createTempVarDeclaration(tempVar, expression));
				return tempVar;
			}

			@Override
			public void createAssignment(Assignment.Op op, String var, Expression expression, @Nullable Type type) {
				final Assignment assignment = ReplaceBinaryExpressionsWithModifyAssignmentsTransformation.this.createAssignment(op, var, type, expression);
				statementList.add(assignment);
			}
		};
	}

	@NotNull
	private VarRead createVarRead(String tempVar, Expression expression) {
		final VarRead varRead = new VarRead(tempVar, -1, -1);
		copyType(expression, varRead);
		return varRead;
	}

	@NotNull
	private String getNextTempVarName() {
		tempVarIndex++;
		return "$" + tempVarIndex;
	}

	// Inner Classes ==========================================================

	private interface TempVarFactory {
		String createTempVarDeclaration(Expression expression);

		void createAssignment(Assignment.Op op, String var, Expression expression, @Nullable Type type);
	}
}
