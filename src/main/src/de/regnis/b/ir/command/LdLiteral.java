package de.regnis.b.ir.command;

import de.regnis.utils.Utils;

/**
 * @author Thomas Singer
 */
public record LdLiteral(int register, int literal) implements Command {

	// Implemented ============================================================

	@Override
	public String toString() {
		if (register == CommandFactory.RP) {
			return "srp #%" + Utils.toHex2(literal);
		}
		return "ld " + Command.register(register) + ", #%" + Utils.toHex2(literal);
	}
}
