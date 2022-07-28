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
					return new Assignment(node.operation, node.name, expression, node.line, node.column);
				}

				@Override
				public Statement visitMemAssignment(MemAssignment node) {
					final Expression expression = handleExpression(node.expression);
					return new MemAssignment(node.name, expression, node.line, node.column);
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
				case add -> new NumberLiteral(left + right);
				case sub -> new NumberLiteral(left - right);
				case multiply -> new NumberLiteral(left * right);
				case divide -> new NumberLiteral(left / right);
				case modulo -> new NumberLiteral(left % right);
				case shiftL -> new NumberLiteral(left << right);
				case shiftR -> new NumberLiteral(left >> right);
				case bitAnd -> new NumberLiteral(left & right);
				case bitOr -> new NumberLiteral(left | right);
				case bitXor -> new NumberLiteral(left ^ right);
				case lessThan -> BooleanLiteral.get(left < right);
				case lessEqual -> BooleanLiteral.get(left <= right);
				case equal -> BooleanLiteral.get(left == right);
				case greaterEqual -> BooleanLiteral.get(left >= right);
				case greaterThan -> BooleanLiteral.get(left > right);
				case notEqual -> BooleanLiteral.get(left != right);
			};
		}

		if (node.left instanceof NumberLiteral) {
			final int left = ((NumberLiteral) node.left).value;
			if (node.operator == BinaryExpression.Op.add) {
				if (left == 0) {
					return node.right;
				}
				// put constant on right side
				return new BinaryExpression(node.right, node.operator, node.left);
			}
			if (node.operator == BinaryExpression.Op.multiply) {
				if (left == 1) {
					return node.right;
				}
				if (left == 0 && node.right instanceof VarRead) {
					return new NumberLiteral(0);
				}
				// put constant on right side
				return new BinaryExpression(node.right, node.operator, node.left);
			}
			if (node.operator == BinaryExpression.Op.bitAnd) {
				if (left == 0) {
					return new NumberLiteral(0);
				}
			}
		}

		if (node.right instanceof NumberLiteral) {
			final int right = ((NumberLiteral) node.right).value;
			//noinspection IfCanBeSwitch
			if (node.operator == BinaryExpression.Op.add
					|| node.operator == BinaryExpression.Op.sub) {
				if (right == 0) {
					return node.left;
				}
			}
			else if (node.operator == BinaryExpression.Op.multiply) {
				if (right == 1) {
					return node.left;
				}
				if (right == 0 && node.left instanceof VarRead) {
					return new NumberLiteral(0);
				}
			}
			else if (node.operator == BinaryExpression.Op.divide) {
				if (right == 1) {
					return node.left;
				}
			}
			else if (node.operator == BinaryExpression.Op.shiftL
					|| node.operator == BinaryExpression.Op.shiftR) {
				if (right == 0) {
					return node.left;
				}
			}
			else if (node.operator == BinaryExpression.Op.bitAnd) {
				if (right == 0) {
					return new NumberLiteral(0);
				}
			}
		}

		if (node.left instanceof BooleanLiteral
				&& node.right instanceof BooleanLiteral) {
			final boolean left = ((BooleanLiteral) node.left).value;
			final boolean right = ((BooleanLiteral) node.right).value;
			if (node.operator == BinaryExpression.Op.equal) {
				return BooleanLiteral.get(left == right);
			}
			if (node.operator == BinaryExpression.Op.notEqual) {
				return BooleanLiteral.get(left != right);
			}
		}

		if (node.left instanceof VarRead
				&& node.right instanceof VarRead) {
			final String left = ((VarRead) node.left).name;
			final String right = ((VarRead) node.right).name;

			if (Objects.equals(left, right)) {
				if (node.operator == BinaryExpression.Op.sub) {
					return new NumberLiteral(0);
				}
				if (node.operator == BinaryExpression.Op.bitAnd) {
					return node.left;
				}
				if (node.operator == BinaryExpression.Op.bitOr) {
					return node.left;
				}
				if (node.operator == BinaryExpression.Op.bitXor) {
					return new NumberLiteral(0);
				}
				if (node.operator == BinaryExpression.Op.lessThan) {
					return BooleanLiteral.FALSE;
				}
				if (node.operator == BinaryExpression.Op.lessEqual) {
					return BooleanLiteral.TRUE;
				}
				if (node.operator == BinaryExpression.Op.equal) {
					return BooleanLiteral.TRUE;
				}
				if (node.operator == BinaryExpression.Op.greaterEqual) {
					return BooleanLiteral.TRUE;
				}
				if (node.operator == BinaryExpression.Op.greaterThan) {
					return BooleanLiteral.FALSE;
				}
				if (node.operator == BinaryExpression.Op.notEqual) {
					return BooleanLiteral.FALSE;
				}
			}
		}
		return node;
	}
}
