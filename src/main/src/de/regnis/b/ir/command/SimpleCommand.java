package de.regnis.b.ir.command;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Thomas Singer
 */
final class SimpleCommand extends Command {

	// Fields =================================================================

	private final Operation operation;

	// Setup ==================================================================

	public SimpleCommand(@NotNull Operation operation) {
		this.operation = operation;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return operation.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final SimpleCommand that = (SimpleCommand) obj;
		return operation == that.operation;
	}

	@Override
	public int hashCode() {
		return Objects.hash(operation);
	}

	// Inner Classes ==========================================================

	public enum Operation {
		rcf, scf, ccf, ret
	}
}
