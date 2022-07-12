package de.regnis.b.ir;

import de.regnis.b.ast.Expression;
import de.regnis.b.ast.IfStatement;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringOutput;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class IfBlock extends ControlFlowBlock {

	// Fields =================================================================

	private final IfStatement node;

	// Setup ==================================================================

	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public IfBlock(@NotNull BasicBlock prevBlock, @NotNull IfStatement node, @NotNull Integer labelIndex) {
		super("if_" + labelIndex, prevBlock);
		this.node = node;

		// they might get replaced by direct links during compacting
		new BasicBlock("then_" + labelIndex, this);
		new BasicBlock("else_" + labelIndex, this);
	}

	// Implemented ============================================================

	@Override
	public void visit(@NotNull BlockVisitor visitor) {
		visitor.visitIf(this);
	}

	// Accessing ==============================================================

	@NotNull
	public Expression getCondition() {
		return node.expression;
	}

	@NotNull
	public AbstractBlock getTrueBlock() {
		return getNext().get(0);
	}

	@NotNull
	public AbstractBlock getFalseBlock() {
		return getNext().get(1);
	}

	public void print(String indentation, StringOutput output) {
		output.print(indentation);
		output.print("if ");
		CodePrinter.print(node.expression, output);
		output.println();
	}
}
