package de.regnis.b.ir.command;

import de.regnis.utils.Utils;

/**
 * @author Thomas Singer
 */
public interface Command {

	String toString();

	static String register(int register) {
		if (CommandFactory.isWorkingRegister(register)) {
			return "r" + CommandFactory.getWorkingRegister(register);
		}
		return "%" + Utils.toHex2(register);
	}
}
