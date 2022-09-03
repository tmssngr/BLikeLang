package de.regnis.b.ir.command;

/**
 * @author Thomas Singer
 */
public record LdToMem(int targetRegister, int sourceRegister) implements Command {

	// Implemented ============================================================

	@Override
	public String toString() {
		return "lde @rr" + targetRegister + ", r" + sourceRegister;
	}
}
