package de.regnis.bril;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

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
		asm.simplify(BrilAsmSimplifier.create());
		return asm.toLines();
	}

	// Utils ==================================================================

	private static void convertToAsm(BrilNode function, BrilAsm asm) {
		final BrilNode functionFromCfg = toCfgAndBackToInstructions(function);

		final String name = BrilFactory.getName(functionFromCfg);
		asm.label(name);

		final BrilVarMapping varMapping = BrilVarMapping.createVarMapping(functionFromCfg);
		varMapping.allocLocalVarSpace(asm);

		final List<BrilNode> instructions = BrilFactory.getInstructions(function);
		final String exitLabel = name + " exit";
		convertToAsm(instructions, exitLabel, varMapping, asm);

		asm.label(exitLabel);
		varMapping.freeLocalVarSpace(asm);
		asm.ret();
	}

	private static BrilNode toCfgAndBackToInstructions(BrilNode function) {
		final BrilNode cfgFunction;
		try {
			cfgFunction = BrilCfg.buildBlocks(function);
		}
		catch (BrilCfg.DuplicateLabelException | BrilCfg.NoExitBlockException | BrilCfg.InvalidTargetLabelException e) {
			throw new AssertionError(e);
		}
		final List<BrilNode> blocks = BrilCfg.getBlocks(cfgFunction);
		BrilCfgDetectVarLiveness.detectLiveness(blocks, true);
		return BrilCfg.flattenBlocks(cfgFunction);
	}

	private static void convertToAsm(List<BrilNode> instructions, String exitLabel, BrilVarMapping varMapping, BrilAsm asm) {
		final class MyHandler extends BrilInstructions.Handler {
			@Nullable
			private String exitLabel;

			private MyHandler(@NotNull String exitLabel) {
				this.exitLabel = exitLabel;
			}

			@Override
			protected void label(String name) {
				asm.label(name);
			}

			@Override
			protected void constant(String dest, int value) {
				varMapping.loadConstant(dest, value, asm);
			}

			@Override
			protected void id(String dest, String var) {
				varMapping.id(dest, var, asm);
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
				throw new UnsupportedOperationException();
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
			protected void ret() {
				throw new UnsupportedOperationException();
			}

			@Override
			protected void ret(String var) {
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

			@Override
			protected void jump(String target) {
				asm.jump(target);
			}

			@Override
			protected void branch(String var, String thenLabel, String elseLabel) {
				final String varType = varMapping.getType(var);
				if (!BrilInstructions.BOOL.equals(varType)) {
					throw new IllegalArgumentException("Expected a bool variable for the br command");
				}

				varMapping.branch(var, thenLabel, elseLabel, asm);
			}

			@Override
			protected void call(String name, List<String> args) {
				throw new UnsupportedOperationException();
			}

			@Override
			protected void call(String dest, String name, List<String> args) {
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

				final String type = varMapping.getType(dest);
				if (BrilInstructions.INT.equals(type)) {
					varMapping.istoreReturnValueInVar(dest, asm);
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

			@Override
			protected void print(String var) {
				final String varType = varMapping.getType(var);
				if (BrilInstructions.INT.equals(varType)) {
					varMapping.callArgument(var, 0, asm);
				}
				else {
					throw new UnsupportedOperationException(varType);
				}

				asm.call("print");
			}
		}
		final MyHandler handler = new MyHandler(exitLabel);
		for (final Iterator<BrilNode> it = instructions.iterator(); it.hasNext(); ) {
			final BrilNode instruction = it.next();
			if (!it.hasNext()) {
				handler.exitLabel = null;
			}
			handler.visit(instruction);
		}
	}
}
