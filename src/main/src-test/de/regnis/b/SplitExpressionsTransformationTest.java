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
		assertEquals(PREFIX + "a = 1" + SUFFIX, SplitExpressionsTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a", new NumberNode(1)))
		));

		assertEquals(PREFIX + "a = b" + SUFFIX, SplitExpressionsTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a", new VarReadNode("b")))
		));

		assertEquals(PREFIX + "a = 1 + 2" + SUFFIX, SplitExpressionsTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a",
						                        BinaryExpressionNode.createAdd(new NumberNode(1),
						                                                       new NumberNode(2))))
		));

		assertEquals(PREFIX + "a = foo()" + SUFFIX, SplitExpressionsTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a", new FunctionCallNode("foo", new FunctionCallParameters())))
		));

		assertEquals(PREFIX + "a = foo(1)" + SUFFIX, SplitExpressionsTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a", new FunctionCallNode("foo",
						                                                  new FunctionCallParameters()
								                                                  .add(new NumberNode(1)))))
		));

		assertEquals(PREFIX + "a = foo(b)" + SUFFIX, SplitExpressionsTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a", new FunctionCallNode("foo",
						                                                  new FunctionCallParameters()
								                                                  .add(new VarReadNode("b")))))
		));
	}

	@Test
	public void testSingleExtraction() {
		assertEquals(PREFIX + "$1 := 2 + 3" + NL
				             + "a = 1 + $1" + SUFFIX, SplitExpressionsTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a",
						                        BinaryExpressionNode.createAdd(new NumberNode(1),
						                                                       BinaryExpressionNode.createAdd(new NumberNode(2),
						                                                                                      new NumberNode(3)))))
		));

		assertEquals(PREFIX + "$1 := 1 + 2" + NL
				             + "a = $1 + 3" + SUFFIX, SplitExpressionsTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a",
						                        BinaryExpressionNode.createAdd(BinaryExpressionNode.createAdd(new NumberNode(1),
						                                                                                      new NumberNode(2)),
						                                                       new NumberNode(3))))
		));

		assertEquals(PREFIX + "$1 := foo()" + NL
				             + "a = $1 + 3" + SUFFIX, SplitExpressionsTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a",
						                        BinaryExpressionNode.createAdd(new FunctionCallNode("foo",
						                                                                            new FunctionCallParameters()),
						                                                       new NumberNode(3))))
		));

		assertEquals(PREFIX + "$1 := bar(1)" + NL +
				             "a = foo($1)" + SUFFIX, SplitExpressionsTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a", new FunctionCallNode("foo",
						                                                  new FunctionCallParameters()
								                                                  .add(new FunctionCallNode("bar",
								                                                                            new FunctionCallParameters()
										                                                                            .add(new NumberNode(1)))))))
		));

		assertEquals(PREFIX + "$1 := 1 - 2" + NL +
				             "a = foo($1)" + SUFFIX, SplitExpressionsTransformation.transform(
				new StatementListNode()
						.add(new AssignmentNode("a", new FunctionCallNode("foo",
						                                                  new FunctionCallParameters()
								                                                  .add(BinaryExpressionNode.createSub(new NumberNode(1),
								                                                                                      new NumberNode(2))))))
		));
	}

	@Test
	public void testMultipleExtractions() {
		assertEquals(PREFIX + "$1 := 1 * 2" + NL
				             + "$2 := 3 * 4" + NL
				             + "a := $1 + $2" + SUFFIX, SplitExpressionsTransformation.transform(
				new StatementListNode()
						.add(new VarDeclarationNode("a",
						                            BinaryExpressionNode.createAdd(BinaryExpressionNode.createMultiply(new NumberNode(1),
						                                                                                               new NumberNode(2)),
						                                                           BinaryExpressionNode.createMultiply(new NumberNode(3),
						                                                                                               new NumberNode(4)))))
		));

		assertEquals(PREFIX + "$1 := 1 + b" + NL
				             + "$2 := 3 * 4" + NL
				             + "a := foo($1, $2)" + SUFFIX, SplitExpressionsTransformation.transform(
				new StatementListNode()
						.add(new VarDeclarationNode("a",
						                            new FunctionCallNode("foo",
						                                                 new FunctionCallParameters()
								                                                 .add(BinaryExpressionNode.createAdd(new NumberNode(1),
								                                                                                     new VarReadNode("b")))
								                                                 .add(BinaryExpressionNode.createMultiply(new NumberNode(3),
								                                                                                          new NumberNode(4))))))
		));
	}
}