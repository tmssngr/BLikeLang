package de.regnis.b.ir;

import de.regnis.b.ast.IfStatement;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringOutput;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class IfBlock extends ControlFlowBlock {

	// Setup ==================================================================

	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public IfBlock(@NotNull BasicBlock prevBlock, @NotNull IfStatement node, String prefix, @NotNull Integer labelIndex) {
		super(node.expression(), prefix + "if_" + labelIndex, prevBlock);

		// they might get replaced by direct links during compacting
		new BasicBlock(prefix + "then_" + labelIndex, this);
		new BasicBlock(prefix + "else_" + labelIndex, this);
	}

	// Implemented ============================================================

	@Override
	public void visit(@NotNull BlockVisitor visitor) {
		visitor.visitIf(this);
	}

	@Override
	public StringOutput print(String indentation, StringOutput output) {
		super.print(indentation, output);

		output.print(indentation);
		output.print("if ");
		CodePrinter.print(getExpression(), output);
		output.println();
		return output;
	}

	// Accessing ==============================================================

	@NotNull
	public Block getTrueBlock() {
		return getNextBlocks().get(0);
	}

	@NotNull
	public Block getFalseBlock() {
		return getNextBlocks().get(1);
	}
}
