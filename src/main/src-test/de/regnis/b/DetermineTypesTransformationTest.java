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
				                                                       "var b=a+A;");
		final StringOutput out = new StringStringOutput();
		final DeclarationList newRoot = DetermineTypesTransformation.transform(rootAst, out);
		assertEquals("g0 : u8 = 1\n" +
				             "g1 : i8 = -1\n" +
				             "g2 : i8 = g0 + g1\n", CodePrinter.print(newRoot));
		assertEquals(SymbolScope.msgVarIsUnused(3, 4, "b") + "\n", out.toString());
	}

	@Test
	public void testLocalVars() {
		final DeclarationList rootAst = AstFactory.parseString("int add(int a, int b) {\n" +
				                                                       "var sum = a + b;\n" +
				                                                       "return sum;\n" +
				                                                       "}");
		final StringOutput out = new StringStringOutput();
		final DeclarationList newRoot = DetermineTypesTransformation.transform(rootAst, out);
		assertEquals("i16 add(i16 p0, i16 p1) {\n" +
				             "  v0 : i16 = p0 + p1\n" +
				             "  return v0\n" +
				             "}\n", CodePrinter.print(newRoot));
		assertEquals("", out.toString());
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
