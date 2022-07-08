package de.regnis.b;

import de.regnis.b.ast.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * @author Thomas Singer
 */
public final class SplitExpressionsTransformation extends AbstractTransformation<Function<Expression, Expression>> {

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
	protected Function<Expression, Expression> createHelper(DeclarationList newDeclarationList) {
		return expression -> {
			final String tempVar = getNextTempVarName();
			newDeclarationList.add(new GlobalVarDeclaration(VarDeclaration.createTempVarDeclaration(tempVar, expression)));
			return new VarRead(tempVar, -1, -1);
		};
	}

	@Override
	protected Function<Expression, Expression> createHelper(StatementList newStatementList) {
		return expression -> {
			final String tempVar = getNextTempVarName();
			newStatementList.add(VarDeclaration.createTempVarDeclaration(tempVar, expression));
			return new VarRead(tempVar, -1, -1);
		};
	}

	@Override
	protected BinaryExpression handleBinary(BinaryExpression node, Function<Expression, Expression> tempVarFactory) {
		final Expression left = splitInnerExpression(node.left, tempVarFactory);
		final Expression right = splitInnerExpression(node.right, tempVarFactory);
		return node.createNew(left, right);
	}

	@Override
	protected FuncCall handleFunctionCall(FuncCall node, Function<Expression, Expression> tempVarFactory) {
		final FuncCallParameters parameters = new FuncCallParameters();
		for (Expression parameter : node.getParameters()) {
			final Expression simplifiedParameter = splitInnerExpression(parameter, tempVarFactory);
			parameters.add(simplifiedParameter);
		}
		return new FuncCall(node.name, parameters, node.line, node.column);
	}

	@NotNull
	private Expression splitInnerExpression(Expression expressionNode, Function<Expression, Expression> tempVarFactory) {
		return expressionNode.visit(new ExpressionVisitor<>() {
			@Override
			public Expression visitBinary(BinaryExpression node) {
				final BinaryExpression ben = handleBinary(node, tempVarFactory);
				return tempVarFactory.apply(ben);
			}

			@Override
			public Expression visitFunctionCall(FuncCall node) {
				final FuncCall fcn = handleFunctionCall(node, tempVarFactory);
				return tempVarFactory.apply(fcn);
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

	@NotNull
	private String getNextTempVarName() {
		tempVarIndex++;
		return "$" + tempVarIndex;
	}
}
