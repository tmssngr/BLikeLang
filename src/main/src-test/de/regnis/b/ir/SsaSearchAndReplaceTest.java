package de.regnis.b.ir;

import de.regnis.b.ast.*;
import de.regnis.b.out.StringStringOutput;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @author Thomas Singer
 */
public class SsaSearchAndReplaceTest {

	// Accessing ==============================================================

	@Test
	public void test() {
		final BasicBlock block = new BasicBlock("");
		block.add(new VarDeclaration("var", new NumberLiteral(10)));
		block.add(new VarDeclaration("var1", new BinaryExpression(new VarRead("var"),
		                                                          BinaryExpression.Op.add,
		                                                          new NumberLiteral(1))));
		block.add(new Assignment(Assignment.Op.add, "var1", new NumberLiteral(2)));
		assertEquals("""
				             var := 10
				             var1 := var + 1
				             var1 += 2
				             """, block);

		SsaSearchAndReplace.replace(block, Map.of("var", new NumberLiteral(10)));

		assertEquals("""
				             var1 := 10 + 1
				             var1 += 2
				             """, block);
	}

	// Utils ==================================================================

	private void assertEquals(String expected, StatementsBlock block) {
		Assert.assertEquals(expected, block.print(new StringStringOutput()).toString());
	}
}