package de.regnis.b.out;

import de.regnis.b.ast.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Singer
 */
public class TreePrinter {

	// Static =================================================================

	public static String print(DeclarationList root) {
		final StringOutput output = new StringStringOutput();
		new TreePrinter().print(root, output);
		return output.toString();
	}

	// Setup ==================================================================

	public TreePrinter() {
	}

	// Accessing ==============================================================

	public void print(DeclarationList node, StringOutput output) {
		for (String string : getStrings(node)) {
			output.print(string);
			output.println();
		}
	}

	public void print(StatementList node, StringOutput output) {
		for (String string : getStrings(node)) {
			output.print(string);
			output.println();
		}
	}

	public List<String> getStrings(BinaryExpression node) {
		final List<String> strings = new ArrayList<>();
		strings.add("operator " + node.operator().text);
		append(getStrings(node.left()), true, strings);
		append(getStrings(node.right()), false, strings);
		return strings;
	}

	public List<String> getStrings(Assignment node) {
		final List<String> strings = new ArrayList<>();
		strings.add(node.name() + " " + node.operation().text);
		append(getStrings(node.expression()), false, strings);
		return strings;
	}

	public List<String> getStrings(VarDeclaration node) {
		final List<String> strings = new ArrayList<>();
		strings.add(node.name() + " :=");
		append(getStrings(node.expression()), false, strings);
		return strings;
	}

	public List<String> getStrings(CallStatement node) {
		final List<String> strings = new ArrayList<>();
		strings.add("call " + node.name());
		final List<Expression> expressions = node.parameters().getExpressions();
		for (int i = 0, size = expressions.size(); i < size; i++) {
			final Expression expressionNode = expressions.get(i);
			append(getStrings(expressionNode), i < size - 1, strings);
		}
		return strings;
	}

	public List<String> getStrings(ReturnStatement node) {
		final List<String> strings = new ArrayList<>();
		strings.add("return");
		if (node.expression() != null) {
			append(getStrings(node.expression()), false, strings);
		}
		return strings;
	}

	public List<String> getStrings(IfStatement node) {
		final List<String> strings = new ArrayList<>();
		strings.add("if");
		append(getStrings(node.expression()), true, strings);
		append(getStrings("then", node.trueStatements()), true, strings);
		append(getStrings("else", node.falseStatements()), false, strings);
		return strings;
	}

	public List<String> getStrings(@Nullable String name, StatementList node) {
		final List<String> strings = new ArrayList<>();
		if (name != null) {
			strings.add(name);
		}
		final List<? extends Statement> statements = node.getStatements();
		for (int i = 0, size = statements.size(); i < size; i++) {
			final Statement statement = statements.get(i);
			append(getStrings(statement), i < size - 1, strings);
		}
		return strings;
	}

	// Utils ==================================================================

	private List<String> getStrings(WhileStatement node) {
		final List<String> strings = new ArrayList<>();
		strings.add("While");
		append(getStrings(node.expression()), true, strings);
		append(getStrings("do", node.statements()), false, strings);
		return strings;
	}

	private List<String> getStrings(StatementList node) {
		return getStrings("statementList", node);
	}

	private List<String> getStrings(DeclarationList node) {
		final List<String> strings = new ArrayList<>();
		final List<Declaration> declarations = node.getDeclarations();
		for (int i = 0, size = declarations.size(); i < size; i++) {
			final Declaration declaration = declarations.get(i);
			append(getStrings(declaration), i < size - 1, strings);
		}
		return strings;
	}

	private List<String> getStrings(Declaration declaration) {
		return declaration.visit(new DeclarationVisitor<>() {
			@Override
			public List<String> visitConst(ConstDeclaration node) {
				final List<String> strings = new ArrayList<>();
				strings.add("const " + node.name());
				append(getStrings(node.expression()), false, strings);
				return strings;
			}

			@Override
			public List<String> visitFunctionDeclaration(FuncDeclaration node) {
				final StringBuilder buffer = new StringBuilder();
				buffer.append(node.type());
				buffer.append(" ");
				buffer.append(node.name());
				buffer.append("(");
				boolean isFirst = true;
				for (FuncDeclarationParameter parameter : node.parameters().getParameters()) {
					if (isFirst) {
						isFirst = false;
					}
					else {
						buffer.append(", ");
					}

					buffer.append("int");
					buffer.append(" ");
					buffer.append(parameter.name());
				}
				buffer.append(")");
				final List<String> strings = new ArrayList<>();
				strings.add(buffer.toString());
				append(getStrings(node.statementList()), false, strings);
				return strings;
			}
		});
	}

	private List<String> getStrings(Statement node) {
		return node.visit(new StatementVisitor<>() {
			@Override
			public List<String> visitAssignment(Assignment node) {
				return getStrings(node);
			}

			@Override
			public List<String> visitStatementList(StatementList node) {
				return getStrings(node);
			}

			@Override
			public List<String> visitLocalVarDeclaration(VarDeclaration node) {
				return getStrings(node);
			}

			@Override
			public List<String> visitCall(CallStatement node) {
				return getStrings(node);
			}

			@Override
			public List<String> visitReturn(ReturnStatement node) {
				return getStrings(node);
			}

			@Override
			public List<String> visitIf(IfStatement node) {
				return getStrings(node);
			}

			@Override
			public List<String> visitWhile(WhileStatement node) {
				return getStrings(node);
			}

			@Override
			public List<String> visitBreak(BreakStatement node) {
				return List.of("break");
			}
		});
	}

	private List<String> getStrings(Expression node) {
		return node.visit(new ExpressionVisitor<>() {
			@Override
			public List<String> visitBinary(BinaryExpression node) {
				return getStrings(node);
			}

			@Override
			public List<String> visitFunctionCall(FuncCall node) {
				return getStrings(node);
			}

			@Override
			public List<String> visitNumber(NumberLiteral node) {
				return getStrings(node);
			}

			@Override
			public List<String> visitVarRead(VarRead node) {
				return getStrings(node);
			}
		});
	}

	private List<String> getStrings(FuncCall node) {
		final List<String> strings = new ArrayList<>();
		strings.add("function call " + node.name());
		final List<Expression> expressions = node.parameters().getExpressions();
		for (int i = 0, size = expressions.size(); i < size; i++) {
			final Expression expressionNode = expressions.get(i);
			append(getStrings(expressionNode), i < size - 1, strings);
		}
		return strings;
	}

	private List<String> getStrings(VarRead node) {
		return Collections.singletonList("read var " + node.name());
	}

	private List<String> getStrings(NumberLiteral node) {
		return Collections.singletonList("literal " + node.value());
	}

	private void append(List<String> lines, boolean furtherSiblingFollows, List<? super String> target) {
		boolean isFirst = true;
		for (String line : lines) {
			if (isFirst) {
				target.add("+- " + line);
				isFirst = false;
			}
			else if (furtherSiblingFollows) {
				target.add("|  " + line);
			}
			else {
				target.add("   " + line);
			}
		}
	}
}
