package de.regnis.b.ir.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Thomas Singer
 */
final class StoreCommand extends Command {

	// Fields =================================================================

	private final String name;
	private final int constant;
	@Nullable
	private final String sourceVar;

	// Setup ==================================================================

	public StoreCommand(@NotNull String name, int constant) {
		this.name = name;
		this.constant = constant & 0xFF;
		sourceVar = null;
	}

	public StoreCommand(@NotNull String name, @NotNull String sourceVar) {
		this.name = name;
		constant = 0;
		this.sourceVar = sourceVar;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return "ld " + name + ", " + (sourceVar != null ? sourceVar : "#" + constant);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final StoreCommand that = (StoreCommand) obj;
		return constant == that.constant && name.equals(that.name) && Objects.equals(sourceVar, that.sourceVar);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, constant, sourceVar);
	}
}
