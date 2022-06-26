package node;

import de.regnis.b.out.StringOutput;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public class TestStringOutput implements StringOutput {
	private final StringBuilder buffer = new StringBuilder();

	@Override
	public void print(@NotNull String s) {
		buffer.append(s);
	}

	@Override
	public void println() {
		print("\n");
	}

	@Override
	public String toString() {
		return buffer.toString();
	}
}
