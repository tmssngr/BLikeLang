package de.regnis.bril;

import org.jetbrains.annotations.NotNull;

import java.util.*;

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
		final List<BrilNode> newInstructions = new ArrayList<>();
		for (BrilNode instruction : instructions) {
			newInstructions.add(handleInstruction(instruction));
		}
		return newInstructions;
	}

	// Utils ==================================================================

	private BrilNode handleInstruction(BrilNode instruction) {
		final String dest = BrilInstructions.getDest(instruction);
		if (dest != null) {
			final String op = BrilInstructions.getOp(instruction);
			if (BrilInstructions.CONST.equals(op)) {
				return handle(dest, instruction);
			}

			if (BrilInstructions.ID.equals(op)) {
				final String var = BrilInstructions.getVarNotNull(instruction);
				final String newVar = getCanonicalVar(var);
				return handle(dest, BrilInstructions.id(dest, newVar));
			}

			if (BrilInstructions.ADD.equals(op)) {
				final String var1 = BrilInstructions.getVar1NotNull(instruction);
				final String var2 = BrilInstructions.getVar2NotNull(instruction);
				final String newVar1 = getCanonicalVar(var1);
				final String newVar2 = getCanonicalVar(var2);
				final BrilNode newInstruction = BrilInstructions.add(dest, newVar1, newVar2);

				return handle(dest, newInstruction);
			}
		}
		return instruction;
	}

	private BrilNode handle(String dest, BrilNode newInstruction) {
		final CanonicalExpression canonicalExpression = getCanonicalExpression(newInstruction);
		for (Value value : availableValues) {
			if (value.canonicalExpression.equals(canonicalExpression)) {
				varToIndex.put(dest, value.index);
				return BrilInstructions.id(dest, value.canonicalVar);
			}
		}

		handleReassign(dest);

		if (canonicalExpression instanceof VarReadCanonicalExpression varRead) {
			varToIndex.put(dest, varRead.index);
		}
		else {
			final int index = getNextIndex();
			availableValues.add(new Value(index, canonicalExpression, dest));
			varToIndex.put(dest, index);
		}
		return newInstruction;
	}

	private String getCanonicalVar(String var) {
		final int index = varToIndex.get(var);
		return getValueAtIndex(index).canonicalVar;
	}

	private void handleReassign(String name) {
		final int reassignedIndex = getIndexNullable(name);
		if (reassignedIndex < 0) {
			return;
		}

		varToIndex.remove(name);

		String otherVarWithSameValueIndex = null;
		for (Map.Entry<String, Integer> entry : varToIndex.entrySet()) {
			if (entry.getValue() == reassignedIndex) {
				otherVarWithSameValueIndex = entry.getKey();
				break;
			}
		}

		for (final Iterator<Value> it = availableValues.iterator(); it.hasNext(); ) {
			final Value value = it.next();
			if (value.canonicalVar.equals(name)) {
				it.remove();
				if (otherVarWithSameValueIndex != null) {
					availableValues.add(new Value(value.index, value.canonicalExpression, otherVarWithSameValueIndex));
				}
				break;
			}
		}
	}

	private int getIndex(String var) {
		return varToIndex.get(var);
	}

	private int getIndexNullable(String name) {
		final Integer index = varToIndex.get(name);
		return index != null ? index : -1;
	}

	private Value getValueAtIndex(int index) {
		for (Value value : availableValues) {
			if (value.index == index) {
				return value;
			}
		}
		throw new IllegalArgumentException("index " + index + " not found");
	}

	private CanonicalExpression getCanonicalExpression(BrilNode instruction) {
		final String dest = BrilInstructions.getDest(instruction);
		if (dest != null) {
			final String op = BrilInstructions.getOp(instruction);
			if (BrilInstructions.CONST.equals(op)) {
				return new LiteralCanonicalExpression(BrilInstructions.getIntValue(instruction));
			}

			if (BrilInstructions.ID.equals(op)) {
				final String var = BrilInstructions.getVarNotNull(instruction);
				return getCanonicalExpression(var);
			}

			if (BrilInstructions.ADD.equals(op)) {
				CanonicalExpression canonical1 = getCanonicalExpression(BrilInstructions.getVar1NotNull(instruction));
				CanonicalExpression canonical2 = getCanonicalExpression(BrilInstructions.getVar2NotNull(instruction));
				if (isSwap(canonical1, op, canonical2)) {
					final var temp = canonical1;
					canonical1 = canonical2;
					canonical2 = temp;
				}
				return new BinaryCanonicalExpression(canonical1, op, canonical2);
			}
		}

		throw new UnsupportedOperationException();
	}

	@NotNull
	private VarReadCanonicalExpression getCanonicalExpression(String var) {
		final int varIndex = getIndex(var);
		return new VarReadCanonicalExpression(varIndex);
	}

	private int getNextIndex() {
		return nextIndex++;
	}

	@SuppressWarnings("RedundantIfStatement")
	private static boolean isSwap(CanonicalExpression left, String op, CanonicalExpression right) {
		if (!BrilInstructions.ADD.equals(op)) {
			return false;
		}

		if (left instanceof VarReadCanonicalExpression leftVar
				&& right instanceof VarReadCanonicalExpression rightVar
				&& leftVar.index > rightVar.index) {
			return true;
		}

		if (left instanceof LiteralCanonicalExpression
				&& !(right instanceof LiteralCanonicalExpression)) {
			return true;
		}
		return false;
	}

	// Inner Classes ==========================================================

	private interface CanonicalExpression {
	}

	private record LiteralCanonicalExpression(int value) implements CanonicalExpression {
		@Override
		public String toString() {
			return String.valueOf(value);
		}
	}

	private record VarReadCanonicalExpression(int index) implements CanonicalExpression {
		@Override
		public String toString() {
			return "#" + index;
		}
	}

	private record BinaryCanonicalExpression(CanonicalExpression left, String op,
	                                         CanonicalExpression right) implements CanonicalExpression {
		@Override
		public String toString() {
			return op + ", " + left + ", " + right;
		}
	}

	private static final class Value {

		private final int index;
		private final CanonicalExpression canonicalExpression;
		private final String canonicalVar;

		public Value(int index, CanonicalExpression canonicalExpression, String canonicalVar) {
			this.index               = index;
			this.canonicalExpression = canonicalExpression;
			this.canonicalVar        = canonicalVar;
		}

		@Override
		public String toString() {
			return "#" + index + " (" + canonicalVar + "): " + canonicalExpression;
		}
	}
}
