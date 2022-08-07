package de.regnis.b.ir;

import de.regnis.b.ast.Expression;
import de.regnis.b.ast.WhileStatement;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringOutput;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class WhileBlock extends ControlFlowBlock {

	// Fields =================================================================

	private final WhileStatement node;

	// Setup ==================================================================

	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public WhileBlock(@NotNull BasicBlock prev, @NotNull WhileStatement node, @NotNull Integer labelIndex) {
		super("while_" + labelIndex, prev);
		this.node = node;

		new BasicBlock("do_" + labelIndex, this);
		new BasicBlock("after_while_" + labelIndex, this);
	}

	// Implemented ============================================================

	@Override
	public void visit(@NotNull BlockVisitor visitor) {
		visitor.visitWhile(this);
	}

	// Accessing ==============================================================

	@NotNull
	public Expression getCondition() {
		return node.expression;
	}

	@NotNull
	public AbstractBlock getInnerBlock() {
		return getNextBlocks().get(0);
	}

	@NotNull
	public AbstractBlock getLeaveBlock() {
		return getNextBlocks().get(1);
	}

	public void print(String indentation, StringOutput output) {
		output.print(indentation);
		output.print("while ");
		CodePrinter.print(node.expression, output);
		output.println();
	}
}
