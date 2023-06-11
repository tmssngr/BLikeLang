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
		final List<BrilNode> blocks = BrilCfg.buildBlocks(List.of(
				BrilInstructions.constant("v", 4),

				BrilInstructions.label(".loop"),
				BrilInstructions.print("v"),
				BrilInstructions.constant("one", 1),
				BrilInstructions.sub("v", "v", "one"),
				BrilInstructions.lessThan("cond", "v", "one"),
				BrilInstructions.branch("cond", ".end", ".loop"),

				BrilInstructions.label(".end"),
				BrilInstructions.ret()
		));
		Assert.assertEquals(4, blocks.size());
		assertEqualsCfg("block 0", List.of(BrilInstructions.constant("v", 4),
		                                   BrilInstructions.jump(".loop")),
		                List.of(),
		                List.of(".loop"),
		                blocks.get(0));
		assertEqualsCfg(".loop", List.of(BrilInstructions.print("v"),
		                                 BrilInstructions.constant("one", 1),
		                                 BrilInstructions.sub("v", "v", "one"),
		                                 BrilInstructions.lessThan("cond", "v", "one"),
		                                 BrilInstructions.branch("cond", ".end", ".loop")),
		                List.of("block 0", ".loop"),
		                List.of(".end", ".loop"),
		                blocks.get(1));
		assertEqualsCfg(".end", List.of(),
		                List.of(".loop"),
		                List.of("exit 3"),
		                blocks.get(2));
		assertEqualsCfg("exit 3", List.of(),
		                List.of(".end"),
		                List.of(),
		                blocks.get(3));
	}

	@Test
	public void testBuildBlocksAddJumpFromFallThroughBlock() throws Exception {
		final List<BrilNode> blocks = BrilCfg.buildBlocks(List.of(
				BrilInstructions.constant("zero", 0),
				BrilInstructions.lessThan("cond", "input", "zero"),
				BrilInstructions.branch("cond", "then", "endif"),
				BrilInstructions.label("then"),
				BrilInstructions.id("input", "zero"),
				BrilInstructions.label("endif"),
				BrilInstructions.print("input")
		));
		Assert.assertEquals(3, blocks.size());
		assertEqualsCfg("block 0", List.of(BrilInstructions.constant("zero", 0),
		                                   BrilInstructions.lessThan("cond", "input", "zero"),
		                                   BrilInstructions.branch("cond", "then", "endif")),
		                List.of(),
		                List.of("then", "endif"),
		                blocks.get(0));
		assertEqualsCfg("then", List.of(BrilInstructions.id("input", "zero"),
		                                BrilInstructions.jump("endif")),
		                List.of("block 0"),
		                List.of("endif"),
		                blocks.get(1));
		assertEqualsCfg("endif", List.of(BrilInstructions.print("input")),
		                List.of("block 0", "then"),
		                List.of(),
		                blocks.get(2));
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
