package node;

import de.regnis.b.node.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Thomas Singer
 */
public class CodePrinterTest {

	@Test
	public void testDeclaration() {
		final TestStringOutput output = new TestStringOutput();
		new CodePrinter().print(
				new DeclarationList()
						.add(new GlobalVarDeclaration(new VarDeclarationNode("a", new NumberNode(0))))
						.add(new FunctionDeclaration("int", "sqr",
						                             new FunctionDeclarationParameters()
								                             .add(new FunctionDeclarationParameter("int", "x")),
						                             new StatementListNode()
								                             .add(new ReturnStatement(
										                             BinaryExpressionNode
												                             .createMultiply(new VarReadNode("x"),
												                                             new VarReadNode("x")))))),
				output);
		Assert.assertEquals("a := 0\n" +
				                    "int sqr(int x) {\n" +
				                    "\treturn x * x\n" +
				                    "}\n", output.toString());
	}
}
