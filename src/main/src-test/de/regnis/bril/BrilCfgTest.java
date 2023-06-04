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
				List.of(BrilInstructions.constant("v", 4),
				        BrilInstructions.jump(".somewhere")),
				List.of(BrilInstructions.constant("v", 2)),
				List.of(BrilInstructions.label(".somewhere"),
				        BrilInstructions.print("v"))
		), BrilCfg.splitIntoBlocks(List.of(BrilInstructions.constant("v", 4),
		                                   BrilInstructions.jump(".somewhere"),
		                                   BrilInstructions.constant("v", 2),
		                                   BrilInstructions.label(".somewhere"),
		                                   BrilInstructions.print("v"))));
	}

	@Test
	public void testBuildBlocks() throws Exception {
		final List<BrilNode> blocks = BrilCfg.buildBlocks(List.of(BrilInstructions.constant("v", 4),
		                                                          BrilInstructions.jump(".somewhere"),
		                                                          BrilInstructions.constant("v", 2),
		                                                          BrilInstructions.label(".somewhere"),
		                                                          BrilInstructions.print("v")));
		BrilCfgDetectVarUsages.detectVarUsages(blocks);

		Assert.assertEquals(4, blocks.size());
		assertEqualsCfg("block 0", List.of(BrilInstructions.constant("v", 4),
		                                   BrilInstructions.jump(".somewhere")),
		                List.of(),
		                List.of(".somewhere"),
		                Set.of(), Set.of("v"),
		                blocks.get(0));
		assertEqualsCfg("block 1", List.of(BrilInstructions.constant("v", 2)),
		                List.of(),
		                List.of(".somewhere"),
		                Set.of(), Set.of("v"),
		                blocks.get(1));
		assertEqualsCfg(".somewhere", List.of(BrilInstructions.print("v")),
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
		Assert.assertEquals(expectedName, BrilCfg.getName(blockNode));
		Assert.assertEquals(expectedInstructions, BrilCfg.getInstructions(blockNode));
		Assert.assertEquals(expectedPredecessors, BrilCfg.getPredecessors(blockNode));
		Assert.assertEquals(expectedSuccessors, BrilCfg.getSuccessors(blockNode));
		Assert.assertEquals(expectedLiveBefore, BrilCfgDetectVarUsages.getVarsBeforeBlock(blockNode));
		Assert.assertEquals(expectedLiveAfter, BrilCfgDetectVarUsages.getVarsAfterBlock(blockNode));
	}
}