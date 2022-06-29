package de.regnis.b.out;

import de.regnis.b.node.*;
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
				public Object visitFunctionDeclaration(FuncDeclaration node) {
					printFunctionDeclaration(node, output);
					return null;
				}
			});
		}
	}

	private void printFunctionDeclaration(FuncDeclaration declaration, StringOutput output) {
		output.print(declaration.type.toString());
		output.print(" ");
		output.print(declaration.name);
		output.print("(");
		boolean isFirst = true;
		for (FuncDeclarationParameter parameter : declaration.parameters.getParameters()) {
			if (isFirst) {
				isFirst = false;
			}
			else {
				output.print(", ");
			}

			output.print(parameter.type.toString());
			output.print(" ");
			output.print(parameter.name);
		}
		output.print(") ");
		print(declaration.statement, 0, output);
	}

	// Utils ==================================================================

	private void print(StatementList listNode, int indentation, StringOutput output) {
		printIndentation(indentation, output);
		output.print("{");
		output.println();

		indentation++;

		for (Statement statement : listNode.getStatements()) {
			print(statement, indentation, output);
		}

		printIndentation(indentation - 1, output);
		output.print("}");
		output.println();
	}

	private void print(Statement statement, int indentation, StringOutput output) {
		statement.visit(new StatementVisitor<Object>() {
			@Nullable
			@Override
			public Object visitAssignment(Assignment node) {
				print(node, indentation, output);
				return null;
			}

			@Nullable
			@Override
			public Object visitStatementList(StatementList node) {
				print(node, indentation, output);
				return null;
			}

			@Nullable
			@Override
			public Object visitLocalVarDeclaration(VarDeclaration node) {
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

	private void print(VarDeclaration node, int indentation, StringOutput output) {
		printIndentation(indentation, output);

		output.print(node.var);
		output.print(" := ");

		print(node.expression, output);

		output.println();
	}

	private void print(Assignment node, int indentation, StringOutput output) {
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

	private void print(Expression expression, StringOutput output) {
		if (expression instanceof NumberLiteral) {
			final NumberLiteral numberNode = (NumberLiteral) expression;
			output.print(String.valueOf(numberNode.value));
		}
		else if (expression instanceof VarRead) {
			final VarRead varReadNode = (VarRead) expression;

			output.print(varReadNode.var);
		}
		else if (expression instanceof BinaryExpression) {
			final BinaryExpression binaryExpressionNode = (BinaryExpression) expression;

			print(binaryExpressionNode.left, output);
			output.print(" ");
			output.print(binaryExpressionNode.operator);
			output.print(" ");
			print(binaryExpressionNode.right, output);
		}
		else if (expression instanceof FuncCall) {
			final FuncCall functionCallNode = (FuncCall) expression;

			output.print(functionCallNode.name);
			output.print("(");
			boolean isFirst = true;
			for (Expression expressionNode : functionCallNode.getParameters()) {
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
