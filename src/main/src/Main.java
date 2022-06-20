import com.syntevo.antlr.b.BLikeLangLexer;
import com.syntevo.antlr.b.BLikeLangParser;
import node.NodeVisitor;
import node.StatementListNode;
import node.TreePrinter;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

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

		final BLikeLangParser.RootContext root = parser.root();
		final AstFactory astFactory = new AstFactory();
		final StatementListNode result = astFactory.visitRoot(root);

		checkVariables(result);

		final TreePrinter printer = new TreePrinter();
		printer.print(printer.getStrings(result), System.out);
	}

	private static void checkVariables(StatementListNode result) {
		final Set<String> definedVariables = new HashSet<>();
		result.visit(new NodeVisitor() {
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
}
