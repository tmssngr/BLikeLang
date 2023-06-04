package de.regnis.bril;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

/**
 * @author Thomas Singer
 */
public class BrilCfgDetectVarUsagesTest {

	// Static =================================================================

	@NotNull
	public static BrilNode createPrintMaxCfg() {
		return BrilCfg.createFunction("printMax", "int", List.of(BrilFactory.argument("a", "int"), BrilFactory.argument("b", "int")),
		                              List.of(BrilCfg.createBlock("start",
		                                                          List.of(
				                                                          BrilInstructions.id("result", "a"),
				                                                          BrilInstructions.lessThan("lt", "a", "b"),
				                                                          BrilInstructions.branch("lt", "b>a", "exit")
		                                                          ),
		                                                          List.of(), List.of("b>a", "exit")
		                                      ),
		                                      BrilCfg.createBlock("b>a",
		                                                          List.of(
				                                                          BrilInstructions.id("result", "b")
		                                                          ),
		                                                          List.of("start"), List.of("exit")
		                                      ),
		                                      BrilCfg.createBlock("exit",
		                                                          List.of(
				                                                          BrilInstructions.print("result")
		                                                          ),
		                                                          List.of("start", "b>a"), List.of()
		                                      )
		                              )
		);
	}

	// Accessing ==============================================================

	@Test
	public void testSimple() {
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

	@Test
	public void testPrintMax() {
		final List<BrilNode> blocks = BrilCfg.getBlocks(createPrintMaxCfg());

		BrilCfgDetectVarUsages.detectVarUsages(blocks);

		Assert.assertEquals(3, blocks.size());
		assertEqualsCfg(Set.of("a", "b"), Set.of("result", "b"),
		                blocks.get(0));
		assertEqualsCfg(Set.of("b"), Set.of("result"),
		                blocks.get(1));
		assertEqualsCfg(Set.of("result"), Set.of(),
		                blocks.get(2));
	}

	// Utils ==================================================================

	private void assertEqualsCfg(Set<String> expectedLiveBefore,
	                             Set<String> expectedLiveAfter, BrilNode blockNode) {
		Assert.assertEquals(expectedLiveBefore, BrilCfgDetectVarUsages.getVarsBeforeBlock(blockNode));
		Assert.assertEquals(expectedLiveAfter, BrilCfgDetectVarUsages.getVarsAfterBlock(blockNode));
	}
}
