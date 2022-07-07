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
		assertEquals("""
				             +- a :=
				                +- literal 0
				             """, TreePrinter.print(AstFactory.parseString("var a = 0;")));
		assertEquals("""
				             +- a :=
				                +- literal false
				             """, TreePrinter.print(AstFactory.parseString("var a = false;")));
		assertEquals("""
				             +- a : Foobar =
				                +- literal 0
				             """, TreePrinter.print(AstFactory.parseString("Foobar a = 0;")));
		assertEquals("""
				             +- a :=
				                +- cast to u16
				                   +- literal 0
				             """, TreePrinter.print(AstFactory.parseString("var a = (u16)0;")));
		assertEquals("""
				             +- a :=
				                +- cast to i8
				                   +- operator +
				                      +- literal 0
				                      +- literal 1000
				             """, TreePrinter.print(AstFactory.parseString("var a = (i8)0 + 1000;")));
		assertEquals("""
				             +- a :=
				                +- operator <
				                   +- literal 0
				                   +- literal 1000
				             """, TreePrinter.print(AstFactory.parseString("var a = 0 < 1000;")));
		assertEquals("""
				             +- a :=
				             |  +- literal 0
				             +- b :=
				                +- operator +
				                   +- read var a
				                   +- literal 1
				             """, TreePrinter.print(AstFactory.parseString("""
						                                                           var a = 0;
						                                                           var b = a + 1;
						                                                           """)));
		assertEquals("""
				             +- void wom(i16 x)
				             |  +- statementList
				             |     +- b :=
				             |        +- operator +
				             |           +- read var a
				             |           +- literal 1
				             +- i16 getLength(i16 address)
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
				             +- a :=
				             |  +- literal 1
				             +- i16 twice(i16 a)
				             |  +- statementList
				             |     +- return
				             |        +- operator *
				             |           +- read var a
				             |           +- literal 2
				             +- i16 zero()
				                +- statementList
				                   +- a :=
				                   |  +- literal 0
				                   +- return
				                      +- read var a
				             """, TreePrinter.print(AstFactory.parseString("""
						                                                           var a = 1;
						                                                           int twice(int a) return a * 2;
						                                                           int zero() {
						                                                             var a = 0;
						                                                             return a;
						                                                           }""")));
		assertEquals("""
				             +- void test(i16 a)
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
				             +- void printDigit(i16 v)
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
	}
}