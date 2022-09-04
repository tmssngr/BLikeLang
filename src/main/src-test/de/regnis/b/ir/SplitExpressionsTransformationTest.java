package de.regnis.b.ir;

import de.regnis.b.ast.AstFactory;
import de.regnis.b.ast.DeclarationList;
import de.regnis.b.ast.transformation.DetermineTypesTransformation;
import de.regnis.b.out.StringStringOutput;
import de.regnis.utils.Utils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Thomas Singer
 */
public class SplitExpressionsTransformationTest {

	// Accessing ==============================================================

	@Test
	public void testKeepAsIs() {
		assertEquals("""
				             int get() {
				                return 1
				             }
				             int get1(int a) {
				                return a
				             }
				             void main() {
				                var a = 1
				                var b = 2
				                var c = 0
				                a = b
				                c = 1 + 2
				                c = get()
				                c = get1(1)
				                c = get1(a)
				             }""",
		             """
				             main_start:
				                 v0 := 1
				                 v1 := 2
				                 v2 := 0
				                 v0 = v1
				                 v2 = 1 + 2
				                 v2 = get()
				                 v2 = get1(1)
				                 v2 = get1(v0)
				             main_exit:
				                 return
				             """);
	}

	@Test
	public void testSingleExtraction() {
		assertEquals("""
				             void main() {
				                var a = 1 + 2 + 3
				                var b = (1 + 2) + 3
				                var c = false == (2 < 3)
				             }""",
		             """
				             main_start:
				                 $1 := 1 + 2
				                 v0 := $1 + 3
				                 $2 := 1 + 2
				                 v1 := $2 + 3
				                 $3 := 2 < 3
				                 v2 := 0 == $3
				             main_exit:
				                 return
				             """);
		assertEquals("""
				             int foo() {
				                return 0
				             }
				             void main() {
				                var a = foo() + 3
				             }""",
		             """
				             main_start:
				                 $1 := foo()
				                 v0 := $1 + 3
				             main_exit:
				                 return
				             """);
		assertEquals("""
				             int foo(int a) {
				                return a
				             }
				             int bar(int a) {
				                return a
				             }
				             void main() {
				                var a = foo(bar(1))
				                var b = foo(1 - 2)
				             }""",
		             """
				             main_start:
				                 $1 := bar(1)
				                 v0 := foo($1)
				                 $2 := 1 - 2
				                 v1 := foo($2)
				             main_exit:
				                 return
				             """);
	}

	@Test
	public void testMultipleExtractions() {
		assertEquals("""
				             void main() {
				                var a = 1 * 2 + 3 * 4
				             }""",
		             """
				             main_start:
				                 $1 := 1 * 2
				                 $2 := 3 * 4
				                 v0 := $1 + $2
				             main_exit:
				                 return
				             """);
		assertEquals("""
				             int foo(int a, int b) {
				                return a + b
				             }
				             void main() {
				                var b = 1
				                var a = foo(1 + b, 3 * 4)
				             }""",
		             """
				             main_start:
				                 v0 := 1
				                 $1 := 1 + v0
				                 $2 := 3 * 4
				                 v1 := foo($1, $2)
				             main_exit:
				                 return
				             """);
		assertEquals("""
				             int call(int a) {
				                return a
				             }
				             void main() {
				                call(10 + 2 * 3)
				             }""",
		             """
				             main_start:
				                 $1 := 2 * 3
				                 $2 := 10 + $1
				                 call($2)
				             main_exit:
				                 return
				             """);
	}

	@Test
	public void testDefineType() {
		assertEquals("""
				             int call(int a) {
				               return a;
				             }
				             void main() {
				               var a = 10 + 2 * 3 + call(1);
				             }""",
		             """
				             main_start:
				                 $1 := 2 * 3
				                 $2 := 10 + $1
				                 $3 := call(1)
				                 v0 := $2 + $3
				             main_exit:
				                 return
				             """);
	}

	@Test
	public void testWhile() {
		assertEquals("""
				             void main() {
				               var a = 10
				               while (a + 1 > 0) {
				                 a -= 1
				               };
				             }""",
		             """
				             main_start:
				                 v0 := 10
				             main_while_1:
				                 $1 := v0 + 1
				                 while $1 > 0
				             main_do_1:
				                 v0 -= 1
				                 goto main_while_1
				                              
				             main_after_while_1:
				             main_exit:
				                 return
				             """);
	}

	// Utils ==================================================================

	private void assertEquals(String input, String expected) {
		DeclarationList root = AstFactory.parseString(input);
		root = DetermineTypesTransformation.transform(root, new StringStringOutput());
		root = DetermineTypesTransformation.transform(root, new StringStringOutput());
		final ControlFlowGraph graph = new ControlFlowGraph(Utils.notNull(root.getFunction("main")));
		SplitExpressionsTransformation.transform(graph);
		Assert.assertEquals(expected, toString(graph));
	}

	private String toString(ControlFlowGraph graph) {
		return ControlFlowGraphPrinter.print(graph, new StringStringOutput()).toString();
	}
}
