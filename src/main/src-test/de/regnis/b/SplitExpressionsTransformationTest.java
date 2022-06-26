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
				assignment("a", new NumberNode(1)));

		assertEquals("a = b", f -> f.
				assignment("a", new VarReadNode("b")));

		assertEquals("a = 1 + 2", f -> f.
				assignment("a",
				           BinaryExpressionNode.createAdd(new NumberNode(1),
				                                          new NumberNode(2))));
		assertEquals("a = foo()", f -> f.
				assignment("a", new FunctionCallNode("foo", new FunctionCallParameters())));

		assertEquals("a = foo(1)", f -> f.
				assignment("a", new FunctionCallNode("foo",
				                                     new FunctionCallParameters()
						                                     .add(new NumberNode(1)))));
		assertEquals("a = foo(b)", f -> f.
				assignment("a", new FunctionCallNode("foo",
				                                     new FunctionCallParameters()
						                                     .add(new VarReadNode("b")))));
	}

	@Test
	public void testSingleExtraction() {
		assertEquals("$1 := 2 + 3" + NL
				             + "a = 1 + $1", f -> f.
				assignment("a",
				           BinaryExpressionNode.createAdd(new NumberNode(1),
				                                          BinaryExpressionNode.createAdd(new NumberNode(2),
				                                                                         new NumberNode(3)))));
		assertEquals("$1 := 1 + 2" + NL
				             + "a = $1 + 3", f -> f.
				assignment("a",
				           BinaryExpressionNode.createAdd(BinaryExpressionNode.createAdd(new NumberNode(1),
				                                                                         new NumberNode(2)),
				                                          new NumberNode(3))));
		assertEquals("$1 := foo()" + NL
				             + "a = $1 + 3", f -> f.
				assignment("a",
				           BinaryExpressionNode.createAdd(new FunctionCallNode("foo",
				                                                               new FunctionCallParameters()),
				                                          new NumberNode(3))));
		assertEquals("$1 := bar(1)" + NL +
				             "a = foo($1)", f -> f.
				assignment("a", new FunctionCallNode("foo",
				                                     new FunctionCallParameters()
						                                     .add(new FunctionCallNode("bar",
						                                                               new FunctionCallParameters()
								                                                               .add(new NumberNode(1)))))));
		assertEquals("$1 := 1 - 2" + NL +
				             "a = foo($1)", f -> f.
				assignment("a", new FunctionCallNode("foo",
				                                     new FunctionCallParameters()
						                                     .add(BinaryExpressionNode.createSub(new NumberNode(1),
						                                                                         new NumberNode(2))))));
	}

	@Test
	public void testMultipleExtractions() {
		assertEquals("$1 := 1 * 2" + NL
				             + "$2 := 3 * 4" + NL
				             + "a := $1 + $2", f -> f.
				varDeclaration("a",
				               BinaryExpressionNode.createAdd(BinaryExpressionNode.createMultiply(new NumberNode(1),
				                                                                                  new NumberNode(2)),
				                                              BinaryExpressionNode.createMultiply(new NumberNode(3),
				                                                                                  new NumberNode(4)))));

		assertEquals("$1 := 1 + b" + NL
				             + "$2 := 3 * 4" + NL
				             + "a := foo($1, $2)", f -> f.
				varDeclaration("a",
				               new FunctionCallNode("foo",
				                                    new FunctionCallParameters()
						                                    .add(BinaryExpressionNode.createAdd(new NumberNode(1),
						                                                                        new VarReadNode("b")))
						                                    .add(BinaryExpressionNode.createMultiply(new NumberNode(3),
						                                                                             new NumberNode(4))))));
	}

	// Utils ==================================================================

	private void assertEquals(String expected, Consumer<StatementListFactory> factory) {
		final StatementListNode root = createDocument(factory);
		assertEquals(PREFIX + expected + SUFFIX, SplitExpressionsTransformation.transform(root));
	}
}