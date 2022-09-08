package de.regnis.b.ir.command;

import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record TempArithmetic(@NotNull ArithmeticOp op, int destRegister, int srcRegister) implements Command {

	// Setup ==================================================================

	public TempArithmetic {
		Utils.assertTrue(op != ArithmeticOp.adc && op != ArithmeticOp.sbc && op != ArithmeticOp.cp);
		Utils.assertTrue(destRegister % 2 == 0);
		Utils.assertTrue(srcRegister % 2 == 0);
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return "_" + op + "w " + Command.register(destRegister) + ", " + Command.register(srcRegister);
	}
}
