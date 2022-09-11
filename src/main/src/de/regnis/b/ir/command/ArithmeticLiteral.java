package de.regnis.b.ir.command;

import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record ArithmeticLiteral(@NotNull ArithmeticOp op, int register, int literal) implements Command {

	// Setup ==================================================================

	public ArithmeticLiteral(@NotNull ArithmeticOp op, int register, int literal) {
		this.op       = op;
		this.register = register;
		this.literal  = literal & 0xFF;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return op + " " + Command.register(register) + ", #%" + Utils.toHex2(literal);
	}
}
