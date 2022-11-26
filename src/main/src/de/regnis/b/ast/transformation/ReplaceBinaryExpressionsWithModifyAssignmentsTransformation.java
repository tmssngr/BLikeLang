package de.regnis.b.ast.transformation;

import de.regnis.b.ast.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Thomas Singer
 */
public final class ReplaceBinaryExpressionsWithModifyAssignmentsTransformation {

	// Static =================================================================

	public static DeclarationList transform(DeclarationList root) {
		final ReplaceBinaryExpressionsWithModifyAssignmentsTransformation transformation = new ReplaceBinaryExpressionsWithModifyAssignmentsTransformation();
		return transformation.splitExpressions(root);
	}

	// Fields =================================================================

	private int tempVarIndex;

	// Setup ==================================================================

	private ReplaceBinaryExpressionsWithModifyAssignmentsTransformation() {
	}

	// Utils ==================================================================

	private DeclarationList splitExpressions(@NotNull DeclarationList declarationList) {
		final DeclarationVisitor<Declaration> visitor = new DeclarationVisitor<>() {
			@Override
			public Declaration visitFunctionDeclaration(FuncDeclaration node) {
				final StatementList newStatementList = handleStatementList(node.statementList());
				return new FuncDeclaration(node.type(), node.name(), node.parameters(), newStatementList);
			}
		};
		return declarationList.transform(declaration -> declaration.visit(visitor));
	}

	@Nullable
	private Assignment.Op toAssignmentOperator(BinaryExpression binEx) {
		return switch (binEx.operator()) {
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
				final Expression left = splitExpression(node.left(), true, tempVarFactory);
				final Expression right = splitExpression(node.right(), true, tempVarFactory);
				final Assignment.Op op = toAssignmentOperator(node);
				if (op == null) {
					final BinaryExpression expression = new BinaryExpression(left, node.operator(), right);
					if (assignToTempVar) {
						final String tempVar = tempVarFactory.createTempVarDeclaration(expression);
						return new VarRead(tempVar);
					}

					return expression;
				}

				final String tempVar = left != node.left() && left instanceof VarRead leftTempVar
						? leftTempVar.name()
						: tempVarFactory.createTempVarDeclaration(left);
				tempVarFactory.createAssignment(op, tempVar, right);
				return new VarRead(tempVar);
			}

			@Override
			public Expression visitFunctionCall(FuncCall node) {
				final FuncCall fcn = handleFunctionCall(node, tempVarFactory);
				if (assignToTempVar) {
					final String tempVar = tempVarFactory.createTempVarDeclaration(fcn);
					return new VarRead(tempVar);
				}
				return fcn;
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

	private Statement handleAssignment(Assignment node, TempVarFactory tempVarFactory) {
		if (node.operation() == Assignment.Op.assign && node.expression() instanceof BinaryExpression binEx) {
			final Assignment.Op op = toAssignmentOperator(binEx);
			if (op != null) {
				final Expression left = splitExpression(binEx.left(), true, tempVarFactory);
				final Expression right = splitExpression(binEx.right(), true, tempVarFactory);
				String tempVar = null;
				if (left instanceof VarRead leftVar) {
					if (leftVar.name().equals(node.name())) {
						return createAssignment(op, node.name(), right);
					}

					if (left != binEx.left()) {
						tempVar = leftVar.name();
					}
				}
				if (tempVar == null) {
					tempVar = tempVarFactory.createTempVarDeclaration(left);
				}
				tempVarFactory.createAssignment(op, tempVar, right);
				return createAssignment(Assignment.Op.assign, node.name(), new VarRead(tempVar));
			}

			// boolean
			final Expression expression = splitExpression(node.expression(), true, tempVarFactory);
			return createAssignment(node.operation(), node.name(), expression);
		}

		final Expression expression = splitExpression(node.expression(), false, tempVarFactory);
		return createAssignment(node.operation(), node.name(), expression);
	}

	@NotNull
	private Assignment createAssignment(Assignment.Op operation, String name, Expression expression) {
		return new Assignment(operation, name, expression);
	}

	private VarDeclaration handleVarDeclaration(VarDeclaration node, TempVarFactory tempVarFactory) {
		final Expression expression = splitExpression(node.expression(), false, tempVarFactory);
		return node.derive(expression);
	}

	private Statement handleCall(CallStatement node, TempVarFactory tempVarFactory) {
		final FuncCallParameters parameters = node.parameters().transform(expression ->
				                                                                  splitExpression(expression, true, tempVarFactory));
		return new CallStatement(node.name(), parameters);
	}

	private Statement handleReturn(ReturnStatement node, TempVarFactory tempVarFactory) {
		if (node.expression() == null) {
			return node;
		}

		final Expression expression = splitExpression(node.expression(), false, tempVarFactory);
		return new ReturnStatement(expression);
	}

	private Statement handleIfStatement(IfStatement node, TempVarFactory tempVarFactory) {
		final Expression expression = splitExpression(node.expression(), false, tempVarFactory);
		final StatementList ifStatements = handleStatementList(node.trueStatements());
		final StatementList elseStatements = handleStatementList(node.falseStatements());
		return new IfStatement(expression, ifStatements, elseStatements);
	}

	private Statement handleWhileStatement(WhileStatement node, TempVarFactory tempVarFactory) {
		final Expression expression = splitExpression(node.expression(), false, tempVarFactory);
		final StatementList statements = handleStatementList(node.statements());
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
		final FuncCallParameters parameters = node.parameters().transform(expression ->
				                                                                  splitExpression(expression, true, tempVarFactory));
		return new FuncCall(node.name(), parameters, node.position());
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
			public void createAssignment(Assignment.Op op, String var, Expression expression) {
				final Assignment assignment = ReplaceBinaryExpressionsWithModifyAssignmentsTransformation.this.createAssignment(op, var, expression);
				statementList.add(assignment);
			}
		};
	}

	@NotNull
	private String getNextTempVarName() {
		tempVarIndex++;
		return "$" + tempVarIndex;
	}

	// Inner Classes ==========================================================

	private interface TempVarFactory {
		String createTempVarDeclaration(Expression expression);

		void createAssignment(Assignment.Op op, String var, Expression expression);
	}
}
