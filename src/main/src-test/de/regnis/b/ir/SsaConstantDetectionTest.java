package de.regnis.b.ir;

import de.regnis.b.ast.*;
import de.regnis.b.out.StringStringOutput;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @author Thomas Singer
 */
public class SsaConstantDetectionTest {

	// Accessing ==============================================================

	@Test
	public void test1() {
		final BasicBlock block = new BasicBlock("");
		block.add(new VarDeclaration("var", new NumberLiteral(10)));
		block.add(new VarDeclaration("var1", new BinaryExpression(new VarRead("var"),
		                                                          BinaryExpression.Op.add,
		                                                          new NumberLiteral(1))));
		block.add(new VarDeclaration("var2", new FuncCall("foo", FuncCallParameters.of(new VarRead("var")))));
		assertBlockEquals("""
				                  var := 10
				                  var1 := var + 1
				                  var2 := foo(var)
				                  """, block);

		final Map<String, SimpleExpression> constants = SsaConstantDetection.detectConstants(block);
		Assert.assertEquals(Map.of("var", new NumberLiteral(10)), constants);
	}

	@Test
	public void test2() {
		final BasicBlock block = new BasicBlock("");
		block.add(new VarDeclaration("var", new FuncCall("foo", FuncCallParameters.empty())));
		block.add(new VarDeclaration("var1", new VarRead("var")));
		block.add(new VarDeclaration("var2", new BinaryExpression(new VarRead("var1"),
		                                                          BinaryExpression.Op.add,
		                                                          new NumberLiteral(1))));
		assertBlockEquals("""
				                  var := foo()
				                  var1 := var
				                  var2 := var1 + 1
				                  """, block);

		final Map<String, SimpleExpression> constants = SsaConstantDetection.detectConstants(block);
		Assert.assertEquals(Map.of("var1", new VarRead("var")), constants);
	}

	@Test
	public void test3SearchAndReplace() {
		final BasicBlock block = new BasicBlock("");
		block.add(new VarDeclaration("var", new FuncCall("foo", FuncCallParameters.empty())));
		block.add(new VarDeclaration("var1", new VarRead("var")));
		block.add(new VarDeclaration("var2", new VarRead("var1")));
		assertBlockEquals("""
				                  var := foo()
				                  var1 := var
				                  var2 := var1
				                  """, block);

		Map<String, SimpleExpression> constants = SsaConstantDetection.detectConstants(block);
		Assert.assertEquals(Map.of("var1", new VarRead("var")), constants);
		// the replacement of var2 -> var1 is prevented and will happen on the next invocation

		SsaSearchAndReplace.replace(block, constants);

		assertBlockEquals("""
				                  var := foo()
				                  var2 := var
				                  """, block);

		constants = SsaConstantDetection.detectConstants(block);
		Assert.assertEquals(Map.of("var2", new VarRead("var")), constants);

		SsaSearchAndReplace.replace(block, constants);

		assertBlockEquals("""
				                  var := foo()
				                  """, block);
	}

	// Utils ==================================================================

	private void assertBlockEquals(String expected, StatementsBlock block) {
		Assert.assertEquals(expected, block.print(new StringStringOutput()).toString());
	}
}