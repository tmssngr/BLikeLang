package de.regnis.bril;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static de.regnis.utils.Utils.notNull;

/**
 * rr0 ... working register A
 * rr2 ... working register B
 * rr4, rr6, rr8 ... available for local variables
 * rr10 ... 1st parameter for subroutine, return value
 * rr12 ... 2nd parameter for subroutine
 * rr14 ... address helper register for accessing variables on the stack
 *
 * @author Thomas Singer
 */
public final class BrilToAsm {

	// Static =================================================================

	public static List<String> convertToAsm(List<BrilNode> functions) {
		final BrilAsm asm = new BrilAsm();
		for (BrilNode function : functions) {
			convertToAsm(function, asm);
		}
		asm.transform(BrilAsmTransformations.transform());
		return asm.toLines();
	}

	// Utils ==================================================================

	private static void convertToAsm(BrilNode function, BrilAsm asm) {
		final String name = BrilFactory.getName(function);
		asm.label(name);

		final VarMapping varMapping = createVarMapping(function);
		varMapping.allocLocalVarSpace(asm);

		final List<BrilNode> instructions = BrilFactory.getInstructions(function);
		final String exitLabel = name + " exit";
		convertToAsm(instructions, exitLabel, varMapping, asm);

		asm.label(exitLabel);
		varMapping.freeLocalVarSpace(asm);
		asm.ret();
	}

	private static void convertToAsm(List<BrilNode> instructions, String exitLabel, VarMapping varMapping, BrilAsm asm) {
		for (final Iterator<BrilNode> it = instructions.iterator(); it.hasNext(); ) {
			final BrilNode instruction = it.next();
			final String op = BrilInstructions.getOp(instruction);
			final String dest = BrilInstructions.getDest(instruction);
			final String type = BrilInstructions.getType(instruction);
			if (BrilInstructions.CONST.equals(op) && dest != null && BrilInstructions.INT.equals(type)) {
				constant(instruction, dest, varMapping, asm);
				continue;
			}

			if (BrilInstructions.ADD.equals(op) && dest != null && BrilInstructions.INT.equals(type)) {
				binary(instruction, dest,
				       (destReg, srcReg, asm1) -> asm1.iadd(destReg, srcReg),
				       varMapping, asm);
				continue;
			}

			if (BrilInstructions.CALL.equals(op) && dest != null && type != null) {
				call(instruction, dest, type, varMapping, asm);
				continue;
			}

			if (BrilInstructions.RET.equals(op)) {
				ret(instruction, it.hasNext() ? exitLabel : null, varMapping, asm);
				continue;
			}

			if (BrilInstructions.PRINT.equals(op)) {
				print(instruction, varMapping, asm);
				continue;
			}

			if (BrilInstructions.BR.equals(op)) {
				branch(instruction, varMapping, asm);
				continue;
			}

			final String label = BrilInstructions.getLabel(instruction);
			if (label != null) {
				asm.label(label);
				continue;
			}

			throw new UnsupportedOperationException(op);
		}
	}

	private static void constant(BrilNode instruction, String dest, VarMapping varMapping, BrilAsm asm) {
		final int value = BrilInstructions.getIntValue(instruction);
		varMapping.loadConstant(dest, value, asm);
	}

	private static void binary(BrilNode instruction, String dest, BinaryRegisterOperator binaryRegisterOperator, VarMapping varMapping, BrilAsm asm) {
		final String var1 = BrilInstructions.getVar1NotNull(instruction);
		final String var2 = BrilInstructions.getVar2NotNull(instruction);
		varMapping.ibinaryOperator(dest, asm, var1, var2, binaryRegisterOperator);
	}

	private static void call(BrilNode instruction, String dest, String type, VarMapping varMapping, BrilAsm asm) {
		final String name = BrilInstructions.getName(instruction);
		final List<String> args = BrilInstructions.getArgs(instruction);

		for (int i = 0; i < args.size(); i++) {
			final String arg = args.get(i);
			final String argType = varMapping.getType(arg);
			if (BrilInstructions.INT.equals(argType)) {
				varMapping.callArgument(arg, i, asm);
			}
			else {
				throw new UnsupportedOperationException(argType);
			}
		}

		asm.call(name);

		if (BrilInstructions.INT.equals(type)) {
			varMapping.storeReturnValueInVar(dest, asm);
		}
		else {
			throw new UnsupportedOperationException(type);
		}

		for (int i = 0; i < args.size(); i++) {
			final String arg = args.get(i);
			final String argType = varMapping.getType(arg);
			if (BrilInstructions.INT.equals(argType)) {
				if (i > 1) {
					asm.ipop(0);
				}
			}
			else {
				throw new UnsupportedOperationException(argType);
			}
		}
	}

