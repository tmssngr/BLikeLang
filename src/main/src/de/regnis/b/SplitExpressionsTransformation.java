package de.regnis.b;

import de.regnis.b.node.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class SplitExpressionsTransformation extends AbstractTransformation {

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

	@Override
	protected BinaryExpression handleBinary(BinaryExpression node, StatementList newStatementList) {
		final Expression left = splitInnerExpression(node.left, newStatementList);
		final Expression right = splitInnerExpression(node.right, newStatementList);
		return node.createNew(left, right);
	}

	@Override
	protected FuncCall handleFunctionCall(FuncCall node, StatementList newStatementList) {
		final FuncCallParameters parameters = new FuncCallParameters();
		for (Expression parameter : node.getParameters()) {
			final Expression simplifiedParameter = splitInnerExpression(parameter, newStatementList);
			parameters.add(simplifiedParameter);
		}
		return new FuncCall(node.name, parameters, node.line, node.column);
	}

	@NotNull
	private Expression splitInnerExpression(Expression expressionNode, StatementList list) {
		return expressionNode.visit(new ExpressionVisitor<>() {
			@Override
			public Expression visitBinary(BinaryExpression node) {
				final BinaryExpression ben = handleBinary(node, list);
				return createTempVar(ben, list);
			}

			@Override
			public Expression visitFunctionCall(FuncCall node) {
				final FuncCall fcn = handleFunctionCall(node, list);
				return createTempVar(fcn, list);
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

	@NotNull
	private VarRead createTempVar(Expression node, StatementList list) {
		final String tempVar = getNextTempVarName();
		list.add(new VarDeclaration(tempVar, node, -1, -1));
		return new VarRead(tempVar, -1, -1);
	}

	@NotNull
	private String getNextTempVarName() {
		tempVarIndex++;
		return "$" + tempVarIndex;
	}
}
