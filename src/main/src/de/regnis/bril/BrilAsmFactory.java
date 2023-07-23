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

	private final List<BrilAsm> commands = new ArrayList<>();

	// Setup ==================================================================

	private int labelCounter;

	protected BrilAsmFactory() {
	}

	// Accessing ==============================================================

	public List<BrilAsm> getCommands() {
		return Collections.unmodifiableList(commands);
	}

	public List<String> toLines() {
		final List<String> lines = new ArrayList<>();
		toLines(line -> lines.add(line));
		return lines;
	}

	public void toLines(Consumer<String> output) {
		for (BrilAsm command : commands) {
			command.appendTo(output);
			output.accept("\n");
		}
	}

	public BrilAsmFactory simplify(Function<List<BrilAsm>, List<BrilAsm>> transformation) {
		boolean changed;
		do {
			final List<BrilAsm> newCommands = transformation.apply(Collections.unmodifiableList(commands));
			changed = !commands.equals(newCommands);
			commands.clear();
			commands.addAll(newCommands);
		}
		while (changed);
		return this;
	}

	public BrilAsmFactory label(String label) {
		addCommand(new BrilAsm.Label(label));
		return this;
	}

	public BrilAsmFactory iload(int dest, int src) {
		addCommand(new BrilAsm.Load16(dest, src));
		return this;
	}

	public BrilAsmFactory iloadFromStack(int destRegister, int spRegister, int offset) {
		addLoadStackPointer(offset);
		addCommand(new BrilAsm.LoadFromMem8(destRegister, spRegister));
		addCommand(new BrilAsm.AddConst16(spRegister, 1));
		addCommand(new BrilAsm.LoadFromMem8(destRegister + 1, spRegister));
		return this;
	}

	public BrilAsmFactory bload(int dest, int src) {
		addCommand(new BrilAsm.Load8(dest, src));
		return this;
	}

	public BrilAsmFactory bloadFromStack(int destRegister, int spRegister, int offset) {
		addLoadStackPointer(offset);
		addCommand(new BrilAsm.LoadFromMem8(destRegister, spRegister));
		return this;
	}

	@NotNull
	public BrilAsmFactory istoreToStack(int sourceRegister, int spRegister, int offset) {
		addLoadStackPointer(offset);
		addCommand(new BrilAsm.StoreToMem8(spRegister, sourceRegister));
		addCommand(new BrilAsm.AddConst16(spRegister, 1));
		addCommand(new BrilAsm.StoreToMem8(spRegister, sourceRegister + 1));
		return this;
	}

	@NotNull
	public BrilAsmFactory bstoreToStack(int sourceRegister, int spRegister, int offset) {
		addLoadStackPointer(offset);
		addCommand(new BrilAsm.StoreToMem8(spRegister, sourceRegister));
		return this;
	}

	@NotNull
	public BrilAsmFactory iadd(int dest, int src) {
		addCommand(new BrilAsm.Add16(dest, src));
		return this;
	}

	@NotNull
	public BrilAsmFactory isub(int dest, int src) {
		addCommand(new BrilAsm.Sub16(dest, src));
		return this;
	}

	@NotNull
	public BrilAsmFactory imul(int dest, int src) {
		addCommand(new BrilAsm.Mul16(dest, src));
		return this;
	}

	@NotNull
	public BrilAsmFactory idiv(int dest, int src) {
		addCommand(new BrilAsm.Div16(dest, src));
		return this;
	}

	@NotNull
	public BrilAsmFactory imod(int dest, int src) {
		addCommand(new BrilAsm.Mod16(dest, src));
		return this;
	}

	@NotNull
	public BrilAsmFactory ieq(int dest, int left, int right) {
		final String labelTrue = "comparison_" + labelCounter++;
		final String labelNext = "comparison_" + labelCounter++;
		addCommand(new BrilAsm.LoadConst8(dest, 0));
		addCommand(new BrilAsm.Cp8(left, right));
		addCommand(new BrilAsm.Branch(BrilAsm.BranchCondition.NZ, labelNext));
		addCommand(new BrilAsm.Cp8(left + 1, right + 1));
		addCommand(new BrilAsm.Branch(BrilAsm.BranchCondition.NZ, labelNext));
		addCommand(new BrilAsm.Label(labelTrue));
		addCommand(new BrilAsm.LoadConst8(dest, 255));
		addCommand(new BrilAsm.Label(labelNext));
		return this;
	}

	@NotNull
	public BrilAsmFactory ilt(int dest, int left, int right) {
		final String labelTrue = "comparison_" + labelCounter++;
		final String labelNext = "comparison_" + labelCounter++;
		addCommand(new BrilAsm.LoadConst8(dest, 0));
		addCommand(new BrilAsm.Cp8(left, right));
		addCommand(new BrilAsm.Branch(BrilAsm.BranchCondition.LT, labelTrue));
		addCommand(new BrilAsm.Branch(BrilAsm.BranchCondition.NZ, labelNext));
		addCommand(new BrilAsm.Cp8(left + 1, right + 1));
		addCommand(new BrilAsm.Branch(BrilAsm.BranchCondition.UGE, labelNext));
		addCommand(new BrilAsm.Label(labelTrue));
		addCommand(new BrilAsm.LoadConst8(dest, 255));
		addCommand(new BrilAsm.Label(labelNext));
		return this;
	}

	@NotNull
	public BrilAsmFactory igt(int dest, int left, int right) {
		final String labelTrue = "comparison_" + labelCounter++;
		final String labelNext = "comparison_" + labelCounter++;
		addCommand(new BrilAsm.LoadConst8(dest, 0));
		addCommand(new BrilAsm.Cp8(left, right));
		addCommand(new BrilAsm.Branch(BrilAsm.BranchCondition.GT, labelTrue));
		addCommand(new BrilAsm.Branch(BrilAsm.BranchCondition.NZ, labelNext));
		addCommand(new BrilAsm.Cp8(left + 1, right + 1));
		addCommand(new BrilAsm.Branch(BrilAsm.BranchCondition.ULE, labelNext));
		addCommand(new BrilAsm.Label(labelTrue));
		addCommand(new BrilAsm.LoadConst8(dest, 255));
		addCommand(new BrilAsm.Label(labelNext));
		return this;
	}

	@NotNull
	public BrilAsmFactory iconst(int register, int value) {
		addCommand(new BrilAsm.LoadConst16(register, value));
		return this;
	}

	@NotNull
	public BrilAsmFactory bconst(int register, boolean value) {
		addCommand(new BrilAsm.LoadConst8(register, value ? 255 : 0));
		return this;
	}

	@NotNull
	public BrilAsmFactory ipush(int register) {
		addCommand(new BrilAsm.Push8(register));
		addCommand(new BrilAsm.Push8(register + 1));
		return this;
	}

	@NotNull
	public BrilAsmFactory ipop(int register) {
		addCommand(new BrilAsm.Pop8(register + 1));
		addCommand(new BrilAsm.Pop8(register));
		return this;
	}

	public BrilAsmFactory call(String target) {
		addCommand(new BrilAsm.Call(target));
		return this;
	}

	public BrilAsmFactory allocSpace(int byteCount) {
		for (int i = 0; i < byteCount; i++) {
			addCommand(new BrilAsm.Push8(0));
		}
		return this;
	}

	public BrilAsmFactory freeSpace(int byteCount) {
		for (int i = 0; i < byteCount; i++) {
			addCommand(new BrilAsm.Pop8(0));
		}
		return this;
	}

	public BrilAsmFactory ret() {
		addCommand(BrilAsm.RET);
		return this;
	}

	public BrilAsmFactory brIfElse(int register, String thenTarget, String elseTarget) {
		addCommand(new BrilAsm.Or8(register, register));
		addCommand(new BrilAsm.Branch(BrilAsm.BranchCondition.Z, elseTarget));
		addCommand(new BrilAsm.Jump(thenTarget));
		return this;
	}

	public BrilAsmFactory brElse(int register, String elseTarget) {
		addCommand(new BrilAsm.Or8(register, register));
		addCommand(new BrilAsm.Branch(BrilAsm.BranchCondition.Z, elseTarget));
		return this;
	}

	public BrilAsmFactory jump(String targetLabel) {
		addCommand(new BrilAsm.Jump(targetLabel));
		return this;
	}

	// Utils ==================================================================

	private void addCommand(@NotNull BrilAsm command) {
		commands.add(command);
	}

	private void addLoadStackPointer(int offset) {
		addCommand(new BrilAsm.Load16SP(14));
		addCommand(new BrilAsm.AddConst16(14, offset));
	}
}
