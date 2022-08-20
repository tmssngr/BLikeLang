package de.regnis.b.ir.command;

/**
 * @author Thomas Singer
 */
public record LoadC(int register, int literal) implements Command {

	// Implemented ============================================================

	@Override
	public String toString() {
		return "load %" + register + ", #" + literal;
	}
}
