package de.regnis.b;

import org.junit.*;

import static org.junit.Assert.*;

/**
 * @author Thomas Singer
 */
public class LexerTest {

	@Test
	public void testInt() {
		new LexerTester("1") {
			@Override
			protected void test() {
				assertInt(1);
				assertEof();
			}
		}.test();

		new LexerTester("0xA") {
			@Override
			protected void test() {
				assertInt(10);
				assertEof();
			}
		}.test();

		new LexerTester("0xA;") {
			@Override
			protected void test() {
				assertInt(10);
				assertType(TokenType.SEMICOLON);
				assertEof();
			}
		}.test();

		new LexerTester("0xBABE") {
			@Override
			protected void test() {
				assertInt(0xbabe);
				assertEof();
			}
		}.test();

		new LexerTester("\n0x") {
			@Override
			protected void test() {
				assertInt(0);
			}
		}.testFailure(1, 2);

		new LexerTester("\n 0xg") {
			@Override
			protected void test() {
				assertInt(0);
			}
		}.testFailure(1, 3);
	}

	@Test
	public void testChars() {
		new LexerTester("' '") {
			@Override
			protected void test() {
				assertInt(0x20);
				assertEof();
			}
		}.test();

		new LexerTester("'\\n'") {
			@Override
			protected void test() {
				assertInt(0x0a);
				assertEof();
			}
		}.test();

		new LexerTester("'\\xaffe'") {
			@Override
			protected void test() {
				assertInt(0xaffe);
				assertEof();
			}
		}.test();

		testInvalidChar("'", Lexer.MSG_INVALID_CHAR, 0, 0);
		testInvalidChar("' ", Lexer.MSG_INVALID_CHAR, 0, 0);
		testInvalidChar("''", Lexer.MSG_INVALID_CHAR, 0, 0);
		testInvalidChar("'' ", Lexer.MSG_INVALID_CHAR, 0, 0);
		testInvalidChar("'a", Lexer.MSG_INVALID_CHAR, 0, 0);
		testInvalidChar("'a ", Lexer.MSG_INVALID_CHAR, 0, 2);
		testInvalidChar("'\\", Lexer.MSG_INVALID_CHAR, 0, 0);
		testInvalidChar("'\\n", Lexer.MSG_INVALID_CHAR, 0, 0);
		testInvalidChar("'\\n ", Lexer.MSG_INVALID_CHAR, 0, 3);
		testInvalidChar("'\\x", Lexer.MSG_INVALID_CHAR_ESCAPE, 0, 1);
		testInvalidChar("'\\x ", Lexer.MSG_INVALID_CHAR_ESCAPE, 0, 1);
		testInvalidChar("'\\xz", Lexer.MSG_INVALID_CHAR_ESCAPE, 0, 1);
		testInvalidChar("'\\xz ", Lexer.MSG_INVALID_CHAR_ESCAPE, 0, 1);
		testInvalidChar("'ab'", Lexer.MSG_INVALID_CHAR, 0, 2);
		testInvalidChar("'\\nb'", Lexer.MSG_INVALID_CHAR, 0, 3);
		testInvalidChar("'\\r\\n'", Lexer.MSG_INVALID_CHAR, 0, 3);
	}

	@Test
	public void testIdentifier() {
		new LexerTester("a") {
			@Override
			protected void test() {
				assertIdentifier("a");
				assertEof();
			}
		}.test();

		new LexerTester("a0") {
			@Override
			protected void test() {
				assertIdentifier("a0");
				assertEof();
			}
		}.test();

		new LexerTester("0a") {
			@Override
			protected void test() {
				assertInt(0);
			}
		}.testFailure(0, 1);
	}

	@Test
	public void testStatements() {
		new LexerTester("int b = a + 1;") {
			@Override
			protected void test() {
				assertIdentifier("int");
				assertIdentifier("b");
				assertType(TokenType.EQ);
				assertIdentifier("a");
				assertType(TokenType.PLUS);
				assertInt(1);
				assertType(TokenType.SEMICOLON);
				assertEof();
			}
		}.test();
	}

	@Test
	public void testMethod() {
		new LexerTester("""
				                int method() {
				                  var a = 0;
				                }""") {
			@Override
			protected void test() {
				assertIdentifier("int");
				assertIdentifier("method");
				assertType(TokenType.PAREN_L);
				assertType(TokenType.PAREN_R);
				assertType(TokenType.BRACE_L);
				assertType(TokenType.VAR);
				assertIdentifier("a");
				assertType(TokenType.EQ);
				assertInt(0);
				assertType(TokenType.SEMICOLON);
				assertType(TokenType.BRACE_R);
				assertEof();
			}
		}.test();
	}

	private static void testInvalidChar(String text, String expectedMsg, int expectedLine, int expectedColumn) {
		new LexerTester(text) {
			@Override
			protected void test() {
				try {
					assertInt(0);
					fail();
				}
				catch (InvalidTokenException ex) {
					assertInvalidTokenException(expectedMsg, expectedLine, expectedColumn, ex);
				}
			}
		}.test();
	}

	private static void assertInvalidTokenException(String expectedMsg, int expectedLine, int expectedColumn, InvalidTokenException ex) {
		assertEquals(expectedMsg, ex.getMessage());
		assertEquals(expectedLine, ex.position.line());
		assertEquals(expectedColumn, ex.position.column());
	}

	private abstract static class LexerTester {
		private final Lexer lexer;

		protected LexerTester(String text) {
			lexer = new Lexer(text);
		}

		protected void test() {
		}

		public final void testFailure(int expectedLine, int expectedColumn) {
			try {
				test();
				fail();
			}
			catch (InvalidTokenException ex) {
				assertEquals(expectedLine, ex.position.line());
				assertEquals(expectedColumn, ex.position.column());
			}
		}

		public void assertEof() {
			assertType(TokenType.EOF);
		}

		public void assertComment(String expected) {
//			_assertText(TokenType.COMMENT, expected);
		}

		public void assertIdentifier(String expected) {
			_assertText(TokenType.IDENTIFIER, expected);
		}

		public void assertInt(int expected) {
			assertType(TokenType.INT);
			assertEquals(expected, lexer.getIntValue());
		}

		public void assertLParen() {
//			_assertType(TokenType.L_PAREN);
		}

		public void assertRParen() {
//			_assertType(TokenType.R_PAREN);
		}

		public void assertString(String expected) {
//			_assertText(TokenType.STRING, expected);
		}

		public void assertType(TokenType type) {
			assertEquals(type, lexer.next());
		}

		private void _assertText(TokenType type, String expected) {
			assertType(type);
			assertEquals(expected, lexer.getText());
		}
	}
}
