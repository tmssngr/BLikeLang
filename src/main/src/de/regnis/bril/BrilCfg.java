package de.regnis.bril;

import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Thomas Singer
 */
public final class BrilCfg {

	// Constants ==============================================================

	private static final Set<String> TERMINATER_OPS = Set.of(BrilInstructions.JMP);

	private static final String KEY_INSTRUCTIONS = "instructions";
	private static final String KEY_NAME = "name";
	private static final String KEY_SUCCESSORS = "successors";
	private static final String KEY_PREDECESSORS = "predecessors";
	private static final String KEY_BLOCKS = "blocks";

	// Static =================================================================

	public static List<List<BrilNode>> splitIntoBlocks(List<BrilNode> instructions) {
		final List<List<BrilNode>> blocks = new ArrayList<>();

		List<BrilNode> currentBlock = new ArrayList<>();

		for (BrilNode instruction : instructions) {
			final String op = BrilInstructions.getOp(instruction);
			if (op != null) {
				currentBlock.add(instruction);
				if (TERMINATER_OPS.contains(op)) {
					blocks.add(currentBlock);
					currentBlock = new ArrayList<>();
				}
			}
			else {
				// label
				if (!currentBlock.isEmpty()) {
					blocks.add(currentBlock);
				}
				currentBlock = new ArrayList<>();
				currentBlock.add(instruction);
			}
		}

		if (!currentBlock.isEmpty()) {
			blocks.add(currentBlock);
		}
		return blocks;
	}

	@NotNull
	public static List<BrilNode> getBlocks(BrilNode function) {
		return function.getOrCreateNodeList(KEY_BLOCKS);
	}

	@NotNull
	public static BrilNode createFunction(String name, String type, List<BrilNode> arguments, List<BrilNode> blocks) {
		final BrilNode node = BrilFactory.createFunction(name, type, arguments);
		node.getOrCreateNodeList(KEY_BLOCKS)
				.addAll(blocks);
		return node;
	}

	public static BrilNode buildBlocks(BrilNode function) throws DuplicateLabelException, NoExitBlockException, InvalidTargetLabelException {
		final String name = BrilFactory.getName(function);
		final String type = BrilFactory.getType(function);
		final List<BrilNode> arguments = BrilFactory.getArguments(function);

		final List<BrilNode> blocks = buildBlocks(BrilFactory.getInstructions(function));

		return createFunction(name, type, arguments, blocks);
	}

	public static List<BrilNode> buildBlocks(List<BrilNode> instructions) throws DuplicateLabelException, InvalidTargetLabelException, NoExitBlockException {
		final Map<String, BrilNode> nameToBlock = new LinkedHashMap<>();

		final List<List<BrilNode>> splitIntoBlocks = splitIntoBlocks(instructions);
		for (int i = 0; i < splitIntoBlocks.size(); i++) {
			final List<BrilNode> blockInstructions = splitIntoBlocks.get(i);
			final BrilNode brilNode = blockInstructions.get(0);
			String name = BrilInstructions.getLabel(brilNode);
			if (name != null) {
				blockInstructions.remove(0);
			}
			else {
				name = "block " + i;
			}

			final BrilNode blockNode = createBlock(name, blockInstructions);
			if (nameToBlock.put(name, blockNode) != null) {
				throw new DuplicateLabelException(name);
			}
		}

		final List<BrilNode> blocks = new ArrayList<>();

		final BrilNode exitBlock = createBlock("exit " + splitIntoBlocks.size(), List.of());

		@Nullable BrilNode fallThroughFromBlock = null;
		for (Map.Entry<String, BrilNode> entry : nameToBlock.entrySet()) {
			final String name = entry.getKey();
			final BrilNode block = entry.getValue();

			if (fallThroughFromBlock != null) {
				connectBlocksAndAppendJump(fallThroughFromBlock, block);
				fallThroughFromBlock = null;
			}

			blocks.add(block);
			final List<BrilNode> blockInstructions = block.getOrCreateNodeList(KEY_INSTRUCTIONS);

			final List<String> successors = block.getOrCreateStringList(KEY_SUCCESSORS);

			final BrilNode lastInstruction = Utils.getLast(blockInstructions);
			final String op = BrilInstructions.getOp(lastInstruction);
			if (BrilInstructions.RET.equals(op)) {
				blockInstructions.remove(lastInstruction);
				connectBlocks(block, exitBlock);
				continue;
			}

			final List<String> targets = BrilInstructions.getJmpTargets(lastInstruction);
			if (targets.isEmpty()) {
				fallThroughFromBlock = block;
				continue;
			}

			for (String target : targets) {
				final BrilNode targetBlock = nameToBlock.get(target);
				if (targetBlock == null) {
					throw new InvalidTargetLabelException(target);
				}

				successors.add(target);
				targetBlock.getOrCreateStringList(KEY_PREDECESSORS).add(name);
			}
		}

		if (fallThroughFromBlock != null) {
			connectBlocksAndAppendJump(fallThroughFromBlock, exitBlock);
		}

		if (exitBlock.getOrCreateStringList(KEY_PREDECESSORS).isEmpty()) {
			throw new NoExitBlockException();
		}

		blocks.add(exitBlock);

		return blocks;
	}

