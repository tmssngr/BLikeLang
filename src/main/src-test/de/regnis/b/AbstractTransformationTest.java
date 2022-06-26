package de.regnis.b;

import de.regnis.b.node.*;
import node.TestStringOutput;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

import java.util.function.Consumer;

/**
 * @author Thomas Singer
 */
public abstract class AbstractTransformationTest {

	// Constants ==============================================================

	protected static final String PREFIX = "void test() {\n\t";
	protected static final String SUFFIX = "\n}\n";
	protected static final String NL = "\n\t";

	// Static =================================================================

	@NotNull
	protected DeclarationList createDocument(Consumer<StatementListFactory> factory) {
		final StatementListNode statementList = new StatementListNode();
		factory.accept(new StatementListFactory() {
			@Override
			public void assignment(String name, ExpressionNode expression) {
				statementList.add(new AssignmentNode(name, expression));
			}

			@Override
			public void varDeclaration(String name, ExpressionNode expression) {
				statementList.add(new VarDeclarationNode(name, expression));
			}
		});

		final DeclarationList root = new DeclarationList();
		root.add(new FunctionDeclaration("void", "test", new FunctionDeclarationParameters(), statementList));
		return root;
	}

	protected static void assertEquals(String expected, DeclarationList root) {
		final TestStringOutput output = new TestStringOutput();
		new CodePrinter().print(root, output);
		Assert.assertEquals(expected, output.toString());
	}

	// Inner Classes ==========================================================

	protected interface StatementListFactory {

		void assignment(String name, ExpressionNode expression);

		void varDeclaration(String name, ExpressionNode expression);
	}
}