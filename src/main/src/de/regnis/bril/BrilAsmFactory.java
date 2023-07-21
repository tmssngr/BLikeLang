package de.regnis.bril;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Thomas Singer
 */
public class BrilAsmFactory {

	// Fields =================================================================

	private final List<BrilCommand> commands = new ArrayList<>();

	// Setup ==================================================================

	private int labelCounter;

	protected BrilAsmFactory() {
	}

	// Accessing ==============================================================

	public List<String> toLines() {
		final List<String> lines = new ArrayList<>();
		toLines(line -> lines.add(line));
		return lines;
	}

	public void toLines(Consumer<String> output) {
		for (BrilCommand command : commands) {
			command.appendTo(output);
			output.accept("\n");
		}
	}

	public BrilAsmFactory simplify(Function<List<BrilCommand>, List<BrilCommand>> transformation) {
		boolean changed;
		do {
			final List<BrilCommand> newCommands = transformation.apply(Collections.unmodifiableList(commands));
			changed = !commands.equals(newCommands);
			commands.clear();
			commands.addAll(newCommands);
		}
		while (changed);
		return this;
	}

	public BrilAsmFactory label(String label) {
		addCommand(new BrilCommand.Label(label));
		return this;
	}

	public BrilAsmFactory iload(int dest, int src) {
		addCommand(new BrilCommand.Load16(dest, src));
		return this;
	}

	public BrilAsmFactory iloadFromStack(int destRegister, int spRegister, int offset) {
		addLoadStackPointer(offset);
		addCommand(new BrilCommand.LoadFromMem8(destRegister, spRegister));
		addCommand(new BrilCommand.AddConst16(spRegister, 1));
		addCommand(new BrilCommand.LoadFromMem8(destRegister + 1, spRegister));
		return this;
	}

	public BrilAsmFactory bload(int dest, int src) {
		addCommand(new BrilCommand.Load8(dest, src));
		return this;
	}

	public BrilAsmFactory bloadFromStack(int destRegister, int spRegister, int offset) {
		addLoadStackPointer(offset);
		addCommand(new BrilCommand.LoadFromMem8(destRegister, spRegister));
		return this;
	}

	@NotNull
	public BrilAsmFactory istoreToStack(int sourceRegister, int spRegister, int offset) {
		addLoadStackPointer(offset);
		addCommand(new BrilCommand.StoreToMem8(spRegister, sourceRegister));
		addCommand(new BrilCommand.AddConst16(spRegister, 1));
		addCommand(new BrilCommand.StoreToMem8(spRegister, sourceRegister + 1));
		return this;
	}

	@NotNull
	public BrilAsmFactory bstoreToStack(int sourceRegister, int spRegister, int offset) {
		addLoadStackPointer(offset);
		addCommand(new BrilCommand.StoreToMem8(spRegister, sourceRegister));
		return this;
	}

	@NotNull
	public BrilAsmFactory iadd(int dest, int src) {
		addCommand(new BrilCommand.Add16(dest, src));
		return this;
	}

	@NotNull
	public BrilAsmFactory isub(int dest, int src) {
		addCommand(new BrilCommand.Sub16(dest, src));
		return this;
	}

	@NotNull
	public BrilAsmFactory imul(int dest, int src) {
		addCommand(new BrilCommand.Mul16(dest, src));
		return this;
	}

	@NotNull
	public BrilAsmFactory idiv(int dest, int src) {
		addCommand(new BrilCommand.Div16(dest, src));
		return this;
	}

	@NotNull
	public BrilAsmFactory imod(int dest, int src) {
		addCommand(new BrilCommand.Mod16(dest, src));
		return this;
	}

	@NotNull
	public BrilAsmFactory ieq(int dest, int left, int right) {
		final String labelTrue = "comparison_" + labelCounter++;
		final String labelNext = "comparison_" + labelCounter++;
		addCommand(new BrilCommand.LoadConst8(dest, 0));
		addCommand(new BrilCommand.Cp8(left, right));
		addCommand(new BrilCommand.Branch(BrilCommand.BranchCondition.NZ, labelNext));
		addCommand(new BrilCommand.Cp8(left + 1, right + 1));
		addCommand(new BrilCommand.Branch(BrilCommand.BranchCondition.NZ, labelNext));
		addCommand(new BrilCommand.Label(labelTrue));
		addCommand(new BrilCommand.LoadConst8(dest, 255));
		addCommand(new BrilCommand.Label(labelNext));
		return this;
	}

