package de.regnis.b;

import de.regnis.b.out.TreePrinter;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Thomas Singer
 */
public class AstFactoryTest {

	@Test
	public void testAstFactory() {
		Assert.assertEquals("+- a :=\n" +
				                    "   +- literal 0\n", TreePrinter.print(AstFactory.parseString("var a = 0;")));
		Assert.assertEquals("+- a :=\n" +
				                    "|  +- literal 0\n" +
				                    "+- b :=\n" +
				                    "   +- operator +\n" +
				                    "      +- read var a\n" +
				                    "      +- literal 1\n", TreePrinter.print(AstFactory.parseString(
				"var a = 0;\n" +
						"var b = a + 1;\n")));
		Assert.assertEquals("+- void wom(int x)\n" +
				                    "|  +- statementList\n" +
				                    "|     +- b :=\n" +
				                    "|        +- operator +\n" +
				                    "|           +- read var a\n" +
				                    "|           +- literal 1\n" +
				                    "+- int getLength(int address)\n" +
				                    "   +- statementList\n", TreePrinter.print(AstFactory.parseString(
				"void wom(int x) {\n" +
						"var b = a + 1;\n" +
						"}\n" +
						"int getLength(int address) {\n" +
						"return 0; // just for now\n" +
						"}")));
	}
}
