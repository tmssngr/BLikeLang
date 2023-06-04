package de.regnis.bril;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Thomas Singer
 */
public class BrilCfgSsaTest {

	// Accessing ==============================================================

	@Test
	public void testSimple() {
		assertSsa(List.of(
				          BrilCfg.createBlock("start",
				                              List.of(
						                              BrilInstructions.constant("a_1", 1),
						                              BrilInstructions.constant("b_1", 2),
						                              BrilInstructions.add("c_1", "a_1", "b_1"),
						                              BrilInstructions.constant("a_2", 3),
						                              BrilInstructions.add("c_2", "a_2", "b_1"),
						                              BrilInstructions.print("c_2")
				                              ))
		          ),
		          List.of(BrilCfg.createBlock("start",
		                                      List.of(
				                                      BrilInstructions.constant("a", 1),
				                                      BrilInstructions.constant("b", 2),
				                                      BrilInstructions.add("c", "a", "b"),
				                                      BrilInstructions.constant("a", 3),
				                                      BrilInstructions.add("c", "a", "b"),
				                                      BrilInstructions.print("c")
		                                      ))));
	}

	// Utils ==================================================================

	private void assertSsa(List<BrilNode> expectedBlocks, List<BrilNode> cfgInput) {
		final List<BrilNode> cfgSsa = new ArrayList<>(cfgInput);
		BrilCfgSsa.transform(cfgSsa);
		final Iterator<BrilNode> expectedIt = expectedBlocks.iterator();
		final Iterator<BrilNode> currentIt = cfgSsa.iterator();
		while (true) {
			final boolean expectedHasNext = expectedIt.hasNext();
			final boolean currentHasNext = currentIt.hasNext();
			Assert.assertEquals(expectedHasNext, currentHasNext);
			if (!expectedHasNext) {
				break;
			}

			final BrilNode expectedBlock = expectedIt.next();
			final BrilNode currentBlock = currentIt.next();
			assertBlock(expectedBlock, currentBlock);
		}
	}

	private void assertBlock(BrilNode expectedBlock, BrilNode block) {
		Assert.assertEquals(BrilCfg.getName(expectedBlock), BrilCfg.getName(block));
		Assert.assertEquals(BrilCfg.getInstructions(expectedBlock), BrilCfg.getInstructions(block));
	}
}
