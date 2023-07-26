package de.regnis.bril;

import de.regnis.utils.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Thomas Singer
 */
public class BrilAsmInterpreter {

	// Constants ==============================================================

	public static final int UNKNOWN = 0x100;

	// Fields =================================================================

	private final Map<String, Integer> labelToIndex = new HashMap<>();
	private final List<BrilAsm> commands;
	private final int[] registers;
	private final int[] ram;
	private final BrilAsm.Visitor visitor;

	private int sp;
	private int ip;
	private int executedCommandCount;
	private boolean finished;
	private boolean flagZ;
	private boolean flagC;
	private boolean flagS;
	private boolean flagV;

	// Setup ==================================================================

	public BrilAsmInterpreter(List<BrilAsm> commands, CallHandler callHandler) {
		this.commands = new ArrayList<>(commands);

		for (int i = 0, commandsSize = commands.size(); i < commandsSize; i++) {
			final BrilAsm command = commands.get(i);
			if (command instanceof BrilAsm.Label label) {
				if (labelToIndex.containsKey(label.label())) {
					throw new InterpretingFailedException("duplicate label " + label);
				}
				labelToIndex.put(label.label(), i);
			}
		}

		registers = new int[16];
		Arrays.fill(registers, UNKNOWN);

		//noinspection CheckForOutOfMemoryOnLargeArrayAllocation
		ram = new int[256];
		Arrays.fill(ram, UNKNOWN);

		sp = ram.length;

		visitor = new BrilAsm.Visitor() {
			@Override
			public void label(String label) {
				executedCommandCount--;
			}

			@Override
			public void nop() {
			}

			@Override
			public void loadConst8(int dest, int value) {
				setValue8(dest, value);
			}

			@Override
			public void loadConst16(int dest, int value) {
				setValue16(dest, value);
			}

			@Override
			public void load8(int dest, int src) {
				setValue8(dest, getValue8(src));
			}

			@Override
			public void load16(int dest, int src) {
				setValue16(dest, getValue16(src));
			}

			@Override
			public void load16SP(int dest) {
				setValue16(dest, sp);
			}

			@Override
			public void loadFromMem8(int dest, int addressReg) {
				setValue8(dest, ram[getValue16(addressReg)]);
			}

			@Override
			public void storeToMem8(int addressReg, int src) {
				ram[getValue16(addressReg)] = getValue8(src);
			}

			@Override
			public void addConst16(int dest, int value) {
				final int destValue = getValue16(dest);
				setValue16(dest, destValue + value);
			}

			@Override
			public void add16(int dest, int src) {
				final int destValue = getValue16(dest);
				final int srcValue = getValue16(src);
				setValue16(dest, destValue + srcValue);
			}

			@Override
			public void sub16(int dest, int src) {
				final int destValue = getValue16(dest);
				final int srcValue = getValue16(src);
				setValue16(dest, destValue - srcValue);
			}

			@Override
			public void mul16(int dest, int src) {
				final int destValue = getValue16(dest);
				final int srcValue = getValue16(src);
				setValue16(dest, destValue * srcValue);
			}

			@Override
			public void div16(int dest, int src) {
				final int destValue = getValue16(dest);
				final int srcValue = getValue16(src);
				setValue16(dest, destValue / srcValue);
			}

			@Override
			public void mod16(int dest, int src) {
				final int destValue = getValue16(dest);
				final int srcValue = getValue16(src);
				setValue16(dest, destValue % srcValue);
			}

			@Override
			public void cp8(int dest, int src) {
				final int destValue = getValue8(dest);
				final int srcValue = getValue8(src);
				final int result = destValue - srcValue;
				//Cleared if there is a carry from the most significant bit of the result. Set otherwise
				//indicating a borrow.
				flagC = result != Utils.lowByte(result);
				flagS = (result & 0x80) != 0;
				//Set if arithmetic overflow occurred (if the operands were of opposite sign and the
				//sign of the result is the same as the sign of the source); reset otherwise.
				flagV = (destValue & 0x80) != (srcValue & 0x80)
						&& (result & 0x80) == (srcValue & 0x80);
				flagZ = result == 0;
			}

			@Override
			public void or8(int dest, int src) {
				final int result = getValue8(dest)
						| getValue8(src);
				// flagC unchanged
				flagS           = (result & 0x80) != 0;
				flagV           = false;
				flagZ           = result == 0;
				registers[dest] = result;
			}

			@Override
			public void branch(BrilAsm.BranchCondition condition, String target) {
				//0111 7 C    Carry                           C = 1
				//1111 F NC   No Carry                        C = 0
				//0110 6 Z    Zero                            Z = 1
				//1110 E NZ   Non-Zero                        Z = 0
				//1101 D PL   Plus                            S = 0
				//0101 5 Ml   Minus                           S = 1
				//0100 4 OV   Overflow                        V = 1
				//1100 C NOV  No Overflow                     V = 0
				//1001 9 GE   Greater Than or Equal           (S XOR V) = 0
				//0001 1 LT   Less Than                       (S XOR V) = 1
				//1010 A GT   Greater Than                    (Z OR (S XOR V)) = 0
				//0010 2 LE   Less Than or Equal              (Z OR (S XOR V)) = 1
				//1111 F UGE  Unsigned Greater Than or Equal  C = 0
				//0111 7 ULT  Unsigned Less Than              C = 1
				//1011 B UGT  Unsigned Greater Than           (C = 0 AND Z = 0) = 1
				//0011 3 ULE  Unsigned Less Than or Equal     (C OR Z) = 1
				final boolean result = switch (condition) {
					case Z, EQ -> flagZ;
					case NZ, NEQ -> !flagZ;
					case C, ULT -> flagC;
					case NC, UGE -> !flagC;
					case MI -> flagS;
					case PL -> !flagS;
					case OV -> flagV;
					case NOV -> !flagV;
					case LT -> flagS != flagV;
					case GE -> flagS == flagV;
					case LE -> (flagZ | (flagS ^ flagV));
					case GT -> !(flagZ | (flagS ^ flagV));
					case UGT -> !flagC && !flagZ;
					case ULE -> flagC || flagZ;
				};
				if (result) {
					jump(target);
				}
			}

			@Override
			public void jump(String target) {
				ip = getLabel(target);
			}

			@Override
			public void call(String target) {
				if (callHandler.handleCall(target,
				                           new RegisterAndRamAccess() {
					                           @Override
					                           public int getRegisterValue8(int register) {
						                           return getValue8(register);
					                           }

					                           @Override
					                           public int getStackValue8(int offset) {
						                           return BrilAsmInterpreter.this.getStackValue8(offset);
					                           }

					                           @Override
					                           public void setRegisterValue8(int register, int value) {
						                           setValue8(register, value);
					                           }

					                           @Override
					                           public void setRegisterValue16(int register, int value) {
						                           setValue16(register, value);
					                           }
				                           })) {
					return;
				}

				final int address = getLabel(target);
				pushValue8(Utils.lowByte(ip));
				pushValue8(Utils.highByte(ip));
				ip = address;
			}

			@Override
			public void ret() {
				if (sp == ram.length) {
					finished = true;
					return;
				}

				final int upper = popValue8();
				final int lower = popValue8();
				if ((upper & UNKNOWN) != 0
						|| (lower & UNKNOWN) != 0) {
					throw new InterpretingFailedException("invalid address on stack");
				}

				ip = upper << 8 | lower;
			}

			@Override
			public void push8(int src) {
				int value = registers[src];
				if (value == UNKNOWN) {
					value = UNKNOWN | src;
				}
				pushValue8(value);
			}

			@Override
			public void pop8(int dest) {
				int value = popValue8();
				if ((value & UNKNOWN) != 0) {
					if (value != (UNKNOWN | dest)) {
						throw new InterpretingFailedException("pop restored unknown value to wrong register " + dest + " instead of " + (value & ~UNKNOWN));
					}

					value = UNKNOWN;
				}
				registers[dest] = value;
			}
		};
	}

