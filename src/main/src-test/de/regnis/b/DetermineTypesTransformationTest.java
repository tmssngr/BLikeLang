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

	// Accessing ==============================================================

	@Test
	public void testGlobalVars() {
		final DeclarationList rootAst = AstFactory.parseString("var a = 1;\n" +
				                                                       "var A = -1;\n" +
				                                                       "var b=a+A;\n" +
				                                                       "var booF = false;");
		final StringOutput out = new StringStringOutput();
		final DeclarationList newRoot = DetermineTypesTransformation.transform(rootAst, out);
		assertEquals("g0 : u8 = 1\n" +
				             "g1 : i8 = -1\n" +
				             "g2 : i8 = g0 + g1\n" +
				             "g3 : boolean = false\n", CodePrinter.print(newRoot));
		assertEquals(SymbolScope.msgVarIsUnused(3, 4, "b") + "\n" +
				             SymbolScope.msgVarIsUnused(4, 4, "booF") + "\n", out.toString());
	}

	@Test
	public void testLocalVars() {
		final DeclarationList rootAst = AstFactory.parseString(
				"int add(int a, int b) {\n" +
						"var sum = a + b;\n" +
						"return sum;\n" +
						"}\n" +
						"boolean getFalse() {" +
						"boolean v = false;" +
						"return v;" +
						"}");
		final StringOutput out = new StringStringOutput();
		final DeclarationList newRoot = DetermineTypesTransformation.transform(rootAst, out);
		assertEquals("i16 add(i16 p0, i16 p1) {\n" +
				             "  v0 : i16 = p0 + p1\n" +
				             "  return v0\n" +
				             "}\n" +
				             "boolean getFalse() {\n" +
				             "  v0 : boolean = false\n" +
				             "  return v0\n" +
				             "}\n",
		             CodePrinter.print(newRoot));
		assertEquals("", out.toString());
	}

	@Test
	public void testReturn() {
		DeclarationList rootAst = AstFactory.parseString(
				"boolean isEqual(int a, int b) {\n" +
						"return a == b;\n" +
						"}");
		StringOutput out = new StringStringOutput();
		DeclarationList newRoot = DetermineTypesTransformation.transform(rootAst, out);
		assertEquals("boolean isEqual(i16 p0, i16 p1) {\n" +
				             "  return p0 == p1\n" +
				             "}\n",
		             CodePrinter.print(newRoot));
		assertEquals("", out.toString());

		rootAst = AstFactory.parseString(
				"boolean isSingleDigit(u16 a) {\n" +
						"return a <= 10;\n" +
						"}");
		out = new StringStringOutput();
		newRoot = DetermineTypesTransformation.transform(rootAst, out);
		assertEquals("boolean isSingleDigit(u16 p0) {\n" +
				             "  return p0 <= 10\n" +
				             "}\n",
		             CodePrinter.print(newRoot));
		assertEquals("", out.toString());

		rootAst = AstFactory.parseString(
				"int print(int a) {\n" +
						"}\n" + // TODO tricky, needs to be handled later
						"void anotherPrint(int a) {\n" +
						"var ignored = print(a);\n" +
						"return;\n" +
						"}");
		out = new StringStringOutput();
		newRoot = DetermineTypesTransformation.transform(rootAst, out);
		assertEquals("i16 print(i16 p0) {\n" +
				             "}\n" +
				             "void anotherPrint(i16 p0) {\n" +
				             "  v0 : i16 = print(p0)\n" +
				             "  return\n" +
				             "}\n",
		             CodePrinter.print(newRoot));
		assertEquals(SymbolScope.msgParamIsUnused(1, 14, "a") + "\n" +
				             SymbolScope.msgVarIsUnused(4, 4, "ignored") + "\n", out.toString());
	}

	@Test
	public void testValidDuplicateDeclarations() {
		DeclarationList rootAst = AstFactory.parseString("var a = 1;\n" +
				                                                 "int twice(int a, int b) return a * 2;\n" +
				                                                 "int zero() {\n" +
				                                                 "var a = 0;\n" +
				                                                 "return a;\n" +
				                                                 "}");
		StringOutput out = new StringStringOutput();
		DeclarationList newRoot = DetermineTypesTransformation.transform(rootAst, out);
		assertEquals("g0 : u8 = 1\n" +
				             "i16 twice(i16 p0, i16 p1) {\n" +
				             "  return p0 * 2\n" +
				             "}\n" +
				             "i16 zero() {\n" +
				             "  v0 : u8 = 0\n" +
				             "  return v0\n" +
				             "}\n", CodePrinter.print(newRoot));
		assertEquals(SymbolScope.msgParamIsUnused(2, 21, "b") + "\n" +
				             SymbolScope.msgVarIsUnused(1, 4, "a") + "\n", out.toString());

		rootAst = AstFactory.parseString("var a = 1;\n" +
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
		out = new StringStringOutput();
		newRoot = DetermineTypesTransformation.transform(rootAst, out);
		assertEquals("g0 : u8 = 1\n" +
				             "i16 twice(i16 p0, i16 p1) {\n" +
				             "  return p0 * 2\n" +
				             "}\n" +
				             "i16 zero() {\n" +
				             "  v0 : i16 = 1000\n" +
				             "  v1 : u8 = 1\n" +
				             "  v0 = twice(v1, v1)\n" +
				             "  v2 : u8 = 0\n" +
				             "  return v2\n" +
				             "}\n", CodePrinter.print(newRoot));
		assertEquals(SymbolScope.msgParamIsUnused(2, 21, "b") + "\n" +
				             SymbolScope.msgVarIsUnused(1, 4, "a") + "\n", out.toString());
	}

	@Test
	public void testInvalidTypes() {
		StringOutput out = new StringStringOutput();
		try {
			DetermineTypesTransformation.transform(AstFactory.parseString("foobar a = 0;"), out);
			fail();
		}
		catch (BasicTypes.UnsupportedTypeException e) {
			assertEquals("foobar", e.getMessage());
		}
		assertEquals("", out.toString());

		out = new StringStringOutput();
		try {
			DetermineTypesTransformation.transform(AstFactory.parseString("u8 a = -1;"), out);
			fail();
		}
		catch (InvalidTypeException ex) {
			assertEquals(DetermineTypesTransformation.msgCantAssignType(1, 3, "a", BasicTypes.INT8, BasicTypes.UINT8), ex.getMessage());
		}
		assertEquals("", out.toString());

		out = new StringStringOutput();
		try {
			DetermineTypesTransformation.transform(AstFactory.parseString("int test() {\n" +
					                                                              "u8 a = 256;\n" +
					                                                              "}"), out);
			fail();
		}
		catch (InvalidTypeException ex) {
			assertEquals(DetermineTypesTransformation.msgCantAssignType(2, 3, "a", BasicTypes.INT16, BasicTypes.UINT8), ex.getMessage());
		}
		assertEquals("", out.toString());

		out = new StringStringOutput();
		try {
			DetermineTypesTransformation.transform(AstFactory.parseString("int test() {\n" +
					                                                              "u8 a = 0;\n" +
					                                                              "int b = 0;\n" +
					                                                              "a = a + b;\n" +
					                                                              "return 0;\n" +
					                                                              "}"), out);
			fail();
		}
		catch (InvalidTypeException ex) {
			assertEquals(DetermineTypesTransformation.msgCantAssignType(4, 0, "a", BasicTypes.INT16, BasicTypes.UINT8), ex.getMessage());
		}
		assertEquals("", out.toString());

		out = new StringStringOutput();
		try {
			DetermineTypesTransformation.transform(AstFactory.parseString("i8 test() {\n" +
					                                                              "return 128;\n" +
					                                                              "}"), out);
			fail();
		}
		catch (InvalidTypeException ex) {
			assertEquals(DetermineTypesTransformation.msgCantAssignReturnType(2, 7, BasicTypes.UINT8, BasicTypes.INT8), ex.getMessage());
		}
		assertEquals("", out.toString());

		out = new StringStringOutput();
		try {
			DetermineTypesTransformation.transform(AstFactory.parseString("void test() {\n" +
					                                                              "return 128;\n" +
					                                                              "}"), out);
			fail();
		}
		catch (InvalidTypeException ex) {
			assertEquals(DetermineTypesTransformation.msgNoReturnExpressionExpectedForVoid(2, 7), ex.getMessage());
		}
		assertEquals("", out.toString());

		out = new StringStringOutput();
		try {
			DetermineTypesTransformation.transform(AstFactory.parseString("int test() {\n" +
					                                                              "return;\n" +
					                                                              "}"), out);
			fail();
		}
		catch (InvalidTypeException ex) {
			assertEquals(DetermineTypesTransformation.msgReturnExpressionExpected(2, 0, BasicTypes.INT16), ex.getMessage());
		}
		assertEquals("", out.toString());
	}

	@Test
	public void testTypeCast() {
		StringOutput out = new StringStringOutput();
		DeclarationList newRoot = DetermineTypesTransformation.transform(
				AstFactory.parseString("var a = (u8)-1;"),
				out);
		assertEquals("g0 : u8 = (u8) -1\n", CodePrinter.print(newRoot));
		assertEquals(SymbolScope.msgVarIsUnused(1, 4, "a") + "\n", out.toString());

		out = new StringStringOutput();
		newRoot = DetermineTypesTransformation.transform(
				AstFactory.parseString("u8 a = (u8)0;"),
				out);
		assertEquals("g0 : u8 = (u8) 0\n", CodePrinter.print(newRoot));
		assertEquals("Unnecessary cast to u8\n" +
				             SymbolScope.msgVarIsUnused(1, 3, "a") + "\n", out.toString());

		out = new StringStringOutput();
		try {
			DetermineTypesTransformation.transform(AstFactory.parseString("var a = (Foo)0;"), out);
			fail();
		}
		catch (BasicTypes.UnsupportedTypeException e) {
			assertEquals("Foo", e.getMessage());
		}
		assertEquals("", out.toString());
	}

	@Test
	public void testVarHidesParameter() {
		DeclarationList rootAst = AstFactory.parseString("int func(int a) {\n" +
				                                                 "var a = 0;\n" +
				                                                 "}");
		try {
			DetermineTypesTransformation.transform(rootAst, StringOutput.out);
			fail();
		}
		catch (SymbolScope.AlreadyDefinedException ex) {
			assertEquals(SymbolScope.msgVarAlreadyDeclaredAsParameter("a", 2, 4), ex.getMessage());
		}

		rootAst = AstFactory.parseString("int func(int a) {\n" +
				                                 "var b = 0;{\n" +
				                                 "\tvar a = 1;\n" +
				                                 "}\n" +
				                                 "}");
		try {
			DetermineTypesTransformation.transform(rootAst, StringOutput.out);
			fail();
		}
		catch (SymbolScope.AlreadyDefinedException ex) {
			assertEquals(SymbolScope.msgVarAlreadyDeclaredAsParameter("a", 3, 5), ex.getMessage());
		}
	}
}
