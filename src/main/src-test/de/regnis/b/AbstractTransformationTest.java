package de.regnis.b;

import de.regnis.b.node.CodePrinter;
import de.regnis.b.node.StatementListNode;
import de.regnis.b.out.StringOutput;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

/**
 * @author Thomas Singer
 */
public abstract class AbstractTransformationTest {

	// Constants ==============================================================

	protected static final String PREFIX = "{\n\t";
	protected static final String SUFFIX = "\n}\n";
	protected static final String NL = "\n\t";

	// Static =================================================================

	protected static void assertEquals(String expected, StatementListNode root) {
		final TestStringOutput output = new TestStringOutput();
		new CodePrinter().print(root, output);
		Assert.assertEquals(expected, output.toString());
	}

	// Inner Classes ==========================================================

	private static class TestStringOutput implements StringOutput {
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
}