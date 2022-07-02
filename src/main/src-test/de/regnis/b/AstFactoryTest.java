package de.regnis.b;

import de.regnis.b.out.TreePrinter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Thomas Singer
 */
public class AstFactoryTest {

	// Accessing ==============================================================

	@Test
	public void testAstFactory() {
		assertEquals("+- a :=\n" +
				             "   +- literal 0\n", TreePrinter.print(AstFactory.parseString("var a = 0;")));
		assertEquals("+- a :=\n" +
				             "   +- literal false\n", TreePrinter.print(AstFactory.parseString("var a = false;")));
		assertEquals("+- a : Foobar =\n" +
				             "   +- literal 0\n", TreePrinter.print(AstFactory.parseString("Foobar a = 0;")));
		assertEquals("+- a :=\n" +
				             "   +- cast to u16\n" +
				             "      +- literal 0\n", TreePrinter.print(AstFactory.parseString("var a = (u16)0;")));
		assertEquals("+- a :=\n" +
				             "   +- cast to i8\n" +
				             "      +- operator +\n" +
				             "         +- literal 0\n" +
				             "         +- literal 1000\n", TreePrinter.print(AstFactory.parseString("var a = (i8)0 + 1000;")));
		assertEquals("+- a :=\n" +
				             "   +- operator <\n" +
				             "      +- literal 0\n" +
				             "      +- literal 1000\n", TreePrinter.print(AstFactory.parseString("var a = 0 < 1000;")));
		assertEquals("+- a :=\n" +
				             "|  +- literal 0\n" +
				             "+- b :=\n" +
				             "   +- operator +\n" +
				             "      +- read var a\n" +
				             "      +- literal 1\n", TreePrinter.print(AstFactory.parseString(
				"var a = 0;\n" +
						"var b = a + 1;\n")));
		assertEquals("+- void wom(i16 x)\n" +
				             "|  +- statementList\n" +
				             "|     +- b :=\n" +
				             "|        +- operator +\n" +
				             "|           +- read var a\n" +
				             "|           +- literal 1\n" +
				             "+- i16 getLength(i16 address)\n" +
				             "   +- statementList\n" +
				             "      +- return\n" +
				             "         +- literal 0\n", TreePrinter.print(AstFactory.parseString(
				"void wom(int x) {\n" +
						"var b = a + 1;\n" +
						"}\n" +
						"int getLength(int address) {\n" +
						"return 0; // just for now\n" +
						"}")));
		assertEquals("+- a :=\n" +
				             "|  +- literal 1\n" +
				             "+- i16 twice(i16 a)\n" +
				             "|  +- statementList\n" +
				             "|     +- return\n" +
				             "|        +- operator *\n" +
				             "|           +- read var a\n" +
				             "|           +- literal 2\n" +
				             "+- i16 zero()\n" +
				             "   +- statementList\n" +
				             "      +- a :=\n" +
				             "      |  +- literal 0\n" +
				             "      +- return\n" +
				             "         +- read var a\n", TreePrinter.print(AstFactory.parseString(
				"var a = 1;\n" +
						"int twice(int a) return a * 2;\n" +
						"int zero() {\n" +
						"var a = 0;\n" +
						"return a;\n" +
						"}")));
	}
}