	@NotNull
	public static Map<String, BrilNode> getNameToBlock(List<BrilNode> blocks) {
		final Map<String, BrilNode> nameToBlock = new HashMap<>();
		for (BrilNode block : blocks) {
			final String name = getName(block);
			if (nameToBlock.put(name, block) != null) {
				throw new IllegalStateException("Duplicate block name " + name);
			}
		}
		return nameToBlock;
	}

	@NotNull
	public static String getName(BrilNode block) {
		return block.getString(KEY_NAME);
	}

	@NotNull
	public static List<String> getPredecessors(BrilNode block) {
		return Collections.unmodifiableList(block.getStringList(KEY_PREDECESSORS));
	}

	@NotNull
	public static List<String> getSuccessors(BrilNode block) {
		return Collections.unmodifiableList(block.getStringList(KEY_SUCCESSORS));
	}

	@NotNull
	public static List<BrilNode> getInstructions(BrilNode block) {
		return Collections.unmodifiableList(block.getOrCreateNodeList(KEY_INSTRUCTIONS));
	}

	public static void setInstructions(List<BrilNode> blockInstructions, BrilNode block) {
		final List<BrilNode> instructions = block.getOrCreateNodeList(KEY_INSTRUCTIONS);
		instructions.clear();
		instructions.addAll(blockInstructions);
	}

	@NotNull
	public static BrilNode createBlock(String name, List<BrilNode> blockInstructions) {
		final BrilNode block = new BrilNode();
		block.set(KEY_NAME, name);
		setInstructions(blockInstructions, block);
		return block;
	}

	@NotNull
	public static BrilNode createBlock(String name, List<BrilNode> blockInstructions, List<String> predecessors, List<String> successors) {
		final BrilNode block = createBlock(name, blockInstructions);
		block.set(KEY_PREDECESSORS, predecessors);
		block.set(KEY_SUCCESSORS, successors);
		return block;
	}

	public static void testValidSuccessorsAndPredecessors(List<BrilNode> blocks) throws IllegalStateException {
		final Map<String, BrilNode> nameToBlock = getNameToBlock(blocks);

		if (getPredecessors(blocks.get(0)).size() > 0) {
			throw new IllegalStateException("expecting the first block to have no predecessors");
		}

		if (getSuccessors(Utils.getLast(blocks)).size() > 0) {
			throw new IllegalStateException("expecting the last block to have no successors");
		}

		for (BrilNode block : blocks) {
			final String name = getName(block);
			for (String predecessor : getPredecessors(block)) {
				final List<String> successors = getSuccessors(nameToBlock.get(predecessor));
				if (!successors.contains(name)) {
					throw new IllegalStateException("expecting " + predecessor + " to have successor " + name);
				}
			}

			for (String successor : getSuccessors(block)) {
				final List<String> predecessors = getPredecessors(nameToBlock.get(successor));
				if (!predecessors.contains(name)) {
					throw new IllegalStateException("expecting " + successor + " to have predecessor " + name);
				}
			}
		}
	}

	// Utils ==================================================================

	private static void connectBlocksAndAppendJump(@NotNull BrilNode prevBlock, BrilNode nextBlock) {
		connectBlocks(prevBlock, nextBlock);
		prevBlock.getOrCreateNodeList(KEY_INSTRUCTIONS)
				.add(BrilInstructions.jump(getName(nextBlock)));
	}

	private static void connectBlocks(@NotNull BrilNode prevBlock, @NotNull BrilNode nextBlock) {
		prevBlock.getOrCreateStringList(KEY_SUCCESSORS).add(getName(nextBlock));
		nextBlock.getOrCreateStringList(KEY_PREDECESSORS).add(getName(prevBlock));
	}

	// Inner Classes ==========================================================

	public static class DuplicateLabelException extends Exception {
		public DuplicateLabelException(String label) {
			super(label);
		}
	}

	public static class InvalidTargetLabelException extends Exception {
		public InvalidTargetLabelException(String target) {
			super(target);
		}
	}

	public static class NoExitBlockException extends Exception {
		public NoExitBlockException() {
		}
	}
}
