package de.regnis.bril;

import de.regnis.utils.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

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

	public static List<Block> formBlocks(List<BrilNode> instructions) {
		final List<Block> blocks = new ArrayList<>();
		final Map<String, Block> labelToBlock = new HashMap<>();

		formBlocks(instructions, blockInstructions -> {
			final BrilNode brilNode = blockInstructions.get(0);
			String name = BrilFactory.getLabel(brilNode);
			if (name != null) {
				blockInstructions.remove(0);
			}
			else {
				name = "block " + labelToBlock.size();
			}

			final Block block = new Block(name, blockInstructions);
			blocks.add(block);
			labelToBlock.put(block.name, block);
		});

		for (int i = 0; i < blocks.size(); i++) {
			final Block block = blocks.get(i);
			final Block nextBlock = i < blocks.size() - 1 ? blocks.get(i + 1) : null;
			connect(block, nextBlock, labelToBlock);
		}

		return blocks;
	}

	private static void connect(Block block, @Nullable Block nextBlock, Map<String, Block> labelToBlock) {
		final BrilNode lastInstruction = Utils.getLast(block.instructions);
		final String op = BrilFactory.getOp(lastInstruction);
		if (BrilFactory.RET.equals(op)) {
			return;
		}

		final List<String> targets = BrilFactory.getJmpTargets(lastInstruction);
		if (targets.isEmpty() && nextBlock != null) {
			targets.add(nextBlock.name);
		}
		for (String target : targets) {
			final Block targetBlock = labelToBlock.get(target);
			block.successors.add(targetBlock);
			targetBlock.predecessors.add(block);
		}
	}

	// Inner Classes ==========================================================

	public static final class Block {
		private final List<Block> predecessors = new ArrayList<>();
		private final List<Block> successors = new ArrayList<>();
		public final List<BrilNode> instructions;
		public final String name;

		public Block(String name, List<BrilNode> instructions) {
			this.name         = name;
			this.instructions = Collections.unmodifiableList(new ArrayList<>(instructions));
		}

		@Override
		public String toString() {
			final StringBuilder buffer = new StringBuilder();
			buffer.append(name);
			buffer.append(": pred[");
			final Function<Block, String> blockStringFunction = block -> String.valueOf(block.name);
			Utils.appendCommaSeparated(predecessors, blockStringFunction, buffer);
			buffer.append("], succ[");
			Utils.appendCommaSeparated(successors, blockStringFunction, buffer);
			buffer.append("]");
			return name;
		}

		public List<Block> getPredecessors() {
			return Collections.unmodifiableList(predecessors);
		}

		public List<Block> getSuccessors() {
			return Collections.unmodifiableList(successors);
		}
	}
}
