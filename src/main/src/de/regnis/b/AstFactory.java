package de.regnis.b;

import com.syntevo.antlr.b.BLikeLangBaseVisitor;
import com.syntevo.antlr.b.BLikeLangLexer;
import com.syntevo.antlr.b.BLikeLangParser;
import de.regnis.b.node.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * @author Thomas Singer
 */
@SuppressWarnings("MethodDoesntCallSuperMethod")
public final class AstFactory extends BLikeLangBaseVisitor<Node> {

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

	private final DeclarationList declarationList = new DeclarationList();

	@Nullable
	private StatementList statementListNode;

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
		return declarationList;
	}

	@Nullable
	@Override
	public Node visitGlobalVarDeclaration(BLikeLangParser.GlobalVarDeclarationContext ctx) {
		final VarDeclaration node = visitVarDeclaration(ctx.varDeclaration());
		declarationList.add(new GlobalVarDeclaration(node));
		return null;
	}

	@Override
	public FuncDeclaration visitFuncDeclaration(BLikeLangParser.FuncDeclarationContext ctx) {
		return visitFunctionDeclaration(ctx.functionDeclaration());
	}

	@Nullable
	@Override
	public FuncDeclaration visitFunctionDeclaration(BLikeLangParser.FunctionDeclarationContext ctx) {
		final Type type = BasicTypes.getType(ctx.type.getText(), true);
		final String name = ctx.name.getText();
		final FuncDeclarationParameters parameters = visitParameterDeclarations(ctx.parameterDeclarations());
		final Statement statement = (Statement) visit(ctx.statement());
		declarationList.add(new FuncDeclaration(type, name, parameters, statement));
		return null;
	}

	@Override
	public FuncDeclarationParameters visitParameterDeclarations(BLikeLangParser.ParameterDeclarationsContext ctx) {
		final FuncDeclarationParameters parameters = new FuncDeclarationParameters();
		final List<BLikeLangParser.ParameterDeclarationContext> declarations = ctx.parameterDeclaration();
		for (BLikeLangParser.ParameterDeclarationContext declaration : declarations) {
			parameters.add(visitParameterDeclaration(declaration));
		}
		return parameters;
	}

	@Override
	public FuncDeclarationParameter visitParameterDeclaration(BLikeLangParser.ParameterDeclarationContext ctx) {
		final Type type = BasicTypes.getType(ctx.type.getText(), false);
		final String name = ctx.name.getText();
		return new FuncDeclarationParameter(type, name, ctx.name.getLine(), ctx.name.getCharPositionInLine());
	}

	@Override
	public StatementList visitStatements(BLikeLangParser.StatementsContext ctx) {
		final StatementList outerStatementList = statementListNode;

		final StatementList statementListNode = new StatementList();
		if (outerStatementList != null) {
			outerStatementList.add(statementListNode);
		}
		this.statementListNode = statementListNode;
		try {
			visitChildren(ctx);

			assert this.statementListNode == statementListNode;
		}
		finally {
			this.statementListNode = outerStatementList;
		}
		return statementListNode;
	}

	@Override
	public Node visitBlockStatement(BLikeLangParser.BlockStatementContext ctx) {
		return visitStatements(ctx.statements());
	}

	@Nullable
	@Override
	public Node visitAssignStatement(BLikeLangParser.AssignStatementContext ctx) {
		Objects.requireNonNull(statementListNode);

		final Assignment node = visitAssignment(ctx.assignment());
		statementListNode.add(node);
		return null;
	}

	@Nullable
	@Override
	public Node visitLocalVarDeclaration(BLikeLangParser.LocalVarDeclarationContext ctx) {
		Objects.requireNonNull(statementListNode);

		final VarDeclaration node = visitVarDeclaration(ctx.varDeclaration());
		statementListNode.add(node);
		return null;
	}

	@Override
	public Assignment visitAssignment(BLikeLangParser.AssignmentContext ctx) {
		final Expression expression = (Expression) visit(ctx.expression());
		return new Assignment(ctx.var.getText(), expression, ctx.var.getLine(), ctx.var.getCharPositionInLine());
	}

	@Override
	public VarDeclaration visitVarDeclaration(BLikeLangParser.VarDeclarationContext ctx) {
		final Expression expression = (Expression) visit(ctx.expression());
		return new VarDeclaration(ctx.var.getText(), expression, ctx.var.getLine(), ctx.var.getCharPositionInLine());
	}

	@Override
	public ReturnStatement visitReturnStatement(BLikeLangParser.ReturnStatementContext ctx) {
		final Expression expression = (Expression) visit(ctx.expression());
		return new ReturnStatement(expression);
	}

	@Override
	public Node visitBinaryExpressionDash(BLikeLangParser.BinaryExpressionDashContext ctx) {
		final Expression left = (Expression) visit(ctx.left);
		final Expression right = (Expression) visit(ctx.right);

		return switch (ctx.operator.getType()) {
			case BLikeLangLexer.Plus -> BinaryExpression.createAdd(left, right);
			case BLikeLangLexer.Minus -> BinaryExpression.createSub(left, right);
			default -> throw new ParseCancellationException();
		};
	}

	@Override
	public Node visitBinaryExpressionPoint(BLikeLangParser.BinaryExpressionPointContext ctx) {
		final Expression left = (Expression) Objects.requireNonNull(visit(ctx.left));
		final Expression right = (Expression) Objects.requireNonNull(visit(ctx.right));

		//noinspection SwitchStatementWithTooFewBranches
		return switch (ctx.operator.getType()) {
			case BLikeLangLexer.Multiply -> BinaryExpression.createMultiply(left, right);
			default -> throw new ParseCancellationException();
		};
	}

	@Override
	public FuncCall visitFunctionCall(BLikeLangParser.FunctionCallContext ctx) {
		final FuncCallParameters parameters = visitFunctionCallParameters(ctx.functionCallParameters());
		return new FuncCall(ctx.func.getText(), parameters, ctx.func.getLine(), ctx.func.getCharPositionInLine());
	}

	@Override
	public FuncCallParameters visitFunctionCallParameters(BLikeLangParser.FunctionCallParametersContext ctx) {
		final FuncCallParameters parameters = new FuncCallParameters();
		final List<BLikeLangParser.ExpressionContext> expressions = ctx.expression();
		for (BLikeLangParser.ExpressionContext expression : expressions) {
			final Expression expressionNode = (Expression) Objects.requireNonNull(visit(expression));
			parameters.add(expressionNode);
		}
		return parameters;
	}

	@Override
	public Expression visitExpressionInParenthesis(BLikeLangParser.ExpressionInParenthesisContext ctx) {
		return (Expression) visit(ctx.expression());
	}

	@Override
	public NumberLiteral visitNumberLiteral(BLikeLangParser.NumberLiteralContext ctx) {
		final Token number = ctx.value;
		final String text = number.getText();
		try {
			final int suffixPos = text.indexOf('_');
			final int value;
			final BasicTypes.NumericType type;
			if (suffixPos < 0) {
				value = Integer.parseInt(text);
				type = BasicTypes.determineType(value);
			}
			else {
				value = Integer.parseInt(text.substring(0, suffixPos));
				final String suffix = text.substring(suffixPos + 1);
				type = BasicTypes.getNumbericType(suffix);
			}

			if (value < type.min || value > type.max) {
				throw new ParseFailedException("Number out of bounds: " + text, number.getLine(), number.getCharPositionInLine());
			}

			final NumberLiteral numberLiteral = new NumberLiteral(value);
			numberLiteral.setType(type);
			return numberLiteral;
		}
		catch (NumberFormatException | BasicTypes.UnsupportedTypeException e) {
			throw new ParseFailedException("Invalid number: " + text, number.getLine(), number.getCharPositionInLine());
		}
	}

	@Override
	public VarRead visitReadVariable(BLikeLangParser.ReadVariableContext ctx) {
		return new VarRead(ctx.var.getText(), ctx.var.getLine(), ctx.var.getCharPositionInLine());
	}

	// Utils ==================================================================

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
