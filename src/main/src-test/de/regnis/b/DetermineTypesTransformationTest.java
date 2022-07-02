package de.regnis.b;

import de.regnis.b.node.DeclarationList;
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
		assertEquals("g0 := 1\n" +
				             "g1 := -1\n" +
				             "g2 := g0 + g1\n", CodePrinter.print(newRoot));
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
				             "  v0 := p0 + p1\n" +
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
		assertEquals("g0 := 1\n" +
				             "i16 twice(i16 p0, i16 p1) {\n" +
				             "  return p0 * 2\n" +
				             "}\n" +
				             "i16 zero() {\n" +
				             "  v0 := 0\n" +
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
		assertEquals("g0 := 1\n" +
				             "i16 twice(i16 p0, i16 p1) {\n" +
				             "  return p0 * 2\n" +
				             "}\n" +
				             "i16 zero() {\n" +
				             "  v0 := 1000\n" +
				             "  v1 := 1\n" +
				             "  v0 = twice(v1, v1)\n" +
				             "  v2 := 0\n" +
				             "  return v2\n" +
				             "}\n", CodePrinter.print(newRoot));
		assertEquals(SymbolScope.msgParamIsUnused(2, 21, "b") + "\n" +
				             SymbolScope.msgVarIsUnused(1, 4, "a") + "\n", out.toString());
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
