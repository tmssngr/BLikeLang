import org.antlr.v4.runtime.misc.ParseCancellationException;

/**
 * @author Thomas Singer
 */
public class ParseFailedException extends ParseCancellationException {

	// Setup ==================================================================

	public ParseFailedException(String message, int line, int column) {
		super("line " + line + ":" + column + ": " + message);
	}
}