	// Accessing ==============================================================

	public void run() {
		run(null);
	}

	public void run(@Nullable String function) {
		if (function != null) {
			ip = getLabel(function);
		}

		while (!finished) {
			final BrilAsm command = commands.get(ip++);
			command.visit(visitor);
			executedCommandCount++;
		}
	}

	public int getExecutedCommandCount() {
		return executedCommandCount;
	}

	public int getIp() {
		return ip;
	}

	public void iterateRegisters(ByteConsumer consumer) {
		for (int i = 0; i < registers.length; i++) {
			consumer.consumer(i, registers[i]);
		}
	}

	public boolean isFlagZ() {
		return flagZ;
	}

	public boolean isFlagC() {
		return flagC;
	}

	public boolean isFlagS() {
		return flagS;
	}

	public boolean isFlagV() {
		return flagV;
	}

	// Utils ==================================================================

	private int getLabel(String label) {
		final Integer index = labelToIndex.get(label);
		if (index == null) {
			throw new InterpretingFailedException("label " + label + " not found");
		}

		return index;
	}

	private int getValue16(int dest) {
		if ((dest & 1) != 0) {
			throw new InterpretingFailedException("invalid register " + dest);
		}

		return getValue8(dest) << 8
				| getValue8(dest + 1);
	}

	private int getValue8(int dest) {
		final int value = registers[dest];
		if (Utils.lowByte(value) != value) {
			throw new InterpretingFailedException("tried reading uninitialized register " + dest);
		}
		return value;
	}

	private void setValue8(int dest, int value) {
		registers[dest] = Utils.lowByte(value);
	}

	private void setValue16(int dest, int value) {
		if ((dest & 1) != 0) {
			throw new InterpretingFailedException("invalid register " + dest);
		}

		setValue8(dest, Utils.highByte(value));
		setValue8(dest + 1, Utils.lowByte(value));
	}

	private int getStackValue8(int offset) {
		final int value = ram[sp + offset];
		if (Utils.lowByte(value) != value) {
			throw new InterpretingFailedException("tried reading uninitialized pushed value from " + offset);
		}
		return value;
	}

	private void pushValue8(int value) {
		sp--;
		ram[sp] = value;
	}

	private int popValue8() {
		final int value = ram[sp];
		sp++;
		return value;
	}

	// Inner Classes ==========================================================

	public static final class InterpretingFailedException extends RuntimeException {
		public InterpretingFailedException(String message) {
			super(message);
		}
	}

	public interface CallHandler {

		boolean handleCall(String name, RegisterAndRamAccess access);
	}

	public interface RegisterAndRamAccess {
		int getRegisterValue8(int register);

		int getStackValue8(int offset);

		void setRegisterValue8(int register, int value);

		void setRegisterValue16(int register, int value);
	}

	public interface ByteConsumer {
		void consumer(int address, int value);
	}
}
