package de.regnis.b.ir.command;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record ArithmeticC(@NotNull ArithmeticOp op, int register, int literal) implements Command {

	// Implemented ============================================================

	@Override
	public String toString() {
		return op + " %" + register + ", #" + literal;
	}
}
