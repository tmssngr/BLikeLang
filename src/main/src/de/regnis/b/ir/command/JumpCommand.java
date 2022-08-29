package de.regnis.b.ir.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Thomas Singer
 */
public record JumpCommand(@Nullable JumpCondition condition, @NotNull String label) implements Command {

	// Setup ==================================================================

	public JumpCommand(@NotNull String label) {
		this(null, label);
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		if (condition != null) {
			return "jp " + condition + ", " + label;
		}
		return "jp " + label;
	}
}
