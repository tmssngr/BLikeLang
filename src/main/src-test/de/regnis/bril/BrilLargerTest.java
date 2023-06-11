package de.regnis.bril;

import org.junit.Test;

import java.util.List;

/**
 * @author Thomas Singer
 */
public class BrilLargerTest {

	// Accessing ==============================================================

	@Test
	public void testSimple() throws Exception {
		assertCompiler(List.of(
				               BrilCfg.createBlock("block 0",
				                                   List.of(
						                                   BrilInstructions.constant("a.1", 2),
						                                   BrilInstructions.print("a.1")
				                                   ))
		               ),
		               BrilFactory.createFunction("main", "void", List.of(),
		                                          List.of(
				                                          BrilInstructions.constant("a", 1),
				                                          BrilInstructions.constant("a", 2),
				                                          BrilInstructions.print("a")
		                                          )
		               )
		);
	}

	@Test
	public void testLoop() throws Exception {
		assertCompiler(List.of(
				               BrilCfg.createBlock("block 0",
				                                   List.of(BrilInstructions.constant("i", 0x20),
				                                           BrilInstructions.id("i.1", "i"),
				                                           BrilInstructions.jump("loop")
				                                   )
				               ),
				               BrilCfg.createBlock("loop",
				                                   List.of(BrilInstructions.constant("max", 0x80),
				                                           BrilInstructions.lessThan("cond", "i.1", "max"),
				                                           BrilInstructions.branch("cond", "body", "exit")
				                                   )
				               ),
				               BrilCfg.createBlock("body",
				                                   List.of(BrilInstructions.constant("mask", 0x0f),
				                                           BrilInstructions.and("value", "i.1", "mask"),
				                                           BrilInstructions.constant("zero", 0),
				                                           BrilInstructions.lessThan("cond.1", "value", "zero"),
				                                           BrilInstructions.branch("cond.1", "print_i", "endif")
				                                   )
				               ),
				               BrilCfg.createBlock("print_i",
				                                   List.of(BrilInstructions.print("i.1"),
				                                           BrilInstructions.jump("endif")
				                                   )
				               ),
				               BrilCfg.createBlock("endif",
				                                   List.of(BrilInstructions.id("i.2", "i.1"),
				                                           BrilInstructions.call("printAscii", List.of("i.2")),
				                                           BrilInstructions.constant("one", 1),
				                                           BrilInstructions.add("i.3", "i.2", "one"),
				                                           BrilInstructions.id("i.1", "i.3"),
				                                           BrilInstructions.jump("loop")
				                                   )
				               ),
				               BrilCfg.createBlock("exit", List.of())
		               ), BrilFactory.createFunction("test", "void", List.of(),
		                                             BrilCfgTest.createLoopInstructions())
		);
	}

	// Utils ==================================================================

	private void assertCompiler(List<BrilNode> expectedBlocks, BrilNode function) throws BrilCfg.DuplicateLabelException, BrilCfg.NoExitBlockException, BrilCfg.InvalidTargetLabelException {
		final BrilNode cfg = BrilCfg.buildBlocks(function);
		BrilCfgSsa.transformToSsaWithPhiFunctions(cfg);
		BrilCfgSsa.inlinePhiFunctions(cfg);
		BrilDeadCodeElimination.simpleDce(cfg);
		BrilCfgSsaTest.assertEqualsFunctionBlocks(expectedBlocks, cfg);
	}
}
