package de.regnis.b.ir;

import de.regnis.b.ast.AstFactory;
import de.regnis.b.ast.transformation.DetermineTypesTransformation;
import de.regnis.b.ast.transformation.ReplaceModifyAssignmentWithBinaryExpressionTransformation;
import de.regnis.b.ast.DeclarationList;
import de.regnis.b.ast.FuncDeclaration;
import de.regnis.b.out.StringStringOutput;
import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static de.regnis.utils.Utils.notNull;
import static org.junit.Assert.assertEquals;

/**
 * @author Thomas Singer
 */
public class StaticSingleAssignmentFactoryTest {

	// Accessing ==============================================================

	@Test
	public void testSimple() {
		final ControlFlowGraph graph = test("""
				                                    void print(int a) {
				                                    }
				                                    void main() {
				                                      int a = 1;
				                                      int b = 2;
				                                      a = a + b;
				                                      b = 1;
				                                      int c = a;
				                                      a = b + c;
				                                      print(a)
				                                    }""",
		                                    "main",
		                                    """
				                                    main_start:
				                                        v0 := 1
				                                        v1 := 2
				                                        v0 = v0 + v1
				                                        v1 = 1
				                                        v2 := v0
				                                        v0 = v1 + v2
				                                        print(v0)
				                                    main_exit:  // main_start
				                                        return
				                                    """,
		                                    """
				                                    main_start:
				                                        v0_0 := 1
				                                        v1_0 := 2
				                                        v0_1 := v0_0 + v1_0
				                                        v1_1 := 1
				                                        v2_0 := v0_1
				                                        v0_2 := v1_1 + v2_0
				                                        print(v0_2)
				                                    main_exit:  // main_start
				                                        return
				                                    """);
		SsaConstantDetection.transform(graph);

		assertEquals("""
				             main_start:
				                 print(4)
				             main_exit:  // main_start
				                 return
				             """, toString(graph));
	}

	@Test
	public void testIf1() {
		final ControlFlowGraph graph = test("""
				                                    int test(int a) {
				                                      int b = a;
				                                      if (a < 0) {
				                                        a = 0 - a;
				                                        if (a % 2 == 1) {
				                                          b = b + 1;
				                                        }
				                                      }
				                                      a += 2;
				                                      return b;
				                                    }
				                                    void main() {
				                                      int result = test(10);
				                                    }""",
		                                    "test",
		                                    """
				                                    test_start:
				                                        v0 := p0
				                                    test_if_1:  // test_start
				                                        if p0 < 0
				                                        if ! goto test_else_1
				                                    test_then_1:  // test_if_1
				                                        p0 = 0 - p0
				                                    test_if_2:  // test_then_1
				                                        if p0 % 2 == 1
				                                        if ! goto test_else_2
				                                    test_then_2:  // test_if_2
				                                        v0 = v0 + 1
				                                    test_after_if_2:  // test_then_2, test_else_2
				                                    test_after_if_1:  // test_after_if_2, test_else_1
				                                        p0 = p0 + 2
				                                        result = v0
				                                        goto test_exit

				                                    test_else_2:  // test_if_2
				                                        goto test_after_if_2

				                                    test_else_1:  // test_if_1
				                                        goto test_after_if_1

				                                    test_exit:  // test_after_if_1
				                                        return
				                                    """,
		                                    """
				                                    test_start:
				                                        v0_0 := p0_0
				                                    test_if_1:  // test_start
				                                        if p0_0 < 0
				                                        if ! goto test_else_1
				                                    test_then_1:  // test_if_1
				                                        p0_1 := 0 - p0_0
				                                    test_if_2:  // test_then_1
				                                        if p0_1 % 2 == 1
				                                        if ! goto test_else_2
				                                    test_then_2:  // test_if_2
				                                        v0_2 := v0_0 + 1
				                                    test_after_if_2:  // test_then_2, test_else_2
				                                        v0_3 := phi (v0_2, v0_0)
				                                    test_after_if_1:  // test_after_if_2, test_else_1
				                                        p0_2 := phi (p0_1, p0_0)
				                                        v0_1 := phi (v0_3, v0_0)
				                                        p0_3 := p0_2 + 2
				                                        result := v0_1
				                                        goto test_exit

				                                    test_else_2:  // test_if_2
				                                        goto test_after_if_2

				                                    test_else_1:  // test_if_1
				                                        goto test_after_if_1

				                                    test_exit:  // test_after_if_1
				                                        return
				                                    """);
		removeUnusedVars("p0_3 p0_2", graph);

		assertEquals("""
				             test_start:
				                 v0_0 := p0_0
				             test_if_1:  // test_start
				                 if p0_0 < 0
				                 if ! goto test_else_1
				             test_then_1:  // test_if_1
				                 p0_1 := 0 - p0_0
				             test_if_2:  // test_then_1
				                 if p0_1 % 2 == 1
				                 if ! goto test_else_2
				             test_then_2:  // test_if_2
				                 v0_2 := v0_0 + 1
				             test_after_if_2:  // test_then_2, test_else_2
				                 v0_3 := phi (v0_2, v0_0)
				             test_after_if_1:  // test_after_if_2, test_else_1
				                 v0_1 := phi (v0_3, v0_0)
				                 result := v0_1
				                 goto test_exit

				             test_else_2:  // test_if_2
				                 goto test_after_if_2

				             test_else_1:  // test_if_1
				                 goto test_after_if_1

				             test_exit:  // test_after_if_1
				                 return
				             """, toString(graph));

		SsaRemovePhiFunctions.transform(graph);

		assertEquals("""
				             test_start:
				                 v0_0 := p0_0
				             test_if_1:  // test_start
				                 if p0_0 < 0
				                 if ! goto test_else_1
				             test_then_1:  // test_if_1
				                 p0_1 := 0 - p0_0
				             test_if_2:  // test_then_1
				                 if p0_1 % 2 == 1
				                 if ! goto test_else_2
				             test_then_2:  // test_if_2
				                 v0_1 := v0_0 + 1
				             test_after_if_2:  // test_then_2, test_else_2
				             test_after_if_1:  // test_after_if_2, test_else_1
				                 result := v0_1
				                 goto test_exit

				             test_else_2:  // test_if_2
				                 v0_1 := v0_0
				                 goto test_after_if_2

				             test_else_1:  // test_if_1
				                 v0_1 := v0_0
				                 goto test_after_if_1

				             test_exit:  // test_after_if_1
				                 return
				             """, toString(graph));

		graph.compact();

		assertEquals("""
				             test_start:
				                 v0_0 := p0_0
				             test_if_1:  // test_start
				                 if p0_0 < 0
				                 if ! goto test_else_1
				             test_then_1:  // test_if_1
				                 p0_1 := 0 - p0_0
				             test_if_2:  // test_then_1
				                 if p0_1 % 2 == 1
				                 if ! goto test_else_2
				             test_then_2:  // test_if_2
				                 v0_1 := v0_0 + 1
				             test_after_if_1:  // test_else_1, test_then_2, test_else_2
				                 result := v0_1
				                 goto test_exit

				             test_else_2:  // test_if_2
				                 v0_1 := v0_0
				                 goto test_after_if_1

				             test_else_1:  // test_if_1
				                 v0_1 := v0_0
				                 goto test_after_if_1

				             test_exit:  // test_after_if_1
				                 return
				             """, toString(graph));
	}

