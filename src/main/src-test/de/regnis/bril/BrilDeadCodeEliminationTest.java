package de.regnis.bril;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

/**
 * @author Thomas Singer
 */
public class BrilDeadCodeEliminationTest {

	@Test
	public void testSimple1() {
		Assert.assertEquals(List.of(
				                    BrilInstructions.constant("a", 4),
				                    BrilInstructions.constant("b", 2),
				                    BrilInstructions.add("d", "a", "b"),
				                    BrilInstructions.print("d")
		                    ),
		                    BrilDeadCodeElimination.simpleDce(
				                    List.of(
						                    BrilInstructions.constant("a", 4),
						                    BrilInstructions.constant("b", 2),
						                    BrilInstructions.constant("c", 1),
						                    BrilInstructions.add("d", "a", "b"),
						                    BrilInstructions.print("d")
				                    ), Set.of()));
	}

	@Test
	public void testSimpleKeep1() {
		Assert.assertEquals(List.of(
				                    BrilInstructions.constant("a", 4),
				                    BrilInstructions.constant("b", 2),
				                    BrilInstructions.constant("c", 1),
				                    BrilInstructions.add("d", "a", "b"),
				                    BrilInstructions.print("d")
		                    ),
		                    BrilDeadCodeElimination.simpleDce(
				                    List.of(
						                    BrilInstructions.constant("a", 4),
						                    BrilInstructions.constant("b", 2),
						                    BrilInstructions.constant("c", 1),
						                    BrilInstructions.add("d", "a", "b"),
						                    BrilInstructions.print("d")
				                    ), Set.of("c")));
	}

	@Test
	public void testSimple2() {
		Assert.assertEquals(List.of(
				                    BrilInstructions.constant("a", 4),
				                    BrilInstructions.constant("b", 2),
				                    BrilInstructions.add("d", "a", "b"),
				                    BrilInstructions.print("d")
		                    ),
		                    BrilDeadCodeElimination.simpleDce(
				                    List.of(
						                    BrilInstructions.constant("a", 4),
						                    BrilInstructions.constant("b", 2),
						                    BrilInstructions.constant("c", 1),
						                    BrilInstructions.add("d", "a", "b"),
						                    BrilInstructions.add("e", "c", "d"),
						                    BrilInstructions.print("d")
				                    ), Set.of()));
	}

	@Test
	public void testSimpleKeep2() {
		Assert.assertEquals(List.of(
				                    BrilInstructions.constant("a", 4),
				                    BrilInstructions.constant("b", 2),
				                    BrilInstructions.constant("c", 1),
				                    BrilInstructions.add("d", "a", "b"),
				                    BrilInstructions.add("e", "c", "d"),
				                    BrilInstructions.print("d")
		                    ),
		                    BrilDeadCodeElimination.simpleDce(
				                    List.of(
						                    BrilInstructions.constant("a", 4),
						                    BrilInstructions.constant("b", 2),
						                    BrilInstructions.constant("c", 1),
						                    BrilInstructions.add("d", "a", "b"),
						                    BrilInstructions.add("e", "c", "d"),
						                    BrilInstructions.print("d")
				                    ), Set.of("a", "e")));
	}

	@Test
	public void testBlocks() {
		final BrilNode cfgFunction = BrilCfg.createFunction(
				"test", "void", List.of(),
				List.of(
						BrilCfg.createBlock("entry", List.of(
								                    BrilInstructions.constant("a", 4),
								                    BrilInstructions.constant("b", 2),
								                    BrilInstructions.jump("next")
						                    ),
						                    List.of(), List.of("next")
						),
						BrilCfg.createBlock("next", List.of(
								                    BrilInstructions.print("b")
						                    ),
						                    List.of("entry"), List.of()
						)
				)
		);
		BrilDeadCodeElimination.simpleDce(cfgFunction);
		BrilCfgSsaTest.assertEqualsFunctionBlocks(List.of(
				                    BrilCfg.createBlock("entry", List.of(
						                                        // BrilInstructions.constant("a", 4),
						                                        BrilInstructions.constant("b", 2),
						                                        BrilInstructions.jump("next")
				                                        ),
				                                        List.of(), List.of("next")
				                    ),
				                    BrilCfg.createBlock("next", List.of(
						                                        BrilInstructions.print("b")
				                                        ),
				                                        List.of("entry"), List.of()
				                    )
		                    ),
		                    cfgFunction);
	}
}
