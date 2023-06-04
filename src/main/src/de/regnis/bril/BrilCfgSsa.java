package de.regnis.bril;

import org.jetbrains.annotations.NotNull;

import java.util.*;

import static de.regnis.utils.Utils.assertTrue;
import static de.regnis.utils.Utils.notNull;

/**
 * @author Thomas Singer
 */
public final class BrilCfgSsa {

	// Static =================================================================

	public static void transform(BrilNode cfgFunction) {
		final BrilCfgSsa ssa = new BrilCfgSsa(cfgFunction);
		ssa.transform();
	}

	@NotNull
	static BrilNode phiFunction(String dest, List<String> sources) {
		return new BrilNode()
				.set("op", "phi")
				.set("dest", dest)
				.set("parameters", sources);
	}

	// Fields =================================================================

	private final Map<String, BlockInfo> blockToInfo = new HashMap<>();
	private final BrilNode cfgFunction;
	private final List<BrilNode> blocks;

	// Setup ==================================================================

	private BrilCfgSsa(BrilNode cfgFunction) {
		this.cfgFunction = cfgFunction;

		blocks = BrilCfg.getBlocks(cfgFunction);

		BrilCfgDetectVarUsages.detectVarUsages(blocks);
	}

	// Utils ==================================================================

	private void transform() {
		final Map<String, Integer> varToVariant = new HashMap<>();

		for (BrilNode block : blocks) {
			final BlockInfo info = new BlockInfo(varToVariant);
			blockToInfo.put(BrilCfg.getName(block), info);

			final List<String> predecessors = BrilCfg.getPredecessors(block);
			if (predecessors.isEmpty()) {
				assertTrue(block == blocks.get(0));

				final List<BrilNode> arguments = BrilFactory.getArguments(cfgFunction);
				info.initializeFromArgumentsUpdateGraph(arguments);
			}
			else {
				final Set<String> incomingVars = BrilCfgDetectVarUsages.getVarsBeforeBlock(block);
				if (predecessors.size() > 1) {
					info.initializePhiDeclarations(incomingVars);
				}
				else {
					final BlockInfo prevInfo = blockToInfo.get(predecessors.get(0));
					info.initializeFrom(prevInfo);
				}
			}

			transform(block, info);
		}

		for (BrilNode block : blocks) {
			final String name = BrilCfg.getName(block);
			final BlockInfo info = blockToInfo.get(name);

			final List<String> predecessors = BrilCfg.getPredecessors(block);
			if (predecessors.size() < 2 || info.phiFunctions.isEmpty()) {
				continue;
			}

			final List<BrilNode> instructions = new ArrayList<>();

			for (PhiFunction phiFunction : info.phiFunctions) {
				final List<String> phiParameters = new ArrayList<>();
				final Set<String> ssaNames = new HashSet<>();
				for (String predecessor : predecessors) {
					final BlockInfo prevInfo = blockToInfo.get(predecessor);
					final String ssaName = prevInfo.getUsageName(phiFunction.originalName);
					ssaNames.add(ssaName);
					phiParameters.add(ssaName);
				}

				if (ssaNames.size() > 1) {
					instructions.add(phiFunction(phiFunction.ssaName, phiParameters));
				}
				else {
					instructions.add(BrilInstructions.id(phiFunction.ssaName, phiParameters.get(0)));
				}
			}

			instructions.addAll(BrilCfg.getInstructions(block));
			BrilCfg.setInstructions(instructions, block);
		}
	}

	private void transform(BrilNode block, BlockInfo varToVariant) {
		for (BrilNode instruction : BrilCfg.getInstructions(block)) {
			final String dest = BrilInstructions.getDest(instruction);
			BrilInstructions.replaceVars(var -> varToVariant.getUsageName(var), instruction);
			if (dest != null) {
				BrilInstructions.setDest(varToVariant.getAssignmentName(dest), instruction);
			}
		}
	}

	// Inner Classes ==========================================================

	private static final class BlockInfo {
		private final List<PhiFunction> phiFunctions = new ArrayList<>();
		private final Map<String, Integer> varToCurrent = new HashMap<>();
		private final Map<String, Integer> varToHighest;

		private BlockInfo(@NotNull Map<String, Integer> varToHighest) {
			// shared!
			this.varToHighest = varToHighest;
		}

		public void initializeFromArgumentsUpdateGraph(List<BrilNode> arguments) {
			for (BrilNode argument : arguments) {
				getAssignmentName(BrilFactory.getArgName(argument));
			}
		}

		public void initializeFrom(@NotNull BlockInfo prevInfo) {
			varToCurrent.putAll(prevInfo.varToCurrent);
		}

		private void initializePhiDeclarations(@NotNull Set<String> incomingVars) {
			for (String incomingVar : incomingVars) {
				final String ssaName = getAssignmentName(incomingVar);
				phiFunctions.add(new PhiFunction(incomingVar, ssaName));
			}
		}

		@NotNull
		public String getAssignmentName(@NotNull String originalName) {
/*
			if (originalName.equals(ControlFlowGraph.RESULT)) {
				return originalName;
			}
*/

			final Integer currentVariant = varToHighest.get(originalName);
			final int variant;
			if (currentVariant != null) {
				variant = currentVariant + 1;
			}
			else {
				variant = 0;
			}
			varToHighest.put(originalName, variant);
			varToCurrent.put(originalName, variant);
			return getVariableName(originalName, variant);
		}

		@NotNull
		public String getUsageName(@NotNull String originalName) {
			final Integer variant = notNull(varToCurrent.get(originalName));
			return getVariableName(originalName, variant);
		}

		@NotNull
		private String getVariableName(@NotNull String originalName, int variant) {
			if (variant == 0) {
				return originalName;
			}
			return originalName + '.' + variant;
		}
	}

	private static final class PhiFunction {
		public final String originalName;
		public final String ssaName;

		private PhiFunction(String originalName, String ssaName) {
			this.originalName = originalName;
			this.ssaName      = ssaName;
		}

		@Override
		public String toString() {
			return ssaName + " = phi()";
		}
	}
}
