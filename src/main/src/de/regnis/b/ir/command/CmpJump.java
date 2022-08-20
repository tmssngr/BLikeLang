package de.regnis.b.ir.command;

/**
 * @author Thomas Singer
 */
public record CmpJump(int destRegister,
                      int srcRegister,
                      JumpCondition trueCondition, String trueLabel,
                      JumpCondition falseCondition, String falseLabel) implements Command {
	@Override
	public String toString() {
		return "cmp %" + destRegister + ", %" + srcRegister + " ; jp " + trueCondition + ", " + trueLabel + " ; jp " + falseCondition + ", " + falseLabel;
	}
}
