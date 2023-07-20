package de.regnis.bril;

import de.regnis.utils.Utils;

import java.util.*;

/**
 * rr0 ... 1st parameter for subroutine, return value
 * rr2 ... 2nd parameter for subroutine
 * rr4-rr12 ... 5 free register pairs
 * rr14 ... address helper register for accessing variables on the stack
 *
 * @author Thomas Singer
 */
final class BrilVarMapping {

	// Constants ==============================================================

	public static final int ARG0_REGISTER = 0; // call-clobbered
	public static final int ARG1_REGISTER = 2; // call-clobbered
	public static final int FP_REGISTER = 14; // call-clobbered

	// Static =================================================================

	public static BrilVarMapping createVarMapping(BrilNode function, String prefixVirtualRegister, String prefixRegister, String prefixStackParameter, int maxParametersInRegisters) {
		final List<BrilNode> arguments = BrilFactory.getArguments(function);
		final List<String> argNames = BrilFactory.getArgNames(arguments);
		final List<BrilNode> instructions = BrilFactory.getInstructions(function);
		final String functionReturnType = BrilFactory.getType(function);

		final Set<String> localVars = getLocalVars(instructions);

		final BrilVars brilVars = new BrilVars(prefixVirtualRegister, prefixRegister, prefixStackParameter, maxParametersInRegisters);
		brilVars.assignArguments(argNames);
		brilVars.assignReturnValue(functionReturnType);
		brilVars.assignLocalVariables(localVars);

		final Map<String, BrilVars.VarLocation> varToLocation = brilVars.getVarToLocationMapping();
		final int byteCountForSpilledLocalVars = brilVars.getByteCountForSpilledLocalVars();
		final List<Integer> globberedRegisters = brilVars.getUsedNonArgumentRegisters();

		return new BrilVarMapping(varToLocation, byteCountForSpilledLocalVars, globberedRegisters);
	}

	// Fields =================================================================

	private final Map<String, BrilVars.VarLocation> varToLocation;
	private final int localBytes;
	private final List<Integer> globberedRegisters;

	// Setup ==================================================================

	private BrilVarMapping(Map<String, BrilVars.VarLocation> varToLocation, int localBytes, List<Integer> globberedRegisters) {
		this.varToLocation      = Collections.unmodifiableMap(varToLocation);
		this.localBytes         = localBytes;
		this.globberedRegisters = globberedRegisters;
	}

	// Accessing ==============================================================

	public void allocLocalVarSpace(BrilAsm asm) {
		asm.allocSpace(localBytes);

		for (Integer usedRegister : globberedRegisters) {
			asm.ipush(usedRegister);
		}
	}

	public void freeLocalVarSpace(BrilAsm asm) {
		final List<Integer> usedRegisters = new ArrayList<>(globberedRegisters);
		Collections.reverse(usedRegisters);
		for (Integer usedRegister : usedRegisters) {
			asm.ipop(usedRegister);
		}
		asm.freeSpace(localBytes);
	}

	public void ireturnValueFromVar(String var, BrilAsm asm) {
		final BrilVars.VarLocation location = getLocation(var);
		if (location.isRegister()) {
			final int register = location.reg();
			if (register != ARG0_REGISTER) {
				asm.iload(ARG0_REGISTER, register);
			}
		}
		else {
			asm.iloadFromStack(ARG0_REGISTER, FP_REGISTER, location.offset());
		}
	}

	public void loadConstant(String dest, int value, BrilAsm asm) {
		final int register = getRegisterLocation(dest);
		asm.iconst(register, value);
	}

	public void i_binaryOperator(String dest, String var1, String var2, BrilAsm asm, I_BinaryRegisterOperator operator) {
		final int var1Reg = getRegisterLocation(var1);
		final int var2Reg = getRegisterLocation(var2);
		final int destReg = getRegisterLocation(dest);
		if (destReg != var1Reg) {
			asm.iload(destReg, var1Reg);
		}
		operator.handle(destReg, var2Reg, asm);
	}

	public void b_binaryOperator(String dest, String var1, String var2, BrilAsm asm, B_BinaryRegisterOperator operator) {
		final int var1Reg = getRegisterLocation(var1);
		final int var2Reg = getRegisterLocation(var2);
		final int destReg = getRegisterLocation(dest);
		operator.handle(destReg, var1Reg, var2Reg, asm);
	}

	public void id(String dest, String type, String src, BrilAsm asm) {
		final BrilVars.VarLocation destLocation = getLocation(dest);
		final BrilVars.VarLocation srcLocation = getLocation(src);
		if (Objects.equals(destLocation, srcLocation)) {
			return;
		}

		if (BrilInstructions.INT.equals(type)) {
			if (destLocation.isRegister()) {
				final int destRegister = destLocation.reg();
				if (srcLocation.isRegister()) {
					asm.iload(destRegister, srcLocation.reg());
				}
				else {
					asm.iloadFromStack(destRegister, FP_REGISTER, srcLocation.offset());
				}
			}
			else {
				final int destStackOffset = destLocation.offset();
				if (srcLocation.isRegister()) {
					asm.istoreToStack(srcLocation.reg(), FP_REGISTER, destStackOffset);
				}
				else {
					asm.iloadFromStack(0, FP_REGISTER, srcLocation.offset());
					asm.istoreToStack(0, FP_REGISTER, destStackOffset);
				}
			}
		}
		else if (BrilInstructions.BOOL.equals(type)) {
			if (destLocation.isRegister()) {
				final int destRegister = destLocation.reg();
				if (srcLocation.isRegister()) {
					asm.bload(destRegister, srcLocation.reg());
				}
				else {
					asm.bloadFromStack(destRegister, FP_REGISTER, srcLocation.offset());
				}
			}
			else {
				final int destStackOffset = destLocation.offset();
				if (srcLocation.isRegister()) {
					asm.bstoreToStack(srcLocation.reg(), FP_REGISTER, destStackOffset);
				}
				else {
					asm.bloadFromStack(0, FP_REGISTER, srcLocation.offset());
					asm.bstoreToStack(0, FP_REGISTER, destStackOffset);
				}
			}
		}
		else {
			throw new IllegalArgumentException(type);
		}
	}

	public void branch(String var, String thenLabel, String elseLabel, BrilAsm asm) {
		final int register = getRegisterLocation(var);
		asm.brIfElse(register, thenLabel, elseLabel);
	}

	// Utils ==================================================================

	private BrilVars.VarLocation getLocation(String var) {
		return varToLocation.get(var);
	}

	private int getRegisterLocation(String var) {
		final BrilVars.VarLocation location = varToLocation.get(var);
		Utils.assertTrue(location.isRegister());
		return location.reg();
	}

	private static Set<String> getLocalVars(List<BrilNode> instructions) {
		final Set<String> localVars = new HashSet<>();
		for (BrilNode instruction : instructions) {
			final String dest = BrilInstructions.getDest(instruction);
			final String type = BrilInstructions.getType(instruction);
			if (dest == null && type == null) {
				continue;
			}

			if (dest != null && type != null) {
				localVars.add(dest);
				continue;
			}

			final String op = BrilInstructions.getOp(instruction);
			if (BrilInstructions.RET.equals(op)) {
				continue;
			}

			throw new IllegalArgumentException("invalid instruction " + instruction);
		}
		return localVars;
	}

	// Inner Classes ==========================================================

	public interface I_BinaryRegisterOperator {
		void handle(int destReg, int srcReg, BrilAsm asm);
	}

	public interface B_BinaryRegisterOperator {
		void handle(int destReg, int leftReg, int rightReg, BrilAsm asm);
	}
}
