package de.regnis.b.ir;

import de.regnis.b.ast.BinaryExpression;
import de.regnis.b.ast.NumberLiteral;
import de.regnis.b.ast.VarDeclaration;
import de.regnis.b.ast.VarRead;
import de.regnis.b.out.StringStringOutput;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * @author Thomas Singer
 */
public class SsaToModifyAssignmentsTest {

	// Accessing ==============================================================

	@Test
	public void test() {
		test(BinaryExpression.Op.add, "+");
		test(BinaryExpression.Op.sub, "-");
		test(BinaryExpression.Op.multiply, "*");
		test(BinaryExpression.Op.divide, "/");
		test(BinaryExpression.Op.modulo, "%");
		test(BinaryExpression.Op.shiftL, "<<");
		test(BinaryExpression.Op.shiftR, ">>");
		test(BinaryExpression.Op.bitAnd, "&");
		test(BinaryExpression.Op.bitOr, "|");
		test(BinaryExpression.Op.bitXor, "^");
	}

	// Utils ==================================================================

	private void test(BinaryExpression.Op operator, String operatorString) {
		final BasicBlock block = new BasicBlock("");
		block.add(new VarDeclaration("var", new NumberLiteral(10)));
		block.add(new VarDeclaration("var1", new BinaryExpression(new VarRead("var"),
		                                                          operator,
		                                                          new NumberLiteral(1))));
		assertEquals("""
				             var := 10
				             var1 := var %s 1
				             """.formatted(operatorString), block);

		SsaToModifyAssignments.transform(block);

		final String expected = """
				var := 10
				var1 := var
				var1 %s= 1
				""".formatted(operatorString);
		assertEquals(expected, block);

		// a second invocation will not like the new assignment
		try {
			SsaToModifyAssignments.transform(block);
			fail();
		}
		catch (IllegalStateException ignore) {
		}
	}

	private void assertEquals(String expected, StatementsBlock block) {
		Assert.assertEquals(expected, block.print(new StringStringOutput()).toString());
	}
}
