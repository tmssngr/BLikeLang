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

		final BrilNode firstBlock = blocks.get(0);
		final Set<String> pending = new LinkedHashSet<>();
		pending.add(BrilCfg.getName(firstBlock));

		while (!pending.isEmpty()) {
			final Iterator<String> iterator = pending.iterator();
			final String name = iterator.next();
			iterator.remove();

			final BrilNode block = nameToBlock.get(name);

			final Set<String> dominations = getDominations(block);

			boolean changed = false;

			final List<String> predecessors = BrilCfg.getPredecessors(block);
			for (String predecessor : predecessors) {
				final BrilNode prevBlock = nameToBlock.get(predecessor);
				final Set<String> prevDominations = getDominations(prevBlock);
				// skip unprocessed previous blocks
				if (prevDominations.isEmpty()) {
					continue;
				}

				prevDominations.add(name);
				if (dominations.isEmpty()) {
					changed |= dominations.addAll(prevDominations);
				}
				else {
					changed |= dominations.retainAll(prevDominations);
				}
			}

			changed |= dominations.add(name);
			setDominations(dominations, block);

			if (changed) {
				final List<String> successors = BrilCfg.getSuccessors(block);
				pending.addAll(successors);
			}
		}
	}

	// Utils ==================================================================

	private static void setDominations(Set<String> dominations, BrilNode block) {
		final List<String> values = new ArrayList<>(dominations);
		values.sort(Comparator.naturalOrder());
		block.set(KEY_DOMINATIONS, values);
	}
}
