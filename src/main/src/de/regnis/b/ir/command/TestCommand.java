package de.regnis.b.ir.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Thomas Singer
 */
final class TestCommand extends Command {

	// Fields =================================================================

	private final Operation operation;
	private final String name;
	private final int constant;
	@Nullable
	private final String sourceVar;

	// Setup ==================================================================

	public TestCommand(@NotNull Operation operation, @NotNull String name, int constant) {
		this.operation = operation;
		this.name = name;
		this.constant = constant & 0xFF;
		sourceVar = null;
	}

	public TestCommand(@NotNull Operation operation, @NotNull String name, @NotNull String sourceVar) {
		this.operation = operation;
		this.name = name;
		constant = 0;
		this.sourceVar = sourceVar;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return sourceVar != null
				? operation + " " + name + ", " + sourceVar
				: operation + " " + name + ", #" + constant;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final TestCommand that = (TestCommand) obj;
		return constant == that.constant && operation == that.operation && name.equals(that.name) && Objects.equals(sourceVar, that.sourceVar);
	}

	@Override
	public int hashCode() {
		return Objects.hash(operation, name, constant, sourceVar);
	}

	// Inner Classes ==========================================================

	public enum Operation {
		or, cmp, tm, tcm
	}
}
