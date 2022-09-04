package de.regnis.b.ir;

import de.regnis.b.ast.AstFactory;
import de.regnis.b.ast.DeclarationList;
import de.regnis.b.ast.FuncDeclaration;
import de.regnis.b.ast.transformation.DetermineTypesTransformation;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringStringOutput;
import de.regnis.utils.Utils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Thomas Singer
 */
public class RegisterAllocationTest {

	// Constants ==============================================================

	private static final boolean DEBUG = false;

	// Accessing ==============================================================

	@Test
	public void testNoVar() {
		final DeclarationList root = DetermineTypesTransformation.transform(AstFactory.parseString(
				"""
						void print(int chr) {
						}

						void main() {
						  print(192);
						}"""
		), new StringStringOutput());

		assertEquals("""
				             void print(int p0) {
				             }
				             void main() {
				               print(192)
				             }
				             """, CodePrinter.print(root));

		final FuncDeclaration function = Utils.notNull(root.getFunction("print"));
		final ControlFlowGraph graph = new ControlFlowGraph(function);

		assertEquals("""
				             print_start:
				             print_exit:
				                 return
				             """, ControlFlowGraphPrinter.print(graph, new StringStringOutput()).toString());

		assertEquals("""
				             print_start:
				                 // []
				             print_exit:
				                 return
				             """,
		             ControlFlowGraphVarUsageDetector
				             .detectVarUsages(graph)
				             .createPrinter(new StringStringOutput())
				             .print()
				             .toString());

		final RegisterAllocation.Result varToRegister = RegisterAllocation.run(graph);
		if (DEBUG) {
			Utils.print(varToRegister.debugToMap(), "expected.put(\"", "\", ", ");\n");
		}

		assertEquals(1, varToRegister.parameterCount);
		assertEquals(0, varToRegister.returnVarCount);
		assertEquals(0, varToRegister.localVarRegisterCount);

		final Map<String, Integer> expected = new HashMap<>();
		expected.put("p0", 0);
		assertEquals(expected, varToRegister.debugToMap());
	}

	@Test
	public void testSingleVar() {
		final DeclarationList root = DetermineTypesTransformation.transform(AstFactory.parseString(
				"""
						int inc(int i) {
							return i + 1
						}

						void main() {
						    int val = inc(0)
						}"""
		), new StringStringOutput());

		assertEquals("""
				             int inc(int p0) {
				               return p0 + 1
				             }
				             void main() {
				               v0 := inc(0)
				             }
				             """, CodePrinter.print(root));

		final FuncDeclaration function = Utils.notNull(root.getFunction("inc"));
		final ControlFlowGraph graph = new ControlFlowGraph(function);

		assertEquals("""
				             inc_start:
				                 result = p0 + 1
				             inc_exit:
				                 return
				             """, ControlFlowGraphPrinter.print(graph, new StringStringOutput()).toString());

		assertEquals("""
				             inc_start:
				                 // [p0]
				                 result = p0 + 1
				                 // [result]
				             inc_exit:
				                 return
				             """,
		             ControlFlowGraphVarUsageDetector
				             .detectVarUsages(graph)
				             .createPrinter(new StringStringOutput())
				             .print()
				             .toString());

		final RegisterAllocation.Result varToRegister = RegisterAllocation.run(graph);
		if (DEBUG) {
			Utils.print(varToRegister.debugToMap(), "expected.put(\"", "\", ", ");\n");
		}

		assertEquals(1, varToRegister.parameterCount);
		assertEquals(1, varToRegister.returnVarCount);
		assertEquals(0, varToRegister.localVarRegisterCount);

		final Map<String, Integer> expected = new HashMap<>();
		expected.put("p0", 0);
		expected.put("result", 0);
		assertEquals(expected, varToRegister.debugToMap());
	}

