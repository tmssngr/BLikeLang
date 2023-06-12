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
