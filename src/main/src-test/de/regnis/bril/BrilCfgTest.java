package de.regnis.bril;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

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
		Assert.assertEquals(4, blocks.size());
		assertEquals("block 0", List.of(BrilFactory.constant("v", 4),
		                                BrilFactory.jump(".somewhere")),
		             List.of(),
		             List.of(".somewhere"), blocks.get(0));
		assertEquals("block 1", List.of(BrilFactory.constant("v", 2)),
		             List.of(),
		             List.of(".somewhere"), blocks.get(1));
		assertEquals(".somewhere", List.of(BrilFactory.print("v")),
		             List.of("block 0", "block 1"),
		             List.of("exit 3"), blocks.get(2));
		assertEquals("exit 3", List.of(),
		             List.of(".somewhere"),
		             List.of(), blocks.get(3));
	}

	// Utils ==================================================================

	private void assertEquals(String expectedName,
	                          List<BrilNode> expectedInstructions,
	                          List<String> expectedPredecessors,
	                          List<String> expectedSuccessors, BrilNode blockNode) {
		Assert.assertEquals(expectedName, blockNode.getString(BrilCfg.KEY_NAME));
		Assert.assertEquals(expectedInstructions, blockNode.getOrCreateNodeList(BrilCfg.KEY_INSTRUCTIONS));
		Assert.assertEquals(expectedPredecessors, blockNode.getOrCreateStringList(BrilCfg.KEY_PREDECESSORS));
		Assert.assertEquals(expectedSuccessors, blockNode.getOrCreateStringList(BrilCfg.KEY_SUCCESSORS));
	}
}
