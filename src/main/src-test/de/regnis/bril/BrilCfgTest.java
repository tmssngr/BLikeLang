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
		Assert.assertEquals(4, blocks.size());
		assertEqualsCfg("block 0", List.of(BrilInstructions.constant("v", 4),
		                                   BrilInstructions.jump(".somewhere")),
		                List.of(),
		                List.of(".somewhere"),
		                blocks.get(0));
		assertEqualsCfg("block 1", List.of(BrilInstructions.constant("v", 2)),
		                List.of(),
		                List.of(".somewhere"),
		                blocks.get(1));
		assertEqualsCfg(".somewhere", List.of(BrilInstructions.print("v")),
		                List.of("block 0", "block 1"),
		                List.of("exit 3"),
		                blocks.get(2));
		assertEqualsCfg("exit 3", List.of(),
		                List.of(".somewhere"),
		                List.of(),
		                blocks.get(3));
	}

	@Test
	public void testValidSuccessorsAndPredecessors() {
		BrilCfg.testValidSuccessorsAndPredecessors(
				List.of(BrilCfg.createBlock("entry", List.of(),
				                            List.of(), List.of("loop")
				        ),
				        BrilCfg.createBlock("loop", List.of(),
				                            List.of("entry", "body"), List.of("body", "exit")
				        ),
				        BrilCfg.createBlock("body", List.of(),
				                            List.of("loop"), List.of("loop")
				        ),
				        BrilCfg.createBlock("exit", List.of(),
				                            List.of("loop"), List.of()
				        )
				)
		);

		try {
			BrilCfg.testValidSuccessorsAndPredecessors(
					List.of(BrilCfg.createBlock("entry", List.of(),
					                            List.of("prohibited"), List.of("loop")
					        ),
					        BrilCfg.createBlock("loop", List.of(),
					                            List.of("entry", "body"), List.of("body", "exit")
					        ),
					        BrilCfg.createBlock("body", List.of(),
					                            List.of("loop"), List.of("loop")
					        ),
					        BrilCfg.createBlock("exit", List.of(),
					                            List.of("loop"), List.of()
					        )
					)
			);
			Assert.fail("expected an exception");
		}
		catch (IllegalStateException ignored) {
		}

		try {
			BrilCfg.testValidSuccessorsAndPredecessors(
					List.of(BrilCfg.createBlock("entry", List.of(),
					                            List.of(), List.of("loop")
					        ),
					        BrilCfg.createBlock("loop", List.of(),
					                            List.of("entry", "body"), List.of("body", "exit")
					        ),
					        BrilCfg.createBlock("body", List.of(),
					                            List.of("loop"), List.of("loop")
					        ),
					        BrilCfg.createBlock("exit", List.of(),
					                            List.of("loop"), List.of("prohibited")
					        )
					)
			);
			Assert.fail("expected an exception");
		}
		catch (IllegalStateException ignored) {
		}

		try {
			BrilCfg.testValidSuccessorsAndPredecessors(
					List.of(BrilCfg.createBlock("entry", List.of(),
					                            List.of(), List.of("loop")
					        ),
					        BrilCfg.createBlock("loop", List.of(),
					                            List.of("entry"/*, "body"*/), List.of("body", "exit")
					        ),
					        BrilCfg.createBlock("body", List.of(),
					                            List.of("loop"), List.of("loop")
					        ),
					        BrilCfg.createBlock("exit", List.of(),
					                            List.of("loop"), List.of()
					        )
					)
			);
			Assert.fail("expected an exception");
		}
		catch (IllegalStateException ignored) {
		}

		try {
			BrilCfg.testValidSuccessorsAndPredecessors(
					List.of(BrilCfg.createBlock("entry", List.of(),
					                            List.of(), List.of("loop")
					        ),
					        BrilCfg.createBlock("loop", List.of(),
					                            List.of("entry", "body"), List.of(/*"body", */"exit")
					        ),
					        BrilCfg.createBlock("body", List.of(),
					                            List.of("loop"), List.of("loop")
					        ),
					        BrilCfg.createBlock("exit", List.of(),
					                            List.of("loop"), List.of()
					        )
					)
			);
			Assert.fail("expected an exception");
		}
		catch (IllegalStateException ignored) {
		}
	}

	// Utils ==================================================================

	private void assertEqualsCfg(String expectedName,
	                             List<BrilNode> expectedInstructions,
	                             List<String> expectedPredecessors,
	                             List<String> expectedSuccessors,
	                             BrilNode blockNode) {
		Assert.assertEquals(expectedName, BrilCfg.getName(blockNode));
		Assert.assertEquals(expectedInstructions, BrilCfg.getInstructions(blockNode));
		Assert.assertEquals(expectedPredecessors, BrilCfg.getPredecessors(blockNode));
		Assert.assertEquals(expectedSuccessors, BrilCfg.getSuccessors(blockNode));
	}
}