	@Test
	public void testIf2() {
		test("""
				     int test(int a) {
				       int b = a;
				       if (a > 10) {
				         b = 0;
				         a -= 1;
				       }
				       a += 2;
				       return b;
				     }
				     void main() {
				       int result = test(10);
				     }""",
		     "test",
		     """
				     test_start:
				         v0 := p0
				     test_if_1:  // test_start
				         if p0 > 10
				         if ! goto test_else_1
				     test_then_1:  // test_if_1
				         v0 = 0
				         p0 = p0 - 1
				     test_after_if_1:  // test_then_1, test_else_1
				         p0 = p0 + 2
				         result = v0
				         goto test_exit

				     test_else_1:  // test_if_1
				         goto test_after_if_1

				     test_exit:  // test_after_if_1
				         return
				     """,
		     """
				     test_start:
				         v0_0 := p0_0
				     test_if_1:  // test_start
				         if p0_0 > 10
				         if ! goto test_else_1
				     test_then_1:  // test_if_1
				         v0_1 := 0
				         p0_1 := p0_0 - 1
				     test_after_if_1:  // test_then_1, test_else_1
				         p0_2 := phi (p0_1, p0_0)
				         v0_2 := phi (v0_1, v0_0)
				         p0_3 := p0_2 + 2
				         result := v0_2
				         goto test_exit

				     test_else_1:  // test_if_1
				         goto test_after_if_1

				     test_exit:  // test_after_if_1
				         return
				     """);
	}

