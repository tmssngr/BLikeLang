package de.regnis.b;

import de.regnis.b.node.BasicTypes;
import de.regnis.b.node.DeclarationList;
import de.regnis.b.node.InvalidTypeException;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringOutput;
import de.regnis.b.out.StringStringOutput;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Thomas Singer
 */
public final class DetermineTypesTransformationTest {

	// Constants ==============================================================

	private static final String NO_WARNING = "";
	private static final String VOID_MAIN = "void main() {\n}\n";

	// Accessing ==============================================================

	@Test
	public void testDeclaredFunctions() {
		assertSuccessfullyTransformed("void init() {\n" +
				                              "}\n" +
				                              "void main() {\n" +
				                              "  init()\n" +
				                              "}\n",
		                              NO_WARNING,
		                              "void init() {\n" +
				                              "}\n" +
				                              "void main() {\n" +
				                              "init();\n" +
				                              "}");

		assertSuccessfullyTransformed("void main() {\n" +
				                              "  init()\n" +
				                              "}\n" +
				                              "void init() {\n" +
				                              "}\n",
		                              NO_WARNING,
		                              "void main() {\n" +
				                              "init();\n" +
				                              "}\n" +
				                              "void init() {\n" +
				                              "}");

		assertUndeclaredException(DetermineTypesTransformation.errorUndeclaredFunction(2, 0, "init"),
		                          NO_WARNING,
		                          "void main() {\n" +
				                          "init();\n" +
				                          "}");
		assertUndeclaredException(DetermineTypesTransformation.errorUndeclaredFunction(2, 10, "init"),
		                          NO_WARNING,
		                          "void main() {\n" +
				                          "var foo = init();\n" +
				                          "}");
	}

	@Test
	public void testGlobalVars() {
		assertSuccessfullyTransformed("g0 : u8 = 1\n" +
				                              "g1 : i8 = -1\n" +
				                              "g2 : i8 = g0 + g1\n" +
				                              "g3 : boolean = false\n" +
				                              VOID_MAIN,
		                              DetermineTypesTransformation.warningUnusedVar(3, 4, "b") + "\n" +
				                              DetermineTypesTransformation.warningUnusedVar(4, 4, "booF") + "\n",
		                              "var a = 1;\n" +
				                              "var A = -1;\n" +
				                              "var b=a+A;\n" +
				                              "var booF = false;\n" +
				                              VOID_MAIN);
	}

	@Test
	public void testLocalVars() {
		assertSuccessfullyTransformed("i16 add(i16 p0, i16 p1) {\n" +
				                              "  v0 : i16 = p0 + p1\n" +
				                              "  return v0\n" +
				                              "}\n" +
				                              "boolean getFalse() {\n" +
				                              "  v0 : boolean = false\n" +
				                              "  return v0\n" +
				                              "}\n" +
				                              VOID_MAIN,
		                              DetermineTypesTransformation.warningUnusedFunction(1, 4, "add") + "\n" +
				                              DetermineTypesTransformation.warningUnusedFunction(5, 8, "getFalse") + "\n",
		                              "int add(int a, int b) {\n" +
				                              "var sum = a + b;\n" +
				                              "return sum;\n" +
				                              "}\n" +
				                              "boolean getFalse() {" +
				                              "boolean v = false;" +
				                              "return v;" +
				                              "}\n" +
				                              VOID_MAIN);
	}

