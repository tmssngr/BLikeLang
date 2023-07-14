package de.regnis.bril;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author Thomas Singer
 */
public final class BrilRegisterIndirection {

	// Constants ==============================================================

	static final String CALL_PUSH = ".push";
	static final String CALL_POP = ".pop";

	// Fields =================================================================

	@Nullable
	private final Map<String, String> mapping;
	private final Predicate<String> needsTempAccess;
	private final String prefixRegister;
	private final int maxParametersInRegisters;
	private final String prefixTempVar;

	private int tempIndex;

	// Setup ==================================================================

	public BrilRegisterIndirection(int tempIndex, Predicate<String> needsTempAccess, String prefixRegister, String prefixTempVar, int maxParametersInRegisters) {
		this.needsTempAccess          = needsTempAccess;
		this.tempIndex                = tempIndex;
		this.prefixRegister           = prefixRegister;
		this.prefixTempVar            = prefixTempVar;
		this.maxParametersInRegisters = maxParametersInRegisters;
		mapping                       = null;
	}

	public BrilRegisterIndirection(String prefixRegister, String prefixTempVar, int maxParametersInRegisters, Map<String, String> mapping, Predicate<String> needsTempAccess) {
		this.mapping                  = new HashMap<>(mapping);
		this.needsTempAccess          = needsTempAccess;
		tempIndex                     = mapping.size();
		this.prefixRegister           = prefixRegister;
		this.prefixTempVar            = prefixTempVar;
		this.maxParametersInRegisters = maxParametersInRegisters;
	}

	// Accessing ==============================================================

	public void transformBlocks(List<BrilNode> blocks) {
		for (BrilNode block : blocks) {
			final List<BrilNode> oldInstructions = BrilCfg.getInstructions(block);
			final List<BrilNode> newInstructions = transformInstructions(oldInstructions);
			BrilCfg.setInstructions(newInstructions, block);
		}
	}

	public List<BrilNode> transformInstructions(List<BrilNode> instructions) {
		final BrilInstructions factory = new BrilInstructions();
		final BrilInstructions.Handler handler = new MyHandler(factory);
		for (BrilNode instruction : instructions) {
			handler.visit(instruction);
		}
		return factory.get();
	}

	// Utils ==================================================================

	@NotNull
	private String createTemp() {
		final String temp = prefixTempVar + tempIndex;
		tempIndex++;
		return temp;
	}

	private boolean isStackParameter(String var) {
		return needsTempAccess.test(var);
	}

	private String getMapped(String var) {
		if (mapping == null) {
			return var;
		}

		final String mapped = mapping.get(var);
		if (mapped == null) {
			throw new IllegalArgumentException("unknown var " + var);
		}
		return mapped;
	}

	// Inner Classes ==========================================================

	private class MyHandler extends BrilInstructions.Handler {
		private final BrilInstructions factory;

		private BrilNode instruction;

		public MyHandler(BrilInstructions factory) {
			this.factory = factory;
		}

		@Override
		public void visit(BrilNode instruction) {
			this.instruction = instruction;
			super.visit(instruction);
			this.instruction = null;
		}

		@Override
		protected void label(String name) {
			factory.label(name);
		}

		@Override
		protected void constant(String dest, int value) {
			final String newDest = getMapped(dest);
			if (isStackParameter(newDest)) {
				final String temp = createTemp();
				factory.constant(temp, value);
				factory.idi(newDest, temp);
			}
			else {
				factory.constant(newDest, value);
			}
		}

		@Override
		protected void id(String dest, String type, String src) {
			final String newSrc = getMapped(src);
			final String newDest = getMapped(dest);
			if (isStackParameter(newSrc) || isStackParameter(newDest)) {
				final String temp = createTemp();
				factory.id(temp, type, newSrc);
				factory.id(newDest, type, temp);
			}
			else {
				factory.id(newDest, type, newSrc);
			}
		}

		private interface BinaryOp {
			void binary(String dest, String var1, String var2);
		}

		private void binary(String dest, String type, String var1, String var2, BinaryOp op) {
			String newVar1 = getMapped(var1);
			String newVar2 = getMapped(var2);
			final String newDest = getMapped(dest);
			String temp1 = null;
			if (isStackParameter(newVar1)) {
				temp1 = createTemp();
				factory.id(temp1, type, newVar1);
				newVar1 = temp1;
			}
			String temp2 = null;
			if (isStackParameter(newVar2)) {
				temp2 = createTemp();
				factory.id(temp2, type, newVar2);
				newVar2 = temp2;
			}

			if (isStackParameter(newDest)) {
				final String temp = temp1 != null
						? temp1
						: temp2 != null
						? temp2
						: createTemp();
				op.binary(temp, newVar1, newVar2);
				factory.id(newDest, type, temp);
			}
			else {
				op.binary(newDest, newVar1, newVar2);
			}
		}

		@Override
		protected void add(String dest, String var1, String var2) {
			binary(dest, BrilInstructions.INT, var1, var2,
			       (d, v1, v2) -> factory.add(d, v1, v2));
		}

