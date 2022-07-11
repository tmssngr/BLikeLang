package de.regnis.b.ir;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Thomas Singer
 */
public final class ControlFlowGraphVarUsageDetector {

	// Static =================================================================

	public static void detectVarUsage(@NotNull ControlFlowGraph graph) {
		detectRequiredAndProvidedVars(graph.getFirstBlock());
		detectInputOutputVars(graph.getExitBlock());
	}

	// Utils ==================================================================

	private static void detectRequiredAndProvidedVars(AbstractBlock firstBlock) {
		final List<AbstractBlock> blocks = new ArrayList<>();
		blocks.add(firstBlock);

		final Set<AbstractBlock> processedBlocks = new HashSet<>();

		while (blocks.size() > 0) {
			final AbstractBlock block = blocks.remove(0);
			if (!processedBlocks.add(block)) {
				continue;
			}

			block.detectRequiredVars();
			blocks.addAll(block.getNext());
		}
	}

	private static void detectInputOutputVars(ExitBlock lastBlock) {
		final List<AbstractBlock> blocks = new ArrayList<>();
		blocks.add(lastBlock);

		final Set<AbstractBlock> processedBlocks = new HashSet<>();

		while (blocks.size() > 0) {
			final AbstractBlock block = blocks.remove(0);
			if (!processedBlocks.add(block)) {
				continue;
			}

			final boolean changed = block.detectInputOutputVars();
			if (changed || processedBlocks.add(block)) {
				blocks.addAll(block.getPrev());
			}
		}
	}
}
