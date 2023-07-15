package de.regnis.bril;

import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Thomas Singer
 */
public class BrilInterpreter {

	// Fields =================================================================

	private final CallSupport callSupport;

	// Setup ==================================================================

	public BrilInterpreter() {
		this(new CallSupport());
	}

	public BrilInterpreter(CallSupport callSupport) {
		this.callSupport = callSupport;
	}

	// Accessing ==============================================================

	/**
	 * @throws InterpretingFailedException
	 */
	@Nullable
	public Object run(BrilNode function, List<Object> argumentValues) {
		callSupport.beforeRun();

		final List<BrilNode> arguments = BrilFactory.getArguments(function);
		if (arguments.size() != argumentValues.size()) {
			throw new InterpretingFailedException("expected " + arguments.size() + " arguments, but got " + argumentValues.size());
		}

		final String returnType = BrilFactory.getType(function);

		final Map<String, Object> varToValue = new HashMap<>();
		fillVarsFromArguments(argumentValues, arguments, varToValue);

		final List<BrilNode> instructions = BrilFactory.getInstructions(function);
		final Map<String, Integer> labelToIndex = determineLabelIndices(instructions);

		final BrilFunctionInterpreter interpreter = new BrilFunctionInterpreter(instructions, labelToIndex, varToValue, callSupport);
		final Object returnValue = interpreter.interpret();

		callSupport.afterRun();

		switch (returnType) {
			case BrilInstructions.INT -> {
				if (returnValue instanceof Integer value) {
					return value;
				}
			}
			case BrilInstructions.BOOL -> {
				if (returnValue instanceof Boolean value) {
					return value;
				}
			}
			case BrilInstructions.VOID -> {
				if (returnValue == null) {
					return null;
				}
			}
		}
		throw new InterpretingFailedException("Invalid return value " + returnValue);
	}

	// Utils ==================================================================

	/**
	 * @throws InterpretingFailedException
	 */
	private void fillVarsFromArguments(List<Object> argumentValues, List<BrilNode> arguments, Map<String, Object> varToValue) {
		final Iterator<Object> argumentValueIt = argumentValues.iterator();
		for (BrilNode argument : arguments) {
			final String argName = BrilFactory.getArgName(argument);
			if (varToValue.containsKey(argName)) {
				throw new InterpretingFailedException("duplicate argument name " + argName);
			}

			final String type = BrilFactory.getArgType(argument);
			final Object value = argumentValueIt.next();
			if (BrilInstructions.INT.equals(type)) {
				if (value instanceof Integer intValue) {
					varToValue.put(argName, intValue);
					continue;
				}

				throw new InterpretingFailedException("expected an integer for argument " + argName + " but got " + value);
			}

			if (BrilInstructions.BOOL.equals(type)) {
				if (value instanceof Boolean boolValue) {
					varToValue.put(argName, boolValue);
					continue;
				}

				throw new InterpretingFailedException("expected a boolean for argument " + argName + " but got " + value);
			}

			throw new InterpretingFailedException("unexpected type " + type + " for argument " + argName);
		}
	}

	/**
	 * @throws InterpretingFailedException
	 */
	private Map<String, Integer> determineLabelIndices(List<BrilNode> instructions) {
		final Map<String, Integer> labelToIndex = new HashMap<>();
		int i = 0;
		for (BrilNode instruction : instructions) {
			final String label = BrilInstructions.getLabel(instruction);
			if (label != null) {
				if (labelToIndex.containsKey(label)) {
					throw new InterpretingFailedException("Duplicate label " + label);
				}

				labelToIndex.put(label, i);
			}
			i++;
		}
		return labelToIndex;
	}

	// Inner Classes ==========================================================

	private static class BrilFunctionInterpreter extends BrilInstructions.Handler {

		private final List<BrilNode> instructions;
		private final Map<String, Integer> labelToIndex;
		private final Map<String, Object> varToValue;
		private final CallSupport callSupport;

		private int pc;
		private Object returnValue;

		public BrilFunctionInterpreter(List<BrilNode> instructions, Map<String, Integer> labelToIndex, Map<String, Object> varToValue, CallSupport callSupport) {
			this.instructions = instructions;
			this.labelToIndex = labelToIndex;
			this.varToValue   = varToValue;
			this.callSupport  = callSupport;
		}

