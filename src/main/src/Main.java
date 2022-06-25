import com.syntevo.antlr.b.BLikeLangLexer;
import com.syntevo.antlr.b.BLikeLangParser;
import de.regnis.b.AstFactory;
import de.regnis.b.ConstantFoldingTransformation;
import de.regnis.b.ParseFailedException;
import de.regnis.b.SplitExpressionsTransformation;
import de.regnis.b.node.*;
import de.regnis.b.out.StringOutput;
import node.NodeVisitor;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

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
		StatementListNode rootAst = astFactory.visitRoot(rootContext);

		checkVariables(rootAst);
		final TreePrinter printer = new TreePrinter();
		printer.print(rootAst, StringOutput.out);

		rootAst = SplitExpressionsTransformation.createTempVars(rootAst);
		new CodePrinter().print(rootAst, StringOutput.out);

		rootAst = ConstantFoldingTransformation.transform(rootAst);
		new CodePrinter().print(rootAst, StringOutput.out);
	}

	private static void checkVariables(StatementListNode root) {
		final StatementVisitor<?> visitor = new NodeVisitor<>() {
			private final Set<String> definedVariables = new HashSet<>();

			@Nullable
			@Override
			public Object visitAssignment(AssignmentNode node) {
				super.visitAssignment(node);

				final String var = node.var;
				if (!definedVariables.contains(var)) {
					throw new ParseFailedException("Var " + var + " undeclared", node.line, node.column);
				}
				return null;
			}

			@Nullable
			@Override
			public Object visitVarDeclaration(VarDeclarationNode node) {
				super.visitVarDeclaration(node);

				final String var = node.var;
				if (definedVariables.contains(var)) {
					throw new ParseFailedException("Var " + var + " already defined", node.line, node.column);
				}
				definedVariables.add(var);
				return null;
			}

			@Nullable
			@Override
			public Object visitVarRead(VarReadNode node) {
				final String var = node.var;
				if (!definedVariables.contains(var)) {
					throw new ParseFailedException("Var " + var + " undeclared", node.line, node.column);
				}
				return null;
			}
		};
		visitor.visitStatementList(root);
	}
}
