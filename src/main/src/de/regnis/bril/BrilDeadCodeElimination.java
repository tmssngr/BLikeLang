package de.regnis.bril;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Thomas Singer
 */
public final class BrilDeadCodeElimination {

	// Static =================================================================

	public static void simpleDce(BrilNode cfg) {

	}

	public static List<BrilNode> simpleDce(List<BrilNode> blockInstructions) {
		while (true) {
			final Set<String> providedVars = new HashSet<>();
			final Set<String> requiredVars = new HashSet<>();
			for (BrilNode instruction : blockInstructions) {
				final String dest = BrilInstructions.getDest(instruction);
				if (dest != null) {
					providedVars.add(dest);
				}

				final Set<String> instructionRequiresVars = BrilInstructions.getRequiredVars(instruction);
				requiredVars.addAll(instructionRequiresVars);
			}

			final Set<String> unusedVars = new HashSet<>(providedVars);
			unusedVars.removeAll(requiredVars);
			if (unusedVars.isEmpty()) {
				return blockInstructions;
			}

			final List<BrilNode> keptInstructions = new ArrayList<>();
			for (BrilNode instruction : blockInstructions) {
				final String dest = BrilInstructions.getDest(instruction);
				if (dest == null || !unusedVars.contains(dest)) {
					keptInstructions.add(instruction);
				}
			}
			blockInstructions = keptInstructions;
		}
	}
}
