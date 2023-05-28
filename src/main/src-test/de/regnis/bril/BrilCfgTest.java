package de.regnis.bril;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Singer
 */
public class BrilCfgTest {

	// Accessing ==============================================================

	@Test
	public void testFormBlocks() {
		testFormBlocks(List.of(
				List.of(BrilFactory.constant("v", 4),
				        BrilFactory.jump(".somewhere")),
				List.of(BrilFactory.constant("v", 2)),
				List.of(BrilFactory.label(".somewhere"),
				        BrilFactory.print("v"))
		), List.of(BrilFactory.constant("v", 4),
		           BrilFactory.jump(".somewhere"),
		           BrilFactory.constant("v", 2),
		           BrilFactory.label(".somewhere"),
		           BrilFactory.print("v")));
	}

	@Test
	public void testFormBlocks2() {
		final List<BrilCfg.Block> blocks = BrilCfg.formBlocks(List.of(BrilFactory.constant("v", 4),
		                                                              BrilFactory.jump(".somewhere"),
		                                                              BrilFactory.constant("v", 2),
		                                                              BrilFactory.label(".somewhere"),
		                                                              BrilFactory.print("v")));
		Assert.assertEquals(3, blocks.size());
		assertEquals("block 0", List.of(BrilFactory.constant("v", 4),
		                                BrilFactory.jump(".somewhere")),
		             List.of(),
		             List.of(blocks.get(2)), blocks.get(0));
		assertEquals("block 1", List.of(BrilFactory.constant("v", 2)),
		             List.of(),
		             List.of(blocks.get(2)), blocks.get(1));
		assertEquals(".somewhere", List.of(BrilFactory.print("v")),
		             List.of(blocks.get(0), blocks.get(1)),
		             List.of(), blocks.get(2));
	}

	// Utils ==================================================================

	private void assertEquals(String expectedName,
	                          List<BrilNode> expectedInstructions,
	                          List<BrilCfg.Block> expectedPredecessors,
	                          List<BrilCfg.Block> expectedSuccessors, BrilCfg.Block block) {
		Assert.assertEquals(expectedName, block.name);
		Assert.assertEquals(expectedInstructions, block.instructions);
		Assert.assertEquals(expectedPredecessors, block.getPredecessors());
		Assert.assertEquals(expectedSuccessors, block.getSuccessors());
	}

	private void testFormBlocks(List<List<BrilNode>> expected, List<BrilNode> input) {
		final List<List<BrilNode>> blocksWithInstructions = new ArrayList<>();
		BrilCfg.formBlocks(input,
		                   blockInstructions -> blocksWithInstructions.add(blockInstructions));
		Assert.assertEquals(expected, blocksWithInstructions);
	}
}
