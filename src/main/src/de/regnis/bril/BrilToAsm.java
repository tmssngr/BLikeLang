package de.regnis.bril;

import de.regnis.utils.Utils;

import java.util.List;

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

	public static List<String> convertToAsm(List<BrilNode> functions) {
		final BrilAsm asm = new BrilAsm();
		for (BrilNode function : functions) {
			convertToAsm(function, asm);
		}
//		asm.simplify(BrilAsmSimplifier.create());
		return asm.toLines();
	}

	// Utils ==================================================================

	private static void convertToAsm(BrilNode function, BrilAsm asm) {
		final BrilPrepareForAsm prepare = new BrilPrepareForAsm(PREFIX_VIRTUAL_REGISTER, PREFIX_REGISTER, PREFIX_STACK_PARAMETER, MAX_REGISTERS, MAX_PARAMETERS_IN_REGISTERS);
		final BrilNode cfgFunction = prepare.prepare(function);

		//		final List<BrilNode> blocks = BrilCfg.getBlocks(cfgFunction);
//		BrilCfgDetectVarLiveness.detectLiveness(blocks, true);
		final BrilNode functionFromCfg = BrilCfg.flattenBlocks(cfgFunction);

		final String name = BrilFactory.getName(functionFromCfg);
		asm.label(name);

		final BrilVarMapping varMapping = BrilVarMapping.createVarMapping(functionFromCfg);
		varMapping.allocLocalVarSpace(asm);

		final List<BrilNode> instructions = BrilFactory.getInstructions(functionFromCfg);
		convertToAsm(instructions, varMapping, asm);

		varMapping.freeLocalVarSpace(asm);
		asm.ret();
	}

	private static void createVarMapping(BrilNode cfgFunction) {
		final BrilPrepareForAsm prepare = new BrilPrepareForAsm(PREFIX_VIRTUAL_REGISTER, PREFIX_REGISTER, PREFIX_STACK_PARAMETER, MAX_REGISTERS, MAX_PARAMETERS_IN_REGISTERS);
		prepare.prepare(cfgFunction);
	}

	private static void convertToAsm(List<BrilNode> instructions, BrilVarMapping varMapping, BrilAsm asm) {
		final class MyHandler extends BrilInstructions.Handler {
			@Override
			protected void label(String name) {
				asm.label(name);
			}

			@Override
			protected void constant(String dest, int value) {
				varMapping.loadConstant(dest, value, asm);
			}

			@Override
			protected void id(String dest, String type, String src) {
				varMapping.id(dest, type, src, asm);
			}

			@Override
			protected void add(String dest, String var1, String var2) {
				varMapping.i_binaryOperator(dest, var1, var2, asm,
				                            (destReg, srcReg, asm1) -> asm1.iadd(destReg, srcReg));
			}

			@Override
			protected void sub(String dest, String var1, String var2) {
				varMapping.i_binaryOperator(dest, var1, var2, asm,
				                            (destReg, srcReg, asm1) -> asm1.isub(destReg, srcReg));
			}

			@Override
			protected void mul(String dest, String var1, String var2) {
				varMapping.i_binaryOperator(dest, var1, var2, asm,
				                            (destReg, srcReg, asm1) -> asm1.imul(destReg, srcReg));
			}

			@Override
			protected void div(String dest, String var1, String var2) {
				varMapping.i_binaryOperator(dest, var1, var2, asm,
				                            (destReg, srcReg, asm1) -> asm1.idiv(destReg, srcReg));
			}

			@Override
			protected void and(String dest, String var1, String var2) {
				throw new UnsupportedOperationException();
			}

			@Override
			protected void lessThan(String dest, String var1, String var2) {
				varMapping.b_binaryOperator(dest, var1, var2, asm,
				                            (destReg, leftReg, rightReg, asm12) -> asm12.ilt(destReg, leftReg, rightReg));
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
				asm.jump(target);
			}

			@Override
			protected void branch(String var, String thenLabel, String elseLabel) {
				varMapping.branch(var, thenLabel, elseLabel, asm);
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

				asm.call(name);
			}

			@Override
			protected void call(String dest, String type, String name, List<BrilNode> args) {
				if (name.equals(BrilRegisterIndirection.CALL_POP)) {
					Utils.assertTrue(args.isEmpty());
					final int register = varToRegister(dest);
					asm.ipop(register);
					return;
				}

				asm.call(name);
			}

			private int varToRegister(String arg) {
				Utils.assertTrue(arg.startsWith(PREFIX_REGISTER));
				return 2 * Integer.parseInt(arg.substring(PREFIX_REGISTER.length()));
			}
		}
		final MyHandler handler = new MyHandler();
		for (BrilNode instruction : instructions) {
			handler.visit(instruction);
		}
	}
}
