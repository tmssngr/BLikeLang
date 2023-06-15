package de.regnis.bril;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class BrilAsm {

	// Fields =================================================================

	private final List<String> output = new ArrayList<>();

	// Setup ==================================================================

	public BrilAsm() {
	}

	// Accessing ==============================================================

	public List<String> getOutput() {
		return Collections.unmodifiableList(output);
	}

	public BrilAsm label(String label) {
		output.add(label + ":");
		return this;
	}

	public BrilAsm ldFramePointer(int offset) {
		output.add("ld r14, %FE");
		output.add("ld r15, %FF");
		output.add("add r15, #" + lowByte(offset));
		output.add("adc r14, #" + highByte(offset));
		return this;
	}

	public BrilAsm iloadX(int offset) {
		return iload(0, offset);
	}

	public BrilAsm istoreX(int offset) {
		ldFramePointer(offset);
		output.add("ldc rr14, r" + offset);
		output.add("incw r14");
		output.add("ldc rr14, r" + (offset + 1));
		return this;
	}

	public BrilAsm iloadY(int offset) {
		return iload(2, offset);
	}

	public BrilAsm iaddXY() {
		output.add("add r1, r3");
		output.add("add r0, r2");
		return this;
	}

	public BrilAsm iconstX(int value) {
		output.add("ld r1, #" + lowByte(value));
		output.add("ld r0, #" + highByte(value));
		return this;
	}

	public BrilAsm ipushX() {
		output.add("push r0");
		output.add("push r1");
		return this;
	}

	public BrilAsm ipop() {
		output.add("pop r2");
		output.add("pop r2");
		return this;
	}

	public BrilAsm call(String name) {
		output.add("call " + name);
		return this;
	}

	public BrilAsm allocSpace(int byteCount) {
		for (int i = 0; i < byteCount; i++) {
			output.add("push r0");
		}
		return this;
	}

	public BrilAsm freeSpace(int byteCount) {
		for (int i = 0; i < byteCount; i++) {
			output.add("pop r0");
		}
		return this;
	}

	public BrilAsm ret() {
		output.add("ret");
		return this;
	}

	// Utils ==================================================================

	private BrilAsm iload(int register, int offset) {
		ldFramePointer(offset);
		output.add("ldc r" + register + ", rr14");
		output.add("incw r14");
		output.add("ldc r" + (register + 1) + ", rr14");
		return this;
	}

	private static int highByte(int offset) {
		return offset >> 8;
	}

	private static int lowByte(int offset) {
		return offset & 0xFF;
	}
}
