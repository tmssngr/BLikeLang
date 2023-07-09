package de.regnis.bril;

import de.regnis.b.ir.RegisterColoring;

import java.util.*;

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

	public static final int A_REGISTER = 0; // call-clobbered
	@SuppressWarnings({"PointlessArithmeticExpression", "WeakerAccess"})
	public static final int B_REGISTER = A_REGISTER + 2; // call-clobbered
	public static final int VAR0_REGISTER = B_REGISTER + 2; // call-preserved
	public static final int VAR1_REGISTER = VAR0_REGISTER + 2; // call-preserved
	public static final int VAR2_REGISTER = VAR1_REGISTER + 2; // call-preserved
	public static final int ARG0_REGISTER = VAR2_REGISTER + 2; // call-clobbered
	public static final int ARG1_REGISTER = ARG0_REGISTER + 2; // call-clobbered
	public static final int FP_REGISTER = ARG1_REGISTER + 2; // call-clobbered

	// Static =================================================================

	public static BrilVarMapping createVarMapping(BrilNode function) {
		final List<BrilNode> arguments = BrilFactory.getArguments(function);
		final List<BrilNode> instructions = BrilFactory.getInstructions(function);

		final RegisterColoring registerAllocation = new RegisterColoring();

		final Map<String, String> varToType = new HashMap<>();
		final List<String> argNames = addVarInfoTypesForArguments(arguments, registerAllocation);

		final Set<String> localVars = new LinkedHashSet<>();
		addVarInfoTypesForInstructions(instructions, registerAllocation, localVars);

		registerAllocation.build();

		final BrilVars brilVars = new BrilVars();
		brilVars.assign(argNames, localVars,
		                name -> registerAllocation.getVirtualRegister(name));
		final Map<String, BrilVars.VarLocation> varToLocation = new HashMap<>();
		for (String argName : argNames) {
			final BrilVars.VarLocation location = brilVars.getLocation(argName);
			varToLocation.put(argName, location);
		}
		for (String localVar : localVars) {
			final BrilVars.VarLocation location = brilVars.getLocation(localVar);
			varToLocation.put(localVar, location);
		}

		final int byteCountForSpilledLocalVars = brilVars.getByteCountForSpilledLocalVars();

		return new BrilVarMapping(varToLocation, byteCountForSpilledLocalVars);
	}

	// Fields =================================================================

	private final Map<String, BrilVars.VarLocation> varToLocation;
	private final int localBytes;
	private final List<Integer> usedRegisters;

	// Setup ==================================================================

	private BrilVarMapping(Map<String, BrilVars.VarLocation> varToLocation, int localBytes) {
		this.localBytes    = localBytes;
		this.varToLocation = Collections.unmodifiableMap(varToLocation);
		usedRegisters      = getUsedRegisters(varToLocation);
	}

	// Accessing ==============================================================

	public void allocLocalVarSpace(BrilAsm asm) {
		asm.allocSpace(localBytes);

		for (Integer usedRegister : usedRegisters) {
			asm.ipush(usedRegister);
		}
	}

	public void freeLocalVarSpace(BrilAsm asm) {
		final List<Integer> usedRegisters = new ArrayList<>(this.usedRegisters);
		Collections.reverse(usedRegisters);
		for (Integer usedRegister : usedRegisters) {
			asm.ipop(usedRegister);
		}
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
		final BrilVars.VarLocation location = getLocation(dest);
		if (location.isRegister()) {
			asm.iconst(location.reg(), value);
		}
		else {
			asm.iconst(0, value);
			asm.istoreToStack(0, FP_REGISTER, location.offset());
		}
	}

	public void i_binaryOperator(String dest, String var1, String var2, BrilAsm asm, I_BinaryRegisterOperator operator) {
		final BrilVars.VarLocation location = getLocation(dest);
		final int destRegister = location.isRegister() ? location.reg() : A_REGISTER;
		iloadTo(var1, destRegister, asm);
		final int register2 = iload(var2, B_REGISTER, asm);

		operator.handle(destRegister, register2, asm);

		if (location.isStackOffset()) {
			asm.istoreToStack(destRegister, FP_REGISTER, location.offset());
		}
	}

	public void b_binaryOperator(String dest, String var1, String var2, BrilAsm asm, B_BinaryRegisterOperator operator) {
		final int register1 = iload(var1, A_REGISTER, asm);
		final int register2 = iload(var2, B_REGISTER, asm);

		operator.handle(A_REGISTER, register1, register2, asm);

		bstore(dest, A_REGISTER, asm);
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
		final int register = bload(var, A_REGISTER, asm);
		asm.brIfElse(register, thenLabel, elseLabel);
	}

	// Utils ==================================================================

	private List<Integer> getUsedRegisters(Map<String, BrilVars.VarLocation> varToInfo) {
		final Set<Integer> usedRegisters = new HashSet<>();
		for (Map.Entry<String, BrilVars.VarLocation> entry : varToInfo.entrySet()) {
			final BrilVars.VarLocation location = entry.getValue();
			if (location.isRegister()) {
				final int reg = location.reg();
				if (reg >= VAR0_REGISTER && reg <= VAR2_REGISTER) {
					usedRegisters.add(reg);
				}
			}
		}
		final List<Integer> usedRegistersSorted = new ArrayList<>(usedRegisters);
		usedRegistersSorted.sort(Comparator.naturalOrder());
		return Collections.unmodifiableList(usedRegistersSorted);
	}

	private void istore(String var, int register, BrilAsm asm) {
		final BrilVars.VarLocation location = getLocation(var);
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
		final BrilVars.VarLocation location = getLocation(var);
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
		final BrilVars.VarLocation location = getLocation(var);
		if (location.isRegister()) {
			return location.reg();
		}

		asm.iloadFromStack(registerForSpilledVar, FP_REGISTER, location.offset());
		return registerForSpilledVar;
	}

	@SuppressWarnings("SameParameterValue")
	private int bload(String var, int registerForSpilledVar, BrilAsm asm) {
		final BrilVars.VarLocation location = getLocation(var);
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

	private BrilVars.VarLocation getLocation(String var) {
		return varToLocation.get(var);
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

	private static void addVarInfoTypesForInstructions(List<BrilNode> instructions, RegisterColoring registerAllocation, Set<String> localVars) {
		for (BrilNode instruction : instructions) {
			final Set<String> liveOut = BrilCfgDetectVarLiveness.getLiveOut(instruction);
			registerAllocation.addEdgesBetween(liveOut);

			final String dest = BrilInstructions.getDest(instruction);
			final String type = BrilInstructions.getType(instruction);
			if (dest == null && type == null) {
				continue;
			}

			if (dest != null && type != null) {
				localVars.add(dest);
				continue;
			}

			throw new IllegalArgumentException("invalid instruction " + instruction);
		}
	}

	// Inner Classes ==========================================================

	public interface I_BinaryRegisterOperator {
		void handle(int destReg, int srcReg, BrilAsm asm);
	}

	public interface B_BinaryRegisterOperator {
		void handle(int destReg, int leftReg, int rightReg, BrilAsm asm);
	}
}
