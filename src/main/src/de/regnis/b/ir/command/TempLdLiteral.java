package de.regnis.b.ir.command;

import de.regnis.utils.Utils;

/**
 * @author Thomas Singer
 */
public record TempLdLiteral(int register, int literal) implements Command {

	// Setup ==================================================================

	public TempLdLiteral {
		Utils.assertTrue(register % 2 == 0);
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return "_ldw " + Command.register(register) + ", #%" + Utils.toHex4(literal);
	}
}
