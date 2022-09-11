package de.regnis.b.ir;

import de.regnis.b.ast.SimpleStatement;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringOutput;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Thomas Singer
 */
public class ControlFlowGraphPrinter {

	// Constants ==============================================================

	public static final String INDENTATION = "    ";

	// Static =================================================================

	public static StringOutput print(ControlFlowGraph graph, StringOutput output) {
		final ControlFlowGraphPrinter printer = new ControlFlowGraphPrinter(graph, output);
		return printer.print();
	}

	// Fields =================================================================

	private final ControlFlowGraph graph;
	private final StringOutput output;

	// Setup ==================================================================

	public ControlFlowGraphPrinter(@NotNull ControlFlowGraph graph, @NotNull StringOutput output) {
		this.graph = graph;
		this.output = output;
	}

	// Accessing ==============================================================

	@NotNull
	public final StringOutput print() {
		final List<AbstractBlock> blocks = graph.getLinearizedBlocks();

		for (AbstractBlock block : blocks) {
			switch (block) {
				case BasicBlock basicBlock -> {
					printLabel(basicBlock);

					printBefore(INDENTATION, basicBlock);
					printStatements(basicBlock);

					printGoto(basicBlock, basicBlock.getSingleNext(), blocks);
				}
				case IfBlock ifBlock -> {
					printLabel(ifBlock);

					printBefore(INDENTATION, ifBlock);
					printStatements(ifBlock);

					output.print(INDENTATION);
					output.print("if ");
					CodePrinter.print(ifBlock.getExpression(), output);
					output.println();

					printlnIndented("if ! goto " + ifBlock.getFalseBlock().label);
					printGoto(ifBlock, ifBlock.getTrueBlock(), blocks);
				}
				case WhileBlock whileBlock -> {
					printLabel(whileBlock);

					printBefore(INDENTATION, whileBlock);
					printStatements(whileBlock);

					output.print(INDENTATION);
					output.print("while ");
					CodePrinter.print(whileBlock.getExpression(), output);
					output.println();
				}
				case ExitBlock exitBlock -> {
					printLabel(exitBlock);

					printlnIndented("return");
				}
			}
		}

		return output;
	}

	private void printGoto(AbstractBlock block, AbstractBlock next, List<AbstractBlock> blocks) {
		if (blocks.indexOf(next) != blocks.indexOf(block) + 1) {
			printlnIndented("goto " + next.label);
			output.println();
		}
	}

	private void printLabel(AbstractBlock block) {
		output.print(getLabelText(block));
		output.println();
	}


	@NotNull
	protected String getLabelText(AbstractBlock block) {
		return block.label + ":";
	}

	protected final void printlnIndented(String s) {
		output.print(INDENTATION + s);
		output.println();
	}

	protected void printBefore(String indentation, AbstractBlock block) {
	}

	protected void print(String indentation, SimpleStatement statement) {
		output.print(indentation);
		CodePrinter.print(statement, output);
	}

	// Utils ==================================================================

	private void printStatements(StatementsBlock block) {
		for (SimpleStatement statement : block.getStatements()) {
			print(INDENTATION, statement);
		}
	}
}
