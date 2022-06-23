package node;

import java.io.PrintStream;

/**
 * @author Thomas Singer
 */
public class CodePrinter {

	// Accessing ==============================================================

	public void print(StatementListNode listNode, PrintStream stream) {
		print(listNode, 0, stream);
	}

	// Utils ==================================================================

	private void print(StatementListNode listNode, int indentation, PrintStream stream) {
		printIndentation(indentation, stream);
		stream.println("{");

		indentation++;

		for (StatementNode statement : listNode.getStatements()) {
			print(statement, indentation, stream);
		}

		printIndentation(indentation - 1, stream);
		stream.println("}");
	}

	private void print(StatementNode statement, int indentation, PrintStream stream) {
		if (statement instanceof VarDeclarationNode) {
			final VarDeclarationNode varDeclarationNode = (VarDeclarationNode) statement;
			print(varDeclarationNode, indentation, stream);
		}
		else if (statement instanceof AssignmentNode) {
			final AssignmentNode assignmentNode = (AssignmentNode) statement;
			print(assignmentNode, indentation, stream);
		}
		else if (statement instanceof StatementListNode) {
			final StatementListNode statementListNode = (StatementListNode) statement;
			print(statementListNode, indentation, stream);
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	private void print(VarDeclarationNode node, int indentation, PrintStream stream) {
		printIndentation(indentation, stream);

		stream.print(node.type.length() > 0 ? node.type : "<type>");
		stream.print(" ");
		stream.print(node.var);
		stream.print(" = ");

		print(node.expression, stream);

		stream.println();
	}

	private void print(AssignmentNode node, int indentation, PrintStream stream) {
		printIndentation(indentation, stream);

		stream.print(node.var);
		stream.print(" = ");

		print(node.expression, stream);

		stream.println();
	}

	private void print(ExpressionNode expression, PrintStream stream) {
		if (expression instanceof NumberNode) {
			final NumberNode numberNode = (NumberNode) expression;
			stream.print(numberNode.value);
		}
		else if (expression instanceof VarReadNode) {
			final VarReadNode varReadNode = (VarReadNode) expression;

			stream.print("read ");
			stream.print(varReadNode.var);
		}
		else if (expression instanceof BinaryExpressionNode) {
			final BinaryExpressionNode binaryExpressionNode = (BinaryExpressionNode) expression;

			print(binaryExpressionNode.left, stream);
			stream.print(" ");
			stream.print(binaryExpressionNode.operator);
			stream.print(" ");
			print(binaryExpressionNode.right, stream);
		}
		else if (expression instanceof FunctionCallNode) {
			final FunctionCallNode functionCallNode = (FunctionCallNode) expression;

			stream.print(functionCallNode.name);
			stream.print("(");
			boolean isFirst = true;
			for (ExpressionNode expressionNode : functionCallNode.getExpressions()) {
				if (isFirst) {
					isFirst = false;
				}
				else {
					stream.print(", ");
				}
				print(expressionNode, stream);
			}
			stream.print(")");
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	private void printIndentation(int indentation, PrintStream stream) {
		for (int i = 0; i < indentation; i++) {
			stream.print("\t");
		}
	}
}
