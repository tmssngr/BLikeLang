package de.regnis.b;

import de.regnis.b.node.*;
import org.junit.Test;

/**
 * @author Thomas Singer
 */
public class SplitExpressionsTransformationTest extends AbstractTransformationTest {

	// Accessing ==============================================================

	@Test
	public void testSimpleCases() {
		assertEquals(PREFIX + "a = 1" + SUFFIX, SplitExpressionsTransformation.createTempVars(
				new StatementListNode()
						.add(new AssignmentNode("a", new NumberNode(1)))
		));

		assertEquals(PREFIX + "a = b" + SUFFIX, SplitExpressionsTransformation.createTempVars(
				new StatementListNode()
						.add(new AssignmentNode("a", new VarReadNode("b")))
		));

		assertEquals(PREFIX + "a = 1 + 2" + SUFFIX, SplitExpressionsTransformation.createTempVars(
				new StatementListNode()
						.add(new AssignmentNode("a",
						                        BinaryExpressionNode.createAdd(new NumberNode(1),
						                                                       new NumberNode(2))))
		));

		assertEquals(PREFIX + "a = foo()" + SUFFIX, SplitExpressionsTransformation.createTempVars(
				new StatementListNode()
						.add(new AssignmentNode("a", new FunctionCallNode("foo", new FunctionParametersNode())))
		));

		assertEquals(PREFIX + "a = foo(1)" + SUFFIX, SplitExpressionsTransformation.createTempVars(
				new StatementListNode()
						.add(new AssignmentNode("a", new FunctionCallNode("foo",
						                                                  new FunctionParametersNode()
								                                                  .add(new NumberNode(1)))))
		));

		assertEquals(PREFIX + "a = foo(b)" + SUFFIX, SplitExpressionsTransformation.createTempVars(
				new StatementListNode()
						.add(new AssignmentNode("a", new FunctionCallNode("foo",
						                                                  new FunctionParametersNode()
								                                                  .add(new VarReadNode("b")))))
		));
	}

	@Test
	public void testSingleExtraction() {
		assertEquals(PREFIX + "t 1 := 2 + 3" + NL
				             + "a = 1 + t 1" + SUFFIX, SplitExpressionsTransformation.createTempVars(
				new StatementListNode()
						.add(new AssignmentNode("a",
						                        BinaryExpressionNode.createAdd(new NumberNode(1),
						                                                       BinaryExpressionNode.createAdd(new NumberNode(2),
						                                                                                      new NumberNode(3)))))
		));

		assertEquals(PREFIX + "t 1 := 1 + 2" + NL
				             + "a = t 1 + 3" + SUFFIX, SplitExpressionsTransformation.createTempVars(
				new StatementListNode()
						.add(new AssignmentNode("a",
						                        BinaryExpressionNode.createAdd(BinaryExpressionNode.createAdd(new NumberNode(1),
						                                                                                      new NumberNode(2)),
						                                                       new NumberNode(3))))
		));

		assertEquals(PREFIX + "t 1 := foo()" + NL
				             + "a = t 1 + 3" + SUFFIX, SplitExpressionsTransformation.createTempVars(
				new StatementListNode()
						.add(new AssignmentNode("a",
						                        BinaryExpressionNode.createAdd(new FunctionCallNode("foo",
						                                                                            new FunctionParametersNode()),
						                                                       new NumberNode(3))))
		));

		assertEquals(PREFIX + "t 1 := bar(1)" + NL +
				             "a = foo(t 1)" + SUFFIX, SplitExpressionsTransformation.createTempVars(
				new StatementListNode()
						.add(new AssignmentNode("a", new FunctionCallNode("foo",
						                                                  new FunctionParametersNode()
								                                                  .add(new FunctionCallNode("bar",
								                                                                            new FunctionParametersNode()
										                                                                            .add(new NumberNode(1)))))))
		));

		assertEquals(PREFIX + "t 1 := 1 - 2" + NL +
				             "a = foo(t 1)" + SUFFIX, SplitExpressionsTransformation.createTempVars(
				new StatementListNode()
						.add(new AssignmentNode("a", new FunctionCallNode("foo",
						                                                  new FunctionParametersNode()
								                                                  .add(BinaryExpressionNode.createSub(new NumberNode(1),
								                                                                                      new NumberNode(2))))))
		));
	}

	@Test
	public void testMultipleExtractions() {
		assertEquals(PREFIX + "t 1 := 1 * 2" + NL
				             + "t 2 := 3 * 4" + NL
				             + "a := t 1 + t 2" + SUFFIX, SplitExpressionsTransformation.createTempVars(
				new StatementListNode()
						.add(new VarDeclarationNode("a",
						                            BinaryExpressionNode.createAdd(BinaryExpressionNode.createMultiply(new NumberNode(1),
						                                                                                               new NumberNode(2)),
						                                                           BinaryExpressionNode.createMultiply(new NumberNode(3),
						                                                                                               new NumberNode(4)))))
		));

		assertEquals(PREFIX + "t 1 := 1 + b" + NL
				             + "t 2 := 3 * 4" + NL
				             + "a := foo(t 1, t 2)" + SUFFIX, SplitExpressionsTransformation.createTempVars(
				new StatementListNode()
						.add(new VarDeclarationNode("a",
						                            new FunctionCallNode("foo",
						                                                 new FunctionParametersNode()
								                                                 .add(BinaryExpressionNode.createAdd(new NumberNode(1),
								                                                                                     new VarReadNode("b")))
								                                                 .add(BinaryExpressionNode.createMultiply(new NumberNode(3),
								                                                                                          new NumberNode(4))))))
		));
	}
}