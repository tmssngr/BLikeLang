package de.regnis.b.ir.command;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record RegisterCommand(@NotNull Op op, int register) implements Command {

	// Implemented ============================================================

	@Override
	public String toString() {
		return op + " " + Command.register(register);
	}

	// Inner Classes ==========================================================

	public enum Op {
		push, pop, rlc, rrc, inc, incw, dec, decw, com
	}
}
