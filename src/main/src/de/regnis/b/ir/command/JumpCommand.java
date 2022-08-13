package de.regnis.b.ir.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Thomas Singer
 */
final class JumpCommand extends Command {

	// Fields =================================================================

	@Nullable
	private final Condition condition;
	private final Supplier<String> target;

	// Setup ==================================================================

	public JumpCommand(@NotNull Condition condition, @NotNull Supplier<String> target) {
		this.condition = condition;
		this.target = target;
	}

	public JumpCommand(@NotNull Supplier<String> target) {
		condition = null;
		this.target = target;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return condition != null
				? "jp " + condition + ", " + target.get()
				: "jp " + target.get();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final JumpCommand that = (JumpCommand) obj;
		return condition == that.condition && target.get().equals(that.target.get());
	}

	@Override
	public int hashCode() {
		return Objects.hash(condition, target);
	}

	// Inner Classes ==========================================================

	public enum Condition {
		z, nz, lt, ult, le, ule, ge, uge, gt, ugt
	}
}
