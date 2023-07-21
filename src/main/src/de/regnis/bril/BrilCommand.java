package de.regnis.bril;

import de.regnis.utils.Utils;

import java.util.function.Consumer;

/**
 * @author Thomas Singer
 */
public interface BrilCommand {

	// Constants ==============================================================

	BrilCommand RET = new BrilCommand() {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("ret");
		}
	};

	BrilCommand NOP = new BrilCommand() {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("nop");
		}
	};

	// Accessing ==============================================================

	void appendTo(Consumer<String> output);

	// Inner Classes ==========================================================

	record Label(String label) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept(label + ":");
		}
	}

	enum BranchCondition { Z, NZ, LT, GT, ULE, UGE }

	record Branch(BranchCondition condition, String target) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("jp " + condition + ", " + target);
		}
	}

	record Jump(String target) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("jp " + target);
		}
	}

	record Call(String target) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("call " + target);
		}
	}

	record Load16(int dest, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("ld r" + dest + ", r" + src);
			output.accept("ld r" + (dest + 1) + ", r" + (src + 1));
		}
	}

	record Load16SP(int dest) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("ld r" + dest + ", %FE");
			output.accept("ld r" + (dest + 1) + ", %FF");
		}
	}

	record Load8(int dest, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("ld r" + dest + ", r" + src);
		}
	}

	record LoadConst8(int dest, int value) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("ld r" + dest + ", #" + Utils.lowByte(value));
		}
	}

	record LoadConst16(int dest, int value) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("ld r" + dest + ", #" + Utils.highByte(value));
			output.accept("ld r" + (dest + 1) + ", #" + Utils.lowByte(value));
		}
	}

	record LoadFromMem8(int dest, int addressReg) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("ldc r" + dest + ", rr" + addressReg);
		}
	}

	record StoreToMem8(int addressReg, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("ldc rr" + addressReg + ", r" + src);
		}
	}

	record Add16(int dest, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("add r" + (dest + 1) + ", r" + (src + 1));
			output.accept("adc r" + dest + ", r" + src);
		}
	}

	record AddConst16(int reg, int value) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			if (value == 1) {
				output.accept("incw r" + reg);
			}
			else {
				output.accept("add r" + (reg + 1) + ", #" + Utils.lowByte(value));
				output.accept("adc r" + reg + ", #" + Utils.highByte(value));
			}
		}
	}

	record Sub16(int dest, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("sub r" + (dest + 1) + ", r" + (src + 1));
			output.accept("sbc r" + dest + ", r" + src);
		}
	}

	record Mul16(int dest, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			Utils.todo();
			output.accept("MUL r" + (dest + 1) + ", r" + (src + 1));
			output.accept("MUL r" + dest + ", r" + src);
		}
	}

	record Div16(int dest, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			Utils.todo();
			output.accept("DIV r" + (dest + 1) + ", r" + (src + 1));
			output.accept("DIV r" + dest + ", r" + src);
		}
	}

	record Mod16(int dest, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			Utils.todo();
			output.accept("MOD r" + (dest + 1) + ", r" + (src + 1));
			output.accept("MOD r" + dest + ", r" + src);
		}
	}

	record Cp(int dest, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("cp r" + dest + ", r" + src);
		}
	}

	record Or8(int dest, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("or r" + dest + ", r" + src);
		}
	}

	record Push8(int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("push r" + src);
		}
	}

	record Pop8(int dest) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("pop r" + dest);
		}
	}
}
