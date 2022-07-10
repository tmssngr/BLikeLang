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
				             +- a :=
				                +- operator +
				                   +- literal 1
				                   +- literal 2
				             """, TreePrinter.print(AstFactory.parseString("var a = 1 + 2;")));
		assertEquals("""
				             +- a :=
				                +- operator -
				                   +- literal 1
				                   +- literal 2
				             """, TreePrinter.print(AstFactory.parseString("var a = 1 - 2;")));
		assertEquals("""
				             +- a :=
				                +- operator *
				                   +- literal 2
				                   +- literal 4
				             """, TreePrinter.print(AstFactory.parseString("var a = 2 * 4;")));
		assertEquals("""
				             +- a :=
				                +- operator <<
				                   +- literal 1
				                   +- literal 4
				             """, TreePrinter.print(AstFactory.parseString("var a = 1 << 4;")));
		assertEquals("""
				             +- a :=
				                +- operator >>
				                   +- literal 192
				                   +- literal 4
				             """, TreePrinter.print(AstFactory.parseString("var a = 192 >> 4;")));
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
				             """, TreePrinter.print(AstFactory.parseString("""
						                                                           var a = 0;
						                                                           var b = a + 1;
						                                                           var c = b & 15;
						                                                           var d = c | 16;
						                                                           var e = d ^ 1;
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
				             +- void printHex4(u8 i)
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
						                                                           void printHex4(u8 i) {
						                                                             i = i & 15;
						                                                             if (i < 10) {
						                                                               print(i + 48);
						                                                             }
						                                                             else {
						                                                               print(i - 10 + 65);
						                                                             }
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
				                   |  +- literal true
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
}
