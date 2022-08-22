package de.regnis.b.ir;

import de.regnis.b.ast.WhileStatement;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringOutput;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class WhileBlock extends ControlFlowBlock {

	// Setup ==================================================================

	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public WhileBlock(@NotNull BasicBlock prev, @NotNull WhileStatement node, @NotNull String prefix, @NotNull Integer labelIndex) {
		super(node.expression(), prefix + "while_" + labelIndex, prev);

		new BasicBlock(prefix + "do_" + labelIndex, this);
		new BasicBlock(prefix + "after_while_" + labelIndex, this);
	}

	// Implemented ============================================================

	@Override
	public void visit(@NotNull BlockVisitor visitor) {
		visitor.visitWhile(this);
	}

	@Override
	public StringOutput print(String indentation, StringOutput output) {
		super.print(indentation, output);

		output.print(indentation);
		output.print("while ");
		CodePrinter.print(getExpression(), output);
		output.println();
		return output;
	}

	// Accessing ==============================================================

	@NotNull
	public AbstractBlock getInnerBlock() {
		return getNextBlocks().get(0);
	}

	@NotNull
	public AbstractBlock getLeaveBlock() {
		return getNextBlocks().get(1);
	}
}
