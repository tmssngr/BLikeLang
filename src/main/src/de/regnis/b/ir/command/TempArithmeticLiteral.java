package de.regnis.b.ir.command;

import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record TempArithmeticLiteral(@NotNull ArithmeticOp op, int register, int literal) implements Command {

	// Setup ==================================================================

	public TempArithmeticLiteral {
		Utils.assertTrue(op != ArithmeticOp.adc && op != ArithmeticOp.sbc && op != ArithmeticOp.cp);
		Utils.assertTrue(register % 2 == 0);
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return "_" + op + "w " + Command.register(register) + ", #%" + Utils.toHex2(literal);
	}
}
