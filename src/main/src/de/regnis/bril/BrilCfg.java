package de.regnis.bril;

import de.regnis.utils.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Thomas Singer
 */
public class BrilCfg {

	// Constants ==============================================================

	private static final Set<String> TERMINATER_OPS = Set.of(BrilFactory.JMP);

	public static final String KEY_INSTRUCTIONS = "instructions";
	public static final String KEY_SUCCESSORS = "successors";
	public static final String KEY_PREDECESSORS = "predecessors";
	public static final String KEY_NAME = "name";

	// Static =================================================================

	public static List<List<BrilNode>> splitIntoBlocks(List<BrilNode> instructions) {
		final List<List<BrilNode>> blocks = new ArrayList<>();

		List<BrilNode> currentBlock = new ArrayList<>();

		for (BrilNode instruction : instructions) {
			final String op = BrilFactory.getOp(instruction);
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

	public static List<BrilNode> getNameToBlock(List<BrilNode> instructions) throws DuplicateLabelException, InvalidTargetLabelException {
		final Map<String, BrilNode> nameToBlock = new LinkedHashMap<>();

		final List<List<BrilNode>> splitIntoBlocks = splitIntoBlocks(instructions);
		for (int i = 0; i < splitIntoBlocks.size(); i++) {
			final List<BrilNode> blockInstructions = splitIntoBlocks.get(i);
			final BrilNode brilNode = blockInstructions.get(0);
			String name = BrilFactory.getLabel(brilNode);
			if (name != null) {
				blockInstructions.remove(0);
			}
			else {
				name = "block " + i;
			}

			final BrilNode blockNode = new BrilNode();
			blockNode.set(KEY_NAME, name);
			blockNode.getOrCreateNodeList(KEY_INSTRUCTIONS).addAll(blockInstructions);
			if (nameToBlock.put(name, blockNode) != null) {
				throw new DuplicateLabelException(name);
			}
		}

		final List<BrilNode> blocks = new ArrayList<>();

		@Nullable BrilNode fallThroughFromBlock = null;
		for (Map.Entry<String, BrilNode> entry : nameToBlock.entrySet()) {
			final String name = entry.getKey();
			final BrilNode blockNode = entry.getValue();

			if (fallThroughFromBlock != null) {
				fallThroughFromBlock.getOrCreateStringList(KEY_SUCCESSORS).add(name);
				blockNode.getOrCreateStringList(KEY_PREDECESSORS).add(fallThroughFromBlock.getString(KEY_NAME));
				fallThroughFromBlock = null;
			}

			blocks.add(blockNode);
			final List<BrilNode> blockInstructions = blockNode.getOrCreateNodeList(KEY_INSTRUCTIONS);

			final List<String> successors = blockNode.getOrCreateStringList(KEY_SUCCESSORS);

			final BrilNode lastInstruction = Utils.getLast(blockInstructions);
			final String op = BrilFactory.getOp(lastInstruction);
			if (BrilFactory.RET.equals(op)) {
				blockInstructions.remove(lastInstruction);
				continue;
			}

			final List<String> targets = BrilFactory.getJmpTargets(lastInstruction);
			if (targets.isEmpty()) {
				fallThroughFromBlock = blockNode;
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

		return blocks;
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
}
