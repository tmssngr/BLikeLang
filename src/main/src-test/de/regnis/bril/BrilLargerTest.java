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
				                                   new BrilInstructions()
						                                   .constant("a.1", 2)
						                                   .print("a.1")
						                                   .get())
		               ),
		               BrilFactory.createFunction("main", "void", List.of(),
		                                          new BrilInstructions()
				                                          .constant("a", 1)
				                                          .constant("a", 2)
				                                          .print("a")
				                                          .get()
		               )
		);
	}

	@Test
	public void testLoop() throws Exception {
		assertCompiler(List.of(
				               BrilCfg.createBlock("block 0",
				                                   new BrilInstructions()
						                                   .constant("i", 0x20)
						                                   .id("i.1", "i")
						                                   .jump("loop")
						                                   .get()
				               ),
				               BrilCfg.createBlock("loop",
				                                   new BrilInstructions()
						                                   .constant("max", 0x80)
						                                   .lessThan("cond", "i.1", "max")
						                                   .branch("cond", "body", "exit")
						                                   .get()
				               ),
				               BrilCfg.createBlock("body",
				                                   new BrilInstructions()
						                                   .constant("mask", 0x0f)
						                                   .and("value", "i.1", "mask")
						                                   .constant("zero", 0)
						                                   .lessThan("cond.1", "value", "zero")
						                                   .branch("cond.1", "print_i", "endif")
						                                   .get()
				               ),
				               BrilCfg.createBlock("print_i",
				                                   new BrilInstructions()
						                                   .print("i.1")
						                                   .jump("endif")
						                                   .get()
				               ),
				               BrilCfg.createBlock("endif",
				                                   new BrilInstructions()
						                                   .id("i.2", "i.1")
						                                   .call("printAscii", List.of("i.2"))
						                                   .constant("one", 1)
						                                   .add("i.3", "i.2", "one")
						                                   .id("i.1", "i.3")
						                                   .jump("loop")
						                                   .get()
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
