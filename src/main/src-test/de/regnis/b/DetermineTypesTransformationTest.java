package de.regnis.b;

import de.regnis.b.node.*;

import org.junit.Assert;
import org.junit.Test;

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
		final SymbolScope scope = DetermineTypesTransformation.run(rootAst);
		Assert.assertEquals(BasicTypes.UINT8, scope.getVariableType("a"));
		Assert.assertEquals(BasicTypes.INT8, scope.getVariableType("A"));
		Assert.assertEquals(BasicTypes.INT8, scope.getVariableType("b"));

		try {
			scope.getVariableType("c");
			Assert.fail();
		}
		catch (SymbolScope.UndeclaredException ex) {
			Assert.assertEquals("c", ex.getMessage());
		}
	}

	@Test
	public void testValidDupliceDeclarations() {
		final DeclarationList rootAst = AstFactory.parseString("var a = 1;\n" +
				                                                       "int twice(int a) return a * 2;\n" +
				                                                       "int zero() {\n" +
				                                                       "var a = 0;\n" +
				                                                       "return a;\n" +
				                                                       "}");
		final SymbolScope scope = DetermineTypesTransformation.run(rootAst);
	}

	@Test
	public void testVarHidesParameter() {
		DeclarationList rootAst = AstFactory.parseString("int func(int a) {\n" +
				                                                 "var a = 0;\n" +
				                                                 "}");
		try {
			DetermineTypesTransformation.run(rootAst);
			Assert.fail();
		}
		catch (SymbolScope.AlreadyDefinedException ex) {
			Assert.assertEquals(SymbolScope.msgVarAlreadyDeclaredAsParameter("a", 2, 4), ex.getMessage());
		}

		rootAst = AstFactory.parseString("int func(int a) {\n" +
				                                 "var b = 0;{\n" +
				                                 "\tvar a = 1;\n" +
				                                 "}\n" +
				                                 "}");
		try {
			DetermineTypesTransformation.run(rootAst);
			Assert.fail();
		}
		catch (SymbolScope.AlreadyDefinedException ex) {
			Assert.assertEquals(SymbolScope.msgVarAlreadyDeclaredAsParameter("a", 3, 5), ex.getMessage());
		}
	}
}
