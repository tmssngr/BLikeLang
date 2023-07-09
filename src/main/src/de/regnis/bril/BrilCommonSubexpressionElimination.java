package de.regnis.bril;

import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Thomas Singer
 */
public class BrilCommonSubexpressionElimination {

	// Fields =================================================================

	private final Map<String, Integer> varToIndex = new HashMap<>();
	private final List<Value> availableValues = new ArrayList<>();

	private int nextIndex;

	// Setup ==================================================================

	public BrilCommonSubexpressionElimination() {
	}

	// Accessing ==============================================================

	public List<BrilNode> transform(List<BrilNode> instructions) {
		final BrilInstructions factory = new BrilInstructions();
		for (BrilNode instruction : instructions) {
			handleInstruction(instruction, factory);
		}
		return factory.get();
	}

	// Utils ==================================================================

	private BrilInstructions handleInstruction(BrilNode instruction, BrilInstructions factory) {
		final String op = BrilInstructions.getOp(instruction);

		final String dest = BrilInstructions.getDest(instruction);
		final String type = BrilInstructions.getType(instruction);
		if (dest != null) {
			if (BrilInstructions.CONST.equals(op) && BrilInstructions.INT.equals(type)) {
				final CanonicalExpression canonicalExpression = new Literal(BrilInstructions.getIntValue(instruction));
				final int canonicalIndex = findCanonicalExpression(canonicalExpression);
				if (canonicalIndex < 0) {
					addValue(dest, type, canonicalExpression);
				}
				return factory.add(instruction);
			}

			if (BrilInstructions.ID.equals(op) && type != null) {
				final String var = BrilInstructions.getVarNotNull(instruction);
				final int varIndex = getVarIndex(var, type);
				final Value value = getValueAtIndex(varIndex);
				varToIndex.put(dest, varIndex);
				if (value.canonicalExpression instanceof Literal literal) {
					return factory.constant(dest, literal.value);
				}

				return id(dest, value, factory);
			}

			if (BrilInstructions.ADD.equals(op) && BrilInstructions.INT.equals(type)) {
				final String var1 = BrilInstructions.getVar1NotNull(instruction);
				final String var2 = BrilInstructions.getVar2NotNull(instruction);
				final int varIndex1 = getVarIndex(var1, type);
				final int varIndex2 = getVarIndex(var2, type);
				final Value value1 = getValueAtIndex(varIndex1);
				final Value value2 = getValueAtIndex(varIndex2);
				if (value1.canonicalExpression instanceof Literal literal1
						&& value2.canonicalExpression instanceof Literal literal2) {
					return factory.constant(dest, literal1.value + literal2.value);
				}

				final CanonicalExpression canonicalExpression = new Binary(op, varIndex1, varIndex2);
				final int expressionIndex = findCanonicalExpression(canonicalExpression);
				if (expressionIndex < 0) {
					addValue(dest, type, canonicalExpression);
					return factory.binary(dest, type, op, value1.getCanonicalVar(), value2.getCanonicalVar());
				}

				final Value value = getValueAtIndex(expressionIndex);
				return id(dest, value, factory);
			}
		}

		if (BrilInstructions.CALL.equals(op)) {
			Utils.todo(); // return value
			final List<BrilNode> args = BrilInstructions.getArgs(instruction);
			final List<BrilNode> newArgs = new ArrayList<>();
			for (BrilNode arg : args) {
				final String var = BrilFactory.getArgName(arg);
				final String argType = BrilFactory.getArgType(arg);
				final Value value = getValueForVar(var, argType);
				final String canonicalVar = value.getCanonicalVar();
				newArgs.add(BrilFactory.arg(canonicalVar, argType));
			}
			return factory.call(BrilInstructions.getName(instruction), newArgs);
		}

		if (BrilInstructions.getRequiredVars(instruction).size() > 0) {
			throw new UnsupportedOperationException();
		}

		return factory.add(instruction);
	}

	@NotNull
	private static BrilInstructions id(String dest, Value value, BrilInstructions factory) {
		final String canonicalVar = value.getCanonicalVar();
		value.addVar(dest);
		Utils.todo();
		final String type = BrilInstructions.INT;
		return factory.id(dest, type, canonicalVar);
	}

	private int addValue(String dest, String type, CanonicalExpression canonicalExpression) {
		final int index = getNextIndex();
		final Integer prevIndex = varToIndex.put(dest, index);
		if (prevIndex != null) {
			final Value oldValueInDest = getValueAtIndex(prevIndex);
			oldValueInDest.canonicalVars.remove(dest);
			if (oldValueInDest.canonicalVars.isEmpty()) {
				availableValues.remove(oldValueInDest);
			}
		}
		availableValues.add(new Value(index, type, canonicalExpression, dest));
		return index;
	}

	@NotNull
	private Value getValueForVar(String var, String type) {
		final int varIndex = getVarIndex(var, type);
		return getValueAtIndex(varIndex);
	}

	private int findCanonicalExpression(CanonicalExpression canonicalExpression) {
		for (Value value : availableValues) {
			if (canonicalExpression.equals(value.canonicalExpression)) {
				return value.index;
			}
		}
		return -1;
	}

	private Value getValueAtIndex(int index) {
		for (Value value : availableValues) {
			if (value.index == index) {
				return value;
			}
		}
		throw new IllegalArgumentException("Unexpected index " + index);
	}

	private int getVarIndex(String var, String type) {
		final Integer index = varToIndex.get(var);
		return index != null
				? index
				: addValue(var, type, new Parameter(var));
	}

	private int getNextIndex() {
		return nextIndex++;
	}

	// Inner Classes ==========================================================

	private interface CanonicalExpression {
	}

	private record Literal(int value) implements CanonicalExpression {
	}

	private record Parameter(String name) implements CanonicalExpression {
	}

	private record Binary(String op, int index1, int index2) implements CanonicalExpression {
		private Binary(String op, int index1, int index2) {
			this.op     = op;
			final boolean commutative = BrilInstructions.ADD.equals(op);
			this.index1 = commutative ? Math.min(index1, index2) : index1;
			this.index2 = commutative ? Math.max(index1, index2) : index2;
		}
	}

	private static final class Value {

		private final int index;
		private final String type;
		private final CanonicalExpression canonicalExpression;
		private final List<String> canonicalVars;

		public Value(int index, String type, CanonicalExpression canonicalExpression, String canonicalVar) {
			this.index               = index;
			this.type                = type;
			this.canonicalExpression = canonicalExpression;

			canonicalVars = new ArrayList<>();
			canonicalVars.add(canonicalVar);
		}

		@Override
		public String toString() {
			return "#" + index + ": " + type + " (" + canonicalVars + "): " + canonicalExpression;
		}

		public String getCanonicalVar() {
			return canonicalVars.get(0);
		}

		public void addVar(String dest) {
			if (canonicalVars.contains(dest)) {
				throw new UnsupportedOperationException();
			}

			canonicalVars.add(dest);
		}
	}
}
