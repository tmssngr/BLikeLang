package de.regnis.b;

import de.regnis.b.ast.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Thomas Singer
 */
public final class ConstantFoldingTransformation {

	// Static =================================================================

	public static DeclarationList transform(DeclarationList root) {
		final ConstantFoldingTransformation transformation = new ConstantFoldingTransformation();
		return transformation.handleDeclarationList(root);
	}

	// Setup ==================================================================

	private ConstantFoldingTransformation() {
	}

	// Utils ==================================================================

	private DeclarationList handleDeclarationList(@NotNull DeclarationList declarationList) {
		final DeclarationList newDeclarationList = new DeclarationList();

		for (Declaration declaration : declarationList.getDeclarations()) {
			final Declaration newDeclaration = declaration.visit(new DeclarationVisitor<>() {
				@Override
				public Declaration visitGlobalVarDeclaration(GlobalVarDeclaration node) {
					final Expression expression = handleExpression(node.node.expression);
					return new GlobalVarDeclaration(node.node.derive(expression));
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

	@NotNull
	private StatementList handleStatementList(@NotNull StatementList statementList) {
		final StatementList newStatementList = new StatementList();

		for (Statement statement : statementList.getStatements()) {
			final Statement newStatement = statement.visit(new StatementVisitor<>() {
				@Override
				public Statement visitAssignment(Assignment node) {
					final Expression expression = handleExpression(node.expression);
					return new Assignment(node.name, expression, node.line, node.column);
				}

				@Override
				public Statement visitStatementList(StatementList node) {
					return handleStatementList(node);
				}

				@Override
				public Statement visitLocalVarDeclaration(VarDeclaration node) {
					final Expression expression = handleExpression(node.expression);
					return node.derive(expression);
				}

				@Override
				public Statement visitCall(CallStatement node) {
					return node;
				}

				@Override
				public Statement visitReturn(ReturnStatement node) {
					return node;
				}

				@Override
				public Statement visitIf(IfStatement node) {
					return node;
				}

				@Override
				public Statement visitWhile(WhileStatement node) {
					return node;
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

	private Expression handleExpression(Expression expression) {
		return expression.visit(new ExpressionVisitor<>() {
			@Override
			public Expression visitBinary(BinaryExpression node) {
				return handleBinary(node);
			}

			@Override
			public Expression visitFunctionCall(FuncCall node) {
				return node;
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
			public Expression visitMemRead(MemRead node) {
				return node;
			}

			@Override
			public Expression visitTypeCast(TypeCast node) {
				return node;
			}
		});
	}

	private Expression handleBinary(BinaryExpression node) {
		if (node.left instanceof NumberLiteral
				&& node.right instanceof NumberLiteral) {
			final int left = ((NumberLiteral) node.left).value;
			final int right = ((NumberLiteral) node.right).value;
			return switch (node.operator) {
				case BinaryExpression.PLUS -> new NumberLiteral(left + right);
				case BinaryExpression.MINUS -> new NumberLiteral(left - right);
				case BinaryExpression.MULTIPLY -> new NumberLiteral(left * right);
				case BinaryExpression.SHIFT_L -> new NumberLiteral(left << right);
				case BinaryExpression.SHIFT_R -> new NumberLiteral(left >> right);
				case BinaryExpression.AND -> new NumberLiteral(left & right);
				case BinaryExpression.OR -> new NumberLiteral(left | right);
				case BinaryExpression.XOR -> new NumberLiteral(left ^ right);
				case BinaryExpression.LT -> BooleanLiteral.get(left < right);
				case BinaryExpression.LE -> BooleanLiteral.get(left <= right);
				case BinaryExpression.EQ -> BooleanLiteral.get(left == right);
				case BinaryExpression.GE -> BooleanLiteral.get(left >= right);
				case BinaryExpression.GT -> BooleanLiteral.get(left > right);
				case BinaryExpression.NE -> BooleanLiteral.get(left != right);
				default -> node;
			};
		}

		if (node.left instanceof NumberLiteral) {
			final int left = ((NumberLiteral) node.left).value;
			if (node.operator.equals(BinaryExpression.PLUS)) {
				if (left == 0) {
					return node.right;
				}
				// put constant on right side
				return node.createNew(node.right, node.left);
			}
			if (node.operator.equals(BinaryExpression.MULTIPLY)) {
				if (left == 1) {
					return node.right;
				}
				if (left == 0 && node.right instanceof VarRead) {
					return new NumberLiteral(0);
				}
				// put constant on right side
				return node.createNew(node.right, node.left);
			}
			if (node.operator.equals(BinaryExpression.AND)) {
				if (left == 0) {
					return new NumberLiteral(0);
				}
			}
		}

		if (node.right instanceof NumberLiteral) {
			final int right = ((NumberLiteral) node.right).value;
			//noinspection IfCanBeSwitch
			if (node.operator.equals(BinaryExpression.PLUS)
					|| node.operator.equals(BinaryExpression.MINUS)) {
				if (right == 0) {
					return node.left;
				}
			}
			else if (node.operator.equals(BinaryExpression.MULTIPLY)) {
				if (right == 1) {
					return node.left;
				}
				if (right == 0 && node.left instanceof VarRead) {
					return new NumberLiteral(0);
				}
			}
			else if (node.operator.equals(BinaryExpression.SHIFT_L)
					|| node.operator.equals(BinaryExpression.SHIFT_R)) {
				if (right == 0) {
					return node.left;
				}
			}
			else if (node.operator.equals(BinaryExpression.AND)) {
				if (right == 0) {
					return new NumberLiteral(0);
				}
			}
		}

		if (node.left instanceof BooleanLiteral
				&& node.right instanceof BooleanLiteral) {
			final boolean left = ((BooleanLiteral) node.left).value;
			final boolean right = ((BooleanLiteral) node.right).value;
			if (node.operator.equals(BinaryExpression.EQ)) {
				return BooleanLiteral.get(left == right);
			}
			if (node.operator.equals(BinaryExpression.NE)) {
				return BooleanLiteral.get(left != right);
			}
		}

		if (node.left instanceof VarRead
				&& node.right instanceof VarRead) {
			final String left = ((VarRead) node.left).name;
			final String right = ((VarRead) node.right).name;

			if (Objects.equals(left, right)) {
				if (node.operator.equals(BinaryExpression.MINUS)) {
					return new NumberLiteral(0);
				}
				if (node.operator.equals(BinaryExpression.AND)) {
					return node.left;
				}
				if (node.operator.equals(BinaryExpression.OR)) {
					return node.left;
				}
				if (node.operator.equals(BinaryExpression.XOR)) {
					return new NumberLiteral(0);
				}
				if (node.operator.equals(BinaryExpression.LT)) {
					return BooleanLiteral.FALSE;
				}
				if (node.operator.equals(BinaryExpression.LE)) {
					return BooleanLiteral.TRUE;
				}
				if (node.operator.equals(BinaryExpression.EQ)) {
					return BooleanLiteral.TRUE;
				}
				if (node.operator.equals(BinaryExpression.GE)) {
					return BooleanLiteral.TRUE;
				}
				if (node.operator.equals(BinaryExpression.GT)) {
					return BooleanLiteral.FALSE;
				}
				if (node.operator.equals(BinaryExpression.NE)) {
					return BooleanLiteral.FALSE;
				}
			}
		}
		return node;
	}
}
