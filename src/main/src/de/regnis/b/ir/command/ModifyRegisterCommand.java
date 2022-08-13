package de.regnis.b.ir.command;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Thomas Singer
 */
final class ModifyRegisterCommand extends Command {

	// Fields =================================================================

	private final Operation operation;
	private final String name;

	// Setup ==================================================================

	public ModifyRegisterCommand(@NotNull Operation operation, @NotNull String name) {
		this.operation = operation;
		this.name = name;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return operation + " " + name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final ModifyRegisterCommand that = (ModifyRegisterCommand) obj;
		return operation == that.operation && name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(operation, name);
	}

	// Inner Classes ==========================================================

	public enum Operation {
		dec, inc, decw, incw, rl, rlc, rr, rrc, push, pop
	}
}
