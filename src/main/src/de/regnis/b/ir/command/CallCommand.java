package de.regnis.b.ir.command;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author Thomas Singer
 */
final class CallCommand extends Command {

	// Constants ==============================================================

	public static final String CALL_PARAMETER = "cp";
	public static final String RETURN_VALUE = "rv";

	// Fields =================================================================

	private final String name;

	// Setup ==================================================================

	public CallCommand(@NotNull String name, @NotNull Set<String> writeVars) {
		this.name = name;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return "call " + name;
	}
}
