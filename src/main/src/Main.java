import com.syntevo.antlr.b.BLikeLangLexer;
import com.syntevo.antlr.b.BLikeLangParser;
import de.regnis.b.AstFactory;
import de.regnis.b.ParseFailedException;
import de.regnis.b.node.*;
import de.regnis.b.out.StringOutput;
import node.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Thomas Singer
 */
public final class Main {

	// Static =================================================================

	public static void main(String[] args) throws IOException {
		final Path file = Paths.get("examples/test.b");

		final TokenStream tokenStream;
		try (InputStream stream = Files.newInputStream(file)) {
			final CharStream charStream = CharStreams.fromStream(stream);
			final BLikeLangLexer lexer = new BLikeLangLexer(charStream);
			tokenStream = new CommonTokenStream(lexer);
		}

		final BLikeLangParser parser = new BLikeLangParser(tokenStream);
		parser.addErrorListener(new BaseErrorListener() {
			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
				throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg);
			}
		});

		final BLikeLangParser.RootContext rootContext = parser.root();
		final AstFactory astFactory = new AstFactory();
		final StatementListNode rootAst = astFactory.visitRoot(rootContext);

		checkVariables(rootAst);
		final TreePrinter printer = new TreePrinter();
		printer.print(printer.getStrings(rootAst), StringOutput.out);

		final StatementListNode flattenedRootAst = createTempVars(rootAst);
		new CodePrinter().print(flattenedRootAst, StringOutput.out);
	}

	private static void checkVariables(StatementListNode root) {
		final Set<String> definedVariables = new HashSet<>();
		root.visit(new NodeVisitor() {
			@Override
			public void visitDeclaration(String var, int line, int column) {
				if (definedVariables.contains(var)) {
					throw new ParseFailedException("Var " + var + " already defined", line, column);
				}
				definedVariables.add(var);
			}

			@Override
			public void visitAssignment(String var, int line, int column) {
				if (!definedVariables.contains(var)) {
					throw new ParseFailedException("Var " + var + " undeclared", line, column);
				}
			}

			@Override
			public void visitVarRead(String var, int line, int column) {
				if (!definedVariables.contains(var)) {
					throw new ParseFailedException("Var " + var + " undeclared", line, column);
				}
			}
		});
	}

	private static StatementListNode createTempVars(StatementListNode root) {
		final Supplier<String> tempVarNameProvider = new Supplier<>() {
			private int index;

			@Override
			public String get() {
				index++;
				return "t " + index;
			}
		};
		return handleStatementList(root, tempVarNameProvider);
	}

	private static StatementListNode handleStatementList(StatementListNode statementList, Supplier<String> tempVarNameProvider) {
		final StatementListNode newStatementList = new StatementListNode();

		final List<? extends StatementNode> statements = statementList.getStatements();
		for (StatementNode statement : statements) {
			if (statement instanceof VarDeclarationNode) {
				final VarDeclarationNode varDeclarationNode = (VarDeclarationNode)statement;
				final ExpressionNode simplifiedExpression = simplifyExpression(varDeclarationNode.expression, tempVarNameProvider, newStatementList);
				newStatementList.add(new VarDeclarationNode(varDeclarationNode.var, simplifiedExpression, varDeclarationNode.line, varDeclarationNode.column));
			}
			else if (statement instanceof AssignmentNode) {
				final AssignmentNode assignmentNode = (AssignmentNode)statement;
				final ExpressionNode simplifiedExpression = simplifyExpression(assignmentNode.expression, tempVarNameProvider, newStatementList);
				newStatementList.add(new AssignmentNode(assignmentNode.var, simplifiedExpression, assignmentNode.line, assignmentNode.column));
			}
			else if (statement instanceof StatementListNode) {
				newStatementList.add(handleStatementList((StatementListNode)statement, tempVarNameProvider));
			}
		}
		return newStatementList;
	}

	private static ExpressionNode simplifyExpression(ExpressionNode expression, Supplier<String> tempVarNameProvider, StatementListNode list) {
		if (expression instanceof BinaryExpressionNode) {
			final BinaryExpressionNode binaryExpressionNode = (BinaryExpressionNode)expression;
			final ExpressionNode left = simplifyExpression2(binaryExpressionNode.left, tempVarNameProvider, list);
			final ExpressionNode right = simplifyExpression2(binaryExpressionNode.right, tempVarNameProvider, list);
			return binaryExpressionNode.createNew(left, right);
		}

		return expression;
	}

	private static ExpressionNode simplifyExpression2(ExpressionNode expressionNode, Supplier<String> tempVarNameProvider, StatementListNode list) {
		if (expressionNode instanceof BinaryExpressionNode) {
			return createTempVarDeclaration((BinaryExpressionNode)expressionNode, tempVarNameProvider, list);
		}
		if (expressionNode instanceof FunctionCallNode) {
			return createTempFunctionCall((FunctionCallNode) expressionNode, tempVarNameProvider, list);
		}
		return expressionNode;
	}

	private static ExpressionNode createTempVarDeclaration(BinaryExpressionNode node, Supplier<String> tempVarNameProvider, StatementListNode list) {
		final ExpressionNode left = simplifyExpression2(node.left, tempVarNameProvider, list);
		final ExpressionNode right = simplifyExpression2(node.right, tempVarNameProvider, list);
		return createTempVar(node.createNew(left, right), tempVarNameProvider, list);
	}

	private static ExpressionNode createTempFunctionCall(FunctionCallNode node, Supplier<String> tempVarNameProvider, StatementListNode list) {
		final FunctionParametersNode parameters = new FunctionParametersNode();
		for (ExpressionNode parameter : node.getExpressions()) {
			final ExpressionNode simplifiedParameter = simplifyExpression2(parameter, tempVarNameProvider, list);
			parameters.add(simplifiedParameter);
		}
		return createTempVar(new FunctionCallNode(node.name, parameters, node.line, node.column), tempVarNameProvider, list);
	}

	@NotNull
	private static VarReadNode createTempVar(ExpressionNode node, Supplier<String> tempVarNameProvider, StatementListNode list) {
		final String tempVar = tempVarNameProvider.get();
		list.add(new VarDeclarationNode(tempVar, node, -1, -1));
		return new VarReadNode(tempVar, -1, -1);
	}
}
