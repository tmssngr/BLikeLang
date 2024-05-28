package de.regnis.b;

/**
 * @author Thomas Singer
 */
public enum TokenType {
	EOF,
	INT, STRING,
	PLUS, MINUS, STAR, SLASH, PERCENT, LT_LT, GT_GT, AND, OR, XOR,
	EQ, LT, LT_EQ, EQ_EQ, GT_EQ, GT,
	SEMICOLON,
	BRACE_L, BRACE_R, CBRACKET_L, CBRACKET_R,
	IDENTIFIER,
	TRUE, FALSE,
	VAR
}
