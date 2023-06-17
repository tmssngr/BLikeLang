package de.regnis.bril;

import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Thomas Singer
 */
public final class BrilCfgDetectVarLiveness {

	// Constants ==============================================================

	private static final String KEY_LIVE_IN = "liveIn";
	private static final String KEY_LIVE_OUT = "liveOut";

	// Static =================================================================

	public static void detectLiveness(List<BrilNode> blocks) {
		final BrilCfgDetectVarLiveness dvu = new BrilCfgDetectVarLiveness(blocks);

		while (dvu.detect()) {
		}
	}

	@NotNull
	public static Set<String> getLiveIn(BrilNode block) {
		return new HashSet<>(block.getStringList(KEY_LIVE_IN));
	}

	@NotNull
	public static Set<String> getLiveOut(BrilNode block) {
		return new HashSet<>(block.getStringList(KEY_LIVE_OUT));
	}

	// Fields =================================================================

	private final Map<String, BrilNode> nameToBlock;
	private final List<BrilNode> blocks;

	// Setup ==================================================================

	private BrilCfgDetectVarLiveness(List<BrilNode> blocks) {
		nameToBlock = BrilCfg.getNameToBlock(blocks);
		this.blocks = blocks;
	}

	// Utils ==================================================================

	private boolean detect() {
		final List<String> pendingBlocks = new ArrayList<>();
		pendingBlocks.add(BrilCfg.getName(Utils.getLast(blocks)));

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

			pendingBlocks.addAll(BrilCfg.getPredecessors(block));
		}
		return changed;
	}

	private boolean process(BrilNode block) {
		final Set<String> live = getLiveInFromAllNext(block);

		final Set<String> liveOut = new HashSet<>(block.getOrCreateStringList(KEY_LIVE_OUT));
		boolean changed = liveOut.addAll(live);
		block.set(KEY_LIVE_OUT, new ArrayList<>(liveOut));

		final List<BrilNode> instructions = new ArrayList<>(BrilCfg.getInstructions(block));
		Collections.reverse(instructions);
		for (BrilNode instruction : instructions) {
			final String destVar = BrilInstructions.getDest(instruction);
			if (destVar != null) {
				live.remove(destVar);
			}

			final Set<String> requiredVars = BrilInstructions.getRequiredVars(instruction);
			live.addAll(requiredVars);
		}

		final Set<String> liveIn = new HashSet<>(block.getOrCreateStringList(KEY_LIVE_IN));
		if (liveIn.addAll(live)) {
			block.set(KEY_LIVE_IN, new ArrayList<>(liveIn));
			changed = true;
		}

		return changed;
	}

	private Set<String> getLiveInFromAllNext(BrilNode block) {
		final Set<String> liveIn = new HashSet<>();
		for (String next : BrilCfg.getSuccessors(block)) {
			final BrilNode nextBlock = nameToBlock.get(next);
			liveIn.addAll(nextBlock.getOrCreateStringList(KEY_LIVE_IN));
		}
		return liveIn;
	}
}