		@Override
		protected void sub(String dest, String var1, String var2) {
			binary(dest, BrilInstructions.INT, var1, var2,
			       (d, v1, v2) -> factory.sub(d, v1, v2));
		}

		@Override
		protected void mul(String dest, String var1, String var2) {
			binary(dest, BrilInstructions.INT, var1, var2,
			       (d, v1, v2) -> factory.mul(d, v1, v2));
		}

		@Override
		protected void div(String dest, String var1, String var2) {
			binary(dest, BrilInstructions.INT, var1, var2,
			       (d, v1, v2) -> factory.div(d, v1, v2));
		}

		@Override
		protected void and(String dest, String var1, String var2) {
			binary(dest, BrilInstructions.INT, var1, var2,
			       (d, v1, v2) -> factory.and(d, v1, v2));
		}

		@Override
		protected void lessThan(String dest, String var1, String var2) {
			binary(dest, BrilInstructions.INT, var1, var2,
			       (d, v1, v2) -> factory.lessThan(d, v1, v2));
		}

		@Override
		protected void ret() {
			factory.ret();
		}

		@Override
		protected void ret(String var, String type) {
			final String newVar = getMapped(var);
			factory.id("r.0", type, newVar);
			// only necessary for making r.0 used:
			factory.ret("r.0", type);
		}

		@Override
		protected void jump(String target) {
			factory.jump(target);
		}

		@Override
		protected void branch(String var, String thenLabel, String elseLabel) {
			String newVar = getMapped(var);
			if (isStackParameter(newVar)) {
				final String temp = createTemp();
				factory.idb(temp, newVar);
				newVar = temp;
			}
			factory.branch(newVar, thenLabel, elseLabel);
		}

		@Override
		protected void call(String name, List<BrilNode> args) {
			final List<Integer> registersToPop = pushArgRegisters(args, null);
			final List<BrilNode> newArgs = handleCallArgs(args);

			factory.call(name, newArgs);

			cleanupCallArgs(args);
			popRegisters(registersToPop);
		}

		@Override
		protected void call(String dest, String type, String name, List<BrilNode> args) {
			final List<Integer> registersToPop = pushArgRegisters(args, dest);
			final List<BrilNode> newArgs = handleCallArgs(args);

			final String result = prefixRegister + 0;

			factory.call(result, type, name, newArgs);

			final String newDest = getMapped(dest);
			factory.id(newDest, type, result);

			cleanupCallArgs(args);
			popRegisters(registersToPop);
		}

		@NotNull
		private List<Integer> pushArgRegisters(List<BrilNode> args, @Nullable String dest) {
			final Set<String> liveOut = BrilCfgDetectVarLiveness.getLiveOut(instruction);

			final List<Integer> registersToPop = new ArrayList<>();
			for (int i = 0; i < args.size() && i < maxParametersInRegisters; i++) {
				final BrilNode arg = args.get(i);
				final String argName = BrilFactory.getArgName(arg);
				if (argName.equals(dest)) {
					continue;
				}

				if (liveOut.contains(argName)) {
					factory.call(CALL_PUSH, List.of(BrilFactory.arg(prefixRegister + i, BrilInstructions.INT)));
					registersToPop.add(0, i);
				}
			}
			return registersToPop;
		}

		private void popRegisters(List<Integer> registersToPop) {
			for (int reg : registersToPop) {
				factory.call(prefixRegister + reg, BrilInstructions.INT, CALL_POP, List.of());
			}
		}

		@NotNull
		private List<BrilNode> handleCallArgs(List<BrilNode> args) {
			final List<BrilNode> newArgs = new ArrayList<>();
			String temp = null;

			for (int i = 0; i < args.size(); i++) {
				final BrilNode arg = args.get(i);
				final String argName = BrilFactory.getArgName(arg);
				final String argType = BrilFactory.getArgType(arg);
				String newArg = getMapped(argName);
				if (i < maxParametersInRegisters) {
					final String registerParameter = prefixRegister + i;
					factory.id(registerParameter, argType, newArg);
					newArgs.add(BrilFactory.arg(registerParameter, argType));
				}
				else {
					if (isStackParameter(newArg)) {
						if (temp == null) {
							temp = createTemp();
						}
						factory.id(temp, argType, newArg);
						newArg = temp;
					}
					factory.call(CALL_PUSH, List.of(BrilFactory.arg(newArg, argType)));
					newArgs.add(BrilFactory.arg(newArg, argType));
				}
			}
			return newArgs;
		}

		private void cleanupCallArgs(List<BrilNode> args) {
			String temp = null;
			for (int i = 2; i < args.size(); i++) {
				final BrilNode arg = args.get(i);
				final String argName = BrilFactory.getArgName(arg);
				final String argType = BrilFactory.getArgType(arg);
				final String newArg = getMapped(argName);
				if (temp == null) {
					temp = createTemp();
				}
				factory.call(newArg, argType, CALL_POP, List.of());
			}
		}
	}
}
