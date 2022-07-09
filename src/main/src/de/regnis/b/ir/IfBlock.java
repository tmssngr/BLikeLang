package de.regnis.b.ir;

import de.regnis.b.ast.IfStatement;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class IfBlock extends ControlFlowBlock {

	// Fields =================================================================

	private final IfStatement node;

	// Setup ==================================================================

	public IfBlock(@NotNull BasicBlock prevBlock, @NotNull IfStatement node) {
		super(prevBlock);
		this.node = node;
	}
}
