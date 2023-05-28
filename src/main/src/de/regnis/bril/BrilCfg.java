package de.regnis.bril;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Thomas Singer
 */
public class BrilCfg {

	// Constants ==============================================================

	private static final Set<String> TERMINATER_OPS = Set.of(BrilFactory.JMP);

	// Static =================================================================

	public static void formBlocks(List<BrilNode> instructions, Consumer<List<BrilNode>> consumer) {
		List<BrilNode> currentBlock = new ArrayList<>();

		for (BrilNode instruction : instructions) {
			final String op = BrilFactory.getOp(instruction);
			if (op != null) {
				currentBlock.add(instruction);
				if (TERMINATER_OPS.contains(op)) {
					consumer.accept(currentBlock);
					currentBlock = new ArrayList<>();
				}
			}
			else {
				// label
				if (!currentBlock.isEmpty()) {
					consumer.accept(currentBlock);
				}
				currentBlock = new ArrayList<>();
				currentBlock.add(instruction);
			}
		}

		if (!currentBlock.isEmpty()) {
			consumer.accept(currentBlock);
		}
	}
}
