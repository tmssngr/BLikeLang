package de.regnis.b;

import de.regnis.b.ast.*;

import org.jetbrains.annotations.*;

import static de.regnis.utils.Utils.assertTrue;

/**
 * @author Thomas Singer
 */
public final class Lexer {

	public static final String MSG_INVALID_CHAR = "Invalid char";
	public static final String MSG_INVALID_CHAR_ESCAPE = "Invalid char escape";
	public static final String MSG_STRING_NOT_CLOSED = "String not closed";
	public static final String MSG_INVALID_TOKEN = "Invalid token";
	private static final String MSG_INVALID_HEX_NUMBER = "Invalid hex number";

	private final StringBuilder text = new StringBuilder();
	private final CharSource source;

	private int chr;
	private int pending;

	private long intValue;

	private int line;
	private int column;
	private Position position;

	public Lexer(@NotNull String text) {
		this(new CharSource() {
			private int index;

			@Override
			public int next() {
				return index == text.length() ? -1 : text.charAt(index++);
			}
		});
	}

	public Lexer(@NotNull CharSource source) {
		this.source = source;
	}

	public TokenType next() {
		text.setLength(0);
		intValue = 0;
		position = new Position(line, column);

		nextCharSkipWhitespace();
		if (chr < 0) {
			return TokenType.EOF;
		}

		if (isDecDigit()) {
			detectNumber();
			return TokenType.INT;
		}

		switch (chr) {
		case '+' -> {
			return TokenType.PLUS;
		}
		case '-' -> {
			return TokenType.MINUS;
		}
		case '*' -> {
			return TokenType.STAR;
		}
		case '/' -> {
			return TokenType.SLASH;
		}
		case '%' -> {
			return TokenType.PERCENT;
		}
		case '=' -> {
			nextChar();
			if (chr == '=') {
				return TokenType.EQ_EQ;
			}
			pushBack();
			return TokenType.EQ;
		}
		case '<' -> {
			nextChar();
			if (chr == '<') {
				return TokenType.LT_LT;
			}
			else if (chr == '=') {
				return TokenType.LT_EQ;
			}
			pushBack();
			return TokenType.LT;
		}
		case '>' -> {
			nextChar();
			if (chr == '>') {
				return TokenType.GT_GT;
			}
			else if (chr == '=') {
				return TokenType.GT_EQ;
			}
			pushBack();
			return TokenType.GT;
		}
		case '(' -> {
			return TokenType.PAREN_L;
		}
		case ')' -> {
			return TokenType.PAREN_R;
		}
		case '{' -> {
			return TokenType.BRACE_L;
		}
		case '}' -> {
			return TokenType.BRACE_R;
		}
		case '&' -> {
			return TokenType.AND;
		}
		case '|' -> {
			return TokenType.OR;
		}
		case '^' -> {
			return TokenType.XOR;
		}
		case ';' -> {
			return TokenType.SEMICOLON;
		}
		case '\'' -> {
			detectStringOrChar(true);
			intValue = text.charAt(0);
			return TokenType.INT;
		}
		case '"' -> {
			detectStringOrChar(false);
			return TokenType.STRING;
		}
		}

		if (isLetter()) {
			detectIdentifier();
			return switch (getText()) {
				case "var" -> TokenType.VAR;
				case "true" -> TokenType.TRUE;
				case "false" -> TokenType.FALSE;
				default -> TokenType.IDENTIFIER;
			};
		}
		throw new InvalidTokenException(MSG_INVALID_TOKEN, position);
	}

	public long getIntValue() {
		return intValue;
	}

	public String getText() {
		return text.toString();
	}

	@NotNull
	public Position getPosition() {
		return position;
	}

