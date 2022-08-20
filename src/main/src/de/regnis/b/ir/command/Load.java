package de.regnis.b.ir.command;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record Load(int register, @NotNull String varName) implements Command {

	// Implemented ============================================================

	@Override
	public String toString() {
		return "load %" + register + ", " + varName;
	}
}
