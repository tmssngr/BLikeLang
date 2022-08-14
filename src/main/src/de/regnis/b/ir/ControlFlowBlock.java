package de.regnis.b.ir;

import de.regnis.b.ast.Expression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Thomas Singer
 */
public abstract class ControlFlowBlock extends StatementsBlock {

	// Fields =================================================================

	private Expression expression;

	// Setup ==================================================================

	protected ControlFlowBlock(Expression expression, @NotNull String label, @Nullable BasicBlock prev) {
		super(label, prev);
		this.expression = expression;
	}

	// Accessing ==============================================================

	@NotNull
	public final Expression getExpression() {
		return expression;
	}

	public final void setExpression(@NotNull Expression expression) {
		this.expression = expression;
	}
}
