package de.regnis.b.ir.command;

import de.regnis.utils.Utils;

/**
 * @author Thomas Singer
 */
public record LdLiteral(int register, int literal) implements Command {

	// Implemented ============================================================

	@Override
	public String toString() {
		return "ld " + Command.register(register) + ", #%" + Utils.toHex2(literal);
	}
}
