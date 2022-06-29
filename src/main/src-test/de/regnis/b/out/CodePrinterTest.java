package de.regnis.b.out;

import de.regnis.b.node.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Thomas Singer
 */
public class CodePrinterTest {

	@Test
	public void testDeclaration() {
		final StringOutput output = new StringStringOutput();
		new CodePrinter().print(
				new DeclarationList()
						.add(new GlobalVarDeclaration(new VarDeclaration("a", new NumberLiteral(0))))
						.add(new FuncDeclaration(BasicTypes.INT16, "sqr",
						                         new FuncDeclarationParameters()
								                             .add(new FuncDeclarationParameter(BasicTypes.INT16, "x")),
						                         new StatementList()
								                             .add(new ReturnStatement(
										                             BinaryExpression
												                             .createMultiply(new VarRead("x"),
												                                             new VarRead("x")))))),
				output);
		Assert.assertEquals("a := 0\n" +
				                    "i16 sqr(i16 x) {\n" +
				                    "\treturn x * x\n" +
				                    "}\n", output.toString());
	}
}
