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

		assertUndeclaredException(DetermineTypesTransformation.msgUndeclaredFunction(2, 0, "init"),
		                          NO_WARNING,
		                          "void main() {\n" +
				                          "init();\n" +
				                          "}");
		assertUndeclaredException(DetermineTypesTransformation.msgUndeclaredFunction(2, 10, "init"),
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
				                              "g3 : boolean = false\n",
		                              SymbolScope.msgVarIsUnused(3, 4, "b") + "\n" +
				                              SymbolScope.msgVarIsUnused(4, 4, "booF") + "\n",
		                              "var a = 1;\n" +
				                              "var A = -1;\n" +
				                              "var b=a+A;\n" +
				                              "var booF = false;");
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
				                              "}\n",
		                              NO_WARNING,
		                              "int add(int a, int b) {\n" +
				                              "var sum = a + b;\n" +
				                              "return sum;\n" +
				                              "}\n" +
				                              "boolean getFalse() {" +
				                              "boolean v = false;" +
				                              "return v;" +
				                              "}");
	}

	@Test
	public void testReturn() {
		assertSuccessfullyTransformed("boolean isEqual(i16 p0, i16 p1) {\n" +
				                              "  return p0 == p1\n" +
				                              "}\n",
		                              NO_WARNING,
		                              "boolean isEqual(int a, int b) {\n" +
				                              "return a == b;\n" +
				                              "}");

		assertSuccessfullyTransformed("boolean isSingleDigit(u16 p0) {\n" +
				                              "  return p0 <= 10\n" +
				                              "}\n",
		                              NO_WARNING,
		                              "boolean isSingleDigit(u16 a) {\n" +
				                              "return a <= 10;\n" +
				                              "}");

		assertSuccessfullyTransformed("i16 print(i16 p0) {\n" +
				                              "}\n" +
				                              "void anotherPrint(i16 p0) {\n" +
				                              "  v0 : i16 = print(p0)\n" +
				                              "  return\n" +
				                              "}\n",
		                              SymbolScope.msgParamIsUnused(1, 14, "a") + "\n" +
				                              SymbolScope.msgVarIsUnused(4, 4, "ignored") + "\n",
		                              "int print(int a) {\n" +
				                              "}\n" + // TODO tricky, needs to be handled later
				                              "void anotherPrint(int a) {\n" +
				                              "var ignored = print(a);\n" +
				                              "return;\n" +
				                              "}");

		assertSuccessfullyTransformed("i16 max(i16 p0, i16 p1) {\n" +
				                              "  if p0 > p1\n" +
				                              "  {\n" +
				                              "    return p0\n" +
				                              "  }\n" +
				                              "  else\n" +
				                              "  {\n" +
				                              "    return p1\n" +
				                              "  }\n" +
				                              "}\n",
		                              NO_WARNING,
		                              "int max(int a, int b) {\n" +
				                              "if (a > b) {\n" +
				                              "return a;\n" +
				                              "} else {\n" +
				                              "return b;\n" +
				                              "}\n" +
				                              "}");
	}

	@Test
	public void testCall() {
		assertSuccessfullyTransformed("i16 one() {\n" +
				                              "  return 1\n" +
				                              "}\n" +
				                              "i16 zero() {\n" +
				                              "  one()\n" +
				                              "  return 0\n" +
				                              "}\n",
		                              DetermineTypesTransformation.msgReturnValueIsIgnored(3, 0, "one", BasicTypes.INT16) + "\n",
		                              "int one() return 1;\n" +
				                              "int zero() {\n" +
				                              "one();\n" +
				                              "return 0;\n" +
				                              "}");
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
				                              "}\n",
		                              SymbolScope.msgParamIsUnused(2, 21, "b") + "\n" +
				                              SymbolScope.msgVarIsUnused(1, 4, "a") + "\n",
		                              "var a = 1;\n" +
				                              "int twice(int a, int b) return a * 2;\n" +
				                              "int zero() {\n" +
				                              "var a = 0;\n" +
				                              "return a;\n" +
				                              "}");

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
				                              "}\n",
		                              SymbolScope.msgParamIsUnused(2, 21, "b") + "\n" +
				                              SymbolScope.msgVarIsUnused(1, 4, "a") + "\n",
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
				                              "}");
	}

	@Test
	public void testInvalidTypes() {
		assertUnsupportedTypeException("foobar",
		                               NO_WARNING,
		                               "foobar a = 0;");

		assertInvalidTypeException(DetermineTypesTransformation.msgCantAssignType(1, 3, "a", BasicTypes.INT8, BasicTypes.UINT8),
		                           NO_WARNING,
		                           "u8 a = -1;");

		assertInvalidTypeException(DetermineTypesTransformation.msgCantAssignType(2, 3, "a", BasicTypes.INT16, BasicTypes.UINT8),
		                           NO_WARNING,
		                           "int test() {\n" +
				                           "u8 a = 256;\n" +
				                           "}");

		assertInvalidTypeException(DetermineTypesTransformation.msgCantAssignType(4, 0, "a", BasicTypes.INT16, BasicTypes.UINT8),
		                           NO_WARNING,
		                           "int test() {\n" +
				                           "u8 a = 0;\n" +
				                           "int b = 0;\n" +
				                           "a = a + b;\n" +
				                           "return 0;\n" +
				                           "}");

		assertInvalidTypeException(DetermineTypesTransformation.msgCantAssignReturnType(2, 7, BasicTypes.UINT8, BasicTypes.INT8),
		                           NO_WARNING,
		                           "i8 test() {\n" +
				                           "return 128;\n" +
				                           "}");

		assertInvalidTypeException(DetermineTypesTransformation.msgNoReturnExpressionExpectedForVoid(2, 7),
		                           NO_WARNING,
		                           "void test() {\n" +
				                           "return 128;\n" +
				                           "}");

		assertInvalidTypeException(DetermineTypesTransformation.msgReturnExpressionExpected(2, 0, BasicTypes.INT16),
		                           NO_WARNING,
		                           "int test() {\n" +
				                           "return;\n" +
				                           "}");

		assertInvalidTypeException(DetermineTypesTransformation.msgFunctionDoesNotReturnAValue(5, 7, "nothing"),
		                           SymbolScope.msgParamIsUnused(1, 17, "a") + "\n",
		                           "void nothing(int a) {\n" +
				                           "return;\n" +
				                           "}\n" +
				                           "int test() {\n" +
				                           "return nothing(1) + 2;\n" +
				                           "}");

		assertInvalidTypeException(DetermineTypesTransformation.msgBooleanExpected(2, 3, BasicTypes.UINT8),
		                           NO_WARNING, "int max(int a, int b) {\n" +
				                           "if (1) {\n" +
				                           "return a;\n" +
				                           "} else {\n" +
				                           "return b;\n" +
				                           "}\n" +
				                           "}");
	}

	@Test
	public void testTypeCast() {
		assertSuccessfullyTransformed("g0 : u8 = (u8) -1\n",
		                              SymbolScope.msgVarIsUnused(1, 4, "a") + "\n",
		                              "var a = (u8)-1;");

		assertSuccessfullyTransformed("g0 : u8 = (u8) 0\n",
		                              "Unnecessary cast to u8\n" +
				                              SymbolScope.msgVarIsUnused(1, 3, "a") + "\n",
		                              "u8 a = (u8)0;");

		assertUnsupportedTypeException("Foo",
		                               NO_WARNING,
		                               "var a = (Foo)0;");
	}

	@Test
	public void testVarHidesParameter() {
		assertAlreadyDefinedException(SymbolScope.msgVarAlreadyDeclaredAsParameter("a", 2, 4),
		                              NO_WARNING,
		                              "int func(int a) {\n" +
				                              "var a = 0;\n" +
				                              "}");

		assertAlreadyDefinedException(SymbolScope.msgVarAlreadyDeclaredAsParameter("a", 3, 5),
		                              NO_WARNING,
		                              "int func(int a) {\n" +
				                              "var b = 0;{\n" +
				                              "\tvar a = 1;\n" +
				                              "}\n" +
				                              "}");
	}

	// Utils ==================================================================

	private void assertSuccessfullyTransformed(String expectedCode, String expectedWarnings, String code) {
		final StringOutput out = new StringStringOutput();
		final DeclarationList newRoot = DetermineTypesTransformation.transform(
				AstFactory.parseString(code),
				out);
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
