package de.regnis.bril;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

/**
 * @author Thomas Singer
 */
public class BrilCfgSsaTest {

	// Static =================================================================

	public static void assertEqualsFunctionBlocks(List<BrilNode> expectedBlocks, BrilNode function) {
		final Iterator<BrilNode> expectedIt = expectedBlocks.iterator();
		final Iterator<BrilNode> currentIt = BrilCfg.getBlocks(function).iterator();
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

	// Accessing ==============================================================

	@Test
	public void testSimple() {
		assertSsa(List.of(
				          BrilCfg.createBlock("start",
				                              List.of(
						                              BrilInstructions.constant("a", 1),
						                              BrilInstructions.constant("b", 2),
						                              BrilInstructions.add("c", "a", "b"),
						                              BrilInstructions.constant("a.1", 3),
						                              BrilInstructions.add("c.1", "a.1", "b"),
						                              BrilInstructions.print("c.1")
				                              ))
		          ),
		          BrilCfg.createFunction("main", "void", List.of(),
		                                 List.of(BrilCfg.createBlock("start",
		                                                             List.of(
				                                                             BrilInstructions.constant("a", 1),
				                                                             BrilInstructions.constant("b", 2),
				                                                             BrilInstructions.add("c", "a", "b"),
				                                                             BrilInstructions.constant("a", 3),
				                                                             BrilInstructions.add("c", "a", "b"),
				                                                             BrilInstructions.print("c")
		                                                             )
		                                         )
		                                 )
		          )
		);
	}

	@Test
	public void testReassignedVar() {
		assertSsa(List.of(
				          BrilCfg.createBlock("start",
				                              List.of(
						                              BrilInstructions.constant("a", 1),
						                              BrilInstructions.constant("a.1", 2),
						                              BrilInstructions.print("a.1")
				                              ))
		          ),
		          BrilCfg.createFunction("main", "void", List.of(),
		                                 List.of(BrilCfg.createBlock("start",
		                                                             List.of(
				                                                             BrilInstructions.constant("a", 1),
				                                                             BrilInstructions.constant("a", 2),
				                                                             BrilInstructions.print("a")
		                                                             )
		                                         )
		                                 )
		          )
		);
	}

	@Test
	public void testParameters() {
		assertSsa(List.of(BrilCfg.createBlock("start",
		                                      List.of(
				                                      BrilInstructions.id("result", "a"),
				                                      BrilInstructions.lessThan("lt", "a", "b"),
				                                      BrilInstructions.branch("lt", "b>a", "exit")
		                                      )
		                  ),
		                  BrilCfg.createBlock("b>a",
		                                      List.of(
				                                      BrilInstructions.id("result.1", "b")
		                                      )
		                  ),
		                  BrilCfg.createBlock("exit",
		                                      List.of(
				                                      BrilCfgSsa.phiFunction("result.2", List.of("result", "result.1")),
				                                      BrilInstructions.print("result.2")
		                                      )
		                  )
		          ),
		          BrilCfgDetectVarUsagesTest.createPrintMaxCfg()
		);
	}

	// Utils ==================================================================

	private static void assertSsa(List<BrilNode> expectedBlocks, BrilNode function) {
		BrilCfgSsa.transform(function);
		assertEqualsFunctionBlocks(expectedBlocks, function);
	}

	private static void assertBlock(BrilNode expectedBlock, BrilNode block) {
		Assert.assertEquals(BrilCfg.getName(expectedBlock), BrilCfg.getName(block));
		Assert.assertEquals(BrilCfg.getInstructions(expectedBlock), BrilCfg.getInstructions(block));
	}
}
