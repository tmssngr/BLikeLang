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

		@Override
		public void visit(Visitor visitor) {
			visitor.ret();
		}
	};

	BrilCommand NOP = new BrilCommand() {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("nop");
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.nop();
		}
	};

	// Accessing ==============================================================

	void appendTo(Consumer<String> output);

	void visit(Visitor visitor);

	interface Visitor {
		void label(String label);

		void branch(BranchCondition condition, String target);

		void jump(String target);

		void call(String target);

		void load16(int dest, int src);

		void load16SP(int dest);

		void load8(int dest, int src);

		void loadConst8(int dest, int value);

		void loadConst16(int dest, int value);

		void loadFromMem8(int dest, int addressReg);

		void storeToMem8(int addressReg, int src);

		void add16(int dest, int src);

		void addConst16(int dest, int value);

		void sub16(int dest, int src);

		void mul16(int dest, int src);

		void div16(int dest, int src);

		void mod16(int dest, int src);

		void cp8(int dest, int src);

		void or8(int dest, int src);

		void push8(int src);

		void pop8(int dest);

		void ret();

		void nop();
	}

	// Inner Classes ==========================================================

	record Label(String label) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept(label + ":");
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.label(label);
		}
	}

	enum BranchCondition { Z, NZ, LT, GT, ULE, UGE }

	record Branch(BranchCondition condition, String target) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("jp " + condition + ", " + target);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.branch(condition, target);
		}
	}

	record Jump(String target) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("jp " + target);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.jump(target);
		}
	}

	record Call(String target) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("call " + target);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.call(target);
		}
	}

	record Load16(int dest, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("ld r" + dest + ", r" + src);
			output.accept("ld r" + (dest + 1) + ", r" + (src + 1));
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.load16(dest, src);
		}
	}

	record Load16SP(int dest) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("ld r" + dest + ", %FE");
			output.accept("ld r" + (dest + 1) + ", %FF");
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.load16SP(dest);
		}
	}

	record Load8(int dest, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("ld r" + dest + ", r" + src);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.load8(dest, src);
		}
	}

	record LoadConst8(int dest, int value) implements BrilCommand {
		public LoadConst8(int dest, int value) {
			this.dest  = dest;
			this.value = Utils.lowByte(value);
		}

		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("ld r" + dest + ", #" + Utils.toHex2(value));
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.loadConst8(dest, value);
		}
	}

	record LoadConst16(int dest, int value) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("ld r" + dest + ", #" + Utils.highByte(value));
			output.accept("ld r" + (dest + 1) + ", #" + Utils.lowByte(value));
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.loadConst16(dest, value);
		}
	}

	record LoadFromMem8(int dest, int addressReg) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("ldc r" + dest + ", rr" + addressReg);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.loadFromMem8(dest, addressReg);
		}
	}

	record StoreToMem8(int addressReg, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("ldc rr" + addressReg + ", r" + src);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.storeToMem8(addressReg, src);
		}
	}

	record Add16(int dest, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("add r" + (dest + 1) + ", r" + (src + 1));
			output.accept("adc r" + dest + ", r" + src);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.add16(dest, src);
		}
	}

	record AddConst16(int dest, int value) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			if (value == 1) {
				output.accept("incw r" + dest);
			}
			else {
				output.accept("add r" + (dest + 1) + ", #" + Utils.lowByte(value));
				output.accept("adc r" + dest + ", #" + Utils.highByte(value));
			}
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.addConst16(dest, value);
		}
	}

	record Sub16(int dest, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("sub r" + (dest + 1) + ", r" + (src + 1));
			output.accept("sbc r" + dest + ", r" + src);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.sub16(dest, src);
		}
	}

	record Mul16(int dest, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			Utils.todo();
			output.accept("MUL r" + (dest + 1) + ", r" + (src + 1));
			output.accept("MUL r" + dest + ", r" + src);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.mul16(dest, src);
		}
	}

	record Div16(int dest, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			Utils.todo();
			output.accept("DIV r" + (dest + 1) + ", r" + (src + 1));
			output.accept("DIV r" + dest + ", r" + src);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.div16(dest, src);
		}
	}

	record Mod16(int dest, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			Utils.todo();
			output.accept("MOD r" + (dest + 1) + ", r" + (src + 1));
			output.accept("MOD r" + dest + ", r" + src);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.mod16(dest, src);
		}
	}

	record Cp8(int dest, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("cp r" + dest + ", r" + src);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.cp8(dest, src);
		}
	}

	record Or8(int dest, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("or r" + dest + ", r" + src);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.or8(dest, src);
		}
	}

	record Push8(int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("push r" + src);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.push8(src);
		}
	}

	record Pop8(int dest) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("pop r" + dest);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.pop8(dest);
		}
	}
}
