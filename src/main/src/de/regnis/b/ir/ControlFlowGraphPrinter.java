package de.regnis.b.ir;

import de.regnis.b.out.StringOutput;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class ControlFlowGraphPrinter {

	// Static =================================================================

	public static StringOutput print(ControlFlowGraph graph, StringOutput output) {
		final List<AbstractBlock> blocks = linearizeBlocks(graph);

		final String indentation = "    ";

		final BlockVisitor visitor = new BlockVisitor() {
			@Override
			public void visitBasic(BasicBlock block) {
				printLabel(block);

				block.print(indentation, output);

				printGoto(block, block.getSingleNext());
			}

			@Override
			public void visitIf(IfBlock block) {
				printLabel(block);

				block.print(indentation, output);
				output.print(indentation + "if ! goto " + block.getFalseBlock().label);
				output.println();
				printGoto(block, block.getTrueBlock());
			}

			@Override
			public void visitWhile(WhileBlock block) {
				printLabel(block);

				block.print(indentation, output);
			}

			@Override
			public void visitExit(ExitBlock block) {
				printLabel(block);

				output.print(indentation + "return");
				output.println();
			}

			private void printGoto(AbstractBlock block, AbstractBlock next) {
				if (blocks.indexOf(next) != blocks.indexOf(block) + 1) {
					output.print(indentation + "goto " + next.label);
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

	// Utils ==================================================================

	private static List<AbstractBlock> linearizeBlocks(ControlFlowGraph graph) {
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
