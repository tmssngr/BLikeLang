package de.regnis.b.ir.command;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record Arithmetic(@NotNull ArithmeticOp op, int destRegister, int srcRegister) implements Command {

	// Implemented ============================================================

	@Override
	public String toString() {
		return op + " %" + destRegister + ", %" + srcRegister;
	}
}