	@NotNull
	public BrilAsmFactory ilt(int dest, int left, int right) {
		final String labelTrue = "comparison_" + labelCounter++;
		final String labelNext = "comparison_" + labelCounter++;
		addCommand(new BrilCommand.LoadConst8(dest, 0));
		addCommand(new BrilCommand.Cp8(left, right));
		addCommand(new BrilCommand.Branch(BrilCommand.BranchCondition.LT, labelTrue));
		addCommand(new BrilCommand.Branch(BrilCommand.BranchCondition.NZ, labelNext));
		addCommand(new BrilCommand.Cp8(left + 1, right + 1));
		addCommand(new BrilCommand.Branch(BrilCommand.BranchCondition.UGE, labelNext));
		addCommand(new BrilCommand.Label(labelTrue));
		addCommand(new BrilCommand.LoadConst8(dest, 255));
		addCommand(new BrilCommand.Label(labelNext));
		return this;
	}

	@NotNull
	public BrilAsmFactory igt(int dest, int left, int right) {
		final String labelTrue = "comparison_" + labelCounter++;
		final String labelNext = "comparison_" + labelCounter++;
		addCommand(new BrilCommand.LoadConst8(dest, 0));
		addCommand(new BrilCommand.Cp8(left, right));
		addCommand(new BrilCommand.Branch(BrilCommand.BranchCondition.GT, labelTrue));
		addCommand(new BrilCommand.Branch(BrilCommand.BranchCondition.NZ, labelNext));
		addCommand(new BrilCommand.Cp8(left + 1, right + 1));
		addCommand(new BrilCommand.Branch(BrilCommand.BranchCondition.ULE, labelNext));
		addCommand(new BrilCommand.Label(labelTrue));
		addCommand(new BrilCommand.LoadConst8(dest, 255));
		addCommand(new BrilCommand.Label(labelNext));
		return this;
	}

	@NotNull
	public BrilAsmFactory iconst(int register, int value) {
		addCommand(new BrilCommand.LoadConst16(register, value));
		return this;
	}

	@NotNull
	public BrilAsmFactory ipush(int register) {
		addCommand(new BrilCommand.Push8(register));
		addCommand(new BrilCommand.Push8(register + 1));
		return this;
	}

	@NotNull
	public BrilAsmFactory ipop(int register) {
		addCommand(new BrilCommand.Pop8(register + 1));
		addCommand(new BrilCommand.Pop8(register));
		return this;
	}

	public BrilAsmFactory call(String target) {
		addCommand(new BrilCommand.Call(target));
		return this;
	}

	public BrilAsmFactory allocSpace(int byteCount) {
		for (int i = 0; i < byteCount; i++) {
			addCommand(new BrilCommand.Push8(0));
		}
		return this;
	}

	public BrilAsmFactory freeSpace(int byteCount) {
		for (int i = 0; i < byteCount; i++) {
			addCommand(new BrilCommand.Pop8(0));
		}
		return this;
	}

	public BrilAsmFactory ret() {
		addCommand(BrilCommand.RET);
		return this;
	}

	public BrilAsmFactory brIfElse(int register, String thenTarget, String elseTarget) {
		addCommand(new BrilCommand.Or8(register, register));
		addCommand(new BrilCommand.Branch(BrilCommand.BranchCondition.Z, elseTarget));
		addCommand(new BrilCommand.Jump(thenTarget));
		return this;
	}

	public BrilAsmFactory brElse(int register, String elseTarget) {
		addCommand(new BrilCommand.Or8(register, register));
		addCommand(new BrilCommand.Branch(BrilCommand.BranchCondition.Z, elseTarget));
		return this;
	}

	public BrilAsmFactory jump(String targetLabel) {
		addCommand(new BrilCommand.Jump(targetLabel));
		return this;
	}

	// Utils ==================================================================

	private void addCommand(@NotNull BrilCommand command) {
		commands.add(command);
	}

	private void addLoadStackPointer(int offset) {
		addCommand(new BrilCommand.Load16SP(14));
		addCommand(new BrilCommand.AddConst16(14, offset));
	}
}
