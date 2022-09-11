package de.regnis.b.ir;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class CfgBlockLinearizer {

	// Static =================================================================

	public static List<AbstractBlock> linearize(@NotNull ControlFlowGraph graph) {
		final List<AbstractBlock> blocks = new ArrayList<>();
		linearizeBlocks(graph.getFirstBlock(), blocks);
		blocks.add(graph.getExitBlock());
		return blocks;
	}

	// Setup ==================================================================

	private CfgBlockLinearizer() {
	}

	// Utils ==================================================================

	private static void linearizeBlocks(AbstractBlock block, List<AbstractBlock> blocks) {
		if (blocks.contains(block)) {
			return;
		}

		switch (block) {
			case BasicBlock basicBlock -> {
				blocks.add(basicBlock);

				linearizeBlocks(basicBlock.getSingleNext(), blocks);
			}
			case IfBlock ifBlock -> {
				blocks.add(ifBlock);
				linearizeBlocks(ifBlock.getTrueBlock(), blocks);
				linearizeBlocks(ifBlock.getFalseBlock(), blocks);
			}
			case WhileBlock whileBlock -> {
				blocks.add(whileBlock);
				linearizeBlocks(whileBlock.getInnerBlock(), blocks);
				linearizeBlocks(whileBlock.getLeaveBlock(), blocks);
			}
			case ExitBlock ignored -> {
			}
		}
	}
}
