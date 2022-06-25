package de.regnis.b;

import de.regnis.b.node.*;

/**
 * @author Thomas Singer
 */
public final class ConstantFoldingTransformation extends AbstractTransformation {

	// Static =================================================================

	public static StatementListNode transform(StatementListNode root) {
		final ConstantFoldingTransformation transformation = new ConstantFoldingTransformation();
		return transformation.handleStatementList(root);
	}

	// Setup ==================================================================

	private ConstantFoldingTransformation() {
	}

	// Utils ==================================================================

	@Override
	protected ExpressionNode handleBinary(BinaryExpressionNode node, StatementListNode newStatementList) {
		if (node.left instanceof NumberNode
				&& node.right instanceof NumberNode) {
			final int left = ((NumberNode) node.left).value;
			final int right = ((NumberNode) node.right).value;
			final int value = switch (node.operator) {
				case BinaryExpressionNode.PLUS -> left + right;
				case BinaryExpressionNode.MINUS -> left - right;
				case BinaryExpressionNode.MULTIPLY -> left * right;
				default -> throw new UnsupportedOperationException();
			};
			return new NumberNode(value);
		}

		if (node.left instanceof NumberNode) {
			final int left = ((NumberNode) node.left).value;
			if (node.operator.equals(BinaryExpressionNode.PLUS)) {
				if (left == 0) {
					return node.right;
				}
			}
			else if (node.operator.equals(BinaryExpressionNode.MULTIPLY)) {
				if (left == 1) {
					return node.right;
				}
				if (left == 0 && node.right instanceof VarReadNode) {
					return new NumberNode(0);
				}
			}
		}

		if (node.right instanceof NumberNode) {
			final int right = ((NumberNode) node.right).value;
			if (node.operator.equals(BinaryExpressionNode.PLUS)
					|| node.operator.equals(BinaryExpressionNode.MINUS)) {
				if (right == 0) {
					return node.left;
				}
			}
			else if (node.operator.equals(BinaryExpressionNode.MULTIPLY)) {
				if (right == 1) {
					return node.left;
				}
				if (right == 0 && node.left instanceof VarReadNode) {
					return new NumberNode(0);
				}
			}
		}
		return node;
	}
}
