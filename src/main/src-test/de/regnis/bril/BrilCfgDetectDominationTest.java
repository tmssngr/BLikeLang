package de.regnis.bril;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Thomas Singer
 */
public class BrilCfgDetectDominationTest {

	// Constants ==============================================================

	private static final List<BrilNode> NOT_NEEDED_INSTRUCTIONS = List.of();

	// Accessing ==============================================================

	@Test
	public void testDomination() {
		final List<BrilNode> blocks = List.of(
				BrilCfg.createBlock("entry", NOT_NEEDED_INSTRUCTIONS, List.of(), List.of("loop")),

				BrilCfg.createBlock("loop", NOT_NEEDED_INSTRUCTIONS, List.of("entry", "endif"), List.of("body", "exit")),
				BrilCfg.createBlock("body", NOT_NEEDED_INSTRUCTIONS, List.of("loop"), List.of("then", "endif")),
				BrilCfg.createBlock("then", NOT_NEEDED_INSTRUCTIONS, List.of("body"), List.of("endif")),
				BrilCfg.createBlock("endif", NOT_NEEDED_INSTRUCTIONS, List.of("then", "body"), List.of("loop")),

				BrilCfg.createBlock("exit", NOT_NEEDED_INSTRUCTIONS, List.of("loop"), List.of())
		);

		BrilCfgDetectDomination.detectDomination(blocks);

		final Iterator<BrilNode> iterator = blocks.iterator();
		assertDomination("entry", Set.of("entry"), iterator.next());
		assertDomination("loop", Set.of("entry", "loop"), iterator.next());
		assertDomination("body", Set.of("entry", "loop", "body"), iterator.next());
		assertDomination("then", Set.of("entry", "loop", "body", "then"), iterator.next());
		assertDomination("endif", Set.of("entry", "loop", "body", "endif"), iterator.next());
		assertDomination("exit", Set.of("entry", "loop", "exit"), iterator.next());
		Assert.assertFalse(iterator.hasNext());
	}

	// Utils ==================================================================

	private void assertDomination(String expectedBlockName, Set<String> expectedDominations, BrilNode blockNode) {
		Assert.assertEquals(expectedBlockName, BrilCfg.getName(blockNode));
		Assert.assertEquals(expectedDominations, BrilCfgDetectDomination.getDominations(blockNode));
	}
}
