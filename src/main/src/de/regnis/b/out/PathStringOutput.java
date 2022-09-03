package de.regnis.b.out;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * @author Thomas Singer
 */
public final class PathStringOutput implements StringOutput, AutoCloseable {

	// Fields =================================================================

	private final PrintWriter printWriter;

	// Setup ==================================================================

	public PathStringOutput(Path path) throws IOException {
		printWriter = new PrintWriter(path.toFile(), StandardCharsets.UTF_8);
	}

	// Implemented ============================================================

	@Override
	public void print(@NotNull String s) {
		printWriter.print(s);
	}

	@Override
	public void println() {
		printWriter.println();
	}

	@Override
	public void close() {
		printWriter.close();
	}
}
