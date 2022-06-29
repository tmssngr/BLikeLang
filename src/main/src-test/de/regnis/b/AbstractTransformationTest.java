package de.regnis.b;

import de.regnis.b.node.*;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringOutput;
import de.regnis.b.out.StringStringOutput;
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
		final StatementList statementList = new StatementList();
		factory.accept(new StatementListFactory() {
			@Override
			public void assignment(String name, Expression expression) {
				statementList.add(new Assignment(name, expression));
			}

			@Override
			public void varDeclaration(String name, Expression expression) {
				statementList.add(new VarDeclaration(name, expression));
			}
		});

		final DeclarationList root = new DeclarationList();
		root.add(new FuncDeclaration("void", "test", new FuncDeclarationParameters(), statementList));
		return root;
	}

	protected static void assertEquals(String expected, DeclarationList root) {
		final StringOutput output = new StringStringOutput();
		new CodePrinter().print(root, output);
		Assert.assertEquals(expected, output.toString());
	}

	// Inner Classes ==========================================================

	protected interface StatementListFactory {

		void assignment(String name, Expression expression);

		void varDeclaration(String name, Expression expression);
	}
}