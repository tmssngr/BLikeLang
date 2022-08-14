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
public class CfgParameterToSimpleExpressionTest {

	// Accessing ==============================================================

	@Test
	public void testKeep() {
		test("""
				     void print(int a) {
				     }
				     void main() {
				       var a = 1 + 2 + 3
				       print(a);
				     }""",
		     "main",
		     """
				     main_start:
				         v0 := 1 + 2 + 3
				         print(v0)
				     main_exit:
				         return
				     """,
		     """
				     main_start:
				         v0 := 1 + 2 + 3
				         print(v0)
				     main_exit:
				         return
				     """);
	}

	@Test
	public void testCall() {
		test("""
				     void print(int a) {
				     }
				     int get10() {
				       return 10
				     }
				     void print2(int a, int b) {
				       print(a + b + get10())
				     }
				     void main() {
				       print2(1, 2);
				     }""",
		     "print2",
		     """
				     print2_start:
				         print(p0 + p1 + get10())
				     print2_exit:
				         return
				     """,
		     """
				     print2_start:
				         t0 := p0 + p1
				         t1 := get10()
				         t2 := t0 + t1
				         print(t2)
				     print2_exit:
				         return
				     """);
	}

	// Utils ==================================================================

	private void test(String input, String function, String expectedBefore, String expectedAfter) {
		DeclarationList root = AstFactory.parseString(input);
		root = DetermineTypesTransformation.transform(root, new StringStringOutput());

		final ControlFlowGraph graph = new ControlFlowGraph(notNull(root.getFunction(function)));
		Assert.assertEquals(expectedBefore, toString(graph));

		CfgParameterToSimpleExpression.transform(graph);
		Assert.assertEquals(expectedAfter, toString(graph));
	}

	private String toString(ControlFlowGraph graph) {
		return ControlFlowGraphPrinter.print(graph, new StringStringOutput()).toString();
	}
}
