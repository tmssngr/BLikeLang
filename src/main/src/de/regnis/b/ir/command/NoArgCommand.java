package de.regnis.b.ir.command;

import java.util.Locale;

/**
 * @author Thomas Singer
 */
public enum NoArgCommand implements Command {

	Return, Scf, Ccf;

	// Implemented ============================================================

	@Override
	public String toString() {
		return name().toLowerCase(Locale.ROOT);
	}
}
