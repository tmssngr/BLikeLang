package de.regnis.b.ir;

import de.regnis.b.ast.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Thomas Singer
 */
public final class CommonSubexpressionEliminationTransformation {

	// Fields =================================================================

	private final Map<String, Integer> varToIndex = new HashMap<>();
	private final List<Value> availableValues = new ArrayList<>();

	private int nextIndex;

	// Setup ==================================================================

	public CommonSubexpressionEliminationTransformation() {
	}

	// Accessing ==============================================================

	public List<SimpleStatement> transform(List<SimpleStatement> statements) {
		final List<SimpleStatement> newStatements = new ArrayList<>();
		for (SimpleStatement statement : statements) {
			newStatements.add(handleStatement(statement));
		}
		return newStatements;
	}

	// Utils ==================================================================

	private SimpleStatement handleStatement(SimpleStatement statement) {
		if (statement instanceof VarDeclaration declaration) {
			return handleVarDeclaration(declaration);
		}
		if (statement instanceof Assignment assignment) {
			return handleAssignment(assignment);
		}
		throw new UnsupportedOperationException();
	}

	private SimpleStatement handleVarDeclaration(VarDeclaration declaration) {
		final String varName = declaration.name();
		final Expression newExpression = handleAssignmentExpression(varName, declaration.expression());
		return new VarDeclaration(varName, newExpression);
	}

	private SimpleStatement handleAssignment(Assignment assignment) {
		if (assignment.operation() != Assignment.Op.assign) {
			throw new UnsupportedOperationException();
		}

		final String varName = assignment.name();
		final Expression newExpression = handleAssignmentExpression(varName, assignment.expression());
		return new Assignment(Assignment.Op.assign, varName, newExpression);
	}

	@NotNull
	private Expression handleAssignmentExpression(String varName, Expression expression) {
		final Expression newExpression;
		if (expression instanceof NumberLiteral literal) {
			newExpression = handleSimpleExpression(literal);
		}
		else if (expression instanceof VarRead varRead) {
			newExpression = handleSimpleExpression(varRead);
		}
		else if (expression instanceof BinaryExpression binaryExpression) {
			final SimpleExpression left = handleSimpleExpression(binaryExpression.left());
			final SimpleExpression right = handleSimpleExpression(binaryExpression.right());
			newExpression = new BinaryExpression(left, binaryExpression.operator(), right);
		}
		else {
			throw new UnsupportedOperationException();
		}

		final CanonicalExpression canonicalExpression = getCanonicalExpression(newExpression);
		for (Value value : availableValues) {
			if (value.canonicalExpression.equals(canonicalExpression)) {
				varToIndex.put(varName, value.index);
				return new VarRead(value.canonicalVar);
			}
		}

		handleReassign(varName);

		if (canonicalExpression instanceof VarReadCanonicalExpression varRead) {
			varToIndex.put(varName, varRead.index);
		}
		else {
			final int index = getNextIndex();
			availableValues.add(new Value(index, canonicalExpression, varName));
			varToIndex.put(varName, index);
		}
		return newExpression;
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

	private SimpleExpression handleSimpleExpression(Expression expression) {
		if (expression instanceof NumberLiteral) {
			return (SimpleExpression) expression;
		}

		if (expression instanceof VarRead varRead) {
			final int index = getIndex(varRead);
			final String var = getValueAtIndex(index).canonicalVar;
			return new VarRead(var);
		}

		throw new UnsupportedOperationException();
	}

	private int getIndex(VarRead varRead) {
		return varToIndex.get(varRead.name());
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

	private CanonicalExpression getCanonicalExpression(Expression expression) {
		if (expression instanceof NumberLiteral literal) {
			return new LiteralCanonicalExpression(literal);
		}

		if (expression instanceof VarRead varRead) {
			final int varIndex = getIndex(varRead);
			return new VarReadCanonicalExpression(varIndex);
		}

		if (expression instanceof BinaryExpression binaryExpression) {
			CanonicalExpression left = getCanonicalExpression(binaryExpression.left());
			CanonicalExpression right = getCanonicalExpression(binaryExpression.right());
			if (isSwap(left, binaryExpression.operator(), right)) {
				final var temp = left;
				left  = right;
				right = temp;
			}
			return new BinaryCanonicalExpression(left, binaryExpression.operator(), right);
		}

		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("RedundantIfStatement")
	private static boolean isSwap(CanonicalExpression left, BinaryExpression.Op operator, CanonicalExpression right) {
		if (!operator.commutative) {
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

	private int getNextIndex() {
		return nextIndex++;
	}

	// Inner Classes ==========================================================

	private interface CanonicalExpression {
	}

	private record LiteralCanonicalExpression(NumberLiteral literal) implements CanonicalExpression {
		@Override
		public String toString() {
			return literal.toString();
		}
	}

	private record VarReadCanonicalExpression(int index) implements CanonicalExpression {
		@Override
		public String toString() {
			return "#" + index;
		}
	}

	private record BinaryCanonicalExpression(CanonicalExpression left, BinaryExpression.Op operator,
	                                         CanonicalExpression right) implements CanonicalExpression {
		@Override
		public String toString() {
			return operator.text + ", " + left + ", " + right;
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
