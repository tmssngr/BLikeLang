package de.regnis.b.ast;

import com.syntevo.antlr.b.BLikeLangBaseVisitor;
import com.syntevo.antlr.b.BLikeLangLexer;
import com.syntevo.antlr.b.BLikeLangParser;
import de.regnis.b.ParseFailedException;
import de.regnis.b.type.BasicTypes;
import de.regnis.b.type.Type;
import de.regnis.utils.Utils;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Thomas Singer
 */
@SuppressWarnings("MethodDoesntCallSuperMethod")
public final class AstFactory extends BLikeLangBaseVisitor<Object> {

	// Static =================================================================

	public static DeclarationList parseFile(Path file) throws IOException {
		try (InputStream stream = Files.newInputStream(file)) {
			return parse(CharStreams.fromStream(stream));
		}
	}

	public static DeclarationList parseString(String s) {
		return parse(CharStreams.fromString(s));
	}

	// Fields =================================================================

	private final List<Declaration> declarationList = new ArrayList<>();

	// Setup ==================================================================

	private AstFactory() {
	}

	// Implemented ============================================================

	@Override
	public DeclarationList visitRoot(BLikeLangParser.RootContext ctx) {
		return visitDeclarations(ctx.declarations());
	}

	@Override
	public DeclarationList visitDeclarations(BLikeLangParser.DeclarationsContext ctx) {
		visitChildren(ctx);
		return DeclarationList.of(declarationList);
	}

	@Nullable
	@Override
	public Object visitFunctionDeclaration(BLikeLangParser.FunctionDeclarationContext ctx) {
		final String typeString = ctx.type.getText();
		final Type type;
		if ("void".equals(typeString)) {
			type = BasicTypes.VOID;
		}
		else if ("int".equals(typeString)) {
			type = BasicTypes.INT16;
		}
		else {
			throw new ParseCancellationException("Unsupported type " + typeString);
		}
		final String name = ctx.name.getText();
		final FuncDeclarationParameters parameters = visitParameterDeclarations(ctx.parameterDeclarations());
		final Statement statement = (Statement) visit(ctx.statement());
		final StatementList statementList = statement.toStatementList();
		declarationList.add(new FuncDeclaration(type, name, parameters, statementList, positionFromToken(ctx.name)));
		return null;
	}

	@Override
	public FuncDeclarationParameters visitParameterDeclarations(BLikeLangParser.ParameterDeclarationsContext ctx) {
		final List<FuncDeclarationParameter> parameters = new ArrayList<>();
		final List<BLikeLangParser.ParameterDeclarationContext> declarations = ctx.parameterDeclaration();
		for (BLikeLangParser.ParameterDeclarationContext declaration : declarations) {
			parameters.add(visitParameterDeclaration(declaration));
		}
		return FuncDeclarationParameters.of(parameters);
	}

	@Override
	public FuncDeclarationParameter visitParameterDeclaration(BLikeLangParser.ParameterDeclarationContext ctx) {
		final String name = ctx.name.getText();
		return new FuncDeclarationParameter(name, positionFromToken(ctx.name));
	}

	@Nullable
	@Override
	public Object visitConstantDefinition(BLikeLangParser.ConstantDefinitionContext ctx) {
		final String name = ctx.name.getText();
		final Expression expression = (Expression) visit(ctx.expression());
		declarationList.add(new ConstDeclaration(name, expression, positionFromToken(ctx.name)));
		return null;
	}

	@Override
	public CallStatement visitCallStatement(BLikeLangParser.CallStatementContext ctx) {
		final FuncCallParameters parameters = visitFunctionCallParameters(ctx.functionCallParameters());
		return new CallStatement(ctx.func.getText(), parameters, positionFromToken(ctx.func));
	}

	@Override
	public StatementList visitBlockStatement(BLikeLangParser.BlockStatementContext ctx) {
		final StatementList statementList = new StatementList();
		for (BLikeLangParser.StatementContext statementCtx : ctx.statement()) {
			if (statementCtx instanceof BLikeLangParser.EmptyStatementContext) {
				continue;
			}

			final Statement statement = (Statement) visit(statementCtx);
			Objects.requireNonNull(statement);
			statementList.add(statement);
		}
		return statementList;
	}

	@Override
	public Assignment visitAssignStatement(BLikeLangParser.AssignStatementContext ctx) {
		final Expression expression = (Expression) visit(ctx.expression());
		final Assignment.Op operation = switch (ctx.operator.getType()) {
			case BLikeLangParser.Assign -> Assignment.Op.assign;
			case BLikeLangParser.PlusAssign -> Assignment.Op.add;
			case BLikeLangParser.MinusAssign -> Assignment.Op.sub;
			case BLikeLangParser.MultiplyAssign -> Assignment.Op.multiply;
			case BLikeLangParser.DivideAssign -> Assignment.Op.divide;
			case BLikeLangParser.ModuloAssign -> Assignment.Op.modulo;
			case BLikeLangParser.ShiftLAssign -> Assignment.Op.shiftL;
			case BLikeLangParser.ShiftRAssign -> Assignment.Op.shiftR;
			case BLikeLangParser.AndAssign -> Assignment.Op.bitAnd;
			case BLikeLangParser.OrAssign -> Assignment.Op.bitOr;
			case BLikeLangParser.XorAssign -> Assignment.Op.bitXor;
			default -> throw new ParseCancellationException();
		};
		return new Assignment(operation, ctx.var.getText(), expression, positionFromToken(ctx.var));
	}