	@Test
	public void testIf3() {
		test("""
				     void main() {
				       int a = 1;
				       int b = 2;
				       if (a < b) {
				         a = b;
				       }
				       a += 1;
				     }""",
		     "main",
		     """
				     main_start:
				         v0 := 1
				         v1 := 2
				     main_if_1:  // main_start
				         if v0 < v1
				         if ! goto main_else_1
				     main_then_1:  // main_if_1
				         v0 = v1
				     main_after_if_1:  // main_then_1, main_else_1
				         v0 = v0 + 1
				         goto main_exit

				     main_else_1:  // main_if_1
				         goto main_after_if_1

				     main_exit:  // main_after_if_1
				         return
				     """,
		     """
				     main_start:
				         v0_0 := 1
				         v1_0 := 2
				     main_if_1:  // main_start
				         if v0_0 < v1_0
				         if ! goto main_else_1
				     main_then_1:  // main_if_1
				         v0_1 := v1_0
				     main_after_if_1:  // main_then_1, main_else_1
				         v0_2 := phi (v0_1, v0_0)
				         v0_3 := v0_2 + 1
				         goto main_exit

				     main_else_1:  // main_if_1
				         goto main_after_if_1

				     main_exit:  // main_after_if_1
				         return
				     """);
	}

	@Test
	public void testWhile() {
		final ControlFlowGraph graph = test("""
				                                    void print(int a) {
				                                    }
				                                    void main() {
				                                      int a = 10
				                                      while (a > 0) {
				                                        print(a)
				                                        a -= 1
				                                      }
				                                    }""",
		                                    "main",
		                                    """
				                                    main_start:
				                                        v0 := 10
				                                    main_while_1:  // main_start, main_do_1
				                                        while v0 > 0
				                                    main_do_1:  // main_while_1
				                                        print(v0)
				                                        v0 = v0 - 1
				                                        goto main_while_1

				                                    main_after_while_1:  // main_while_1
				                                    main_exit:  // main_after_while_1
				                                        return
				                                    """,
		                                    """
				                                    main_start:
				                                        v0_0 := 10
				                                    main_while_1:  // main_start, main_do_1
				                                        v0_1 := phi (v0_0, v0_2)
				                                        while v0_1 > 0
				                                    main_do_1:  // main_while_1
				                                        print(v0_1)
				                                        v0_2 := v0_1 - 1
				                                        goto main_while_1

				                                    main_after_while_1:  // main_while_1
				                                    main_exit:  // main_after_while_1
				                                        return
				                                    """);
		// v0_0 must not be considered as inline-able constant
		SsaConstantDetection.transform(graph);

		assertEquals("""
				             main_start:
				                 v0_0 := 10
				             main_while_1:  // main_start, main_do_1
				                 v0_1 := phi (v0_0, v0_2)
				                 while v0_1 > 0
				             main_do_1:  // main_while_1
				                 print(v0_1)
				                 v0_2 := v0_1 - 1
				                 goto main_while_1

				             main_after_while_1:  // main_while_1
				             main_exit:  // main_after_while_1
				                 return
				             """, toString(graph));
	}