	@Test
	public void testReturn() {
		assertSuccessfullyTransformed("boolean isEqual(i16 p0, i16 p1) {\n" +
				                              "  return p0 == p1\n" +
				                              "}\n" +
				                              VOID_MAIN,
		                              DetermineTypesTransformation.warningUnusedFunction(1, 8, "isEqual") + "\n",
		                              "boolean isEqual(int a, int b) {\n" +
				                              "return a == b;\n" +
				                              "}\n" +
				                              VOID_MAIN);

		assertSuccessfullyTransformed("boolean isSingleDigit(u16 p0) {\n" +
				                              "  return p0 <= 10\n" +
				                              "}\n" +
				                              VOID_MAIN,
		                              DetermineTypesTransformation.warningUnusedFunction(1, 8, "isSingleDigit") + "\n",
		                              "boolean isSingleDigit(u16 a) {\n" +
				                              "return a <= 10;\n" +
				                              "}\n" +
				                              VOID_MAIN);

		assertSuccessfullyTransformed("i16 print(i16 p0) {\n" +
				                              "  return 0\n" +
				                              "}\n" +
				                              "void anotherPrint(i16 p0) {\n" +
				                              "  v0 : i16 = print(p0)\n" +
				                              "  return\n" +
				                              "}\n" +
				                              VOID_MAIN,
		                              DetermineTypesTransformation.warningUnusedParameter(1, 14, "a") + "\n" +
				                              DetermineTypesTransformation.warningUnusedVar(5, 4, "ignored") + "\n" +
				                              DetermineTypesTransformation.warningUnusedFunction(4, 5, "anotherPrint") + "\n",
		                              "int print(int a) {\n" +
				                              "return 0;\n" +
				                              "}\n" +
				                              "void anotherPrint(int a) {\n" +
				                              "var ignored = print(a);\n" +
				                              "return;\n" +
				                              "}\n" +
				                              VOID_MAIN);

		assertSuccessfullyTransformed("i16 max(i16 p0, i16 p1) {\n" +
				                              "  if p0 > p1\n" +
				                              "  {\n" +
				                              "    return p0\n" +
				                              "  }\n" +
				                              "  else\n" +
				                              "  {\n" +
				                              "    return p1\n" +
				                              "  }\n" +
				                              "}\n" +
				                              VOID_MAIN,
		                              DetermineTypesTransformation.warningUnusedFunction(1, 4, "max") + "\n",
		                              "int max(int a, int b) {\n" +
				                              "if (a > b) {\n" +
				                              "return a;\n" +
				                              "} else {\n" +
				                              "return b;\n" +
				                              "}\n" +
				                              "}\n" +
				                              VOID_MAIN);

		assertSuccessfullyTransformed("void print() {\n" +
				                              "}\n" +
				                              "i16 max() {\n" +
				                              "  print()\n" +
				                              "  return 0\n" +
				                              "}\n" +
				                              VOID_MAIN,
		                              DetermineTypesTransformation.warningStatementAfterReturn() + "\n" +
				                              DetermineTypesTransformation.warningStatementAfterReturn() + "\n" +
				                              DetermineTypesTransformation.warningUnusedVar(8, 4, "a") + "\n" +
				                              DetermineTypesTransformation.warningUnusedFunction(3, 4, "max") + "\n",
		                              "void print() {\n" +
				                              "}\n" +
				                              "int max() {\n" +
				                              "{\n" +
				                              "print();" +
				                              "return 0;\n" +
				                              "print();\n" +
				                              "}\n" +
				                              "var a = 0;\n" +
				                              "}\n" +
				                              VOID_MAIN);

		assertSuccessfullyTransformed("i16 max() {\n" +
				                              "  return 0\n" +
				                              "}\n" +
				                              VOID_MAIN,
		                              DetermineTypesTransformation.warningStatementAfterReturn() + "\n" +
				                              DetermineTypesTransformation.warningUnusedVar(3, 4, "a") + "\n" +
				                              DetermineTypesTransformation.warningUnusedFunction(1, 4, "max") + "\n",
		                              "int max() {\n" +
				                              "return 0;\n" +
				                              "var a = 0;\n" +
				                              "}\n" +
				                              VOID_MAIN);

		assertInvalidTypeException(DetermineTypesTransformation.errorMissingReturnStatement("test"),
		                           NO_WARNING,
		                           "int test() {\n" +
				                           "}\n" +
				                           VOID_MAIN);
	}

	@Test
	public void testCall() {
		assertSuccessfullyTransformed("i16 one() {\n" +
				                              "  return 1\n" +
				                              "}\n" +
				                              "i16 zero() {\n" +
				                              "  one()\n" +
				                              "  return 0\n" +
				                              "}\n" +
				                              VOID_MAIN,
		                              DetermineTypesTransformation.warningIgnoredReturnValue(3, 0, "one", BasicTypes.INT16) + "\n" +
				                              DetermineTypesTransformation.warningUnusedFunction(2, 4, "zero") + "\n",
		                              "int one() return 1;\n" +
				                              "int zero() {\n" +
				                              "one();\n" +
				                              "return 0;\n" +
				                              "}\n" +
				                              VOID_MAIN);
	}

