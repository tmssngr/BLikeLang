package de.regnis.b.ir.command;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record Label(@NotNull String name) implements Command {

	// Implemented ============================================================

	@Override
	public String toString() {
		return name + ':';
	}
}