	@Test
	public void testCall() {
		final ControlFlowGraph graph = test("""
				                                    void print(int chr) {
				                                    }

				                                    void printHex4(int i) {
				                                      i = i & 15;
				                                      int chr = 0;
				                                      if (i < 10) {
				                                        chr = i + 48;
				                                      }
				                                      else {
				                                        chr = i - 10 + 65;
				                                      }
				                                      print(chr);
				                                    }

				                                    void printHex8(int i) {
				                                      printHex4(i >> 4);
				                                      printHex4(i);
				                                    }

				                                    void printHex16(int i) {
				                                      printHex8(i >> 8);
				                                      printHex8(i);
				                                    }

				                                    void main() {
				                                      printHex16(192);
				                                    }""",
		                                    "printHex4",
		                                    """
				                                    printHex4_start:
				                                        p0 = p0 & 15
				                                        v0 := 0
				                                    printHex4_if_1:  // printHex4_start
				                                        if p0 < 10
				                                        if ! goto printHex4_else_1
				                                    printHex4_then_1:  // printHex4_if_1
				                                        v0 = p0 + 48
				                                    printHex4_after_if_1:  // printHex4_then_1, printHex4_else_1
				                                        print(v0)
				                                        goto printHex4_exit

				                                    printHex4_else_1:  // printHex4_if_1
				                                        v0 = p0 - 10 + 65
				                                        goto printHex4_after_if_1

				                                    printHex4_exit:  // printHex4_after_if_1
				                                        return
				                                    """,
		                                    """
				                                    printHex4_start:
				                                        p0_1 := p0_0 & 15
				                                        v0_0 := 0
				                                    printHex4_if_1:  // printHex4_start
				                                        if p0_1 < 10
				                                        if ! goto printHex4_else_1
				                                    printHex4_then_1:  // printHex4_if_1
				                                        v0_1 := p0_1 + 48
				                                    printHex4_after_if_1:  // printHex4_then_1, printHex4_else_1
				                                        v0_3 := phi (v0_1, v0_2)
				                                        print(v0_3)
				                                        goto printHex4_exit

				                                    printHex4_else_1:  // printHex4_if_1
				                                        v0_2 := p0_1 - 10 + 65
				                                        goto printHex4_after_if_1

				                                    printHex4_exit:  // printHex4_after_if_1
				                                        return
				                                    """);
		removeUnusedVars("v0_0", graph);

		assertEquals("""
				             printHex4_start:
				                 p0_1 := p0_0 & 15
				             printHex4_if_1:  // printHex4_start
				                 if p0_1 < 10
				                 if ! goto printHex4_else_1
				             printHex4_then_1:  // printHex4_if_1
				                 v0_1 := p0_1 + 48
				             printHex4_after_if_1:  // printHex4_then_1, printHex4_else_1
				                 v0_3 := phi (v0_1, v0_2)
				                 print(v0_3)
				                 goto printHex4_exit

				             printHex4_else_1:  // printHex4_if_1
				                 v0_2 := p0_1 - 10 + 65
				                 goto printHex4_after_if_1

				             printHex4_exit:  // printHex4_after_if_1
				                 return
				             """, toString(graph));

		SsaRemovePhiFunctions.transform(graph);

		assertEquals("""
				             printHex4_start:
				                 p0_1 := p0_0 & 15
				             printHex4_if_1:  // printHex4_start
				                 if p0_1 < 10
				                 if ! goto printHex4_else_1
				             printHex4_then_1:  // printHex4_if_1
				                 v0_3 := p0_1 + 48
				             printHex4_after_if_1:  // printHex4_then_1, printHex4_else_1
				                 print(v0_3)
				                 goto printHex4_exit

				             printHex4_else_1:  // printHex4_if_1
				                 v0_3 := p0_1 - 10 + 65
				                 goto printHex4_after_if_1

				             printHex4_exit:  // printHex4_after_if_1
				                 return
				             """, toString(graph));

		SsaToModifyAssignments.transform(graph);

		assertEquals("""
				             printHex4_start:
				                 p0_1 := p0_0
				                 p0_1 &= 15
				             printHex4_if_1:  // printHex4_start
				                 if p0_1 < 10
				                 if ! goto printHex4_else_1
				             printHex4_then_1:  // printHex4_if_1
				                 v0_3 := p0_1
				                 v0_3 += 48
				             printHex4_after_if_1:  // printHex4_then_1, printHex4_else_1
				                 print(v0_3)
				                 goto printHex4_exit

				             printHex4_else_1:  // printHex4_if_1
				                 v0_3 := p0_1
				                 v0_3 -= 10
				                 v0_3 += 65
				                 goto printHex4_after_if_1

				             printHex4_exit:  // printHex4_after_if_1
				                 return
				             """, toString(graph));
	}

	// Utils ==================================================================

	private ControlFlowGraph test(String source, String functionName, String expectedCfg, String expectedSsaCfg) {
		DeclarationList root = AstFactory.parseString(source);
		root = DetermineTypesTransformation.transform(root, new StringStringOutput());
		root = ReplaceModifyAssignmentWithBinaryExpressionTransformation.transform(root);
		final FuncDeclaration func = notNull(root.getFunction(functionName));
		final ControlFlowGraph graph = new ControlFlowGraph(func);
		assertEquals(expectedCfg, toString(graph));

		StaticSingleAssignmentFactory.transform(graph);

		final String actual = toString(graph);
		assertEquals(expectedSsaCfg, actual);
		return graph;
	}

	private void removeUnusedVars(String expectedVars, ControlFlowGraph graph) {
		final StringBuilder buffer = new StringBuilder();
		buffer.append("removed:");
		while (true) {
			final Set<String> unusedVariables = SsaUnusedVarDetector.detectUnusedVariables(graph);
			if (unusedVariables.isEmpty()) {
				break;
			}

			buffer.append(" ");
			Utils.appendCommaSeparated(Utils.toSortedList(unusedVariables), buffer);
			SsaSearchAndReplace.remove(graph, unusedVariables);
		}

		assertEquals("removed: " + expectedVars, buffer.toString());
	}

	private String toString(ControlFlowGraph graph) {
		final ControlFlowGraphPrinter printer = new ControlFlowGraphPrinter(graph, new StringStringOutput()) {
			@NotNull
			@Override
			protected String getLabelText(AbstractBlock block) {
				final StringBuilder buffer = new StringBuilder();
				buffer.append(super.getLabelText(block));

				final List<AbstractBlock> prevBlocks = block.getPrevBlocks();
				if (prevBlocks.size() > 0) {
					buffer.append("  // ");
					Utils.appendCommaSeparated(Utils.convert(prevBlocks, new Function<>() {
						@Override
						public String apply(AbstractBlock block) {
							return block.label;
						}
					}), buffer);
				}

				return buffer.toString();
			}
		};
		return printer.print().toString();
	}
}
