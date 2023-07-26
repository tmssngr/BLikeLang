package de.regnis.bril;

import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Thomas Singer
 */
public final class BrilToAsm {

	// Constants ==============================================================

	private static final String PREFIX_STACK_PARAMETER = "p.";
	private static final String PREFIX_REGISTER = "r.";
	private static final String PREFIX_VIRTUAL_REGISTER = "v.";
	private static final int MAX_PARAMETERS_IN_REGISTERS = 2;
	private static final int MAX_REGISTERS = 8;

	// Static =================================================================

	@NotNull
	public static String label(String name) {
		return label(name, 0);
	}

	@NotNull
	public static String label(String name, int index) {
		return name + "_" + index;
	}

	public static void convertToAsm(BrilNode function, BrilAsmFactory asm) {
		final BrilPrepareForAsm prepare = new BrilPrepareForAsm(PREFIX_VIRTUAL_REGISTER, PREFIX_REGISTER, PREFIX_STACK_PARAMETER, MAX_REGISTERS, MAX_PARAMETERS_IN_REGISTERS);
		final BrilNode cfgFunction = prepare.prepare(function);

		final BrilNode functionFromCfg = BrilCfg.flattenBlocks(cfgFunction);

		final String name = BrilFactory.getName(functionFromCfg);
		asm.label(label(name));

		final BrilVarMapping varMapping = BrilVarMapping.createVarMapping(functionFromCfg, PREFIX_VIRTUAL_REGISTER, PREFIX_REGISTER, PREFIX_STACK_PARAMETER, MAX_PARAMETERS_IN_REGISTERS);
		varMapping.allocLocalVarSpace(asm);

		final List<BrilNode> instructions = BrilFactory.getInstructions(functionFromCfg);
		final Map<String, String> labelToIndexLabel = createIndexLabels(instructions, name);
		final MyHandler handler = new MyHandler(varMapping, labelToIndexLabel, asm);

		for (BrilNode instruction : instructions) {
			handler.visit(instruction);
		}

		varMapping.freeLocalVarSpace(asm);
		asm.ret();
	}

	// Utils ==================================================================

	private static Map<String, String> createIndexLabels(List<BrilNode> instructions, String name) {
		final Map<String, String> labelToSortedLabel = new HashMap<>();
		for (BrilNode instruction : instructions) {
			final String label = BrilInstructions.getLabel(instruction);
			if (label == null) {
				continue;
			}

			if (labelToSortedLabel.put(label, label(name, labelToSortedLabel.size() + 1)) != null) {
				throw new IllegalStateException("Duplicate label " + label);
			}
		}
		return labelToSortedLabel;
	}

	// Inner Classes ==========================================================

	private static final class MyHandler extends BrilInstructions.Handler {

		private final BrilVarMapping varMapping;
		private final Map<String, String> labelToIndexLabel;
		private final BrilAsmFactory asm;

		public MyHandler(BrilVarMapping varMapping, Map<String, String> labelToIndexLabel, BrilAsmFactory asm) {
			this.varMapping        = varMapping;
			this.labelToIndexLabel = labelToIndexLabel;
			this.asm               = asm;
		}

		@Override
		protected void label(String name) {
			asm.label(toIndexLabel(name));
		}

		@Override
		protected void constant(String dest, int value) {
			varMapping.loadConstant(dest, value, asm);
		}

		@Override
		protected void constant(String dest, boolean value) {
			varMapping.loadConstant(dest, value, asm);
		}

		@Override
		protected void id(String dest, String type, String src) {
			varMapping.id(dest, type, src, asm);
		}

		@Override
		protected void add(String dest, String var1, String var2) {
			varMapping.i_binaryOperator(dest, var1, var2, asm,
			                            (destReg, srcReg, asm) -> asm.iadd(destReg, srcReg));
		}

		@Override
		protected void sub(String dest, String var1, String var2) {
			varMapping.i_binaryOperator(dest, var1, var2, asm,
			                            (destReg, srcReg, asm) -> asm.isub(destReg, srcReg));
		}

		@Override
		protected void mul(String dest, String var1, String var2) {
			varMapping.i_binaryOperator(dest, var1, var2, asm,
			                            (destReg, srcReg, asm) -> asm.imul(destReg, srcReg));
		}

		@Override
		protected void div(String dest, String var1, String var2) {
			varMapping.i_binaryOperator(dest, var1, var2, asm,
			                            (destReg, srcReg, asm) -> asm.idiv(destReg, srcReg));
		}

		@Override
		protected void mod(String dest, String var1, String var2) {
			varMapping.i_binaryOperator(dest, var1, var2, asm,
			                            (destReg, srcReg, asm) -> asm.imod(destReg, srcReg));
		}

		@Override
		protected void and(String dest, String var1, String var2) {
			throw new UnsupportedOperationException();
		}

		@Override
		protected void equal(String dest, String var1, String var2) {
			varMapping.b_binaryOperator(dest, var1, var2, asm,
			                            (destReg, leftReg, rightReg, asm) -> asm.ieq(destReg, leftReg, rightReg));
		}

		@Override
		protected void lessThan(String dest, String var1, String var2) {
			varMapping.b_binaryOperator(dest, var1, var2, asm,
			                            (destReg, leftReg, rightReg, asm) -> asm.ilt(destReg, leftReg, rightReg));
		}

		@Override
		protected void greaterThan(String dest, String var1, String var2) {
			varMapping.b_binaryOperator(dest, var1, var2, asm,
			                            (destReg, leftReg, rightReg, asm) -> asm.igt(destReg, leftReg, rightReg));
		}

		@Override
		protected void ret(String var, String type) {
			if (BrilInstructions.INT.equals(type)) {
				varMapping.ireturnValueFromVar(var, asm);
			}
			else {
				throw new UnsupportedOperationException(type);
			}
		}

		@Override
		protected void jump(String target) {
			asm.jump(toIndexLabel(target));
		}

		@Override
		protected void branch(String var, String thenLabel, String elseLabel) {
			varMapping.branch(var, toIndexLabel(thenLabel), toIndexLabel(elseLabel), asm);
		}

		@Override
		protected void call(String name, List<BrilNode> args) {
			if (name.equals(BrilRegisterIndirection.CALL_PUSH)) {
				Utils.assertTrue(args.size() == 1);
				final BrilNode arg = args.get(0);
				final String argName = BrilFactory.getArgName(arg);
				final int register = varToRegister(argName);
				asm.ipush(register);
				return;
			}

			asm.call(BrilToAsm.label(name));
		}

		@Override
		protected void call(String dest, String type, String name, List<BrilNode> args) {
			if (name.equals(BrilRegisterIndirection.CALL_POP)) {
				Utils.assertTrue(args.isEmpty());
				final int register = varToRegister(dest);
				asm.ipop(register);
				return;
			}

			asm.call(BrilToAsm.label(name));
		}

		@NotNull
		private String toIndexLabel(String label) {
			return labelToIndexLabel.get(label);
		}

		private int varToRegister(String var) {
			return 2 * varMapping.getRegisterLocation(var);
		}
	}
}
