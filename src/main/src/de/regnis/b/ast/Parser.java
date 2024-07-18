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
			consume();
			if (token == TokenType.EOF) {
				break;
			}

			final Type type = getType(consumeIdentifier());

			final Position position = getPosition();
			final String name = consumeIdentifier();

			consume(TokenType.PAREN_L);
			consume(TokenType.PAREN_R);

			final StatementList statementList = getStatementList();
			declarations.add(new FuncDeclaration(type, name, FuncDeclarationParameters.of(List.of()), statementList, position));
		}
		return DeclarationList.of(declarations);
	}

	@NotNull
	private StatementList getStatementList() {
		consume(TokenType.BRACE_L);

		final StatementList statementList = new StatementList();
		while (token != TokenType.BRACE_R) {
			if (isConsume(TokenType.VAR)) {
				statementList.add(getVarDeclaration());
			}
			else {
				throw new InvalidSyntaxException("Unexpected token " + token, getPosition());
			}
		}

		return statementList;
	}

	private Statement getVarDeclaration() {
		final Position position = getPosition();
		final String name = consumeIdentifier();
		consume(TokenType.EQ);

		final Expression expression = getExpression(0);
		// optional ;
		isConsume(TokenType.SEMICOLON);
		return new VarDeclaration(name, expression, position);
	}

	private Expression getExpression(int precedence) {
		Expression left;
		if (token == TokenType.INT) {
			left = new NumberLiteral((int)lexer.getIntValue());
			consume();
		}
		else if (isConsume(TokenType.TRUE)) {
			left = NumberLiteral.TRUE;
		}
		else if (isConsume(TokenType.FALSE)) {
			left = NumberLiteral.FALSE;
		}
		else if (token == TokenType.IDENTIFIER) {
			left = new VarRead(consumeIdentifier(), getPosition());
		}
		else {
			throw new InvalidSyntaxException("Expected literal or var", getPosition());
		}

		while (true) {
			final BinaryExpression.Op operator = getOperator();
			if (operator == null) {
				return left;
			}

			consume();
			final Expression right = getExpression(precedence);
			left = new BinaryExpression(left, operator, right);
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

	private boolean isConsume(TokenType type) {
		if (token == type) {
			consume();
			return true;
		}
		return false;
	}

	private void consume(TokenType type) {
		expect(type);
		consume();
	}

	private String consumeIdentifier() {
		expect(TokenType.IDENTIFIER);
		final String identifier = getText();
		consume();
		return identifier;
	}

	private void consume() {
		token = lexer.next();
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
}