	@Test
	public void testTwoVars() {
		final DeclarationList root = DetermineTypesTransformation.transform(AstFactory.parseString(
				"""
						int add(int a, int b) {
							return a + b
						}

						void main() {
						    int val = add(1, 2)
						}"""
		), new StringStringOutput());

		assertEquals("""
				             int add(int p0, int p1) {
				               return p0 + p1
				             }
				             void main() {
				               v0 := add(1, 2)
				             }
				             """, CodePrinter.print(root));

		final FuncDeclaration function = Utils.notNull(root.getFunction("add"));
		final ControlFlowGraph graph = new ControlFlowGraph(function);

		assertEquals("""
				             add_start:
				                 result = p0 + p1
				             add_exit:
				                 return
				             """, ControlFlowGraphPrinter.print(graph, new StringStringOutput()).toString());

		assertEquals("""
				             add_start:
				                 // [p0, p1]
				                 result = p0 + p1
				                 // [result]
				             add_exit:
				                 return
				             """,
		             ControlFlowGraphVarUsageDetector
				             .detectVarUsages(graph)
				             .createPrinter(new StringStringOutput())
				             .print()
				             .toString());

		final RegisterAllocation.Result varToRegister = RegisterAllocation.run(graph);
		if (DEBUG) {
			Utils.print(varToRegister.debugToMap(), "expected.put(\"", "\", ", ");\n");
		}

		assertEquals(2, varToRegister.parameterCount);
		assertEquals(1, varToRegister.returnVarCount);
		assertEquals(0, varToRegister.localVarRegisterCount);

		final Map<String, Integer> expected = new HashMap<>();
		expected.put("p0", 0);
		expected.put("p1", 1);
		expected.put("result", 0);
		assertEquals(expected, varToRegister.debugToMap());
	}

	@Test
	public void testMore() {
		final DeclarationList root = DetermineTypesTransformation.transform(AstFactory.parseString(
				"""
						int calc(int a, int b) {
							int a2 = a * a
							int b2 = b * b
							return a2 + b2 + a - b
						}

						void main() {
						    int val = calc(1, 2)
						}"""
		), new StringStringOutput());

		assertEquals("""
				             int calc(int p0, int p1) {
				               v0 := p0 * p0
				               v1 := p1 * p1
				               return v0 + v1 + p0 - p1
				             }
				             void main() {
				               v0 := calc(1, 2)
				             }
				             """, CodePrinter.print(root));

		final FuncDeclaration function = Utils.notNull(root.getFunction("calc"));
		final ControlFlowGraph graph = new ControlFlowGraph(function);
		SplitExpressionsTransformation.transform(graph);
		assertEquals("""
				             calc_start:
				                 v0 := p0 * p0
				                 v1 := p1 * p1
				                 $1 := v0 + v1
				                 $2 := $1 + p0
				                 result = $2 - p1
				             calc_exit:
				                 return
				             """, ControlFlowGraphPrinter.print(graph, new StringStringOutput()).toString());

		assertEquals("""
				             calc_start:
				                 // [p0, p1]
				                 v0 := p0 * p0
				                 // [p0, p1, v0]
				                 v1 := p1 * p1
				                 // [p0, p1, v0, v1]
				                 $1 := v0 + v1
				                 // [$1, p0, p1]
				                 $2 := $1 + p0
				                 // [$2, p1]
				                 result = $2 - p1
				                 // [result]
				             calc_exit:
				                 return
				             """,
		             ControlFlowGraphVarUsageDetector
				             .detectVarUsages(graph)
				             .createPrinter(new StringStringOutput())
				             .print()
				             .toString());

		final RegisterAllocation.Result varToRegister = RegisterAllocation.run(graph);
		if (DEBUG) {
			Utils.print(varToRegister.debugToMap(), "expected.put(\"", "\", ", ");\n");
		}

		assertEquals(2, varToRegister.parameterCount);
		assertEquals(1, varToRegister.returnVarCount);
		assertEquals(2, varToRegister.localVarRegisterCount);

		final Map<String, Integer> expected = new HashMap<>();
		expected.put("p0", 0);
		expected.put("p1", 1);
		expected.put("v0", 2);
		expected.put("v1", 3);
		expected.put("$1", 2);
		expected.put("$2", 0);
		expected.put("result", 0);
		assertEquals(expected, varToRegister.debugToMap());
	}
}
