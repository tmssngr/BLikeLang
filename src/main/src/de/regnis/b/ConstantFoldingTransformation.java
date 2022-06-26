package de.regnis.b;

import de.regnis.b.node.*;

/**
 * @author Thomas Singer
 */
public final class ConstantFoldingTransformation extends AbstractTransformation {

	// Static =================================================================

	public static DeclarationList transform(DeclarationList root) {
		final ConstantFoldingTransformation transformation = new ConstantFoldingTransformation();
		return transformation.handleDeclarationList(root);
	}

	// Setup ==================================================================

	private ConstantFoldingTransformation() {
	}

	// Utils ==================================================================

	@Override
	protected Expression handleBinary(BinaryExpression node, StatementList newStatementList) {
		if (node.left instanceof NumberLiteral
				&& node.right instanceof NumberLiteral) {
			final int left = ((NumberLiteral) node.left).value;
			final int right = ((NumberLiteral) node.right).value;
			final int value = switch (node.operator) {
				case BinaryExpression.PLUS -> left + right;
				case BinaryExpression.MINUS -> left - right;
				case BinaryExpression.MULTIPLY -> left * right;
				default -> throw new UnsupportedOperationException();
			};
			return new NumberLiteral(value);
		}

		if (node.left instanceof NumberLiteral) {
			final int left = ((NumberLiteral) node.left).value;
			if (node.operator.equals(BinaryExpression.PLUS)) {
				if (left == 0) {
					return node.right;
				}
			}
			else if (node.operator.equals(BinaryExpression.MULTIPLY)) {
				if (left == 1) {
					return node.right;
				}
				if (left == 0 && node.right instanceof VarRead) {
					return new NumberLiteral(0);
				}
			}
		}

		if (node.right instanceof NumberLiteral) {
			final int right = ((NumberLiteral) node.right).value;
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
		}
		return node;
	}
}
