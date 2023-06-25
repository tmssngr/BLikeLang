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

	public static void detectLiveness(List<BrilNode> blocks, boolean setLiveOutToInstructions) {
		final BrilCfgDetectVarLiveness dvu = new BrilCfgDetectVarLiveness(blocks, setLiveOutToInstructions);

		while (dvu.detect()) {
		}
	}

	@NotNull
	public static Set<String> getLiveIn(BrilNode blockOrInstruction) {
		return new HashSet<>(blockOrInstruction.getStringList(KEY_LIVE_IN));
	}

	@NotNull
	public static Set<String> getLiveOut(BrilNode blockOrInstruction) {
		return new HashSet<>(blockOrInstruction.getStringList(KEY_LIVE_OUT));
	}

	// Fields =================================================================

	private final List<BrilNode> blocks;
	private final boolean setLiveOutToInstructions;
	private final Map<String, BrilNode> nameToBlock;

	// Setup ==================================================================

	private BrilCfgDetectVarLiveness(List<BrilNode> blocks, boolean setLiveOutToInstructions) {
		this.blocks                   = blocks;
		this.setLiveOutToInstructions = setLiveOutToInstructions;
		nameToBlock                   = BrilCfg.getNameToBlock(blocks);
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

		boolean changed = addAll(KEY_LIVE_OUT, live, block);

		final List<BrilNode> instructions = new ArrayList<>(BrilCfg.getInstructions(block));
		Collections.reverse(instructions);
		for (BrilNode instruction : instructions) {
			if (setLiveOutToInstructions && addAll(KEY_LIVE_OUT, live, instruction)) {
				changed = true;
			}
			final String destVar = BrilInstructions.getDest(instruction);
			if (destVar != null) {
				live.remove(destVar);
			}

			final Set<String> requiredVars = BrilInstructions.getRequiredVars(instruction);
			live.addAll(requiredVars);
		}

		if (addAll(KEY_LIVE_IN, live, block)) {
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

	private static boolean addAll(String key, Set<String> newVars, BrilNode node) {
		final Set<String> existingVars = new HashSet<>(node.getOrCreateStringList(key));
		if (existingVars.addAll(newVars)) {
			node.set(key, new ArrayList<>(existingVars));
			return true;
		}
		return false;
	}
}
