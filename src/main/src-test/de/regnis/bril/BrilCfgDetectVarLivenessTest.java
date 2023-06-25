package de.regnis.bril;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Thomas Singer
 */
public class BrilCfgDetectVarLivenessTest {

	// Static =================================================================

	@NotNull
	public static BrilNode createPrintMaxCfg() {
		return BrilCfg.createFunction("printMax", "int", List.of(BrilFactory.argument("a", "int"), BrilFactory.argument("b", "int")),
		                              List.of(BrilCfg.createBlock("start",
		                                                          new BrilInstructions()
				                                                          .id("result", "a")
				                                                          .lessThan("lt", "a", "b")
				                                                          .branch("lt", "b>a", "exit")
				                                                          .get(),
		                                                          List.of(), List.of("b>a", "exit")
		                                      ),
		                                      BrilCfg.createBlock("b>a",
		                                                          new BrilInstructions()
				                                                          .id("result", "b")
				                                                          .get(),
		                                                          List.of("start"), List.of("exit")
		                                      ),
		                                      BrilCfg.createBlock("exit",
		                                                          new BrilInstructions()
				                                                          .print("result")
				                                                          .get(),
		                                                          List.of("start", "b>a"), List.of()
		                                      )
		                              )
		);
	}

	// Accessing ==============================================================

	@Test
	public void testSimple() {
		final List<BrilNode> blocks = List.of(
				BrilCfg.createBlock("start", new BrilInstructions()
						.constant("v", 4)
						.jump("print")
						.get(), List.of(), List.of("print")),

				BrilCfg.createBlock("unused", new BrilInstructions()
						.constant("v", 2)
						.get(), List.of(), List.of("print")),

				BrilCfg.createBlock("print", new BrilInstructions()
						.print("v")
						.get(), List.of("start", "unused"), List.of("exit")),

				BrilCfg.createBlock("exit", List.of(),
				                    List.of("print"), List.of())
		);

		BrilCfgDetectVarLiveness.detectLiveness(blocks, true);

		Assert.assertEquals(4, blocks.size());
		assertEqualsCfg(Set.of(),
		                List.of(
				                Set.of("v"),
				                Set.of("v")
		                ),
		                Set.of("v"),
		                blocks.get(0));
		assertEqualsCfg(Set.of(),
		                List.of(
				                Set.of("v")
		                ),
		                Set.of("v"),
		                blocks.get(1));
		assertEqualsCfg(Set.of("v"),
		                List.of(
				                Set.of()
		                ),
		                Set.of(),
		                blocks.get(2));
		assertEqualsCfg(Set.of(),
		                List.of(),
		                Set.of(),
		                blocks.get(3));
	}

	@Test
	public void testPrintMax() {
		final List<BrilNode> blocks = BrilCfg.getBlocks(createPrintMaxCfg());

		BrilCfgDetectVarLiveness.detectLiveness(blocks, true);

		Assert.assertEquals(3, blocks.size());
		assertEqualsCfg(Set.of("a", "b"),
		                List.of(
				                Set.of("result", "a", "b"),
				                Set.of("result", "b", "lt"),
				                Set.of("result", "b")
		                ),
		                Set.of("result", "b"),
		                blocks.get(0));
		assertEqualsCfg(Set.of("b"),
		                List.of(
				                Set.of("result")
		                ),
		                Set.of("result"),
		                blocks.get(1));
		assertEqualsCfg(Set.of("result"),
		                List.of(
				                Set.of()
		                ),
		                Set.of(),
		                blocks.get(2));
	}

	// Utils ==================================================================

	private void assertEqualsCfg(Set<String> expectedLiveBefore,
	                             List<Set<String>> expectedInstructionsLiveOut,
	                             Set<String> expectedLiveAfter, BrilNode blockNode) {
		final Iterator<Set<String>> expectedIt = expectedInstructionsLiveOut.iterator();
		final Iterator<BrilNode> instructionsIt = BrilCfg.getInstructions(blockNode).iterator();
		while (true) {
			final boolean hasNext = expectedIt.hasNext();
			Assert.assertEquals(hasNext, instructionsIt.hasNext());
			if (!hasNext) {
				break;
			}

			final Set<String> expectedOut = expectedIt.next();
			final BrilNode instruction = instructionsIt.next();
			final Set<String> liveOut = BrilCfgDetectVarLiveness.getLiveOut(instruction);
			Assert.assertEquals(expectedOut, liveOut);
		}
		Assert.assertEquals(expectedLiveBefore, BrilCfgDetectVarLiveness.getLiveIn(blockNode));
		Assert.assertEquals(expectedLiveAfter, BrilCfgDetectVarLiveness.getLiveOut(blockNode));
	}
}
