package de.regnis.b;

import de.regnis.b.node.*;
import org.junit.Test;

import java.util.function.Consumer;

/**
 * @author Thomas Singer
 */
public final class ConstantFoldingTransformationTest extends AbstractTransformationTest {

	// Accessing ==============================================================

	@Test
	public void testUnchangedCases() {
		assertEquals("a = 1", f -> f.
				assignment("a", new NumberLiteral(1)));

		assertEquals("a = b", f -> f.
				assignment("a", new VarRead("b")));

		assertEquals("a = 1 + b", f -> f.
				assignment("a",
				           BinaryExpression.createAdd(new NumberLiteral(1),
				                                      new VarRead("b"))));
		assertEquals("a = b - 4", f -> f.
				assignment("a",
				           BinaryExpression.createSub(new VarRead("b"),
				                                      new NumberLiteral(4))));
		assertEquals("a = foo()", f -> f.
				assignment("a", new FuncCall("foo", new FuncCallParameters())));

		assertEquals("a = foo(1)", f -> f.
				assignment("a", new FuncCall("foo",
				                             new FuncCallParameters()
						                                     .add(new NumberLiteral(1)))));
		assertEquals("a = foo(b)", f -> f.
				assignment("a", new FuncCall("foo",
				                             new FuncCallParameters()
						                                     .add(new VarRead("b")))));
	}

	@Test
	public void testReplacements() {
		assertEquals("a = 3", f -> f.
				assignment("a",
				           BinaryExpression.createAdd(new NumberLiteral(1),
				                                      new NumberLiteral(2))));
		assertEquals("a = b", f -> f.
				assignment("a",
				           BinaryExpression.createAdd(new VarRead("b"),
				                                      new NumberLiteral(0))));
		assertEquals("a = b", f -> f.
				assignment("a",
				           BinaryExpression.createAdd(new NumberLiteral(0),
				                                      new VarRead("b"))));
		assertEquals("a = b", f -> f.
				assignment("a",
				           BinaryExpression.createSub(new VarRead("b"),
				                                      new NumberLiteral(0))));
		assertEquals("a = 0", f -> f.
				assignment("a",
				           BinaryExpression.createMultiply(new VarRead("b"),
				                                           new NumberLiteral(0))));
		assertEquals("a = 0", f -> f.
				assignment("a",
				           BinaryExpression.createMultiply(new NumberLiteral(0),
				                                           new VarRead("b"))));
		assertEquals("a = b", f -> f.
				assignment("a",
				           BinaryExpression.createMultiply(new VarRead("b"),
				                                           new NumberLiteral(1))));
		assertEquals("a = b", f -> f.
				assignment("a",
				           BinaryExpression.createMultiply(new NumberLiteral(1),
				                                           new VarRead("b"))));
	}

	// Utils ==================================================================

	private void assertEquals(String expected, Consumer<StatementListFactory> factory) {
		final DeclarationList root = createDocument(factory);
		assertEquals(PREFIX + expected + SUFFIX, ConstantFoldingTransformation.transform(root));
	}
}
