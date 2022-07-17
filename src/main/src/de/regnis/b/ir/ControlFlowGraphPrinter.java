package de.regnis.b.ir;

import de.regnis.b.ast.SimpleStatement;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringOutput;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Singer
 */
public class ControlFlowGraphPrinter {

	// Constants ==============================================================

	private static final String INDENTATION = "    ";

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
		final List<AbstractBlock> blocks = linearizeBlocks();

		final BlockVisitor visitor = new BlockVisitor() {
			@Override
			public void visitBasic(BasicBlock block) {
				printLabel(block);
				print(block);
				printGoto(block, block.getSingleNext());
			}

			@Override
			public void visitIf(IfBlock block) {
				printLabel(block);

				print(block);
				output.print(INDENTATION + "if ! goto " + block.getFalseBlock().label);
				output.println();
				printGoto(block, block.getTrueBlock());
			}

			@Override
			public void visitWhile(WhileBlock block) {
				printLabel(block);

				print(block);
			}

			@Override
			public void visitExit(ExitBlock block) {
				printLabel(block);

				output.print(INDENTATION + "return");
				output.println();
			}

			private void printGoto(AbstractBlock block, AbstractBlock next) {
				if (blocks.indexOf(next) != blocks.indexOf(block) + 1) {
					output.print(INDENTATION + "goto " + next.label);
					output.println();
					output.println();
				}
			}

			private void printLabel(AbstractBlock block) {
				output.print(block.label + ":");
				output.println();
			}
		};

		for (AbstractBlock block : blocks) {
			block.visit(visitor);
		}

		return output;
	}

	protected void printBefore(String indentation, AbstractBlock block) {
	}

	protected void print(String indentation, SimpleStatement statement) {
		output.print(indentation);
		CodePrinter.print(statement, output);
	}

	// Utils ==================================================================

	private void print(BasicBlock block) {
		printBefore(INDENTATION, block);

		for (SimpleStatement statement : block.getStatements()) {
			print(INDENTATION, statement);
		}
	}

	private void print(IfBlock block) {
		printBefore(INDENTATION, block);

		block.print(INDENTATION, output);
	}

	private void print(WhileBlock block) {
		printBefore(INDENTATION, block);

		block.print(INDENTATION, output);
	}

	private List<AbstractBlock> linearizeBlocks() {
		final List<AbstractBlock> blocks = new ArrayList<>();
		linearizeBlocks(graph.getFirstBlock(), blocks);
		blocks.add(graph.getExitBlock());
		return blocks;
	}

	private static void linearizeBlocks(AbstractBlock block, List<AbstractBlock> blocks) {
		if (blocks.contains(block)) {
			return;
		}

		block.visit(new BlockVisitor() {
			@Override
			public void visitBasic(BasicBlock block) {
				blocks.add(block);

				linearizeBlocks(block.getSingleNext(), blocks);
			}

			@Override
			public void visitIf(IfBlock block) {
				blocks.add(block);
				linearizeBlocks(block.getTrueBlock(), blocks);
				linearizeBlocks(block.getFalseBlock(), blocks);
			}

			@Override
			public void visitWhile(WhileBlock block) {
				blocks.add(block);
				linearizeBlocks(block.getInnerBlock(), blocks);
				linearizeBlocks(block.getLeaveBlock(), blocks);
			}

			@Override
			public void visitExit(ExitBlock block) {
			}
		});
	}
}
