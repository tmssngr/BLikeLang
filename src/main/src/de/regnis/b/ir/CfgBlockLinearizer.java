package de.regnis.b.ir;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class CfgBlockLinearizer {

	// Static =================================================================

	public static List<Block> linearize(@NotNull ControlFlowGraph graph) {
		final List<Block> blocks = new ArrayList<>();
		linearizeBlocks(graph.getFirstBlock(), blocks);
		blocks.add(graph.getExitBlock());
		return blocks;
	}

	// Setup ==================================================================

	private CfgBlockLinearizer() {
	}

	// Utils ==================================================================

	private static void linearizeBlocks(Block block, List<Block> blocks) {
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
