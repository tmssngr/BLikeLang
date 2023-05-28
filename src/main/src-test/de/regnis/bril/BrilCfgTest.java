package de.regnis.bril;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

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

	// Utils ==================================================================

	private void testFormBlocks(List<List<BrilNode>> expected, List<BrilNode> input) {
		final List<List<BrilNode>> blocksWithInstructions = new ArrayList<>();
		BrilCfg.formBlocks(input,
		                   blockInstructions -> blocksWithInstructions.add(blockInstructions));
		assertEquals(expected, blocksWithInstructions);
	}
}