	private void detectNumber() {
		if (chr == '0') {
			nextChar();
			if (chr == 'x') {
				nextChar();
				if (!isHexDigit()) {
					throw new InvalidTokenException(MSG_INVALID_HEX_NUMBER, createPrevPosition());
				}

				do {
					intValue = 16 * intValue + getHexNibble();
					nextChar();
				}
				while (isHexDigit());
				if (isLetter() || chr == '_') {
					throw new InvalidTokenException(MSG_INVALID_TOKEN, position);
				}
				pushBack();
				return;
			}

			if (!isDecDigit()) {
				if (isLetter()) {
					throw new InvalidTokenException(MSG_INVALID_TOKEN, createPrevPosition());
				}
				pushBack();
				return;
			}
		}

		do {
			intValue = 10 * intValue + (chr - '0');
			nextChar();
		}
		while (isDecDigit());
		if (isLetter() || chr == '_') {
			throw new InvalidTokenException(MSG_INVALID_TOKEN, position);
		}
		pushBack();
	}

	@NotNull
	private Position createPrevPosition() {
		return new Position(line, column - 1);
	}

	private void detectStringOrChar(boolean isChar) {
		boolean escaped = false;
		while (true) {
			nextChar();
			if (chr < 0) {
				throw new InvalidTokenException(isChar ? MSG_INVALID_CHAR : MSG_STRING_NOT_CLOSED, isChar ? position : new Position(line, column));
			}
			if (escaped) {
				escaped = false;
				final Position position = new Position(line, column - 2);
				append(switch (chr) {
					case 't' -> '\t';
					case 'n' -> '\n';
					case 'r' -> '\r';
					case 'x' -> {
						boolean tooShort = true;
						int hexValue = 0;
						while (true) {
							nextChar();
							if (chr < 0) {
								if (tooShort) {
									throw new InvalidTokenException(MSG_INVALID_CHAR_ESCAPE, position);
								}
								break;
							}

							if (!isHexDigit()) {
								if (tooShort) {
									throw new InvalidTokenException(MSG_INVALID_CHAR_ESCAPE, position);
								}
								pushBack();
								break;
							}

							hexValue = (16 * hexValue + getHexNibble()) & 0xFFFF;
							tooShort = false;
						}
						yield hexValue;
					}
					default -> chr;
				});
			}
			else if (isChar && chr == '\'') {
				if (text.length() != 1) {
					throw new InvalidTokenException(MSG_INVALID_CHAR, position);
				}
				break;
			}
			else if (!isChar && chr == '"') {
				break;
			}
			else if (isChar && text.length() == 1) {
				throw new InvalidTokenException(MSG_INVALID_CHAR, new Position(line, column - 1));
			}
			else if (chr == '\\') {
				escaped = true;
			}
			else if (chr == '\r' || chr == '\n') {
				throw new InvalidTokenException(MSG_STRING_NOT_CLOSED, new Position(line, column - 1));
			}
			else {
				append();
			}
		}
	}

	private void detectIdentifier() {
		do {
			append();
			nextChar();
		}
		while (chr == '_'
		       || isLetter()
		       || isDecDigit());
		pushBack();
	}

	private void append() {
		append(chr);
	}

	private void append(int chr) {
		//noinspection NumericCastThatLosesPrecision
		text.append((char)chr);
	}

	private int getHexNibble() {
		assertTrue(isHexDigit());

		int value = chr - '0';
		if (value < 10) {
			return value;
		}

		value -= 7;
		return value & 0x0F;
	}

	private boolean isHexDigit() {
		return isDecDigit()
		       || isInRange(chr, 'A', 'F')
		       || isInRange(chr, 'a', 'f');
	}

	private boolean isLetter() {
		return isInRange(chr, 'A', 'Z')
		       || isInRange(chr, 'a', 'z');
	}

	private boolean isDecDigit() {
		return isInRange(chr, '0', '9');
	}

	private boolean isWhitespace() {
		return " \t\r\n".indexOf(chr) >= 0;
	}

	private void pushBack() {
		pending = chr;
	}

	private void nextCharSkipWhitespace() {
		do {
			nextChar();
		}
		while (isWhitespace());
	}

	private void nextChar() {
		if (pending != 0) {
			chr = pending;
			pending = 0;
			return;
		}

		if (chr < 0) {
			return;
		}

		chr = source.next();
		if (chr == '\n') {
			line++;
			column = 0;
		}
		else {
			column++;
		}
	}

	private static boolean isInRange(int chr, char from, char to) {
		return from <= chr && chr <= to;
	}
}
