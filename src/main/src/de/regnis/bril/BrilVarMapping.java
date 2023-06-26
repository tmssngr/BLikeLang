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
final class BrilVarMapping {

	// Constants ==============================================================

	public static final int A_REGISTER = 0;
	@SuppressWarnings({"PointlessArithmeticExpression", "WeakerAccess"})
	public static final int B_REGISTER = A_REGISTER + 2;
	public static final int VAR0_REGISTER = B_REGISTER + 2;
	public static final int VAR1_REGISTER = VAR0_REGISTER + 2;
	public static final int VAR2_REGISTER = VAR1_REGISTER + 2;
	public static final int ARG0_REGISTER = VAR2_REGISTER + 2;
	public static final int ARG1_REGISTER = ARG0_REGISTER + 2;
	public static final int FP_REGISTER = ARG1_REGISTER + 2;

	// Static =================================================================

	public static BrilVarMapping createVarMapping(BrilNode function) {
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

		return new BrilVarMapping(Collections.unmodifiableMap(varToInfo), byteCountForSpilledLocalVars);
	}

	// Fields =================================================================

	private final Map<String, VarInfo> varToInfo;
	private final int localBytes;

	// Setup ==================================================================

	private BrilVarMapping(Map<String, VarInfo> varToInfo, int localBytes) {
		this.varToInfo  = varToInfo;
		this.localBytes = localBytes;
	}

	// Accessing ==============================================================

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

	public void callArgument(String arg, int argIndex, BrilAsm asm) {
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
	}

	public void ireturnValueFromVar(String var, BrilAsm asm) {
		iloadTo(var, ARG0_REGISTER, asm);
	}

	public void loadConstant(String dest, int value, BrilAsm asm) {
		final int offsetOrRegister = getOffset(dest);
		if (offsetOrRegister < 0) {
			asm.iconst(-offsetOrRegister, value);
		}
		else {
			asm.iconst(0, value);
			asm.istoreToStack(0, FP_REGISTER, offsetOrRegister);
		}
	}

	public void istoreReturnValueInVar(String dest, BrilAsm asm) {
		store(dest, ARG0_REGISTER, asm);
	}

	public void i_binaryOperator(String dest, String var1, String var2, BrilAsm asm, I_BinaryRegisterOperator operator) {
		final int destOffsetOrRegister = getOffset(dest);
		final int destRegister = destOffsetOrRegister < 0 ? -destOffsetOrRegister : A_REGISTER;
		iloadTo(var1, destRegister, asm);
		final int register2 = iload(var2, B_REGISTER, asm);

		operator.handle(destRegister, register2, asm);

		if (destOffsetOrRegister >= 0) {
			asm.istoreToStack(destRegister, FP_REGISTER, destOffsetOrRegister);
		}
	}

	public void b_binaryOperator(String dest, String var1, String var2, BrilAsm asm, B_BinaryRegisterOperator operator) {
		final int register1 = iload(var1, A_REGISTER, asm);
		final int register2 = iload(var2, B_REGISTER, asm);

		operator.handle(A_REGISTER, register1, register2, asm);

		store(dest, A_REGISTER, asm);
	}

	public void id(String dest, String var, BrilAsm asm) {
		final int destOffsetOrRegister = getOffset(dest);
		final int varOffsetOrRegister = getOffset(var);
		if (destOffsetOrRegister == varOffsetOrRegister) {
			return;
		}

		if (destOffsetOrRegister < 0) {
			final int destRegister = -destOffsetOrRegister;
			if (varOffsetOrRegister < 0) {
				asm.iload(destRegister, -varOffsetOrRegister);
			}
			else {
				asm.iloadFromStack(destRegister, FP_REGISTER, varOffsetOrRegister);
			}
		}
		else {
			if (varOffsetOrRegister < 0) {
				asm.istoreToStack(-varOffsetOrRegister, FP_REGISTER, destOffsetOrRegister);
			}
			else {
				asm.iloadFromStack(0, FP_REGISTER, varOffsetOrRegister);
				asm.istoreToStack(0, FP_REGISTER, destOffsetOrRegister);
			}
		}
	}

	public void branch(String var, String thenLabel, String elseLabel, BrilAsm asm) {
		final int register = bload(var, A_REGISTER, asm);
		asm.brIfElse(register, thenLabel, elseLabel);
	}

	// Utils ==================================================================

	private void store(String var, int register, BrilAsm asm) {
		final int offsetOrRegister = getOffset(var);

		final String type = getType(var);
		if (BrilInstructions.INT.equals(type)) {
			if (offsetOrRegister < 0) {
				if (register != -offsetOrRegister) {
					asm.iload(-offsetOrRegister, register);
				}
			}
			else {
				asm.istoreToStack(register, FP_REGISTER, offsetOrRegister);
			}
		}
		else if (BrilInstructions.BOOL.equals(type)) {
			if (offsetOrRegister < 0) {
				if (register != -offsetOrRegister) {
					asm.bload(-offsetOrRegister, register);
				}
			}
			else {
				asm.bstoreToStack(register, FP_REGISTER, offsetOrRegister);
			}
		}
		else {
			throw new UnsupportedOperationException("unsupported type " + type);
		}
	}

	private int iload(String var, int registerForSpilledVar, BrilAsm asm) {
		final int offsetOrRegister = getOffset(var);
		if (offsetOrRegister < 0) {
			return -offsetOrRegister;
		}

		asm.iloadFromStack(registerForSpilledVar, FP_REGISTER, offsetOrRegister);
		return registerForSpilledVar;
	}

	@SuppressWarnings("SameParameterValue")
	private int bload(String var, int registerForSpilledVar, BrilAsm asm) {
		final int offsetOrRegister = getOffset(var);
		if (offsetOrRegister < 0) {
			return -offsetOrRegister;
		}

		asm.bloadFromStack(registerForSpilledVar, FP_REGISTER, offsetOrRegister);
		return registerForSpilledVar;
	}

	private void iloadTo(String var, int target, BrilAsm asm) {
		final int register = iload(var, target, asm);
		if (register != target) {
			asm.iload(target, register);
		}
	}

	private int getOffset(String var) {
		return varToInfo.get(var).offset;
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
			String type = BrilInstructions.getType(instruction);
			if (dest == null && type == null) {
				continue;
			}

			final String op = BrilInstructions.getOp(instruction);
			if (BrilInstructions.ID.equals(op)) {
				final String var = BrilInstructions.getVarNotNull(instruction);
				final VarInfo info = varToInfo.get(var);
				if (info == null) {
					throw new IllegalArgumentException("variable " + var + " undefined for " + instruction);
				}

				type = info.type;
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
		int register = VAR0_REGISTER;
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
				registerOrOffset = -ARG0_REGISTER;
			}
			else if (count == 1) {
				registerOrOffset = -ARG1_REGISTER;
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

	public interface I_BinaryRegisterOperator {
		void handle(int destReg, int srcReg, BrilAsm asm);
	}

	public interface B_BinaryRegisterOperator {
		void handle(int destReg, int leftReg, int rightReg, BrilAsm asm);
	}

	private record VarInfo(int offset, String type) {
	}
}
