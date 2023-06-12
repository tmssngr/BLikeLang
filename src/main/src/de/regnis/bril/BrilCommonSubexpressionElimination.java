package de.regnis.bril;

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
		if (dest != null) {
			if (BrilInstructions.CONST.equals(op)) {
				final CanonicalExpression canonicalExpression = new Literal(BrilInstructions.getIntValue(instruction));
				final int canonicalIndex = findCanonicalExpression(canonicalExpression);
				if (canonicalIndex < 0) {
					addValue(dest, canonicalExpression);
				}
				return factory.add(instruction);
			}

			if (BrilInstructions.ID.equals(op)) {
				final String var = BrilInstructions.getVarNotNull(instruction);
				final int varIndex = getVarIndex(var);
				final Value value = getValueAtIndex(varIndex);
				varToIndex.put(dest, varIndex);
				if (value.canonicalExpression instanceof Literal literal) {
					return factory.constant(dest, literal.value);
				}

				return id(dest, value, factory);
			}

			if (BrilInstructions.ADD.equals(op)) {
				final String var1 = BrilInstructions.getVar1NotNull(instruction);
				final String var2 = BrilInstructions.getVar2NotNull(instruction);
				final int varIndex1 = getVarIndex(var1);
				final int varIndex2 = getVarIndex(var2);
				final Value value1 = getValueAtIndex(varIndex1);
				final Value value2 = getValueAtIndex(varIndex2);
				if (value1.canonicalExpression instanceof Literal literal1
						&& value2.canonicalExpression instanceof Literal literal2) {
					return factory.constant(dest, literal1.value + literal2.value);
				}

				final CanonicalExpression canonicalExpression = new Binary(op, varIndex1, varIndex2);
				final int expressionIndex = findCanonicalExpression(canonicalExpression);
				if (expressionIndex < 0) {
					addValue(dest, canonicalExpression);
					return factory.binary(dest, op, value1.getCanonicalVar(), value2.getCanonicalVar());
				}

				final Value value = getValueAtIndex(expressionIndex);
				return id(dest, value, factory);
			}
		}

		if (BrilInstructions.PRINT.equals(op)) {
			final String var = BrilInstructions.getVarNotNull(instruction);
			final Value value = getValueForVar(var);
			final String canonicalVar = value.getCanonicalVar();
			return factory.print(canonicalVar);
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
		return factory.id(dest, canonicalVar);
	}

	private int addValue(String dest, CanonicalExpression canonicalExpression) {
		final int index = getNextIndex();
		final Integer prevIndex = varToIndex.put(dest, index);
		if (prevIndex != null) {
			final Value oldValueInDest = getValueAtIndex(prevIndex);
			oldValueInDest.canonicalVars.remove(dest);
			if (oldValueInDest.canonicalVars.isEmpty()) {
				availableValues.remove(oldValueInDest);
			}
		}
		availableValues.add(new Value(index, canonicalExpression, dest));
		return index;
	}

	@NotNull
	private Value getValueForVar(String var) {
		final int varIndex = getVarIndex(var);
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

	private int getVarIndex(String var) {
		final Integer index = varToIndex.get(var);
		return index != null
				? index
				: addValue(var, new Parameter(var));
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
		private final CanonicalExpression canonicalExpression;
		private final List<String> canonicalVars;

		public Value(int index, CanonicalExpression canonicalExpression, String canonicalVar) {
			this.index               = index;
			this.canonicalExpression = canonicalExpression;

			canonicalVars = new ArrayList<>();
			canonicalVars.add(canonicalVar);
		}

		@Override
		public String toString() {
			return "#" + index + " (" + canonicalVars + "): " + canonicalExpression;
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