	@Test
	public void testValidDuplicateDeclarations() {
		assertSuccessfullyTransformed("g0 : u8 = 1\n" +
				                              "i16 twice(i16 p0, i16 p1) {\n" +
				                              "  return p0 * 2\n" +
				                              "}\n" +
				                              "i16 zero() {\n" +
				                              "  v0 : u8 = 0\n" +
				                              "  return v0\n" +
				                              "}\n" +
				                              VOID_MAIN,
		                              DetermineTypesTransformation.warningUnusedParameter(2, 21, "b") + "\n" +
				                              DetermineTypesTransformation.warningUnusedVar(1, 4, "a") + "\n" +
				                              DetermineTypesTransformation.warningUnusedFunction(2, 4, "twice") + "\n" +
				                              DetermineTypesTransformation.warningUnusedFunction(3, 4, "zero") + "\n",
		                              "var a = 1;\n" +
				                              "int twice(int a, int b) return a * 2;\n" +
				                              "int zero() {\n" +
				                              "var a = 0;\n" +
				                              "return a;\n" +
				                              "}\n" +
				                              VOID_MAIN);

		assertSuccessfullyTransformed("g0 : u8 = 1\n" +
				                              "i16 twice(i16 p0, i16 p1) {\n" +
				                              "  return p0 * 2\n" +
				                              "}\n" +
				                              "i16 zero() {\n" +
				                              "  v0 : i16 = 1000\n" +
				                              "  v1 : u8 = 1\n" +
				                              "  v0 = twice(v1, v1)\n" +
				                              "  v2 : u8 = 0\n" +
				                              "  return v2\n" +
				                              "}\n" +
				                              VOID_MAIN,
		                              DetermineTypesTransformation.warningUnusedParameter(2, 21, "b") + "\n" +
				                              DetermineTypesTransformation.warningUnusedVar(1, 4, "a") + "\n" +
				                              DetermineTypesTransformation.warningUnusedFunction(3, 4, "zero") + "\n",
		                              "var a = 1;\n" +
				                              "int twice(int a, int b) return a * 2;\n" +
				                              "int zero() {\n" +
				                              "var b = 1000;\n" +
				                              "{\n" +
				                              "var a = 1;\n" +
				                              "b = twice(a, a);\n" +
				                              "}\n" +
				                              "var a = 0;\n" +
				                              "return a;\n" +
				                              "}\n" +
				                              VOID_MAIN);
	}

	@Test
	public void testInvalidTypes() {
		assertUnsupportedTypeException("foobar",
		                               NO_WARNING,
		                               "foobar a = 0;\n" +
				                               VOID_MAIN);

		assertInvalidTypeException(DetermineTypesTransformation.errorCantAssignType(1, 3, "a", BasicTypes.INT8, BasicTypes.UINT8),
		                           NO_WARNING,
		                           "u8 a = -1;\n" +
				                           VOID_MAIN);

		assertInvalidTypeException(DetermineTypesTransformation.errorCantAssignType(2, 3, "a", BasicTypes.INT16, BasicTypes.UINT8),
		                           NO_WARNING,
		                           "int test() {\n" +
				                           "u8 a = 256;\n" +
				                           "}\n" +
				                           VOID_MAIN);

		assertInvalidTypeException(DetermineTypesTransformation.errorCantAssignType(4, 0, "a", BasicTypes.INT16, BasicTypes.UINT8),
		                           NO_WARNING,
		                           "int test() {\n" +
				                           "u8 a = 0;\n" +
				                           "int b = 0;\n" +
				                           "a = a + b;\n" +
				                           "return 0;\n" +
				                           "}\n" +
				                           VOID_MAIN);

		assertInvalidTypeException(DetermineTypesTransformation.errorCantAssignReturnType(2, 7, BasicTypes.UINT8, BasicTypes.INT8),
		                           NO_WARNING,
		                           "i8 test() {\n" +
				                           "return 128;\n" +
				                           "}\n" +
				                           VOID_MAIN);

		assertInvalidTypeException(DetermineTypesTransformation.errorNoReturnExpressionExpectedForVoid(2, 7),
		                           NO_WARNING,
		                           "void test() {\n" +
				                           "return 128;\n" +
				                           "}\n" +
				                           VOID_MAIN);

		assertInvalidTypeException(DetermineTypesTransformation.errorReturnExpressionExpected(2, 0, BasicTypes.INT16),
		                           NO_WARNING,
		                           "int test() {\n" +
				                           "return;\n" +
				                           "}\n" +
				                           VOID_MAIN);

		assertInvalidTypeException(DetermineTypesTransformation.errorFunctionDoesNotReturnAValue(5, 7, "nothing"),
		                           DetermineTypesTransformation.warningUnusedParameter(1, 17, "a") + "\n",
		                           "void nothing(int a) {\n" +
				                           "return;\n" +
				                           "}\n" +
				                           "int test() {\n" +
				                           "return nothing(1) + 2;\n" +
				                           "}\n" +
				                           VOID_MAIN);

		assertInvalidTypeException(DetermineTypesTransformation.errorBooleanExpected(2, 3, BasicTypes.UINT8),
		                           NO_WARNING,
		                           "int max(int a, int b) {\n" +
				                           "if (1) {\n" +
				                           "return a;\n" +
				                           "} else {\n" +
				                           "return b;\n" +
				                           "}\n" +
				                           "}\n" +
				                           VOID_MAIN);
	}

