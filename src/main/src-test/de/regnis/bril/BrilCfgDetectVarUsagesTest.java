package de.regnis.bril;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

/**
 * @author Thomas Singer
 */
public class BrilCfgDetectVarUsagesTest {

	// Accessing ==============================================================

	@Test
	public void testVarUsages() {
		final List<BrilNode> blocks = List.of(
				BrilCfg.createBlock("start", List.of(
						BrilInstructions.constant("v", 4),
						BrilInstructions.jump("print")
				), List.of(), List.of("print")),

				BrilCfg.createBlock("unused", List.of(
						BrilInstructions.constant("v", 2)
				), List.of(), List.of("print")),

				BrilCfg.createBlock("print", List.of(
						BrilInstructions.print("v")
				), List.of("start", "unused"), List.of("exit")),

				BrilCfg.createBlock("exit", List.of(),
				                    List.of("print"), List.of())
		);

		BrilCfgDetectVarUsages.detectVarUsages(blocks);

		Assert.assertEquals(4, blocks.size());
		assertEqualsCfg(Set.of(), Set.of("v"),
		                blocks.get(0));
		assertEqualsCfg(Set.of(), Set.of("v"),
		                blocks.get(1));
		assertEqualsCfg(Set.of("v"), Set.of(),
		                blocks.get(2));
		assertEqualsCfg(Set.of(), Set.of(),
		                blocks.get(3));
	}

	// Utils ==================================================================

	private void assertEqualsCfg(Set<String> expectedLiveBefore,
	                             Set<String> expectedLiveAfter, BrilNode blockNode) {
		Assert.assertEquals(expectedLiveBefore, BrilCfgDetectVarUsages.getVarsBeforeBlock(blockNode));
		Assert.assertEquals(expectedLiveAfter, BrilCfgDetectVarUsages.getVarsAfterBlock(blockNode));
	}
}
