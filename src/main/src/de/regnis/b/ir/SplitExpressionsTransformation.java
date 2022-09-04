package de.regnis.b.ir;

import de.regnis.b.ast.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class SplitExpressionsTransformation implements BlockVisitor {

	// Static =================================================================

	public static void transform(@NotNull ControlFlowGraph graph) {
		graph.iterate(new SplitExpressionsTransformation());
	}

	// Fields =================================================================

	private int tempVarIndex;

	// Setup ==================================================================

	private SplitExpressionsTransformation() {
	}

	// Implemented ============================================================

	@Override
	public void visitBasic(BasicBlock block) {
		final List<SimpleStatement> newStatements = new ArrayList<>();
		final TempVarFactory tempVarFactory = createTempVarFactory(newStatements);
		handleStatements(block, newStatements, tempVarFactory);
		block.set(newStatements);
	}

	@Override
	public void visitIf(IfBlock block) {
		final List<SimpleStatement> newStatements = new ArrayList<>();
		final TempVarFactory tempVarFactory = createTempVarFactory(newStatements);
		handleStatements(block, newStatements, tempVarFactory);
		final Expression expression = handleExpression(block.getExpression(), tempVarFactory);
		block.setExpression(expression);
		block.set(newStatements);
	}

	@Override
	public void visitWhile(WhileBlock block) {
		final List<SimpleStatement> newStatements = new ArrayList<>();
		final TempVarFactory tempVarFactory = createTempVarFactory(newStatements);
		handleStatements(block, newStatements, tempVarFactory);
		final Expression expression = handleExpression(block.getExpression(), tempVarFactory);
		block.setExpression(expression);
		block.set(newStatements);
	}

	@Override
	public void visitExit(ExitBlock block) {
	}

	// Utils ==================================================================

	private void handleStatements(StatementsBlock block, List<SimpleStatement> newStatements, TempVarFactory tempVarFactory) {
		for (SimpleStatement statement : block.getStatements()) {
			newStatements.add(statement.visit(new SimpleStatementVisitor<>() {
				@Override
				public SimpleStatement visitAssignment(Assignment node) {
					return handleAssignment(node, tempVarFactory);
				}

				@Override
				public SimpleStatement visitLocalVarDeclaration(VarDeclaration node) {
					return handleVarDeclaration(node, tempVarFactory);
				}

				@Override
				public SimpleStatement visitCall(CallStatement node) {
					return handleCall(node, tempVarFactory);
				}
			}));
		}
	}

	private SimpleStatement handleAssignment(Assignment node, TempVarFactory tempVarFactory) {
		final Expression expression = handleExpression(node.expression(), tempVarFactory);
		return new Assignment(node.operation(), node.name(), expression, node.position());
	}

	private VarDeclaration handleVarDeclaration(VarDeclaration node, TempVarFactory tempVarFactory) {
		final Expression expression = handleExpression(node.expression(), tempVarFactory);
		return node.derive(expression);
	}

	private SimpleStatement handleCall(CallStatement node, TempVarFactory tempVarFactory) {
		final FuncCallParameters parameters = node.parameters().transform(expression ->
				                                                                  splitInnerExpression(expression, tempVarFactory));
		return new CallStatement(node.name(), parameters);
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
		final Expression left = splitInnerExpression(node.left(), tempVarFactory);
		final Expression right = splitInnerExpression(node.right(), tempVarFactory);
		return new BinaryExpression(left, node.operator(), right);
	}

	private FuncCall handleFunctionCall(FuncCall node, TempVarFactory tempVarFactory) {
		final FuncCallParameters parameters = node.parameters().transform(expression ->
				                                                                  splitInnerExpression(expression, tempVarFactory));
		return new FuncCall(node.name(), parameters, node.position());
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

	private TempVarFactory createTempVarFactory(List<SimpleStatement> newStatementList) {
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
