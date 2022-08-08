package de.regnis.b.out;

import de.regnis.b.ast.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Thomas Singer
 */
public class CodePrinter {

	// Static =================================================================

	public static String print(DeclarationList listNode) {
		final StringStringOutput output = new StringStringOutput();
		new CodePrinter().print(listNode, output);
		return output.toString();
	}

	public static void print(Statement statement, StringOutput output) {
		print(statement, 0, output);
	}

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

	// Utils ==================================================================

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
		print(declaration.statementList, 0, output);
	}

	private static void print(StatementList listNode, int indentation, StringOutput output) {
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

	private static void print(Statement statement, int indentation, StringOutput output) {
		statement.visit(new StatementVisitor<>() {
			@Override
			public Object visitAssignment(Assignment node) {
				print(node, indentation, output);
				return node;
			}

			@Override
			public Object visitStatementList(StatementList node) {
				print(node, indentation, output);
				return node;
			}

			@Override
			public Object visitLocalVarDeclaration(VarDeclaration node) {
				print(node, indentation, output);
				return node;
			}

			@Override
			public Object visitCall(CallStatement node) {
				print(node, indentation, output);
				return node;
			}

			@Override
			public Object visitReturn(ReturnStatement node) {
				print(node, indentation, output);
				return node;
			}

			@Override
			public Object visitIf(IfStatement node) {
				print(node, indentation, output);
				return node;
			}

			@Override
			public Object visitWhile(WhileStatement node) {
				print(node, indentation, output);
				return node;
			}

			@Override
			public Object visitBreak(BreakStatement node) {
				print(node, indentation, output);
				return node;
			}
		});
	}

	private static void print(VarDeclaration node, int indentation, StringOutput output) {
		printIndentation(indentation, output);

		output.print(node.name);
		if (node.typeName != null) {
			output.print(" : ");
			output.print(node.typeName);
			output.print(" = ");
		}
		else {
			output.print(" := ");
		}

		print(node.expression, output);

		output.println();
	}

	private static void print(Assignment node, int indentation, StringOutput output) {
		printIndentation(indentation, output);

		output.print(node.name);
		output.print(" ");
		output.print(node.operation.text);
		output.print(" ");

		print(node.expression, output);

		output.println();
	}

	private static void print(CallStatement node, int indentation, StringOutput output) {
		printIndentation(indentation, output);

		handleCall(node.name, node.getParameters(), output);

		output.println();
	}

	private static void print(ReturnStatement node, int indentation, StringOutput output) {
		printIndentation(indentation, output);

		output.print("return");

		if (node.expression != null) {
			output.print(" ");

			print(node.expression, output);
		}

		output.println();
	}

	private static void print(IfStatement node, int indentation, StringOutput output) {
		printIndentation(indentation, output);
		output.print("if ");
		print(node.expression, output);
		output.println();

		print(node.trueStatements, indentation, output);

		if (node.falseStatements.getStatements().size() > 0) {
			printIndentation(indentation, output);
			output.print("else");
			output.println();

			print(node.falseStatements, indentation, output);
		}
	}

	private static void print(WhileStatement node, int indentation, StringOutput output) {
		printIndentation(indentation, output);
		output.print("while ");
		print(node.expression, output);
		output.println();

		print(node.statements, indentation, output);
	}

	private static void print(BreakStatement ignoredNode, int indentation, StringOutput output) {
		printIndentation(indentation, output);
		output.print("break");
		output.println();
	}

	public static void print(Expression expression, StringOutput output) {
		expression.visit(new ExpressionVisitor<>() {
			@Override
			public Object visitBinary(BinaryExpression node) {
				print(node.left, output);
				output.print(" ");
				output.print(node.operator.text);
				output.print(" ");
				print(node.right, output);
				return node;
			}

			@Override
			public Object visitFunctionCall(FuncCall node) {
				handleCall(node.name, node.getParameters(), output);
				return node;
			}

			@Override
			public Object visitNumber(NumberLiteral node) {
				output.print(String.valueOf(node.value));
				return node;
			}

			@Override
			public Object visitBoolean(BooleanLiteral node) {
				output.print(String.valueOf(node.value));
				return node;
			}

			@Override
			public Object visitVarRead(VarRead node) {
				output.print(node.name);
				return node;
			}

			@Override
			public Object visitTypeCast(TypeCast node) {
				output.print("(");
				output.print(node.typeName);
				output.print(") ");
				print(node.expression, output);
				return node;
			}
		});
	}

	private static void handleCall(String name, List<Expression> parameters, StringOutput output) {
		output.print(name);
		output.print("(");
		boolean isFirst = true;
		for (Expression expressionNode : parameters) {
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

	private static void printIndentation(int indentation, StringOutput output) {
		for (int i = 0; i < indentation; i++) {
			output.print("  ");
		}
	}
}
