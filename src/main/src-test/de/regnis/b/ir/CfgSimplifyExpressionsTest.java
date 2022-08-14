package de.regnis.b.ir;

import de.regnis.b.AstFactory;
import de.regnis.b.DetermineTypesTransformation;
import de.regnis.b.ast.DeclarationList;
import de.regnis.b.out.StringStringOutput;
import org.junit.Assert;
import org.junit.Test;

import static de.regnis.utils.Utils.notNull;

/**
 * @author Thomas Singer
 */
public class CfgSimplifyExpressionsTest {

	// Accessing ==============================================================

	@Test
	public void testKeep() {
		test("""
				     void print(int a) {
				     }
				     void printHex4(int i) {
				       int chr = 'A' + i - 10
				       print(chr);
				     }
				     void main() {
				       printHex4(10)
				     }""",
		     "printHex4",
		     """
				     printHex4_start:
				         v0 := 65 + p0 - 10
				         print(v0)
				     printHex4_exit:
				         return
				     """,
		     """
				     printHex4_start:
				         v0 := p0 - 55
				         print(v0)
				     printHex4_exit:
				         return
				     """);
	}

	// Utils ==================================================================

	private void test(String input, String function, String expectedBefore, String expectedAfter) {
		DeclarationList root = AstFactory.parseString(input);
		root = DetermineTypesTransformation.transform(root, new StringStringOutput());

		final ControlFlowGraph graph = new ControlFlowGraph(notNull(root.getFunction(function)));
		Assert.assertEquals(expectedBefore, toString(graph));

		CfgSimplifyExpressions.transform(graph);
		Assert.assertEquals(expectedAfter, toString(graph));
	}

	private String toString(ControlFlowGraph graph) {
		return ControlFlowGraphPrinter.print(graph, new StringStringOutput()).toString();
	}
}
