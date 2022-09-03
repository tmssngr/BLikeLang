package de.regnis.b.ir.command;

/**
 * @author Thomas Singer
 */
public record LdFromMem(int targetRegister, int sourceRegister) implements Command {

	// Implemented ============================================================

	@Override
	public String toString() {
		return "lde r" + targetRegister + ", @rr" + sourceRegister;
	}
}