	@Override
	public VarDeclaration visitLocalVarDeclaration(BLikeLangParser.LocalVarDeclarationContext ctx) {
		return (VarDeclaration) visit(ctx.varDeclaration());
	}

	@Override
	public VarDeclaration visitInferVarDeclaration(BLikeLangParser.InferVarDeclarationContext ctx) {
		final String name = ctx.var.getText();
		final Expression expression = (Expression) visit(ctx.expression());
		return new VarDeclaration(name, expression, positionFromToken(ctx.var));
	}

	@Override
	public VarDeclaration visitTypeVarDeclaration(BLikeLangParser.TypeVarDeclarationContext ctx) {
		final String name = ctx.var.getText();
		final Expression expression = (Expression) visit(ctx.expression());
		return new VarDeclaration(name, expression, positionFromToken(ctx.var));
	}

	@Override
	public ReturnStatement visitReturnStatement(BLikeLangParser.ReturnStatementContext ctx) {
		final BLikeLangParser.ExpressionContext expressionContext = ctx.expression();
		if (expressionContext != null) {
			final Expression expression = (Expression) visit(expressionContext);
			return new ReturnStatement(expression, positionFromToken(expressionContext.getStart()));
		}

		return new ReturnStatement(null, positionFromToken(ctx.start));
	}

	@Override
	public IfStatement visitIfStatement(BLikeLangParser.IfStatementContext ctx) {
		final BLikeLangParser.ExpressionContext expressionContext = ctx.expression();
		final Expression expression = (Expression) visit(expressionContext);

		final Statement trueStatement = (Statement) visit(ctx.trueStatement);
		final StatementList trueStatements = trueStatement.toStatementList();

		final StatementList falseStatements;
		if (ctx.falseStatement != null) {
			final Statement falseStatement = (Statement) visit(ctx.falseStatement);
			falseStatements = falseStatement.toStatementList();
		}
		else {
			falseStatements = new StatementList();
		}

		return new IfStatement(expression, trueStatements, falseStatements, positionFromToken(expressionContext.getStart()));
	}

	@Override
	public WhileStatement visitWhileStatement(BLikeLangParser.WhileStatementContext ctx) {
		final BLikeLangParser.ExpressionContext expressionContext = ctx.expression();
		final Expression expression = (Expression) visit(expressionContext);

		final Statement statement = (Statement) visit(ctx.statement());
		final StatementList statementList = statement.toStatementList();

		return new WhileStatement(expression, statementList, positionFromToken(expressionContext.getStart()));
	}

	@Override
	public BreakStatement visitBreakStatement(BLikeLangParser.BreakStatementContext ctx) {
		return new BreakStatement(positionFromToken(ctx.start));
	}

	@Override
	public Object visitEmptyStatement(BLikeLangParser.EmptyStatementContext ctx) {
		throw new UnsupportedOperationException();
	}

	@Override
	public BinaryExpression visitBinaryExpressionDash(BLikeLangParser.BinaryExpressionDashContext ctx) {
		final Expression left = (Expression) visit(ctx.left);
		final Expression right = (Expression) visit(ctx.right);

		return new BinaryExpression(left,
		                            switch (ctx.operator.getType()) {
			                            case BLikeLangLexer.Plus -> BinaryExpression.Op.add;
			                            case BLikeLangLexer.Minus -> BinaryExpression.Op.sub;
			                            default -> throw new ParseCancellationException();
		                            },
		                            right);
	}

	@Override
	public BinaryExpression visitBinaryExpressionPoint(BLikeLangParser.BinaryExpressionPointContext ctx) {
		final Expression left = (Expression) Objects.requireNonNull(visit(ctx.left));
		final Expression right = (Expression) Objects.requireNonNull(visit(ctx.right));

		return new BinaryExpression(left,
		                            switch (ctx.operator.getType()) {
			                            case BLikeLangLexer.Multiply -> BinaryExpression.Op.multiply;
			                            case BLikeLangLexer.Divide -> BinaryExpression.Op.divide;
			                            case BLikeLangLexer.Modulo -> BinaryExpression.Op.modulo;
			                            case BLikeLangLexer.ShiftL -> BinaryExpression.Op.shiftL;
			                            case BLikeLangLexer.ShiftR -> BinaryExpression.Op.shiftR;
			                            default -> throw new ParseCancellationException();
		                            },
		                            right);
	}

