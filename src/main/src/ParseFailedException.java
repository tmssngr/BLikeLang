import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;

/**
 * @author Thomas Singer
 */
public class ParseFailedException extends ParseCancellationException {

	// Setup ==================================================================

	public ParseFailedException(String message, Token token) {
		super("line " + token.getLine() + ":" + token.getCharPositionInLine() + ": " + message);
	}
}
