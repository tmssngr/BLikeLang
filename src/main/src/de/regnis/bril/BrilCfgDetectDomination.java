package de.regnis.bril;

import java.util.*;

/**
 * @author Thomas Singer
 */
public final class BrilCfgDetectDomination {

	// Constants ==============================================================

	private static final String KEY_DOMINATIONS = "dominations";

	// Static =================================================================

	public static Set<String> getDominations(BrilNode block) {
		return new HashSet<>(block.getStringList(KEY_DOMINATIONS));
	}

	public static void detectDomination(List<BrilNode> blocks) {
		final Map<String, BrilNode> nameToBlock = BrilCfg.getNameToBlock(blocks);

		initialize(blocks);

		final Set<String> pending = new LinkedHashSet<>();
		blocks.forEach(block -> pending.add(BrilCfg.getName(block)));

		for (String name : pending) {
			final BrilNode block = nameToBlock.get(name);

			final Set<String> dominations = getDominations(block);

			boolean changed = false;

			final List<String> predecessors = BrilCfg.getPredecessors(block);
			for (String predecessor : predecessors) {
				final BrilNode prevBlock = nameToBlock.get(predecessor);
				final Set<String> prevDominations = getDominations(prevBlock);
				prevDominations.add(name);
				changed |= dominations.retainAll(prevDominations);
			}

			if (changed) {
				setDominations(dominations, block);
				pending.addAll(BrilCfg.getSuccessors(block));
			}
		}
	}

	// Utils ==================================================================

	private static void initialize(List<BrilNode> blocks) {
		final Set<String> dominations = new HashSet<>();
		for (BrilNode block : blocks) {
			dominations.add(BrilCfg.getName(block));
			setDominations(dominations, block);
		}
	}

	private static void setDominations(Set<String> dominations, BrilNode block) {
		final List<String> values = new ArrayList<>(dominations);
		values.sort(Comparator.naturalOrder());
		block.set(KEY_DOMINATIONS, values);
	}
}
