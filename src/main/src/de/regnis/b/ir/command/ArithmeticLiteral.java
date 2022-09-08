package de.regnis.b.ir.command;

import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record ArithmeticLiteral(@NotNull ArithmeticOp op, int register, int literal) implements Command {

	// Implemented ============================================================

	@Override
	public String toString() {
		return op + " " + Command.register(register) + ", #%" + Utils.toHex2(literal);
	}
}
