package de.regnis.b.ir.command;

/**
 * @author Thomas Singer
 */
public record CmpCJump(int register,
                       int literal,
                       JumpCondition trueCondition, String trueLabel,
                       JumpCondition falseCondition, String falseLabel) implements Command {
	@Override
	public String toString() {
		return "cmp %" + register + ", #" + literal + " ; jp " + trueCondition + ", " + trueLabel + " ; jp " + falseCondition + ", " + falseLabel;
	}
}