	private static void print(BrilNode instruction, VarMapping varMapping, BrilAsm asm) {
		final String var = BrilInstructions.getVarNotNull(instruction);
		final String varType = varMapping.getType(var);
		if (BrilInstructions.INT.equals(varType)) {
			varMapping.callArgument(var, 0, asm);
		}
		else {
			throw new UnsupportedOperationException(varType);
		}

		asm.call("print");
	}

	private static void ret(BrilNode instruction, @Nullable String exitLabel, VarMapping varMapping, BrilAsm asm) {
		final String var = BrilInstructions.getVarNotNull(instruction);
		final String varType = varMapping.getType(var);
		if (BrilInstructions.INT.equals(varType)) {
			varMapping.ireturnValueFromVar(var, asm);
		}
		else {
			throw new UnsupportedOperationException(varType);
		}

		if (exitLabel != null) {
			asm.jump(exitLabel);
		}
	}

	private static void branch(BrilNode instruction, VarMapping varMapping, BrilAsm asm) {
		final String var = BrilInstructions.getVarNotNull(instruction);
		final String varType = varMapping.getType(var);
		if (!BrilInstructions.BOOL.equals(varType)) {
			throw new IllegalArgumentException("Expected a bool variable for the br command");
		}

		final int register = varMapping.bload(var, 0, asm);
		asm.brIfElse(register, BrilInstructions.getThenTarget(instruction), BrilInstructions.getElseTarget(instruction));
	}

	private static VarMapping createVarMapping(BrilNode function) {
		final List<BrilNode> arguments = BrilFactory.getArguments(function);
		final List<BrilNode> instructions = BrilFactory.getInstructions(function);

		final Map<String, VarInfo> varToInfo = new HashMap<>();
		addVarInfoTypesForArguments(arguments, varToInfo);

		final Set<String> localVars = new LinkedHashSet<>();
		addVarInfoTypesForInstructions(instructions, varToInfo, localVars);

		final int byteCountForSpilledLocalVars = assignLocalVars(localVars, varToInfo);

		// return address
		final int offset = byteCountForSpilledLocalVars + 2;
		assignArguments(arguments, varToInfo, offset);

		return new VarMapping(Collections.unmodifiableMap(varToInfo), byteCountForSpilledLocalVars);
	}

	private static void addVarInfoTypesForArguments(List<BrilNode> arguments, Map<String, VarInfo> varToInfo) {
		for (BrilNode argument : arguments) {
			final String argName = BrilFactory.getArgName(argument);
			final String argType = BrilFactory.getArgType(argument);
			if (varToInfo.put(argName, new VarInfo(0, argType)) != null) {
				throw new IllegalArgumentException("duplicate parameter " + argName);
			}
		}
	}

	private static void addVarInfoTypesForInstructions(List<BrilNode> instructions, Map<String, VarInfo> varToInfo, Set<String> localVars) {
		for (BrilNode instruction : instructions) {
			final String dest = BrilInstructions.getDest(instruction);
			final String type = BrilInstructions.getType(instruction);
			if (dest == null && type == null) {
				continue;
			}

			if (dest != null && type != null) {
				final VarInfo prevInfo = varToInfo.put(dest, new VarInfo(0, type));
				if (prevInfo != null && !Objects.equals(prevInfo.type, type)) {
					throw new IllegalArgumentException("invalid type for " + dest + ": " + type + " vs. " + prevInfo.type);
				}

				localVars.add(dest);
				continue;
			}

			throw new IllegalArgumentException("invalid instruction " + instruction);
		}
	}

	private static int assignLocalVars(Set<String> localVars, Map<String, VarInfo> varToInfo) {
		int maxRegisters = 3;
		int offset = 0;
		int register = 4;
		for (String localVar : localVars) {
			final VarInfo info = notNull(varToInfo.get(localVar));
			final int registerOrOffset;
			if (maxRegisters > 0) {
				registerOrOffset = -register;
				register += 2;
				maxRegisters--;
			}
			else {
				registerOrOffset = offset;
				offset += 2;
			}
			varToInfo.put(localVar, new VarInfo(registerOrOffset, info.type));
		}

		return offset;
	}

