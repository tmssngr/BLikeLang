package de.regnis.bril;

import org.jetbrains.annotations.NotNull;

import java.util.*;

import static de.regnis.utils.Utils.notNull;

/**
 * @author Thomas Singer
 */
public final class BrilToAsm {

	// Static =================================================================

	public static List<String> convertToAsm(List<BrilNode> functions) {
		final BrilAsm asm = new BrilAsm();
		for (BrilNode function : functions) {
			convertToAsm(function, asm);
		}
		return asm.getOutput();
	}

	// Utils ==================================================================

	private static void convertToAsm(BrilNode function, BrilAsm asm) {
		asm.label(BrilFactory.getName(function));

		final VarMapping varMapping = createVarMapping(function);
		asm.allocSpace(varMapping.localBytes);

		final List<BrilNode> instructions = BrilFactory.getInstructions(function);
		convertToAsm(instructions, varMapping, asm);

		asm.freeSpace(varMapping.localBytes);
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
		asm.iconstX(BrilInstructions.getIntValue(instruction));
		asm.istoreX(varMapping.getOffset(dest));
	}

	private static void add(BrilNode instruction, String dest, VarMapping varMapping, BrilAsm asm) {
		asm.iloadX(varMapping.getOffset(BrilInstructions.getVar1NotNull(instruction)));
		asm.iloadY(varMapping.getOffset(BrilInstructions.getVar2NotNull(instruction)));
		asm.iaddXY();
		asm.istoreX(varMapping.getOffset(dest));
	}

	private static void call(BrilNode instruction, String dest, String type, VarMapping varMapping, BrilAsm asm) {
		final String name = BrilInstructions.getName(instruction);
		final List<String> args = BrilInstructions.getArgs(instruction);
		for (String arg : args) {
			final String argType = varMapping.getType(arg);
			if (BrilInstructions.INT.equals(argType)) {
				asm.iloadX(varMapping.getOffset(arg));
				asm.ipushX();
			}
			else {
				throw new UnsupportedOperationException(argType);
			}
		}

		asm.call(name);

		if (BrilInstructions.INT.equals(type)) {
			asm.istoreX(varMapping.getOffset(dest));
		}
		else {
			throw new UnsupportedOperationException(type);
		}

		for (String arg : args) {
			final String argType = varMapping.getType(arg);
			if (BrilInstructions.INT.equals(argType)) {
				asm.ipop();
			}
			else {
				throw new UnsupportedOperationException(argType);
			}
		}
	}

	private static void ret(BrilNode instruction, VarMapping varMapping, BrilAsm asm) {
		final String var = BrilInstructions.getVarNotNull(instruction);
		final String varType = varMapping.getType(var);
		if (BrilInstructions.INT.equals(varType)) {
			asm.iloadX(varMapping.getOffset(var));
		}
		else {
			throw new UnsupportedOperationException(varType);
		}
	}

	private static void print(BrilNode instruction, VarMapping varMapping, BrilAsm asm) {
		final String var = BrilInstructions.getVarNotNull(instruction);
		final String varType = varMapping.getType(var);
		if (BrilInstructions.INT.equals(varType)) {
			asm.iloadX(varMapping.getOffset(var));
			asm.ipushX();
		}
		else {
			throw new UnsupportedOperationException(varType);
		}

		asm.call("print");

		//noinspection ConstantValue
		if (BrilInstructions.INT.equals(varType)) {
			asm.ipop();
		}
		else {
			throw new UnsupportedOperationException(varType);
		}
	}

	private static VarMapping createVarMapping(BrilNode function) {
		final List<BrilNode> arguments = BrilFactory.getArguments(function);
		final List<BrilNode> instructions = BrilFactory.getInstructions(function);

		final Map<String, VarInfo> varToInfo = new HashMap<>();
		for (BrilNode argument : arguments) {
			final String argName = BrilFactory.getArgName(argument);
			final String argType = BrilFactory.getArgType(argument);
			if (varToInfo.put(argName, new VarInfo(0, argType)) != null) {
				throw new IllegalArgumentException("duplicate parameter " + argName);
			}
		}

		final Set<String> localVars = new LinkedHashSet<>();
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

		int offset = 0;
		for (String localVar : localVars) {
			final VarInfo info = notNull(varToInfo.get(localVar));
			varToInfo.put(localVar, new VarInfo(offset, info.type));
			final int size = info.getSize();
			offset += size;
		}

		final int localBytes = offset;

		// return address
		offset += 2;

		for (BrilNode argument : arguments) {
			final String argName = BrilFactory.getArgName(argument);
			final VarInfo info = notNull(varToInfo.get(argName));
			varToInfo.put(argName, new VarInfo(offset, info.type));
			final int size = info.getSize();
			offset += size;
		}

		return new VarMapping(Collections.unmodifiableMap(varToInfo), localBytes);
	}

	// Inner Classes ==========================================================

	private static final class VarMapping {
		public final Map<String, VarInfo> varToInfo;
		public final int localBytes;

		public VarMapping(Map<String, VarInfo> varToInfo, int localBytes) {
			this.varToInfo = varToInfo;
			this.localBytes  = localBytes;
		}

		public int getOffset(String var) {
			return varToInfo.get(var).offset;
		}

		@NotNull
		public String getType(String var) {
			return varToInfo.get(var).type;
		}
	}

	private record VarInfo(int offset, String type) {
		public int getSize() {
			if (BrilInstructions.INT.equals(type)) {
				return 2;
			}
			if (BrilInstructions.BOOL.equals(type)) {
				return 1;
			}
			throw new IllegalArgumentException("unsupported type " + type);
		}
	}
}
