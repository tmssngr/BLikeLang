import com.syntevo.antlr.b.BLikeLangLexer;
import com.syntevo.antlr.b.BLikeLangParser;
import node.StatementListNode;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Thomas Singer
 */
public class Main {

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
	}
}
