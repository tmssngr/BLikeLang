package de.regnis.b;

import de.regnis.b.node.*;
import de.regnis.b.out.StringOutput;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Thomas Singer
 */
public class SplitExpressionsTransformationTest {

	// Constants ==============================================================

	private static final String PREFIX = "{\n\t";
	private static final String SUFFIX = "\n}\n";
	private static final String NL = "\n\t";

	// Accessing ==============================================================

	@Test
	public void testSimpleCases() {
		assertEquals(PREFIX + "a = 1" + SUFFIX, SplitExpressionsTransformation.createTempVars(
				new StatementListNode()
						.add(new AssignmentNode("a", new NumberNode(1)))
		));

		assertEquals(PREFIX + "a = read b" + SUFFIX, SplitExpressionsTransformation.createTempVars(
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

		assertEquals(PREFIX + "a = foo(read b)" + SUFFIX, SplitExpressionsTransformation.createTempVars(
				new StatementListNode()
						.add(new AssignmentNode("a", new FunctionCallNode("foo",
						                                                  new FunctionParametersNode()
								                                                  .add(new VarReadNode("b")))))
		));
	}

	@Test
	public void testSingleExtraction() {
		assertEquals(PREFIX + "t 1 := 2 + 3" + NL
				             + "a = 1 + read t 1" + SUFFIX, SplitExpressionsTransformation.createTempVars(
				new StatementListNode()
						.add(new AssignmentNode("a",
						                        BinaryExpressionNode.createAdd(new NumberNode(1),
						                                                       BinaryExpressionNode.createAdd(new NumberNode(2),
						                                                                                      new NumberNode(3)))))
		));

		assertEquals(PREFIX + "t 1 := 1 + 2" + NL
				             + "a = read t 1 + 3" + SUFFIX, SplitExpressionsTransformation.createTempVars(
				new StatementListNode()
						.add(new AssignmentNode("a",
						                        BinaryExpressionNode.createAdd(BinaryExpressionNode.createAdd(new NumberNode(1),
						                                                                                      new NumberNode(2)),
						                                                       new NumberNode(3))))
		));

		assertEquals(PREFIX + "t 1 := foo()" + NL
				             + "a = read t 1 + 3" + SUFFIX, SplitExpressionsTransformation.createTempVars(
				new StatementListNode()
						.add(new AssignmentNode("a",
						                        BinaryExpressionNode.createAdd(new FunctionCallNode("foo",
						                                                                            new FunctionParametersNode()),
						                                                       new NumberNode(3))))
		));

		assertEquals(PREFIX + "t 1 := bar(1)" + NL +
				             "a = foo(read t 1)" + SUFFIX, SplitExpressionsTransformation.createTempVars(
				new StatementListNode()
						.add(new AssignmentNode("a", new FunctionCallNode("foo",
						                                                  new FunctionParametersNode()
								                                                  .add(new FunctionCallNode("bar",
								                                                                            new FunctionParametersNode()
										                                                                            .add(new NumberNode(1)))))))
		));

		assertEquals(PREFIX + "t 1 := 1 - 2" + NL +
				             "a = foo(read t 1)" + SUFFIX, SplitExpressionsTransformation.createTempVars(
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
				             + "a := read t 1 + read t 2" + SUFFIX, SplitExpressionsTransformation.createTempVars(
				new StatementListNode()
						.add(new VarDeclarationNode("a",
						                            BinaryExpressionNode.createAdd(BinaryExpressionNode.createMultiply(new NumberNode(1),
						                                                                                               new NumberNode(2)),
						                                                           BinaryExpressionNode.createMultiply(new NumberNode(3),
						                                                                                               new NumberNode(4)))))
		));

		assertEquals(PREFIX + "t 1 := 1 + read b" + NL
				             + "t 2 := 3 * 4" + NL
				             + "a := foo(read t 1, read t 2)" + SUFFIX, SplitExpressionsTransformation.createTempVars(
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

	// Utils ==================================================================

	private static void assertEquals(String expected, StatementListNode root) {
		final TestStringOutput output = new TestStringOutput();
		new CodePrinter().print(root, output);
		Assert.assertEquals(expected, output.toString());
	}

	// Inner Classes ==========================================================

	private static class TestStringOutput implements StringOutput {
		private final StringBuilder buffer = new StringBuilder();

		@Override
		public void print(@NotNull String s) {
			buffer.append(s);
		}

		@Override
		public void println() {
			print("\n");
		}

		@Override
		public String toString() {
			return buffer.toString();
		}
	}
}