package de.regnis.b.ir.command;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record CallCommand(@NotNull String label) implements Command {

	// Implemented ============================================================

	@Override
	public String toString() {
		return "call " + label;
	}
}
