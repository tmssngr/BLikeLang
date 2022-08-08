package de.regnis.b;

import de.regnis.b.ast.*;
import org.junit.Test;

/**
 * @author Thomas Singer
 */
public final class ConstantFoldingTransformationTest extends AbstractTransformationTest {

	// Accessing ==============================================================

	@Test
	public void testUnchangedCases() {
		assertUnchanged("1");
		assertUnchanged("b");
		assertUnchanged("b + 1");
		assertUnchanged("b - 4");
		assertUnchanged("4 - b");
		assertUnchanged("b * 2");
		assertUnchanged("b / 2");
		assertUnchanged("2500 / a");
		assertUnchanged("foo()");
		assertUnchanged("foo(1)");
		assertUnchanged("foo(b)");

		assertUnchanged("b < 1");
		assertUnchanged("b <= 1");
		assertUnchanged("b == 1");
		assertUnchanged("b >= 1");
		assertUnchanged("b > 1");
		assertUnchanged("b != 1");

		assertUnchanged("2 < c");
		assertUnchanged("2 <= c");
		assertUnchanged("2 == c");
		assertUnchanged("2 >= c");
		assertUnchanged("2 > c");
		assertUnchanged("2 != c");

		assertUnchanged("b < c");
		assertUnchanged("b <= c");
		assertUnchanged("b == c");
		assertUnchanged("b >= c");
		assertUnchanged("b > c");
		assertUnchanged("b != c");
	}

	@Test
	public void testIntentionallyUnchangedBecauseOfSplitExpressionTransformation() {
		assertStatement("a = foo(1 + 2)",
		                "a = foo(1 + 2);");
		assertStatement("foo(1 + 2)",
		                "foo(1 + 2);");
		assertStatement("return 1 + 2",
		                "return 1 + 2;");
		assertStatement("""
				                if 1 + 2
				                  {
				                  }""", "if (1 + 2) { }");
	}

	@Test
	public void testSwapArguments() {
		assertChanged("b + 1", "1 + b");
		assertChanged("b * 2", "2 * b");
	}

	@Test
	public void testNumericOperands() {
		assertChanged("3", "1 + 2");
		assertChanged("b", "b + 0");
		assertChanged("b", "0 + b");
		assertChanged("b", "b - 0");
		assertChanged("20", "10 * 2");
		assertChanged("0", "b * 0");
		assertChanged("0", "0 * b");
		assertChanged("b", "b * 1");
		assertChanged("b", "1 * b");
		assertChanged("7", "15 / 2");
		assertChanged("b", "b / 1");
		assertChanged("3", "15 % 4");
		assertChanged("0", "b - b");
		assertChanged("2", "3 & 2");
		assertChanged("6", "4 | 2");
		assertChanged("1", "3 ^ 2");
		assertChanged("0", "a & 0");
		assertChanged("0", "0 & a");
		assertChanged("a", "a & a");
		assertChanged("a", "a | a");
		assertChanged("0", "a ^ a");
		assertChanged("1", "1 << 0");
		assertChanged("4", "1 << 2");
		assertChanged("b", "b << 0");
		assertChanged("b", "b >> 0");
	}

	@Test
	public void testComparisonReplacements() {
		assertChanged("1", "1 < 2");
		assertChanged("1", "1 <= 2");
		assertChanged("0", "1 == 2");
		assertChanged("0", "1 >= 2");
		assertChanged("0", "1 > 2");
		assertChanged("1", "1 != 2");

		assertChanged("0", "b < b");
		assertChanged("1", "b <= b");
		assertChanged("1", "b == b");
		assertChanged("1", "b >= b");
		assertChanged("0", "b > b");
		assertChanged("0", "b != b");
	}

	@Test
	public void testBooleanReplacements() {
		assertChanged("1", "false == false");
		assertChanged("0", "false == true");
		assertChanged("0", "true == false");
		assertChanged("1", "true == true");

		assertChanged("0", "false != false");
		assertChanged("1", "false != true");
		assertChanged("1", "true != false");
		assertChanged("0", "true != true");
	}

	// Utils ==================================================================

	private void assertUnchanged(String expression) {
		assertStatement("a = " + expression,
		                "a = " + expression + ";");
		// local variable
		assertStatement("a := " + expression,
		                "var a = " + expression + ";");
		// global variable
		assertCode("a := " + expression + "\n",
		           "var a = " + expression + ";");
	}

	private void assertChanged(String expectedExpression, String initialExpression) {
		assertStatement("a = " + expectedExpression,
		                "a = " + initialExpression + ";");
		assertStatement("a := " + expectedExpression,
		                "var a = " + initialExpression + ";");
	}

	private void assertStatement(String expectedStatement, String initialStatement) {
		assertCode(PREFIX + expectedStatement + SUFFIX, PREFIX + initialStatement + SUFFIX);
	}

	private void assertCode(String expectedCode, String initialCode) {
		final DeclarationList root = AstFactory.parseString(initialCode);
		assertEquals(expectedCode, ConstantFoldingTransformation.transform(root));
	}
}
