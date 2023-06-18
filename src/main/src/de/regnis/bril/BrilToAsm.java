package de.regnis.bril;

import org.jetbrains.annotations.NotNull;

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
		return asm.toLines();
	}

	// Utils ==================================================================

	private static void convertToAsm(BrilNode function, BrilAsm asm) {
		asm.label(BrilFactory.getName(function));

		final VarMapping varMapping = createVarMapping(function);
		varMapping.allocLocalVarSpace(asm);

		final List<BrilNode> instructions = BrilFactory.getInstructions(function);
		convertToAsm(instructions, varMapping, asm);

		varMapping.freeLocalVarSpace(asm);
		asm.ret();
	}

	private static void convertToAsm(List<BrilNode> instructions, VarMapping varMapping, BrilAsm asm) {
		for (BrilNode instruction : instructions) {
			final String op = BrilInstructions.getOp(instruction);
			final String dest = BrilInstructions.getDest(instruction);
			final String type = BrilInstructions.getType(instruction);
			if (BrilInstructions.CONST.equals(op) && dest != null && BrilInstructions.INT.equals(type)) {
				constant(instruction, dest, varMapping, asm);
				continue;
			}

			if (BrilInstructions.ADD.equals(op) && dest != null && BrilInstructions.INT.equals(type)) {
				add(instruction, dest, varMapping, asm);
				continue;
			}

			if (BrilInstructions.CALL.equals(op) && dest != null && type != null) {
				call(instruction, dest, type, varMapping, asm);
				continue;
			}

			if (BrilInstructions.RET.equals(op)) {
				ret(instruction, varMapping, asm);
				continue;
			}

			if (BrilInstructions.PRINT.equals(op)) {
				print(instruction, varMapping, asm);
				continue;
			}

			throw new UnsupportedOperationException(op);
		}
	}

	private static void constant(BrilNode instruction, String dest, VarMapping varMapping, BrilAsm asm) {
		final int value = BrilInstructions.getIntValue(instruction);
		varMapping.loadConstant(dest, value, asm);
	}

	private static void add(BrilNode instruction, String dest, VarMapping varMapping, BrilAsm asm) {
		varMapping.loadTo(BrilInstructions.getVar1NotNull(instruction), 0, asm);
		final int register2 = varMapping.load(BrilInstructions.getVar2NotNull(instruction), 2, asm);
		asm.iadd(0, register2);
		varMapping.store(dest, 0, asm);
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

	private static void ret(BrilNode instruction, VarMapping varMapping, BrilAsm asm) {
		final String var = BrilInstructions.getVarNotNull(instruction);
		final String varType = varMapping.getType(var);
		if (BrilInstructions.INT.equals(varType)) {
			varMapping.returnValueFromVar(var, asm);
		}
		else {
			throw new UnsupportedOperationException(varType);
		}
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

		public int load(String var, int registerForSpilledVar, BrilAsm asm) {
			final int offsetOrRegister = getOffset(var);
			if (offsetOrRegister < 0) {
				return -offsetOrRegister;
			}

			asm.iloadFromStack(registerForSpilledVar, 14, offsetOrRegister);
			return registerForSpilledVar;
		}

		public void loadTo(String var, int target, BrilAsm asm) {
			final int register = load(var, target, asm);
			if (register != target) {
				asm.iload(target, register);
			}
		}

		public void store(String var, int register, BrilAsm asm) {
			final int offsetOrRegister = getOffset(var);
			if (offsetOrRegister < 0) {
				if (register != -offsetOrRegister) {
					asm.iload(-offsetOrRegister, register);
				}
				return;
			}

			asm.istoreToStack(register, getSpRegister(), offsetOrRegister);
		}

		public int getSpRegister() {
			return 14;
		}

		public void callArgument(String arg, int argIndex, BrilAsm asm) {
			if (argIndex == 0) {
				loadTo(arg, 10, asm);
			}
			else if (argIndex == 1) {
				loadTo(arg, 12, asm);
			}
			else {
				final int register = load(arg, 0, asm);
				asm.ipush(register);
			}
		}

		public void returnValueFromVar(String var, BrilAsm asm) {
			loadTo(var, 10, asm);
		}

		public void storeReturnValueInVar(String dest, BrilAsm asm) {
			store(dest, 10, asm);
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
	}

	private record VarInfo(int offset, String type) {
	}
}
