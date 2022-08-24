package de.regnis.b.ast.transformation;

import de.regnis.b.ast.*;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.type.BasicTypes;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

import java.util.function.Consumer;

/**
 * @author Thomas Singer
 */
public abstract class AbstractTransformationTest {

	// Constants ==============================================================

	protected static final String PREFIX = "void test() {\n  ";
	protected static final String SUFFIX = "\n}\n";
	protected static final String NL = "\n  ";

	// Static =================================================================

	@NotNull
	protected DeclarationList createDocument(Consumer<StatementListFactory> factory) {
		final StatementList statementList = new StatementList();
		factory.accept(new StatementListFactory() {
			@Override
			public void assignment(String name, Expression expression) {
				statementList.add(new Assignment(Assignment.Op.assign, name, expression));
			}

			@Override
			public void varDeclaration(String name, Expression expression) {
				statementList.add(new VarDeclaration(name, expression));
			}

			@Override
			public void returnStm(Expression expression) {
				statementList.add(new ReturnStatement(expression));
			}
		});

		return DeclarationList.of(new FuncDeclaration(BasicTypes.VOID, "test", FuncDeclarationParameters.empty(), statementList));
	}

	protected static void assertEquals(String expected, DeclarationList root) {
		Assert.assertEquals(expected, CodePrinter.print(root));
	}

	// Inner Classes ==========================================================

	protected interface StatementListFactory {

		void assignment(String name, Expression expression);

		void varDeclaration(String name, Expression expression);

		void returnStm(Expression expression);
	}
}
