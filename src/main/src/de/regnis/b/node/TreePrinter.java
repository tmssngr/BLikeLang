package de.regnis.b.node;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Singer
 */
public class TreePrinter {

	// Setup ==================================================================

	public TreePrinter() {
	}

	// Accessing ==============================================================

	public void print(List<String> strings, PrintStream stream) {
		for (String string : strings) {
			stream.println(string);
		}
	}

	public List<String> getStrings(BinaryExpressionNode node) {
		final List<String> strings = new ArrayList<>();
		strings.add("operator " + node.operator);
		append(getStrings(node.left), true, strings);
		append(getStrings(node.right), false, strings);
		return strings;
	}

	public List<String> getStrings(AssignmentNode node) {
		final List<String> strings = new ArrayList<>();
		strings.add(node.var + " = ");
		append(getStrings(node.expression), false, strings);
		return strings;
	}

	public List<String> getStrings(VarDeclarationNode node) {
		final List<String> strings = new ArrayList<>();
		strings.add(node.var + " := ");
		append(getStrings(node.expression), false, strings);
		return strings;
	}

	public List<String> getStrings(StatementListNode node) {
		final List<String> strings = new ArrayList<>();
		strings.add("statementList");
		final List<? extends StatementNode> statements = node.getStatements();
		for (int i = 0, size = statements.size(); i < size; i++) {
			final StatementNode statement = statements.get(i);
			append(getStrings(statement), i < size - 1, strings);
		}
		return strings;
	}

	private List<String> getStrings(StatementNode node) {
		if (node instanceof VarDeclarationNode) {
			return getStrings((VarDeclarationNode)node);
		}
		if (node instanceof AssignmentNode) {
			return getStrings((AssignmentNode)node);
		}
		if (node instanceof StatementListNode) {
			return getStrings((StatementListNode)node);
		}
		throw new UnsupportedOperationException();
	}

	// Utils ==================================================================

	private List<String> getStrings(ExpressionNode node) {
		if (node instanceof BinaryExpressionNode) {
			return getStrings((BinaryExpressionNode)node);
		}
		if (node instanceof NumberNode) {
			return getStrings((NumberNode)node);
		}
		if (node instanceof VarReadNode) {
			return getStrings((VarReadNode)node);
		}
		if (node instanceof FunctionCallNode) {
			return getStrings((FunctionCallNode) node);
		}
		throw new UnsupportedOperationException();
	}

	private List<String> getStrings(FunctionCallNode node) {
		final List<String> strings = new ArrayList<>();
		strings.add("function call " + node.name);
		final List<ExpressionNode> expressions = node.getExpressions();
		for (int i = 0, size = expressions.size(); i < size; i++) {
			final ExpressionNode expressionNode = expressions.get(i);
			append(getStrings(expressionNode), i < size - 1, strings);
		}
		return strings;
	}

	private List<String> getStrings(VarReadNode node) {
		return Collections.singletonList("read var " + node.var);
	}

	private List<String> getStrings(NumberNode node) {
		return Collections.singletonList("literal " + node.value);
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
