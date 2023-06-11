package de.regnis.bril;

import org.junit.Test;

import java.util.List;

/**
 * @author Thomas Singer
 */
public class BrilLargerTest {

	// Accessing ==============================================================

	@Test
	public void testCompiler() throws Exception {
		assertCompiler(List.of(
				               BrilCfg.createBlock("block 0",
				                                   List.of(
						                                   BrilInstructions.constant("a.1", 2),
						                                   BrilInstructions.print("a.1"),
						                                   BrilInstructions.jump("exit 1")
				                                   )),
				               BrilCfg.createBlock("exit 1", List.of())
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

	// Utils ==================================================================

	private void assertCompiler(List<BrilNode> expectedBlocks, BrilNode function) throws BrilCfg.DuplicateLabelException, BrilCfg.NoExitBlockException, BrilCfg.InvalidTargetLabelException {
		final BrilNode cfg = BrilCfg.buildBlocks(function);
		BrilCfgSsa.transform(cfg);
		BrilDeadCodeElimination.simpleDce(cfg);
		BrilCfgSsaTest.assertEqualsFunctionBlocks(expectedBlocks, cfg);
	}
}