	private static void assignArguments(List<BrilNode> arguments, Map<String, VarInfo> varToInfo, int offset) {
		int count = 0;
		for (BrilNode argument : arguments) {
			final String argName = BrilFactory.getArgName(argument);
			final VarInfo info = notNull(varToInfo.get(argName));
			final int registerOrOffset;
			if (count == 0) {
				registerOrOffset = -10;
			}
			else if (count == 1) {
				registerOrOffset = -12;
			}
			else {
				registerOrOffset = offset;
				offset += 2;
			}
			varToInfo.put(argName, new VarInfo(registerOrOffset, info.type));
			count++;
		}
	}

	// Inner Classes ==========================================================

	private static final class VarMapping {
		public final Map<String, VarInfo> varToInfo;
		public final int localBytes;

		public VarMapping(Map<String, VarInfo> varToInfo, int localBytes) {
			this.varToInfo  = varToInfo;
			this.localBytes = localBytes;
		}

		private int getOffset(String var) {
			return varToInfo.get(var).offset;
		}

		@NotNull
		public String getType(String var) {
			return varToInfo.get(var).type;
		}

		public void allocLocalVarSpace(BrilAsm asm) {
			asm.allocSpace(localBytes);
		}

		public void freeLocalVarSpace(BrilAsm asm) {
			asm.freeSpace(localBytes);
		}

		public int iload(String var, int registerForSpilledVar, BrilAsm asm) {
			final int offsetOrRegister = getOffset(var);
			if (offsetOrRegister < 0) {
				return -offsetOrRegister;
			}

			asm.iloadFromStack(registerForSpilledVar, 14, offsetOrRegister);
			return registerForSpilledVar;
		}

		public void iloadTo(String var, int target, BrilAsm asm) {
			final int register = iload(var, target, asm);
			if (register != target) {
				asm.iload(target, register);
			}
		}

		public int bload(String var, int registerForSpilledVar, BrilAsm asm) {
			final int offsetOrRegister = getOffset(var);
			if (offsetOrRegister < 0) {
				return -offsetOrRegister;
			}

			asm.bloadFromStack(registerForSpilledVar, 14, offsetOrRegister);
			return registerForSpilledVar;
		}

		public int getSpRegister() {
			return 14;
		}

		public void callArgument(String arg, int argIndex, BrilAsm asm) {
			if (argIndex == 0) {
				iloadTo(arg, 10, asm);
			}
			else if (argIndex == 1) {
				iloadTo(arg, 12, asm);
			}
			else {
				final int register = iload(arg, 0, asm);
				asm.ipush(register);
			}
		}

		public void ireturnValueFromVar(String var, BrilAsm asm) {
			iloadTo(var, 10, asm);
		}

		public void loadConstant(String dest, int value, BrilAsm asm) {
			final int offsetOrRegister = getOffset(dest);
			if (offsetOrRegister < 0) {
				asm.iconst(-offsetOrRegister, value);
			}
			else {
				asm.iconst(0, value);
				asm.istoreToStack(0, getSpRegister(), offsetOrRegister);
			}
		}

		public void istore(String var, int register, BrilAsm asm) {
			final int offsetOrRegister = getOffset(var);
			if (offsetOrRegister < 0) {
				if (register != -offsetOrRegister) {
					asm.iload(-offsetOrRegister, register);
				}
			}
			else {
				asm.istoreToStack(register, getSpRegister(), offsetOrRegister);
			}
		}

		public void storeReturnValueInVar(String dest, BrilAsm asm) {
			istore(dest, 10, asm);
		}

		public void ibinaryOperator(String dest, BrilAsm asm, String var1, String var2, BinaryRegisterOperator operator) {
			final int destOffsetOrRegister = getOffset(dest);
			final int destRegister = destOffsetOrRegister < 0 ? -destOffsetOrRegister : 0;
			iloadTo(var1, destRegister, asm);
			final int register2 = iload(var2, 2, asm);

			operator.handle(destRegister, register2, asm);

			if (destOffsetOrRegister >= 0) {
				asm.istoreToStack(0, getSpRegister(), destOffsetOrRegister);
			}
		}
	}

	private interface BinaryRegisterOperator {
		void handle(int destReg, int srcReg, BrilAsm asm);
	}

	private record VarInfo(int offset, String type) {
	}
}
