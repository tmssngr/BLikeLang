package de.regnis.bril;

import java.util.*;

/**
 * @author Thomas Singer
 */
final class BrilLoopDetection {

	// Static =================================================================

	public static void detectLoopLevels(BrilNode cfgFunction) {
		final List<BrilNode> blocks = BrilCfg.getBlocks(cfgFunction);
		detectLoopLevels(blocks);
	}

	public static Map<String, Set<String>> detectLoopLevels(List<BrilNode> blocks) {
		BrilCfg.testValidSuccessorsAndPredecessors(blocks);
		final BrilLoopDetection loopDetection = new BrilLoopDetection(blocks);
		return loopDetection.detect();
	}

	// Fields =================================================================

	private final List<BrilNode> blocks;
	private final Map<String, BrilNode> nameToBlock;

	// Setup ==================================================================

	private BrilLoopDetection(List<BrilNode> blocks) {
		this.blocks = blocks;
		nameToBlock = BrilCfg.getNameToBlock(blocks);
	}

	// Utils ==================================================================

	private Map<String, Set<String>> detect() {
		BrilCfgDetectDomination.detectDomination(blocks);

		final Map<String, Set<String>> loops = new HashMap<>();

		final List<String> pending = new ArrayList<>();
		pending.add(BrilCfg.getName(blocks.get(0)));

		while (!pending.isEmpty()) {
			final String name = pending.remove(0);
			final BrilNode block = nameToBlock.get(name);
			final Set<String> dominations = BrilCfgDetectDomination.getDominations(block);
			final List<String> successors = BrilCfg.getSuccessors(block);
			for (String successor : successors) {
				if (dominations.contains(successor)) {
					final Set<String> loopNodes = getAllPredecessorsUpTo(name, successor);
					loops.computeIfAbsent(successor, key -> new HashSet<>())
							.addAll(loopNodes);
				}
				else {
					pending.add(successor);
				}
			}
		}

		return loops;
	}

	private Set<String> getAllPredecessorsUpTo(String start, String loopHeader) {
		final Set<String> loopNodes = new HashSet<>();
		final List<String> pending = new ArrayList<>();
		pending.add(start);
		while (!pending.isEmpty()) {
			final String name = pending.remove(0);
			final boolean added = loopNodes.add(name);
			if (loopHeader.equals(name)) {
				continue;
			}

			if (!added) {
				continue;
			}

			final BrilNode block = nameToBlock.get(name);
			final List<String> predecessors = BrilCfg.getPredecessors(block);
			pending.addAll(predecessors);
		}
		return loopNodes;
	}
}
