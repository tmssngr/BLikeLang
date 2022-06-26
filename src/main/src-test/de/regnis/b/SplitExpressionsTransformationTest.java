package de.regnis.b;

import de.regnis.b.node.*;
import org.junit.Test;

import java.util.function.Consumer;

/**
 * @author Thomas Singer
 */
public class SplitExpressionsTransformationTest extends AbstractTransformationTest {

	// Accessing ==============================================================

	@Test
	public void testSimpleCases() {
		assertEquals("a = 1", f -> f.
				assignment("a", new NumberLiteral(1)));

		assertEquals("a = b", f -> f.
				assignment("a", new VarRead("b")));

		assertEquals("a = 1 + 2", f -> f.
				assignment("a",
				           BinaryExpression.createAdd(new NumberLiteral(1),
				                                      new NumberLiteral(2))));
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
	public void testSingleExtraction() {
		assertEquals("$1 := 2 + 3" + NL
				             + "a = 1 + $1", f -> f.
				assignment("a",
				           BinaryExpression.createAdd(new NumberLiteral(1),
				                                      BinaryExpression.createAdd(new NumberLiteral(2),
				                                                                 new NumberLiteral(3)))));
		assertEquals("$1 := 1 + 2" + NL
				             + "a = $1 + 3", f -> f.
				assignment("a",
				           BinaryExpression.createAdd(BinaryExpression.createAdd(new NumberLiteral(1),
				                                                                 new NumberLiteral(2)),
				                                      new NumberLiteral(3))));
		assertEquals("$1 := foo()" + NL
				             + "a = $1 + 3", f -> f.
				assignment("a",
				           BinaryExpression.createAdd(new FuncCall("foo",
				                                                   new FuncCallParameters()),
				                                      new NumberLiteral(3))));
		assertEquals("$1 := bar(1)" + NL +
				             "a = foo($1)", f -> f.
				assignment("a", new FuncCall("foo",
				                             new FuncCallParameters()
						                                     .add(new FuncCall("bar",
						                                                       new FuncCallParameters()
								                                                               .add(new NumberLiteral(1)))))));
		assertEquals("$1 := 1 - 2" + NL +
				             "a = foo($1)", f -> f.
				assignment("a", new FuncCall("foo",
				                             new FuncCallParameters()
						                                     .add(BinaryExpression.createSub(new NumberLiteral(1),
						                                                                     new NumberLiteral(2))))));
	}

	@Test
	public void testMultipleExtractions() {
		assertEquals("$1 := 1 * 2" + NL
				             + "$2 := 3 * 4" + NL
				             + "a := $1 + $2", f -> f.
				varDeclaration("a",
				               BinaryExpression.createAdd(BinaryExpression.createMultiply(new NumberLiteral(1),
				                                                                          new NumberLiteral(2)),
				                                          BinaryExpression.createMultiply(new NumberLiteral(3),
				                                                                          new NumberLiteral(4)))));

		assertEquals("$1 := 1 + b" + NL
				             + "$2 := 3 * 4" + NL
				             + "a := foo($1, $2)", f -> f.
				varDeclaration("a",
				               new FuncCall("foo",
				                            new FuncCallParameters()
						                                    .add(BinaryExpression.createAdd(new NumberLiteral(1),
						                                                                    new VarRead("b")))
						                                    .add(BinaryExpression.createMultiply(new NumberLiteral(3),
						                                                                         new NumberLiteral(4))))));
	}

	// Utils ==================================================================

	private void assertEquals(String expected, Consumer<StatementListFactory> factory) {
		final DeclarationList root = createDocument(factory);
		assertEquals(PREFIX + expected + SUFFIX, SplitExpressionsTransformation.transform(root));
	}
}