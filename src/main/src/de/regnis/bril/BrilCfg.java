package de.regnis.bril;

import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

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

			final BrilNode lastInstruction = Utils.getLastOrNull(blockInstructions);
			if (lastInstruction == null) {
				fallThroughFromBlock = block;
				continue;
			}

			final String op = BrilInstructions.getOp(lastInstruction);
			if (BrilInstructions.RET.equals(op)) {
				if (BrilInstructions.getRequiredVars(lastInstruction).isEmpty()) {
					blockInstructions.remove(lastInstruction);
				}
				connectBlocksAndAppendJump(block, exitBlock);
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

		// append the exit block only if it already has some predecessors
		if (exitBlock.getOrCreateStringList(KEY_PREDECESSORS).isEmpty()) {
			if (fallThroughFromBlock == null) {
				throw new NoExitBlockException();
			}
		}
		else {
			if (fallThroughFromBlock != null) {
				connectBlocksAndAppendJump(fallThroughFromBlock, exitBlock);
			}
			blocks.add(exitBlock);
		}

		return blocks;
	}

	public static void removeUnusedBlocks(List<BrilNode> blocks) {
		boolean changed;
		do {
			final Map<String, BrilNode> nameToBlock = getNameToBlock(blocks);

			final Set<String> usedLabels = getUsedLabels(blocks, nameToBlock);

			changed = false;

			for (final Iterator<BrilNode> it = blocks.iterator(); it.hasNext(); ) {
				final BrilNode block = it.next();
				final String name = getName(block);
				if (usedLabels.contains(name)) {
					continue;
				}

				it.remove();
				changed = true;

				final List<String> successors = getSuccessors(block);
				for (String successor : successors) {
					final BrilNode successorBlock = nameToBlock.get(successor);
					successorBlock
							.getOrCreateStringList(KEY_PREDECESSORS)
							.remove(name);
				}
			}
		}
		while (changed);
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
		if (!instructions.equals(blockInstructions)) {
			instructions.clear();
			instructions.addAll(blockInstructions);
		}
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

	public static BrilNode flattenBlocks(BrilNode cfgFunction) {
		final String name = BrilFactory.getName(cfgFunction);
		final String type = BrilFactory.getType(cfgFunction);
		final List<BrilNode> arguments = BrilFactory.getArguments(cfgFunction);
		final List<BrilNode> instructions = new ArrayList<>();
		boolean addLabel = false;
		for (BrilNode block : getBlocks(cfgFunction)) {
			final String blockName = getName(block);
			if (addLabel) {
				instructions.add(BrilInstructions.createLabel(blockName));
			}
			instructions.addAll(getInstructions(block));
			addLabel = true;
		}
		return BrilFactory.createFunction(name, type, arguments, instructions);
	}

	/**
	 * @throws IllegalStateException
	 */
	public static void testValidSuccessorsAndPredecessors(BrilNode cfgFunction) {
		testValidSuccessorsAndPredecessors(getBlocks(cfgFunction));
	}

	/**
	 * @throws IllegalStateException
	 */
	public static void testValidSuccessorsAndPredecessors(List<BrilNode> blocks) {
		final Map<String, BrilNode> nameToBlock = getNameToBlock(blocks);

		if (getSuccessors(Utils.getLast(blocks)).size() > 0) {
			throw new IllegalStateException("expecting the last block to have no successors");
		}

		for (BrilNode block : blocks) {
			final String name = getName(block);
			for (String predecessor : getPredecessors(block)) {
				final BrilNode successorBlock = nameToBlock.get(predecessor);
				if (successorBlock == null) {
					throw new IllegalStateException("unknown predecessor " + predecessor);
				}
				final List<String> successors = getSuccessors(successorBlock);
				if (!successors.contains(name)) {
					throw new IllegalStateException("expecting " + predecessor + " to have successor " + name);
				}
			}

			for (String successor : getSuccessors(block)) {
				final BrilNode predecessorBlock = nameToBlock.get(successor);
				if (predecessorBlock == null) {
					throw new IllegalStateException("unknown successor " + successor);
				}
				final List<String> predecessors = getPredecessors(predecessorBlock);
				if (!predecessors.contains(name)) {
					throw new IllegalStateException("expecting " + successor + " to have predecessor " + name);
				}
			}
		}
	}

	// Utils ==================================================================

	private static Set<String> getUsedLabels(List<BrilNode> blocks, Map<String, BrilNode> nameToBlock) {
		final Set<String> usedLabels = new HashSet<>();

		final List<String> pending = new ArrayList<>();
		pending.add(getName(blocks.get(0)));
		while (!pending.isEmpty()) {
			final Iterator<String> it = pending.iterator();
			final String name = it.next();
			it.remove();

			if (!usedLabels.add(name)) {
				continue;
			}

			final BrilNode block = nameToBlock.get(name);
			final List<String> successors = getSuccessors(block);
			pending.addAll(successors);
		}
		return usedLabels;
	}

	private static void connectBlocksAndAppendJump(@NotNull BrilNode prevBlock, BrilNode nextBlock) {
		connectBlocks(prevBlock, nextBlock);
		new BrilInstructions(prevBlock.getOrCreateNodeList(KEY_INSTRUCTIONS))
				.jump(getName(nextBlock));
	}

	private static void connectBlocks(@NotNull BrilNode prevBlock, @NotNull BrilNode nextBlock) {
		prevBlock.getOrCreateStringList(KEY_SUCCESSORS).add(getName(nextBlock));
		nextBlock.getOrCreateStringList(KEY_PREDECESSORS).add(getName(prevBlock));
	}

	public static void foreachInstructionOverAllBlocks(List<BrilNode> blocks, Consumer<BrilNode> instructionConsumer) {
		for (BrilNode block : blocks) {
			final List<BrilNode> instructions = getInstructions(block);
			for (BrilNode instruction : instructions) {
				instructionConsumer.accept(instruction);
			}
		}
	}

	public static void replaceAllVars(BrilNode cfgFunction, Function<String, String> varReplace) {
		final List<BrilNode> arguments = BrilFactory.getArguments(cfgFunction);
		for (BrilNode argument : arguments) {
			final String argName = BrilFactory.getArgName(argument);
			final String newArgName = varReplace.apply(argName);
			BrilFactory.setArgName(newArgName, argument);
		}

		final List<BrilNode> blocks = getBlocks(cfgFunction);
		foreachInstructionOverAllBlocks(blocks,
		                                instruction -> BrilInstructions.replaceInOutVars(varReplace, instruction));
	}

	public static void debugPrint(List<BrilNode> blocks) {
		for (BrilNode block : blocks) {
			System.out.println(getName(block) + ":");

			for (BrilNode instruction : getInstructions(block)) {
				System.out.println("\t" + instruction);
			}
		}
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
