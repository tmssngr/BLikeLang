package de.regnis.b.ir.command;

/**
 * @author Thomas Singer
 */
public record LdFromMem(int destRegister, int srcRegister) implements Command {

	// Implemented ============================================================

	@Override
	public String toString() {
		return "lde r" + destRegister + ", @rr" + srcRegister;
	}
}
