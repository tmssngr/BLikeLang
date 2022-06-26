package de.regnis.b;

import de.regnis.b.node.*;
import org.junit.Test;

/**
 * @author Thomas Singer
 */
public final class ConstantFoldingTransformationTest extends AbstractTransformationTest {

	@Test
	public void testUnchangedCases() {
		assertEquals(PREFIX + "a = 1" + SUFFIX, ConstantFoldingTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a", new NumberNode(1)))
		));

		assertEquals(PREFIX + "a = b" + SUFFIX, ConstantFoldingTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a", new VarReadNode("b")))
		));

		assertEquals(PREFIX + "a = 1 + b" + SUFFIX, ConstantFoldingTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a",
						                        BinaryExpressionNode.createAdd(new NumberNode(1),
						                                                       new VarReadNode("b"))))
		));

		assertEquals(PREFIX + "a = b - 4" + SUFFIX, ConstantFoldingTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a",
						                        BinaryExpressionNode.createSub(new VarReadNode("b"),
						                                                       new NumberNode(4))))
		));

		assertEquals(PREFIX + "a = foo()" + SUFFIX, ConstantFoldingTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a", new FunctionCallNode("foo", new FunctionCallParameters())))
		));

		assertEquals(PREFIX + "a = foo(1)" + SUFFIX, ConstantFoldingTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a", new FunctionCallNode("foo",
						                                                  new FunctionCallParameters()
								                                                  .add(new NumberNode(1)))))
		));

		assertEquals(PREFIX + "a = foo(b)" + SUFFIX, ConstantFoldingTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a", new FunctionCallNode("foo",
						                                                  new FunctionCallParameters()
								                                                  .add(new VarReadNode("b")))))
		));
	}

	@Test
	public void testReplacements() {
		assertEquals(PREFIX + "a = 3" + SUFFIX, ConstantFoldingTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a",
						                        BinaryExpressionNode.createAdd(new NumberNode(1),
						                                                       new NumberNode(2))))
		));

		assertEquals(PREFIX + "a = b" + SUFFIX, ConstantFoldingTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a",
						                        BinaryExpressionNode.createAdd(new VarReadNode("b"),
						                                                       new NumberNode(0))))
		));

		assertEquals(PREFIX + "a = b" + SUFFIX, ConstantFoldingTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a",
						                        BinaryExpressionNode.createAdd(new NumberNode(0),
						                                                       new VarReadNode("b"))))
		));

		assertEquals(PREFIX + "a = b" + SUFFIX, ConstantFoldingTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a",
						                        BinaryExpressionNode.createSub(new VarReadNode("b"),
						                                                       new NumberNode(0))))
		));

		assertEquals(PREFIX + "a = 0" + SUFFIX, ConstantFoldingTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a",
						                        BinaryExpressionNode.createMultiply(new VarReadNode("b"),
						                                                            new NumberNode(0))))
		));

		assertEquals(PREFIX + "a = 0" + SUFFIX, ConstantFoldingTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a",
						                        BinaryExpressionNode.createMultiply(new NumberNode(0),
						                                                            new VarReadNode("b"))))
		));

		assertEquals(PREFIX + "a = b" + SUFFIX, ConstantFoldingTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a",
						                        BinaryExpressionNode.createMultiply(new VarReadNode("b"),
						                                                            new NumberNode(1))))
		));

		assertEquals(PREFIX + "a = b" + SUFFIX, ConstantFoldingTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a",
						                        BinaryExpressionNode.createMultiply(new NumberNode(1),
						                                                            new VarReadNode("b"))))
		));
	}
}
