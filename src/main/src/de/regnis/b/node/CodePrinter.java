package de.regnis.b.node;

import de.regnis.b.out.StringOutput;
import org.jetbrains.annotations.Nullable;

/**
 * @author Thomas Singer
 */
public class CodePrinter {

	// Accessing ==============================================================

	public void print(DeclarationList listNode, StringOutput output) {
		for (Declaration declaration : listNode.getDeclarations()) {
			declaration.visit(new DeclarationVisitor<>() {
				@Nullable
				@Override
				public Object visitGlobalVarDeclaration(GlobalVarDeclaration node) {
					print(node.node, 0, output);
					return null;
				}

				@Nullable
				@Override
				public Object visitFunctionDeclaration(FunctionDeclaration node) {
					printFunctionDeclaration(node, output);
					return null;
				}
			});
		}
	}

	private void printFunctionDeclaration(FunctionDeclaration declaration, StringOutput output) {
		output.print(declaration.type);
		output.print(" ");
		output.print(declaration.name);
		output.print("(");
		boolean isFirst = true;
		for (FunctionDeclarationParameter parameter : declaration.parameters.getParameters()) {
			if (isFirst) {
				isFirst = false;
			}
			else {
				output.print(", ");
			}

			output.print(parameter.type);
			output.print(" ");
			output.print(parameter.name);
		}
		output.print(") ");
		print(declaration.statement, 0, output);
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
		statement.visit(new StatementVisitor<Object>() {
			@Nullable
			@Override
			public Object visitAssignment(AssignmentNode node) {
				print(node, indentation, output);
				return null;
			}

			@Nullable
			@Override
			public Object visitStatementList(StatementListNode node) {
				print(node, indentation, output);
				return null;
			}

			@Nullable
			@Override
			public Object visitLocalVarDeclaration(VarDeclarationNode node) {
				print(node, indentation, output);
				return null;
			}

			@Nullable
			@Override
			public Object visitReturn(ReturnStatement node) {
				print(node, indentation, output);
				return null;
			}
		});
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

	private void print(ReturnStatement node, int indentation, StringOutput output) {
		printIndentation(indentation, output);

		output.print("return ");

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
			for (ExpressionNode expressionNode : functionCallNode.getParameters()) {
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
