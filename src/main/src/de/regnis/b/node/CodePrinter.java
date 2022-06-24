package de.regnis.b.node;

import de.regnis.b.out.StringOutput;

/**
 * @author Thomas Singer
 */
public class CodePrinter {

	// Accessing ==============================================================

	public void print(StatementListNode listNode, StringOutput output) {
		print(listNode, 0, output);
	}

	// Utils ==================================================================

	private void print(StatementListNode listNode, int indentation, StringOutput output) {
		printIndentation(indentation, output);
		output.print("{");
		output.println();

		indentation++;

		for (StatementNode statement : listNode.getStatements()) {
			print(statement, indentation, output);
		}

		printIndentation(indentation - 1, output);
		output.print("}");
		output.println();
	}

	private void print(StatementNode statement, int indentation, StringOutput output) {
		if (statement instanceof VarDeclarationNode) {
			final VarDeclarationNode varDeclarationNode = (VarDeclarationNode) statement;
			print(varDeclarationNode, indentation, output);
		}
		else if (statement instanceof AssignmentNode) {
			final AssignmentNode assignmentNode = (AssignmentNode) statement;
			print(assignmentNode, indentation, output);
		}
		else if (statement instanceof StatementListNode) {
			final StatementListNode statementListNode = (StatementListNode) statement;
			print(statementListNode, indentation, output);
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	private void print(VarDeclarationNode node, int indentation, StringOutput output) {
		printIndentation(indentation, output);

		output.print(node.var);
		output.print(" := ");

		print(node.expression, output);

		output.println();
	}

	private void print(AssignmentNode node, int indentation, StringOutput output) {
		printIndentation(indentation, output);

		output.print(node.var);
		output.print(" = ");

		print(node.expression, output);

		output.println();
	}

	private void print(ExpressionNode expression, StringOutput output) {
		if (expression instanceof NumberNode) {
			final NumberNode numberNode = (NumberNode) expression;
			output.print(String.valueOf(numberNode.value));
		}
		else if (expression instanceof VarReadNode) {
			final VarReadNode varReadNode = (VarReadNode) expression;

			output.print("read ");
			output.print(varReadNode.var);
		}
		else if (expression instanceof BinaryExpressionNode) {
			final BinaryExpressionNode binaryExpressionNode = (BinaryExpressionNode) expression;

			print(binaryExpressionNode.left, output);
			output.print(" ");
			output.print(binaryExpressionNode.operator);
			output.print(" ");
			print(binaryExpressionNode.right, output);
		}
		else if (expression instanceof FunctionCallNode) {
			final FunctionCallNode functionCallNode = (FunctionCallNode) expression;

			output.print(functionCallNode.name);
			output.print("(");
			boolean isFirst = true;
			for (ExpressionNode expressionNode : functionCallNode.getExpressions()) {
				if (isFirst) {
					isFirst = false;
				}
				else {
					output.print(", ");
				}
				print(expressionNode, output);
			}
			output.print(")");
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	private void printIndentation(int indentation, StringOutput output) {
		for (int i = 0; i < indentation; i++) {
			output.print("\t");
		}
	}
}
