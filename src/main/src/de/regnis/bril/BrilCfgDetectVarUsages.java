package de.regnis.bril;

import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Thomas Singer
 */
public final class BrilCfgDetectVarUsages {

	// Constants ==============================================================

	public static final String KEY_VARS_LIFE_BEFORE = "varsLifeBefore";
	public static final String KEY_VARS_LIFE_AFTER = "varsLifeAfter";

	// Static =================================================================

	public static void detectVarUsages(List<BrilNode> blocks) {
		final BrilCfgDetectVarUsages dvu = new BrilCfgDetectVarUsages(blocks);

		while (dvu.detect()) {
		}
	}

	// Fields =================================================================

	private final Map<String, BrilNode> nameToBlock;
	private final List<BrilNode> blocks;

	// Setup ==================================================================

	private BrilCfgDetectVarUsages(List<BrilNode> blocks) {
		nameToBlock = getNameToBlock(blocks);
		this.blocks = blocks;
	}

	// Utils ==================================================================

	private boolean detect() {
		final List<String> pendingBlocks = new ArrayList<>();
		pendingBlocks.add(Utils.getLast(blocks).getString(BrilCfg.KEY_NAME));

		boolean changed = false;

		final Set<String> processedBlocks = new HashSet<>();

		while (pendingBlocks.size() > 0) {
			final String name = pendingBlocks.remove(0);

			if (!processedBlocks.add(name)) {
				continue;
			}

			final BrilNode block = nameToBlock.get(name);
			if (process(block)) {
				changed = true;
			}

			pendingBlocks.addAll(block.getOrCreateStringList(BrilCfg.KEY_PREDECESSORS));
		}
		return changed;
	}

	private boolean process(BrilNode block) {
		final Set<String> live = getLifeFromAllNext(block);

		final Set<String> lifeAfter = new HashSet<>(block.getOrCreateStringList(KEY_VARS_LIFE_AFTER));
		boolean changed = lifeAfter.addAll(live);
		block.set(KEY_VARS_LIFE_AFTER, new ArrayList<>(lifeAfter));

		final List<BrilNode> instructions = new ArrayList<>(block.getOrCreateNodeList(BrilCfg.KEY_INSTRUCTIONS));
		Collections.reverse(instructions);
		for (BrilNode instruction : instructions) {
			final String destVar = BrilInstructions.getDest(instruction);
			if (destVar != null) {
				live.remove(destVar);
			}

			final Set<String> requiredVars = BrilInstructions.getRequiredVars(instruction);
			live.addAll(requiredVars);
		}

		final Set<String> lifeBefore = new HashSet<>(block.getOrCreateStringList(KEY_VARS_LIFE_BEFORE));
		if (lifeBefore.addAll(live)) {
			block.set(KEY_VARS_LIFE_BEFORE, new ArrayList<>(lifeBefore));
			changed = true;
		}

		return changed;
	}

	private Set<String> getLifeFromAllNext(BrilNode block) {
		final Set<String> lifeFromNext = new HashSet<>();
		for (String next : block.getOrCreateStringList(BrilCfg.KEY_SUCCESSORS)) {
			final BrilNode nextBlock = nameToBlock.get(next);
			lifeFromNext.addAll(nextBlock.getOrCreateStringList(KEY_VARS_LIFE_BEFORE));
		}
		return lifeFromNext;
	}

	@NotNull
	private static Map<String, BrilNode> getNameToBlock(List<BrilNode> blocks) {
		final Map<String, BrilNode> nameToBlock = new HashMap<>();
		for (BrilNode block : blocks) {
			nameToBlock.put(block.getString(BrilCfg.KEY_NAME), block);
		}
		return nameToBlock;
	}
}
