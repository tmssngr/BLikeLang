package de.regnis.b.ir;

import de.regnis.b.ast.AstFactory;
import de.regnis.b.ast.transformation.DetermineTypesTransformation;
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
				         v0 := (65 + p0) - 10
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

	@Test
	public void test() {
		test("""
				     int calculate(int day, int month, int year) {
				       var h = year / 100
				       var v = year / 400
				       var z = (year + year/4 - h + v + 1) % 7
				       return z
				     }
				     void main() {
				       var a = calculate(18, 9, 2022)
				     }""",
		     "calculate",
		     """
				     calculate_start:
				         v0 := p2 / 100
				         v1 := p2 / 400
				         v2 := ((((p2 + (p2 / 4)) - v0) + v1) + 1) % 7
				         result = v2
				     calculate_exit:
				         return
				     """,
		     """
				     calculate_start:
				         v0 := p2 / 100
				         v1 := p2 / 400
				         v2 := ((((p2 + (p2 / 4)) - v0) + v1) + 1) % 7
				         result = v2
				     calculate_exit:
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