	@Override
	public BinaryExpression visitBinaryExpressionBits(BLikeLangParser.BinaryExpressionBitsContext ctx) {
		final Expression left = (Expression) Objects.requireNonNull(visit(ctx.left));
		final Expression right = (Expression) Objects.requireNonNull(visit(ctx.right));

		return new BinaryExpression(left,
		                            switch (ctx.operator.getType()) {
			                            case BLikeLangLexer.BitAnd -> BinaryExpression.Op.bitAnd;
			                            case BLikeLangLexer.BitOr -> BinaryExpression.Op.bitOr;
			                            case BLikeLangLexer.BitXor -> BinaryExpression.Op.bitXor;
			                            default -> throw new ParseCancellationException();
		                            },
		                            right);
	}

	@Override
	public BinaryExpression visitBinaryExpressionBool(BLikeLangParser.BinaryExpressionBoolContext ctx) {
		final Expression left = (Expression) visit(ctx.left);
		final Expression right = (Expression) visit(ctx.right);

		return new BinaryExpression(left,
		                            switch (ctx.operator.getType()) {
			                            case BLikeLangLexer.Lt -> BinaryExpression.Op.lessThan;
			                            case BLikeLangLexer.Le -> BinaryExpression.Op.lessEqual;
			                            case BLikeLangLexer.Eq -> BinaryExpression.Op.equal;
			                            case BLikeLangLexer.Ge -> BinaryExpression.Op.greaterEqual;
			                            case BLikeLangLexer.Gt -> BinaryExpression.Op.greaterThan;
			                            case BLikeLangLexer.Ne -> BinaryExpression.Op.notEqual;
			                            default -> throw new ParseCancellationException();
		                            },
		                            right);
	}

	@Override
	public FuncCall visitFunctionCall(BLikeLangParser.FunctionCallContext ctx) {
		final FuncCallParameters parameters = visitFunctionCallParameters(ctx.functionCallParameters());
		return new FuncCall(ctx.func.getText(), parameters, positionFromToken(ctx.func));
	}

	@Override
	public FuncCallParameters visitFunctionCallParameters(BLikeLangParser.FunctionCallParametersContext ctx) {
		final List<BLikeLangParser.ExpressionContext> expressionCtxs = ctx.expression();
		final List<Expression> expressions = new ArrayList<>();
		for (BLikeLangParser.ExpressionContext expression : expressionCtxs) {
			final Expression expressionNode = (Expression) Objects.requireNonNull(visit(expression));
			expressions.add(expressionNode);
		}
		return FuncCallParameters.of(expressions);
	}

	@Override
	public Expression visitExpressionInParenthesis(BLikeLangParser.ExpressionInParenthesisContext ctx) {
		return (Expression) visit(ctx.expression());
	}

	@Override
	public Expression visitSubExpression(BLikeLangParser.SubExpressionContext ctx) {
		return (Expression) visitChildren(ctx);
	}

	@Override
	public NumberLiteral visitNumberLiteral(BLikeLangParser.NumberLiteralContext ctx) {
		final Token number = ctx.Number().getSymbol();
		final String text = number.getText();
		try {
			final int value = parseInt(text);
			return new NumberLiteral(value);
		}
		catch (NumberFormatException | BasicTypes.UnsupportedTypeException e) {
			throw new ParseFailedException("Invalid number: " + text, number.getLine(), number.getCharPositionInLine());
		}
	}

	@Override
	public NumberLiteral visitCharLiteral(BLikeLangParser.CharLiteralContext ctx) {
		final String text = ctx.getText();
		final String parsed = Utils.parseString(text, '\'');
		if (parsed == null || parsed.length() != 1) {
			throw new ParseFailedException("invalid char " + text, ctx.start.getLine(), ctx.start.getCharPositionInLine());
		}

		final char chr = parsed.charAt(0);
		if (chr >= 0x80) {
			throw new ParseFailedException("char out of bounds " + text, ctx.start.getLine(), ctx.start.getCharPositionInLine());
		}

		return new NumberLiteral(chr);
	}

	@Override
	public NumberLiteral visitBooleanLiteral(BLikeLangParser.BooleanLiteralContext ctx) {
		final String text = ctx.BooleanLiteral().getText();
		return NumberLiteral.get("true".equals(text));
	}

	@Override
	public VarRead visitReadVariable(BLikeLangParser.ReadVariableContext ctx) {
		final Token varName = ctx.Identifier().getSymbol();
		return new VarRead(varName.getText(), positionFromToken(varName));
	}

	// Utils ==================================================================

	private int parseInt(String text) {
		final String hexPrefix = "0x";
		if (text.startsWith(hexPrefix)) {
			return Integer.parseInt(text.substring(hexPrefix.length()), 16);
		}
		return Integer.parseInt(text);
	}

	@NotNull
	private static Position positionFromToken(@NotNull Token token) {
		return new Position(token.getLine(), token.getCharPositionInLine());
	}

	private static DeclarationList parse(CharStream charStream) {
		final BLikeLangLexer lexer = new BLikeLangLexer(charStream);
		final TokenStream tokenStream = new CommonTokenStream(lexer);

		final BLikeLangParser parser = new BLikeLangParser(tokenStream);
		parser.addErrorListener(new BaseErrorListener() {
			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
				throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg);
			}
		});

		final BLikeLangParser.RootContext rootContext = parser.root();
		final AstFactory astFactory = new AstFactory();
		return astFactory.visitRoot(rootContext);
	}
}
