package de.regnis.b;

import de.regnis.b.ast.DeclarationList;
import de.regnis.b.ast.StatementList;
import de.regnis.b.out.StringOutput;
import de.regnis.b.out.StringStringOutput;
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
						                                         """));
		assertEquals("""
				             +- void wom(int x)
				             |  +- statementList
				             |     +- b :=
				             |        +- operator +
				             |           +- read var a
				             |           +- literal 1
				             +- int getLength(int address)
				                +- statementList
				                   +- return
				                      +- literal 0
				             """, TreePrinter.print(AstFactory.parseString("""
						                                                           void wom(int x) {
						                                                             var b = a + 1;
						                                                           }
						                                                           int getLength(int address) {
						                                                             return 0; // just for now
						                                                           }""")));
		assertEquals("""
				             +- void println()
				                +- statementList
				                   +- ignored :=
				                   |  +- function call print
				                   |     +- literal 10
				                   +- return
				             """, TreePrinter.print(AstFactory.parseString("""
						                                                           void println() {
						                                                             var ignored = print(10);
						                                                             return;
						                                                           }""")));
		assertEquals("""
				             +- int twice(int a)
				             |  +- statementList
				             |     +- return
				             |        +- operator *
				             |           +- read var a
				             |           +- literal 2
				             +- int zero()
				                +- statementList
				                   +- a :=
				                   |  +- literal 0
				                   +- return
				                      +- read var a
				             """, TreePrinter.print(AstFactory.parseString("""
						                                                           int twice(int a) return a * 2;
						                                                           int zero() {
						                                                             var a = 0;
						                                                             return a;
						                                                           }""")));
		assertEquals("""
				             +- void test(int a)
				                +- statementList
				                   +- if
				                   |  +- operator >
				                   |  |  +- read var a
				                   |  |  +- literal 0
				                   |  +- then
				                   |  |  +- a =
				                   |  |     +- operator -
				                   |  |        +- literal 0
				                   |  |        +- read var a
				                   |  +- else
				                   +- i :=
				                      +- function call print
				                         +- read var a
				             """, TreePrinter.print(AstFactory.parseString("""
						                                                           void test(int a) {
						                                                             if (a > 0) a = 0 - a;
						                                                             var i = print(a);
						                                                           }""")));
		assertEquals("""
				             +- void printHex4(int i)
				                +- statementList
				                   +- i =
				                   |  +- operator &
				                   |     +- read var i
				                   |     +- literal 15
				                   +- if
				                      +- operator <
				                      |  +- read var i
				                      |  +- literal 10
				                      +- then
				                      |  +- call print
				                      |     +- operator +
				                      |        +- read var i
				                      |        +- literal 48
				                      +- else
				                         +- call print
				                            +- operator +
				                               +- operator -
				                               |  +- read var i
				                               |  +- literal 10
				                               +- literal 65
				             """, TreePrinter.print(AstFactory.parseString("""
						                                                           void printHex4(int i) {
						                                                             i = i & 15;
						                                                             if (i < 10) {
						                                                               print(i + 48);
						                                                             }
						                                                             else {
						                                                               print(i - 10 + 65);
						                                                             }
						                                                           }""")));
		assertEquals("""
				             +- void printDigit(int v)
				                +- statementList
				                   +- if
				                   |  +- operator >
				                   |  |  +- read var v
				                   |  |  +- literal 9
				                   |  +- then
				                   |  |  +- return
				                   |  +- else
				                   +- if
				                   |  +- operator <
				                   |  |  +- read var v
				                   |  |  +- literal 0
				                   |  +- then
				                   |  |  +- return
				                   |  +- else
				                   +- result :=
				                      +- function call print
				                         +- read var v
				             """, TreePrinter.print(AstFactory.parseString("""
						                                                           void printDigit(int v) {
						                                                             if (v > 9) {
						                                                               return;
						                                                             }
						                                                             if (v < 0) {
						                                                               return;
						                                                             }
						                                                             var result = print(v);
						                                                           }""")));
		assertEquals("""
				             +- void printNL()
				                +- statementList
				                   +- call print
				                      +- literal 10
				             """, TreePrinter.print(AstFactory.parseString("""
						                                                           void printNL() {
						                                                             print(10);
						                                                           }""")));
		assertEquals("""
				             +- void main()
				                +- statementList
				                   +- i :=
				                   |  +- literal 0
				                   +- While
				                      +- operator <
				                      |  +- read var i
				                      |  +- literal 10
				                      +- do
				                         +- i =
				                            +- operator +
				                               +- read var i
				                               +- literal 1
				             """, TreePrinter.print(AstFactory.parseString("""
						                                                           void main() {
						                                                             var i = 0;
						                                                             while (i < 10) {
						                                                               i = i + 1;
						                                                             }
						                                                           }""")));
		assertEquals("""
				             +- void main()
				                +- statementList
				                   +- i :=
				                   |  +- literal 0
				                   +- While
				                   |  +- literal 1
				                   |  +- do
				                   |     +- call print
				                   |     |  +- operator +
				                   |     |     +- read var i
				                   |     |     +- literal 65
				                   |     +- i =
				                   |     |  +- operator +
				                   |     |     +- read var i
				                   |     |     +- literal 1
				                   |     +- if
				                   |        +- operator ==
				                   |        |  +- read var i
				                   |        |  +- literal 10
				                   |        +- then
				                   |        |  +- break
				                   |        +- else
				                   +- call print
				                      +- literal 13
				             """, TreePrinter.print(AstFactory.parseString("""
						                                                           void main() {
						                                                             var i = 0;
						                                                             while (true) {
						                                                               print(i + 65);
						                                                               i = i + 1;
						                                                               if (i == 10) {
						                                                                 break;
						                                                               }
						                                                             }
						                                                             print(13);
						                                                           }""")));
	}

	// Utils ==================================================================

	private String parseAndPrintStatement(String statements) {
		final DeclarationList root = AstFactory.parseString("int method() {\n" + statements + "\n}");
		final StatementList statementsList = root.getFunction("method").statementList;
		final StringOutput output = new StringStringOutput();
		for (String string : new TreePrinter().getStrings(null, statementsList)) {
			output.print(string);
			output.println();
		}
		return output.toString();
	}
}
