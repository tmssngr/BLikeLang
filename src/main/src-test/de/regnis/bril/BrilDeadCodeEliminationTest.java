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
		Assert.assertEquals(new BrilInstructions()
				                    .constant("a", 4)
				                    .constant("b", 2)
				                    .add("d", "a", "b")
				                    .print("d")
				                    .get(),
		                    BrilDeadCodeElimination.simpleDce(
				                    new BrilInstructions()
						                    .constant("a", 4)
						                    .constant("b", 2)
						                    .constant("c", 1)
						                    .add("d", "a", "b")
						                    .print("d")
						                    .get(), Set.of()));
	}

	@Test
	public void testSimpleKeep1() {
		Assert.assertEquals(new BrilInstructions()
				                    .constant("a", 4)
				                    .constant("b", 2)
				                    .constant("c", 1)
				                    .add("d", "a", "b")
				                    .print("d")
				                    .get(),
		                    BrilDeadCodeElimination.simpleDce(
				                    new BrilInstructions()
						                    .constant("a", 4)
						                    .constant("b", 2)
						                    .constant("c", 1)
						                    .add("d", "a", "b")
						                    .print("d")
						                    .get(), Set.of("c")));
	}

	@Test
	public void testSimple2() {
		Assert.assertEquals(new BrilInstructions()
				                    .constant("a", 4)
				                    .constant("b", 2)
				                    .add("d", "a", "b")
				                    .print("d")
				                    .get(),
		                    BrilDeadCodeElimination.simpleDce(
				                    new BrilInstructions()
						                    .constant("a", 4)
						                    .constant("b", 2)
						                    .constant("c", 1)
						                    .add("d", "a", "b")
						                    .add("e", "c", "d")
						                    .print("d")
						                    .get(), Set.of()));
	}

	@Test
	public void testSimpleKeep2() {
		Assert.assertEquals(new BrilInstructions()
				                    .constant("a", 4)
				                    .constant("b", 2)
				                    .constant("c", 1)
				                    .add("d", "a", "b")
				                    .add("e", "c", "d")
				                    .print("d")
				                    .get(),
		                    BrilDeadCodeElimination.simpleDce(
				                    new BrilInstructions()
						                    .constant("a", 4)
						                    .constant("b", 2)
						                    .constant("c", 1)
						                    .add("d", "a", "b")
						                    .add("e", "c", "d")
						                    .print("d")
						                    .get(), Set.of("a", "e")));
	}

	@Test
	public void testBlocks() {
		final BrilNode cfgFunction = BrilCfg.createFunction(
				"test", "void", List.of(),
				List.of(
						BrilCfg.createBlock("entry", new BrilInstructions()
								                    .constant("a", 4)
								                    .constant("b", 2)
								                    .jump("next")
								                    .get(),
						                    List.of(), List.of("next")
						),
						BrilCfg.createBlock("next", new BrilInstructions()
								                    .print("b")
								                    .get(),
						                    List.of("entry"), List.of()
						)
				)
		);
		BrilDeadCodeElimination.simpleDce(cfgFunction);
		BrilCfgSsaTest.assertEqualsFunctionBlocks(List.of(
				                    BrilCfg.createBlock("entry", new BrilInstructions()
						                                        // .constant("a", 4)
						                                        .constant("b", 2)
						                                        .jump("next")
						                                        .get(),
				                                        List.of(), List.of("next")
				                    ),
				                    BrilCfg.createBlock("next", new BrilInstructions()
						                                        .print("b")
						                                        .get(),
				                                        List.of("entry"), List.of()
				                    )
		                    ),
		                    cfgFunction);
	}
}
