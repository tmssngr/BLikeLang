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
	private final BasicBlock trueBlock;
	private final BasicBlock falseBlock;

	// Setup ==================================================================

	public IfBlock(@NotNull BasicBlock prevBlock, @NotNull IfStatement node, @NotNull Integer labelIndex) {
		super("if_" + labelIndex, prevBlock);
		this.node = node;

		trueBlock = new BasicBlock("then_" + labelIndex, this);
		falseBlock = new BasicBlock("else_" + labelIndex, this);
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
	public BasicBlock getTrueBlock() {
		return trueBlock;
	}

	@NotNull
	public BasicBlock getFalseBlock() {
		return falseBlock;
	}

	public void print(String indentation, StringOutput output) {
		output.print(indentation);
		output.print("if ");
		CodePrinter.print(node.expression, output);
		output.println();
	}
}
