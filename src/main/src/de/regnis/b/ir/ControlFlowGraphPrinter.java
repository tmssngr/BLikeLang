package de.regnis.b.ir;

import de.regnis.b.ast.SimpleStatement;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringOutput;
import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

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

	private boolean printPrevBlocks;

	// Setup ==================================================================

	public ControlFlowGraphPrinter(@NotNull ControlFlowGraph graph, @NotNull StringOutput output) {
		this.graph  = graph;
		this.output = output;
	}

	// Accessing ==============================================================

	public ControlFlowGraphPrinter setPrintPrevBlocks() {
		this.printPrevBlocks = true;
		return this;
	}

	@NotNull
	public final StringOutput print() {
		final List<Block> blocks = graph.getLinearizedBlocks();

		final BlockVisitor visitor = new BlockVisitor() {
			@Override
			public void visitBasic(BasicBlock block) {
				printLabel(block);

				printBefore(INDENTATION, block);
				printStatements(block);

				printGoto(block, block.getSingleNext());
			}

			@Override
			public void visitIf(IfBlock block) {
				printLabel(block);

				printBefore(INDENTATION, block);
				printStatements(block);

				output.print(INDENTATION);
				output.print("if ");
				CodePrinter.print(block.getExpression(), output);
				output.println();

				printlnIndented("if ! goto " + block.getFalseBlock().label);
				printGoto(block, block.getTrueBlock());
			}

			@Override
			public void visitWhile(WhileBlock block) {
				printLabel(block);

				printBefore(INDENTATION, block);
				printStatements(block);

				output.print(INDENTATION);
				output.print("while ");
				CodePrinter.print(block.getExpression(), output);
				output.println();
			}

			@Override
			public void visitExit(ExitBlock block) {
				printLabel(block);

				printlnIndented("return");
			}

			private void printGoto(Block block, Block next) {
				if (blocks.indexOf(next) != blocks.indexOf(block) + 1) {
					printlnIndented("goto " + next.label);
					output.println();
				}
			}

			private void printLabel(Block block) {
				output.print(getLabelText(block));
				output.println();
			}
		};

		for (Block block : blocks) {
			block.visit(visitor);
		}

		return output;
	}

	protected void printBefore(String indentation, Block block) {
	}

	protected void print(String indentation, SimpleStatement statement) {
		output.print(indentation);
		CodePrinter.print(statement, output);
	}

	// Utils ==================================================================

	@NotNull
	private String getLabelText(Block block) {
		final StringBuilder buffer = new StringBuilder();
		buffer.append(block.label);
		buffer.append(":");

		if (printPrevBlocks) {
			final List<Block> prevBlocks = block.getPrevBlocks();
			if (prevBlocks.size() > 0) {
				buffer.append("  // ");
				Utils.appendCommaSeparated(Utils.convert(prevBlocks, new Function<>() {
					@Override
					public String apply(Block block) {
						return block.label;
					}
				}), buffer);
			}
		}

		return buffer.toString();
	}

	private void printlnIndented(String s) {
		output.print(INDENTATION + s);
		output.println();
	}

	private void printStatements(StatementsBlock block) {
		for (SimpleStatement statement : block.getStatements()) {
			print(INDENTATION, statement);
		}
	}
}
