package de.regnis.b.ir;

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
	private final BasicBlock innerBlock;
	private final BasicBlock leaveBlock;

	// Setup ==================================================================

	public WhileBlock(@NotNull BasicBlock prev, @NotNull WhileStatement node, @NotNull Integer labelIndex) {
		super("while_" + labelIndex, prev);
		this.node = node;
		innerBlock = new BasicBlock("do_" + labelIndex, this);
		leaveBlock = new BasicBlock("after_while_" + labelIndex, this);
	}

	// Implemented ============================================================

	@Override
	public void visit(@NotNull BlockVisitor visitor) {
		visitor.visitWhile(this);
	}

	// Accessing ==============================================================

	public BasicBlock getInnerBlock() {
		return innerBlock;
	}

	public BasicBlock getLeaveBlock() {
		return leaveBlock;
	}

	public void print(String indentation, StringOutput output) {
		output.print(indentation);
		output.print("while ");
		CodePrinter.print(node.expression, output);
		output.println();
	}
}
