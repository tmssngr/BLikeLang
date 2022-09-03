package de.regnis.b.ir.command;

/**
 * @author Thomas Singer
 */
public record Ld(int targetRegister, int sourceRegister) implements Command {

	// Implemented ============================================================

	@Override
	public String toString() {
		return "ld " + Command.register(targetRegister) + ", " + Command.register(sourceRegister);
	}
}