		@Nullable
		public Object interpret() {
			while (pc < instructions.size()) {
				final BrilNode instruction = instructions.get(pc);
				pc++;

				visit(instruction);
			}
			return returnValue;
		}

		@Override
		protected void label(String name) {
			// do nothing
		}

		@Override
		protected void constant(String dest, int value) {
			varToValue.put(dest, value);
		}

		/**
		 * @throws InterpretingFailedException
		 */
		private Object getValue(String var, String type) {
			final Object obj = varToValue.get(var);
			if (obj == null) {
				throw new InterpretingFailedException("Expected var " + var);
			}

			if (!checkType(obj, type)) {
				throw new InterpretingFailedException("Unexpected value for var " + var);
			}
			return obj;
		}

		private boolean checkType(Object obj, String type) {
			if (BrilInstructions.INT.equals(type)) {
				return obj instanceof Integer;
			}
			return BrilInstructions.BOOL.equals(type)
					&& obj instanceof Boolean;
		}

		private int getIntValue(String var) {
			return (Integer) getValue(var, BrilInstructions.INT);
		}

		private boolean getBoolValue(String var) {
			return (Boolean) getValue(var, BrilInstructions.BOOL);
		}

		@Override
		protected void id(String dest, String type, String src) {
			final Object value = getValue(src, type);
			varToValue.put(dest, value);
		}

		@Override
		protected void add(String dest, String var1, String var2) {
			varToValue.put(dest, getIntValue(var1) + getIntValue(var2));
		}

		@Override
		protected void sub(String dest, String var1, String var2) {
			varToValue.put(dest, getIntValue(var1) - getIntValue(var2));
		}

		@Override
		protected void mul(String dest, String var1, String var2) {
			varToValue.put(dest, getIntValue(var1) * getIntValue(var2));
		}

		@Override
		protected void div(String dest, String var1, String var2) {
			varToValue.put(dest, getIntValue(var1) / getIntValue(var2));
		}

		@Override
		protected void and(String dest, String var1, String var2) {
			varToValue.put(dest, getIntValue(var1) & getIntValue(var2));
		}

		@Override
		protected void lessThan(String dest, String var1, String var2) {
			varToValue.put(dest, getIntValue(var1) < getIntValue(var2));
		}

		@Override
		protected void ret() {
			pc = instructions.size();
		}

		@Override
		protected void ret(String var, String type) {
			returnValue = getValue(var, type);
			ret();
		}

		@Override
		protected void jump(String target) {
			final Integer index = labelToIndex.get(target);
			if (index == null) {
				throw new InterpretingFailedException("Can't jump to " + target);
			}

			pc = index;
		}

		@Override
		protected void branch(String var, String thenLabel, String elseLabel) {
			jump(getBoolValue(var) ? thenLabel : elseLabel);
		}

		@Override
		protected void call(String name, List<BrilNode> args) {
			final List<Object> arguments = new ArrayList<>();
			for (BrilNode arg : args) {
				final String var = BrilFactory.getArgName(arg);
				final String type = BrilFactory.getArgType(arg);
				arguments.add(getValue(var, type));
			}
			final Object result = callSupport.call(name, arguments);
			if (result != null) {
				throw new InterpretingFailedException("expected no return value from call to " + name);
			}
		}

		@Override
		protected void call(String dest, String type, String name, List<BrilNode> args) {
			final List<Object> arguments = new ArrayList<>();
			for (BrilNode arg : args) {
				final String var = BrilFactory.getArgName(arg);
				final String argType = BrilFactory.getArgType(arg);
				arguments.add(getValue(var, argType));
			}
			final Object result = callSupport.call(name, arguments);
			checkType(result, type);
			varToValue.put(dest, result);
		}
	}

	public static final class InterpretingFailedException extends RuntimeException {
		public InterpretingFailedException(String message) {
			super(message);
		}
	}

	public static class CallSupport {
		public CallSupport() {
		}

		@Nullable
		public Object call(String name, List<Object> arguments) {
			throw new InterpretingFailedException("No method for " + name + " found");
		}

		public void beforeRun() {
		}

		public void afterRun() {
		}
	}
}
