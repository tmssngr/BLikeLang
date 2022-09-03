package de.regnis.b.ir.command;

import java.util.Locale;

/**
 * @author Thomas Singer
 */
public enum NoArgCommand implements Command {

	Ret, Scf, Ccf;

	// Implemented ============================================================

	@Override
	public String toString() {
		return name().toLowerCase(Locale.ROOT);
	}
}
