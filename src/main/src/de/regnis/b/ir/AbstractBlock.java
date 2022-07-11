package de.regnis.b.ir;

import de.regnis.b.ast.*;
import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Thomas Singer
 */
public abstract class AbstractBlock {

	// Abstract ===============================================================

	public abstract void visit(@NotNull BlockVisitor visitor);

	// Fields =================================================================

	private final List<AbstractBlock> prev = new ArrayList<>();
	private final List<AbstractBlock> next = new ArrayList<>();
	private final Set<String> input = new LinkedHashSet<>();
	private final Set<String> output = new LinkedHashSet<>();
	public final String label;

	// Setup ==================================================================

	protected AbstractBlock(@NotNull String label, @Nullable AbstractBlock prev) {
		this.label = label;

		if (prev != null) {
			addPrev(prev);
		}
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		if (input.isEmpty() && output.isEmpty()) {
			return label;
		}

		final StringBuilder buffer = new StringBuilder();
		buffer.append(label);
		buffer.append(" (");
		Utils.appendCommaSeparated(input, buffer);
		buffer.append(") -> (");
		Utils.appendCommaSeparated(output, buffer);
		buffer.append(")");
		return buffer.toString();
	}

	// Accessing ==============================================================

	public final void addPrev(@NotNull AbstractBlock prev) {
		this.prev.add(prev);
		prev.next.add(this);
	}

	public final List<AbstractBlock> getPrev() {
		return Collections.unmodifiableList(prev);
	}

	public final List<AbstractBlock> getNext() {
		return Collections.unmodifiableList(next);
	}

	public final Set<String> getInput() {
		return Collections.unmodifiableSet(input);
	}

	public final Set<String> getOutput() {
		return Collections.unmodifiableSet(output);
	}

	protected final void addProvides(@NotNull String name) {
		output.add(name);
	}

	public void detectRequiredVars() {
	}

	protected final void detectRequiredVars(Expression expression) {
		expression.visit(new ExpressionVisitor<>() {
			@Override
			public Object visitBinary(BinaryExpression node) {
				detectRequiredVars(node.left);
				detectRequiredVars(node.right);
				return node;
			}

			@Override
			public Object visitFunctionCall(FuncCall node) {
				for (Expression parameter : node.getParameters()) {
					detectRequiredVars(parameter);
				}
				return node;
			}

			@Override
			public Object visitNumber(NumberLiteral node) {
				return node;
			}

			@Override
			public Object visitBoolean(BooleanLiteral node) {
				return node;
			}

			@Override
			public Object visitVarRead(VarRead node) {
				if (!output.contains(node.name)) {
					input.add(node.name);
				}
				return node;
			}

			@Override
			public Object visitTypeCast(TypeCast node) {
				return node;
			}
		});
	}

	protected final boolean detectInputOutputVars() {
		final Set<String> requiredByNext = new HashSet<>();
		for (AbstractBlock nextBlock : next) {
			requiredByNext.addAll(nextBlock.input);
		}

		boolean changed = output.retainAll(requiredByNext);

		for (String required : requiredByNext) {
			if (output.add(required)) {
				changed = true;
				input.add(required);
			}
		}

		return changed;
	}
}
