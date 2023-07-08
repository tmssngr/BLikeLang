package de.regnis.bril;

import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author Thomas Singer
 */
public final class BrilRegisterIndirection {

	// Fields =================================================================

	private final Map<String, String> mapping;
	private final Set<String> vars;
	private final Predicate<String> needsTempAccess;

	// Setup ==================================================================

	public BrilRegisterIndirection(Map<String, String> mapping, Predicate<String> needsTempAccess) {
		this.mapping         = new HashMap<>(mapping);
		this.needsTempAccess = needsTempAccess;
		vars                 = new HashSet<>(mapping.values());
		Utils.assertTrue(vars.size() == mapping.size());
	}

	// Accessing ==============================================================

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
		final String temp = "t." + vars.size();
		vars.add(temp);
		return temp;
	}

	private boolean isStackParameter(String var) {
		return needsTempAccess.test(var);
	}

	private String getMapped(String var) {
		final String mapped = mapping.get(var);
		if (mapped == null) {
			throw new IllegalArgumentException("unknown var " + var);
		}
		return mapped;
	}

	// Inner Classes ==========================================================

	private class MyHandler extends BrilInstructions.Handler {
		private final BrilInstructions factory;

		public MyHandler(BrilInstructions factory) {
			this.factory = factory;
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
				factory.id(newDest, temp);
			}
			else {
				factory.constant(newDest, value);
			}
		}

		@Override
		protected void id(String dest, String src) {
			final String newSrc = getMapped(src);
			final String newDest = getMapped(dest);
			if (isStackParameter(newSrc) || isStackParameter(newDest)) {
				final String temp = createTemp();
				factory.id(temp, newSrc);
				factory.id(newDest, temp);
			}
			else {
				factory.id(newDest, newSrc);
			}
		}

		private interface BinaryOp {
			void binary(String dest, String var1, String var2);
		}

		private void binary(String dest, String var1, String var2, BinaryOp op) {
			String newVar1 = getMapped(var1);
			String newVar2 = getMapped(var2);
			final String newDest = getMapped(dest);
			String temp1 = null;
			if (isStackParameter(newVar1)) {
				temp1 = createTemp();
				factory.id(temp1, newVar1);
				newVar1 = temp1;
			}
			String temp2 = null;
			if (isStackParameter(newVar2)) {
				temp2 = createTemp();
				factory.id(temp2, newVar2);
				newVar2 = temp2;
			}

			if (isStackParameter(newDest)) {
				final String temp = temp1 != null
						? temp1
						: temp2 != null
						? temp2
						: createTemp();
				op.binary(temp, newVar1, newVar2);
				factory.id(newDest, temp);
			}
			else {
				op.binary(newDest, newVar1, newVar2);
			}
		}

		@Override
		protected void add(String dest, String var1, String var2) {
			binary(dest, var1, var2,
			       (d, v1, v2) -> factory.add(d, v1, v2));
		}

		@Override
		protected void sub(String dest, String var1, String var2) {
			binary(dest, var1, var2,
			       (d, v1, v2) -> factory.sub(d, v1, v2));
		}

		@Override
		protected void mul(String dest, String var1, String var2) {
			binary(dest, var1, var2,
			       (d, v1, v2) -> factory.mul(d, v1, v2));
		}

		@Override
		protected void and(String dest, String var1, String var2) {
			binary(dest, var1, var2,
			       (d, v1, v2) -> factory.and(d, v1, v2));
		}

		@Override
		protected void lessThan(String dest, String var1, String var2) {
			binary(dest, var1, var2,
			       (d, v1, v2) -> factory.lessThan(d, v1, v2));
		}

		@Override
		protected void ret() {
			factory.ret();
		}

		@Override
		protected void ret(String var) {
			final String newVar = getMapped(var);
			factory.id("r.0", newVar);
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
				factory.id(temp, newVar);
				newVar = temp;
			}
			factory.branch(newVar, thenLabel, elseLabel);
		}

		@Override
		protected void call(String name, List<String> args) {
			String temp = null;
			for (int i = 0; i < args.size(); i++) {
				final String arg = args.get(i);
				String newArg = getMapped(arg);
				if (i < 2) {
					factory.id("r.0", newArg);
				}
				else {
					if (isStackParameter(newArg)) {
						if (temp == null) {
							temp = createTemp();
						}
						factory.id(temp, newArg);
						newArg = temp;
					}
					factory.call(".push", List.of(newArg));
				}
			}
			factory.call(name, List.of());

			for (int i = 2; i < args.size(); i++) {
				final String arg = args.get(i);
				final String newArg = getMapped(arg);
				if (temp == null) {
					temp = createTemp();
				}
				factory.call(newArg, ".pop", List.of());
			}
		}

		@Override
		protected void call(String dest, String name, List<String> args) {
			call(name, args);
			final String newDest = getMapped(dest);
			factory.constant("r.0", 0); // for usages
			factory.id(newDest, "r.0");
		}

		@Override
		protected void print(String var) {
			String newVar = getMapped(var);
			if (isStackParameter(newVar)) {
				final String temp = createTemp();
				factory.id(temp, newVar);
				newVar = temp;
			}
			factory.print(newVar);
		}
	}
}
