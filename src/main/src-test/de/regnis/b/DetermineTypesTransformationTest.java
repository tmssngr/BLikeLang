package de.regnis.b;

import de.regnis.b.node.DeclarationList;
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
		DetermineTypesTransformation.run(rootAst, out);
		assertEquals(SymbolScope.msgVarIsUnused(3, 4, "b") + "\n", out.toString());
	}

	@Test
	public void testValidDuplicateDeclarations() {
		final DeclarationList rootAst = AstFactory.parseString("var a = 1;\n" +
				                                                       "int twice(int a, int b) return a * 2;\n" +
				                                                       "int zero() {\n" +
				                                                       "var a = 0;\n" +
				                                                       "return a;\n" +
				                                                       "}");
		final StringOutput out = new StringStringOutput();
		DetermineTypesTransformation.run(rootAst, out);
		assertEquals(SymbolScope.msgParamIsUnused(2, 21, "b") + "\n" +
				                    SymbolScope.msgVarIsUnused(1, 4, "a") + "\n", out.toString());
	}

	@Test
	public void testVarHidesParameter() {
		DeclarationList rootAst = AstFactory.parseString("int func(int a) {\n" +
				                                                 "var a = 0;\n" +
				                                                 "}");
		try {
			DetermineTypesTransformation.run(rootAst, StringOutput.out);
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
			DetermineTypesTransformation.run(rootAst, StringOutput.out);
			fail();
		}
		catch (SymbolScope.AlreadyDefinedException ex) {
			assertEquals(SymbolScope.msgVarAlreadyDeclaredAsParameter("a", 3, 5), ex.getMessage());
		}
	}
}
