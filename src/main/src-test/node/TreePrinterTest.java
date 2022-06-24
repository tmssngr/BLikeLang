package node;

import de.regnis.b.node.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Thomas Singer
 */
public class TreePrinterTest {

	@Test
	public void testPrint() {
		final TreePrinter printer = new TreePrinter();

		Assert.assertEquals(List.of("operator +",
		                            "+- read var a",
		                            "+- literal 5"), printer.getStrings(BinaryExpressionNode.createAdd(new VarReadNode("a"), new NumberNode(5))));
		Assert.assertEquals(List.of("a =",
		                            "+- literal 1"), printer.getStrings(new AssignmentNode("a", new NumberNode(1))));
		Assert.assertEquals(List.of("a :=",
		                            "+- literal 1"), printer.getStrings(new VarDeclarationNode("a", new NumberNode(1))));
	}
}
