package de.regnis.b.ast;

import de.regnis.b.out.*;

import org.junit.*;

import static org.junit.Assert.*;

/**
 * @author Thomas Singer
 */
public class ParserTest {

	@Test
	public void testVarDeclaration() {
		assertEquals("""
				             +- a :=
				                +- literal 0
				             """, parseAndPrintStatement("var a = 0;"));
		assertEquals("""
				             +- a :=
				                +- literal 10
				             """, parseAndPrintStatement("var a = 0xA;"));
		assertEquals("""
				             +- a :=
				                +- literal 65
				             """, parseAndPrintStatement("var a = 'A';"));
		assertEquals("""
				             +- a :=
				                +- literal 0
				             """, parseAndPrintStatement("var a = false;"));
		assertEquals("""
				             +- a :=
				                +- operator +
				                   +- literal 1
				                   +- literal 2
				             """, parseAndPrintStatement("var a = 1 + 2;"));
		assertEquals("""
				             +- a :=
				                +- operator -
				                   +- literal 1
				                   +- literal 2
				             """, parseAndPrintStatement("var a = 1 - 2;"));
		assertEquals("""
				             +- a :=
				                +- operator *
				                   +- literal 2
				                   +- literal 4
				             """, parseAndPrintStatement("var a = 2 * 4;"));
		assertEquals("""
				             +- a :=
				                +- operator /
				                   +- literal 10
				                   +- literal 2
				             """, parseAndPrintStatement("var a = 10 / 2;"));
		assertEquals("""
				             +- a :=
				                +- operator %
				                   +- literal 16
				                   +- literal 6
				             """, parseAndPrintStatement("var a = 16 % 6;"));
		assertEquals("""
				             +- a :=
				                +- operator <<
				                   +- literal 1
				                   +- literal 4
				             """, parseAndPrintStatement("var a = 1 << 4;"));
		assertEquals("""
				             +- a :=
				                +- operator >>
				                   +- literal 192
				                   +- literal 4
				             """, parseAndPrintStatement("var a = 192 >> 4;"));
		assertEquals("""
				             +- a :=
				                +- operator <
				                   +- literal 0
				                   +- literal 1000
				             """, parseAndPrintStatement("var a = 0 < 1000;"));
		assertEquals("""
				             +- a :=
				             |  +- literal 0
				             +- b :=
				             |  +- operator +
				             |     +- read var a
				             |     +- literal 1
				             +- c :=
				             |  +- operator &
				             |     +- read var b
				             |     +- literal 15
				             +- d :=
				             |  +- operator |
				             |     +- read var c
				             |     +- literal 16
				             +- e :=
				                +- operator ^
				                   +- read var d
				                   +- literal 1
				             """, parseAndPrintStatement("""
						                                         var a = 0;
						                                         var b = a + 1;
						                                         var c = b & 15;
						                                         var d = c | 16;
						                                         var e = d ^ 1;
						                                         """));	}

	private String parseAndPrintStatement(String statements) {
		final DeclarationList root = Parser.parseString("int method() {\n" + statements + "\n}");
		final StatementList statementsList = root.getFunction("method").statementList();
		final StringOutput output = new StringStringOutput();
		for (String string : new TreePrinter().getStrings(null, statementsList)) {
			output.print(string);
			output.println();
		}
		return output.toString();
	}
}