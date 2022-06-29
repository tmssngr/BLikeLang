package de.regnis.b;

import de.regnis.b.node.*;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Thomas Singer
 */
public final class DetermineTypesTest {

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
	public void testVarHidesParameter() {
		final DeclarationList rootAst = AstFactory.parseString("int func(int a) {\n" +
				                                                       "var a = 0;\n" +
				                                                       "}");
		try {
			DetermineTypesTransformation.run(rootAst);
			Assert.fail();
		}
		catch (SymbolScope.AlreadyDefinedException ex) {
			Assert.assertEquals("a", ex.getMessage());
		}
	}
}
