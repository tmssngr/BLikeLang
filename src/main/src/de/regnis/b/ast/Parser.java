package de.regnis.b.ast;

import de.regnis.b.*;
import de.regnis.b.type.*;

import java.util.*;

import org.jetbrains.annotations.*;

/**
 * @author Thomas Singer
 */
public class Parser {

	@NotNull
	public static DeclarationList parseString(@NotNull String s) {
		final Parser parser = new Parser(s);
		return parser.getDeclarations();
	}

	private final Lexer lexer;

	private TokenType token;

	public Parser(String s) {
		lexer = new Lexer(s);
	}

	private DeclarationList getDeclarations() {
		final List<Declaration> declarations = new ArrayList<>();
		while (true) {
			next();
			if (token == TokenType.EOF) {
				break;
			}

			final Type type = getType(getIdentifier());

			next();
			final Position position = getPosition();
			final String name = getIdentifier();

			next();
			expect(TokenType.BRACE_L);

			next();
			expect(TokenType.BRACE_R);

			final StatementList statementList = getStatementList();
			declarations.add(new FuncDeclaration(type, name, FuncDeclarationParameters.of(List.of()), statementList, position));
		}
		return DeclarationList.of(declarations);
	}

	@NotNull
	private StatementList getStatementList() {
		next();
		expect(TokenType.CBRACKET_L);

		final StatementList statementList = new StatementList();
		while (true) {
			next();
			if (token == TokenType.CBRACKET_R) {
				break;
			}

			if (token == TokenType.VAR) {
				statementList.add(getVarDeclaration());
			}
		}

		return statementList;
	}

	private Statement getVarDeclaration() {
		next();
		final Position position = getPosition();
		final String name = getIdentifier();

		next();
		expect(TokenType.EQ);

		final Expression expression = getExpression(0);

		return new VarDeclaration(name, expression, position);
	}

	private Expression getExpression(int precedence) {
		next();

		Expression left;
		if (token == TokenType.INT) {
			left = new NumberLiteral((int)lexer.getIntValue());
		}
		else if (token == TokenType.TRUE) {
			left = NumberLiteral.TRUE;
		}
		else if (token == TokenType.FALSE) {
			left = NumberLiteral.FALSE;
		}
		else if (token == TokenType.IDENTIFIER) {
			left = new VarRead(getIdentifier(), getPosition());
		}
		else {
			throw new InvalidSyntaxException("Expected literal or var", getPosition());
		}

		next();
		while (true) {
			if (token == TokenType.SEMICOLON) {
				return left;
			}

			final BinaryExpression.Op operator = getOperator();
			if (operator != null) {
				final Expression right = getExpression(precedence);
				left = new BinaryExpression(left, operator, right);
			}
			else {
				throw new InvalidSyntaxException("Unexpected token " + token, getPosition());
			}
		}
	}

	@Nullable
	private BinaryExpression.Op getOperator() {
		return switch (token) {
			case PLUS -> BinaryExpression.Op.add;
			case MINUS -> BinaryExpression.Op.sub;
			case STAR -> BinaryExpression.Op.multiply;
			case SLASH -> BinaryExpression.Op.divide;
			case PERCENT -> BinaryExpression.Op.modulo;
			case LT_LT -> BinaryExpression.Op.shiftL;
			case GT_GT -> BinaryExpression.Op.shiftR;
			case LT -> BinaryExpression.Op.lessThan;
			case LT_EQ -> BinaryExpression.Op.lessEqual;
			case EQ_EQ -> BinaryExpression.Op.equal;
			case GT_EQ -> BinaryExpression.Op.greaterEqual;
			case GT -> BinaryExpression.Op.greaterThan;
			case AND -> BinaryExpression.Op.bitAnd;
			case OR -> BinaryExpression.Op.bitOr;
			case XOR -> BinaryExpression.Op.bitXor;
			default -> null;
		};
	}

	private Type getType(String identifier) {
		return BasicTypes.getType(identifier, true);
	}

	private String getIdentifier() {
		expect(TokenType.IDENTIFIER);
		return getText();
	}

	private void expect(TokenType type) {
		if (token != type) {
			throw new InvalidSyntaxException("Expected " + type + " but got " + token, getPosition());
		}
	}

	private Position getPosition() {
		return lexer.getPosition();
	}

	@NotNull
	private String getText() {
		return lexer.getText();
	}

	private void next() {
		token = lexer.next();
	}
}
