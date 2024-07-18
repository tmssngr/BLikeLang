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
	PAREN_L, PAREN_R, BRACE_L, BRACE_R,
	IDENTIFIER,
	TRUE, FALSE,
	VAR
}