	@Test
	public void testTypeCast() {
		assertSuccessfullyTransformed("g0 : u8 = (u8) -1\n" +
				                              VOID_MAIN,
		                              DetermineTypesTransformation.warningUnusedVar(1, 4, "a") + "\n",
		                              "var a = (u8)-1;\n" +
				                              VOID_MAIN);

		assertSuccessfullyTransformed("g0 : u8 = (u8) 0\n" +
				                              VOID_MAIN,
		                              DetermineTypesTransformation.warningUnnecessaryCastTo(1, 8, BasicTypes.UINT8) + "\n" +
				                              DetermineTypesTransformation.warningUnusedVar(1, 3, "a") + "\n",
		                              "u8 a = (u8)0;\n" +
				                              VOID_MAIN);

		assertUnsupportedTypeException("Foo",
		                               NO_WARNING,
		                               "var a = (Foo)0;\n" +
				                               VOID_MAIN);
	}

	@Test
	public void testVarHidesParameter() {
		assertAlreadyDefinedException(DetermineTypesTransformation.errorVarAlreadyDeclaredAsParameter("a", 2, 4),
		                              NO_WARNING,
		                              "int func(int a) {\n" +
				                              "var a = 0;\n" +
				                              "}\n" +
				                              VOID_MAIN);

		assertAlreadyDefinedException(DetermineTypesTransformation.errorVarAlreadyDeclaredAsParameter("a", 3, 5),
		                              NO_WARNING,
		                              "int func(int a) {\n" +
				                              "var b = 0;{\n" +
				                              "\tvar a = 1;\n" +
				                              "}\n" +
				                              "}\n" +
				                              VOID_MAIN);
	}

	@Test
	public void testMain() {
		assertSuccessfullyTransformed(VOID_MAIN,
		                              NO_WARNING,
		                              VOID_MAIN);
		assertInvalidTypeException(DetermineTypesTransformation.errorMissingMain(),
		                           NO_WARNING,
		                           "");
		assertInvalidTypeException(DetermineTypesTransformation.errorMissingMain(),
		                           NO_WARNING,
		                           "var global = 1;");
		assertInvalidTypeException(DetermineTypesTransformation.errorMissingMain(),
		                           NO_WARNING,
		                           "void foo() {\n" +
				                           "}");
		assertInvalidTypeException(DetermineTypesTransformation.errorMissingMain(),
		                           NO_WARNING,
		                           "int main() {\n" +
				                           "}");
		assertInvalidTypeException(DetermineTypesTransformation.errorMissingMain(),
		                           NO_WARNING,
		                           "void main(int a) {\n" +
				                           "}");
	}

	// Utils ==================================================================

	private void assertSuccessfullyTransformed(String expectedCode, String expectedWarnings, String code) {
		final DeclarationList root = AstFactory.parseString(code);
		final StringOutput out = new StringStringOutput();
		final DeclarationList newRoot = DetermineTypesTransformation.transform(root, out);
		assertEquals(expectedCode, CodePrinter.print(newRoot));
		assertEquals(expectedWarnings, out.toString());
	}

	@SuppressWarnings("SameParameterValue")
	private void assertAlreadyDefinedException(String expectedExceptionMsg, String expectedWarnings, String code) {
		final StringOutput out = new StringStringOutput();
		final DeclarationList rootAst = AstFactory.parseString(code);
		try {
			DetermineTypesTransformation.transform(rootAst, out);
			fail();
		}
		catch (SymbolScope.AlreadyDefinedException ex) {
			assertEquals(expectedExceptionMsg, ex.getMessage());
		}
		assertEquals(expectedWarnings, out.toString());
	}

	private void assertInvalidTypeException(String expectedExceptionMsg, String expectedWarnings, String code) {
		final StringOutput out = new StringStringOutput();
		try {
			DetermineTypesTransformation.transform(AstFactory.parseString(code), out);
			fail();
		}
		catch (InvalidTypeException ex) {
			assertEquals(expectedExceptionMsg, ex.getMessage());
		}
		assertEquals(expectedWarnings, out.toString());
	}

	@SuppressWarnings("SameParameterValue")
	private void assertUndeclaredException(String expectedExceptionMsg, String expectedWarnings, String code) {
		final StringOutput out = new StringStringOutput();
		try {
			DetermineTypesTransformation.transform(AstFactory.parseString(code), out);
			fail();
		}
		catch (DetermineTypesTransformation.UndeclaredException e) {
			assertEquals(expectedExceptionMsg, e.getMessage());
		}
		assertEquals(expectedWarnings, out.toString());
	}

	@SuppressWarnings("SameParameterValue")
	private void assertUnsupportedTypeException(String expectedExceptionMsg, String expectedWarnings, String code) {
		final StringOutput out = new StringStringOutput();
		try {
			DetermineTypesTransformation.transform(AstFactory.parseString(code), out);
			fail();
		}
		catch (BasicTypes.UnsupportedTypeException e) {
			assertEquals(expectedExceptionMsg, e.getMessage());
		}
		assertEquals(expectedWarnings, out.toString());
	}
}
