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
				assignment("a", new NumberNode(1)));

		assertEquals("a = b", f -> f.
				assignment("a", new VarReadNode("b")));

		assertEquals("a = 1 + b", f -> f.
				assignment("a",
				           BinaryExpressionNode.createAdd(new NumberNode(1),
				                                          new VarReadNode("b"))));
		assertEquals("a = b - 4", f -> f.
				assignment("a",
				           BinaryExpressionNode.createSub(new VarReadNode("b"),
				                                          new NumberNode(4))));
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
	public void testReplacements() {
		assertEquals("a = 3", f -> f.
				assignment("a",
				           BinaryExpressionNode.createAdd(new NumberNode(1),
				                                          new NumberNode(2))));
		assertEquals("a = b", f -> f.
				assignment("a",
				           BinaryExpressionNode.createAdd(new VarReadNode("b"),
				                                          new NumberNode(0))));
		assertEquals("a = b", f -> f.
				assignment("a",
				           BinaryExpressionNode.createAdd(new NumberNode(0),
				                                          new VarReadNode("b"))));
		assertEquals("a = b", f -> f.
				assignment("a",
				           BinaryExpressionNode.createSub(new VarReadNode("b"),
				                                          new NumberNode(0))));
		assertEquals("a = 0", f -> f.
				assignment("a",
				           BinaryExpressionNode.createMultiply(new VarReadNode("b"),
				                                               new NumberNode(0))));
		assertEquals("a = 0", f -> f.
				assignment("a",
				           BinaryExpressionNode.createMultiply(new NumberNode(0),
				                                               new VarReadNode("b"))));
		assertEquals("a = b", f -> f.
				assignment("a",
				           BinaryExpressionNode.createMultiply(new VarReadNode("b"),
				                                               new NumberNode(1))));
		assertEquals("a = b", f -> f.
				assignment("a",
				           BinaryExpressionNode.createMultiply(new NumberNode(1),
				                                               new VarReadNode("b"))));
	}

	// Utils ==================================================================

	private void assertEquals(String expected, Consumer<StatementListFactory> factory) {
		final DeclarationList root = createDocument(factory);
		assertEquals(PREFIX + expected + SUFFIX, ConstantFoldingTransformation.transform(root));
	}
}
