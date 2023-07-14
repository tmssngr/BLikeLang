package de.regnis.bril;

import de.regnis.b.ir.RegisterColoring;
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
final class BrilVarMapping2 {

	// Constants ==============================================================

	public static final int ARG0_REGISTER = 0; // call-clobbered
	public static final int ARG1_REGISTER = 2; // call-clobbered
	public static final int FP_REGISTER = 14; // call-clobbered

	// Static =================================================================

	public static BrilVarMapping2 createVarMapping(BrilNode function, String prefixVirtualRegister, String prefixRegister, String prefixStackParameter, int maxParametersInRegisters) {
		final List<BrilNode> arguments = BrilFactory.getArguments(function);
		final List<String> argNames = BrilFactory.getArgNames(arguments);
		final List<BrilNode> instructions = BrilFactory.getInstructions(function);
		final String functionReturnType = BrilFactory.getType(function);

		final Set<String> localVars = getLocalVars(instructions);

		final BrilVars2 brilVars = new BrilVars2(prefixVirtualRegister, prefixRegister, prefixStackParameter, maxParametersInRegisters);
		brilVars.assignArguments(argNames);
		brilVars.assignReturnValue(functionReturnType);
		brilVars.assignLocalVariables(localVars);

		final Map<String, BrilVars2.VarLocation> varToLocation = brilVars.getVarToLocationMapping();
		final int byteCountForSpilledLocalVars = brilVars.getByteCountForSpilledLocalVars();
		final List<Integer> globberedRegisters = brilVars.getUsedNonArgumentRegisters();

		return new BrilVarMapping2(varToLocation, byteCountForSpilledLocalVars, globberedRegisters);
	}

	// Fields =================================================================

	private final Map<String, BrilVars2.VarLocation> varToLocation;
	private final int localBytes;
	private final List<Integer> globberedRegisters;

	// Setup ==================================================================

	private BrilVarMapping2(Map<String, BrilVars2.VarLocation> varToLocation, int localBytes, List<Integer> globberedRegisters) {
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

	public void callArgument(String arg, int argIndex, BrilAsm asm) {
/*
		if (argIndex == 0) {
			iloadTo(arg, ARG0_REGISTER, asm);
		}
		else if (argIndex == 1) {
			iloadTo(arg, ARG1_REGISTER, asm);
		}
		else {
			final int register = iload(arg, A_REGISTER, asm);
			asm.ipush(register);
		}
*/
	}

	public void ireturnValueFromVar(String var, BrilAsm asm) {
		iloadTo(var, ARG0_REGISTER, asm);
	}

	public void loadConstant(String dest, int value, BrilAsm asm) {
		final BrilVars2.VarLocation location = getLocation(dest);
		if (location.isRegister()) {
			asm.iconst(location.reg(), value);
		}
		else {
			asm.iconst(0, value);
			asm.istoreToStack(0, FP_REGISTER, location.offset());
		}
	}

	public void i_binaryOperator(String dest, String var1, String var2, BrilAsm asm, I_BinaryRegisterOperator operator) {
		final int var1Reg = getRegisterLocation(var1);
		final int var2Reg = getRegisterLocation(var2);
		final int destReg = getRegisterLocation(dest);
		asm.iload(destReg, var1Reg);
		operator.handle(destReg, var2Reg, asm);
/*
		final BrilVars2.VarLocation location = getLocation(dest);
		final int destRegister = location.isRegister() ? location.reg() : A_REGISTER;
		iloadTo(var1, destRegister, asm);
		final int register2 = iload(var2, B_REGISTER, asm);

		operator.handle(destRegister, register2, asm);

		if (location.isStackOffset()) {
			asm.istoreToStack(destRegister, FP_REGISTER, location.offset());
		}
*/
	}

	public void b_binaryOperator(String dest, String var1, String var2, BrilAsm asm, B_BinaryRegisterOperator operator) {
		final int var1Reg = getRegisterLocation(var1);
		final int var2Reg = getRegisterLocation(var2);
		final int destReg = getRegisterLocation(dest);
		operator.handle(destReg, var1Reg, var2Reg, asm);
/*
		final int register1 = iload(var1, A_REGISTER, asm);
		final int register2 = iload(var2, B_REGISTER, asm);

		operator.handle(A_REGISTER, register1, register2, asm);

		bstore(dest, A_REGISTER, asm);
*/
	}

	public void id(String dest, String type, String src, BrilAsm asm) {
		final BrilVars2.VarLocation destLocation = getLocation(dest);
		final BrilVars2.VarLocation srcLocation = getLocation(src);
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
/*
		final int register = bload(var, A_REGISTER, asm);
		asm.brIfElse(register, thenLabel, elseLabel);
*/
	}

	// Utils ==================================================================

	private void istore(String var, int register, BrilAsm asm) {
		final BrilVars2.VarLocation location = getLocation(var);
		if (location.isRegister()) {
			if (register != location.reg()) {
				asm.iload(location.reg(), register);
			}
		}
		else {
			asm.istoreToStack(register, FP_REGISTER, location.offset());
		}
	}

	private void bstore(String var, int register, BrilAsm asm) {
		final BrilVars2.VarLocation location = getLocation(var);
		if (location.isRegister()) {
			if (register != location.reg()) {
				asm.bload(location.reg(), register);
			}
		}
		else {
			asm.bstoreToStack(register, FP_REGISTER, location.offset());
		}
	}

	private int iload(String var, int registerForSpilledVar, BrilAsm asm) {
		final BrilVars2.VarLocation location = getLocation(var);
		if (location.isRegister()) {
			return location.reg();
		}

		asm.iloadFromStack(registerForSpilledVar, FP_REGISTER, location.offset());
		return registerForSpilledVar;
	}

	@SuppressWarnings("SameParameterValue")
	private int bload(String var, int registerForSpilledVar, BrilAsm asm) {
		final BrilVars2.VarLocation location = getLocation(var);
		if (location.isRegister()) {
			return location.reg();
		}

		asm.bloadFromStack(registerForSpilledVar, FP_REGISTER, location.offset());
		return registerForSpilledVar;
	}

	private void iloadTo(String var, int target, BrilAsm asm) {
		final int register = iload(var, target, asm);
		if (register != target) {
			asm.iload(target, register);
		}
	}

	private BrilVars2.VarLocation getLocation(String var) {
		return varToLocation.get(var);
	}

	private int getRegisterLocation(String var) {
		final BrilVars2.VarLocation location = varToLocation.get(var);
		Utils.assertTrue(location.isRegister());
		return location.reg();
	}

	private static List<String> addVarInfoTypesForArguments(List<BrilNode> arguments, RegisterColoring registerAllocation) {
		final List<String> argNames = new ArrayList<>();
		for (int i = 0; i < arguments.size(); i++) {
			final BrilNode argument = arguments.get(i);
			final String argName = BrilFactory.getArgName(argument);
			final String argType = BrilFactory.getArgType(argument);

			argNames.add(argName);
			registerAllocation.setRegister(argName, i);
		}
		registerAllocation.addEdgesBetween(new HashSet<>(argNames));
		return argNames;
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