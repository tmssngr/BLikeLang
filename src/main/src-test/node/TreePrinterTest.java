package node;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Thomas Singer
 */
public class TreePrinterTest {

	@Test
	public void testPrint() {
		final TreePrinter printer = new TreePrinter();

		Assert.assertEquals(List.of("operator +",
		                            "+- read var a",
		                            "+- literal 5"), printer.getStrings(BinaryExpressionNode.createAdd(new VarReadNode("a", 0, 0), new NumberNode(5))));
		Assert.assertEquals(List.of("operator +",
		                            "+- read var a",
		                            "+- literal 5"), printer.getStrings(new AssignmentNode("a", new NumberNode(1), 0, 0)));
	}
}
