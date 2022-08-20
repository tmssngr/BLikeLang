package de.regnis.b.ir.command;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record Store(@NotNull String varName, int register) implements Command {

	// Implemented ============================================================

	@Override
	public String toString() {
		return "store " + varName + ", %" + register;
	}
}
