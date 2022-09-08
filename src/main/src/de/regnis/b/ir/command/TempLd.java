package de.regnis.b.ir.command;

import de.regnis.utils.Utils;

/**
 * @author Thomas Singer
 */
public record TempLd(int destRegister, int srcRegister) implements Command {

	// Setup ==================================================================

	public TempLd {
		Utils.assertTrue(destRegister % 2 == 0);
		Utils.assertTrue(srcRegister % 2 == 0);
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return "_ldw " + Command.register(destRegister) + ", " + Command.register(srcRegister);
	}
}
