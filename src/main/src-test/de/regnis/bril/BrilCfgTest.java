package de.regnis.bril;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Thomas Singer
 */
public class BrilCfgTest {

	// Accessing ==============================================================

	@Test
	public void testFormBlocks() {
		Assert.assertEquals(List.of(
				List.of(BrilFactory.constant("v", 4),
				        BrilFactory.jump(".somewhere")),
				List.of(BrilFactory.constant("v", 2)),
				List.of(BrilFactory.label(".somewhere"),
				        BrilFactory.print("v"))
		), BrilCfg.splitIntoBlocks(List.of(BrilFactory.constant("v", 4),
		                                   BrilFactory.jump(".somewhere"),
		                                   BrilFactory.constant("v", 2),
		                                   BrilFactory.label(".somewhere"),
		                                   BrilFactory.print("v"))));
	}

	@Test
	public void testBuildBlocks() throws Exception {
		final List<BrilNode> blocks = BrilCfg.buildBlocks(List.of(BrilFactory.constant("v", 4),
		                                                          BrilFactory.jump(".somewhere"),
		                                                          BrilFactory.constant("v", 2),
		                                                          BrilFactory.label(".somewhere"),
		                                                          BrilFactory.print("v")));
		BrilCfgDetectVarUsages.detectVarUsages(blocks);

		Assert.assertEquals(4, blocks.size());
		assertEqualsCfg("block 0", List.of(BrilFactory.constant("v", 4),
		                                   BrilFactory.jump(".somewhere")),
		                List.of(),
		                List.of(".somewhere"),
		                Set.of(), Set.of("v"),
		                blocks.get(0));
		assertEqualsCfg("block 1", List.of(BrilFactory.constant("v", 2)),
		                List.of(),
		                List.of(".somewhere"),
		                Set.of(), Set.of("v"),
		                blocks.get(1));
		assertEqualsCfg(".somewhere", List.of(BrilFactory.print("v")),
		                List.of("block 0", "block 1"),
		                List.of("exit 3"),
		                Set.of("v"), Set.of(),
		                blocks.get(2));
		assertEqualsCfg("exit 3", List.of(),
		                List.of(".somewhere"),
		                List.of(),
		                Set.of(), Set.of(),
		                blocks.get(3));
	}

	// Utils ==================================================================

	private void assertEqualsCfg(String expectedName,
	                             List<BrilNode> expectedInstructions,
	                             List<String> expectedPredecessors,
	                             List<String> expectedSuccessors,
	                             Set<String> expectedLiveBefore,
	                             Set<String> expectedLiveAfter, BrilNode blockNode) {
		Assert.assertEquals(expectedName, blockNode.getString(BrilCfg.KEY_NAME));
		Assert.assertEquals(expectedInstructions, blockNode.getOrCreateNodeList(BrilCfg.KEY_INSTRUCTIONS));
		Assert.assertEquals(expectedPredecessors, blockNode.getOrCreateStringList(BrilCfg.KEY_PREDECESSORS));
		Assert.assertEquals(expectedSuccessors, blockNode.getOrCreateStringList(BrilCfg.KEY_SUCCESSORS));
		Assert.assertEquals(expectedLiveBefore, new HashSet<>(blockNode.getOrCreateStringList(BrilCfgDetectVarUsages.KEY_VARS_LIFE_BEFORE)));
		Assert.assertEquals(expectedLiveAfter, new HashSet<>(blockNode.getOrCreateStringList(BrilCfgDetectVarUsages.KEY_VARS_LIFE_AFTER)));
	}
}
