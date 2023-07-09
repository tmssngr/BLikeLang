package de.regnis.bril;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Thomas Singer
 */
public class BrilCfgTest {

	// Static =================================================================

	@NotNull
	public static List<BrilNode> createLoopInstructions() {
		return new BrilInstructions()
				.constant("i", 0x20)

				.label("loop")
				.constant("max", 0x80)
				.lessThan("cond", "i", "max")
				.branch("cond", "body", "exit")

				.label("body")
				.constant("mask", 0x0f)
				.and("value", "i", "mask")
				.constant("zero", 0)
				.lessThan("cond", "value", "zero")
				.branch("cond", "print_i", "endif")

				.label("print_i")
				.printi("i")

				.label("endif")
				.call("printAscii", List.of(BrilFactory.argi("i")))
				.constant("one", 1)
				.add("i", "i", "one")
				.jump("loop")

				.label("exit")
				.get();
	}

	// Accessing ==============================================================

	@Test
	public void testGetSetInstructions() {
		final BrilNode block = BrilCfg.createBlock("name", new BrilInstructions()
				.add("a", "b", "c")
				.get());

		List<BrilNode> instructions = BrilCfg.getInstructions(block);
		BrilCfg.setInstructions(instructions, block);
		Assert.assertEquals(new BrilInstructions()
				                    .add("a", "b", "c")
				                    .get(), BrilCfg.getInstructions(block));

		instructions = new ArrayList<>(instructions);
		instructions.addAll(new BrilInstructions()
				                    .printi("a")
				                    .get());
		BrilCfg.setInstructions(instructions, block);
		Assert.assertEquals(new BrilInstructions()
				                    .add("a", "b", "c")
				                    .printi("a")
				                    .get(), BrilCfg.getInstructions(block));
	}

	@Test
	public void testFormBlocks() {
		Assert.assertEquals(List.of(
				new BrilInstructions()
						.constant("v", 4)
						.jump(".somewhere")
						.get(),
				new BrilInstructions()
						.constant("v", 2)
						.get(),
				new BrilInstructions()
						.label(".somewhere")
						.printi("v")
						.get()
		), BrilCfg.splitIntoBlocks(new BrilInstructions()
				                           .constant("v", 4)
				                           .jump(".somewhere")
				                           .constant("v", 2)
				                           .label(".somewhere")
				                           .printi("v")
				                           .get()));
	}

	@Test
	public void testBuildBlocks() throws Exception {
		final List<BrilNode> blocks = BrilCfg.buildBlocks(new BrilInstructions()
				                                                  .constant("v", 4)

				                                                  .label(".loop")
				                                                  .printi("v")
				                                                  .constant("one", 1)
				                                                  .sub("v", "v", "one")
				                                                  .lessThan("cond", "v", "one")
				                                                  .branch("cond", ".end", ".loop")

				                                                  .label(".end")
				                                                  .ret()
				                                                  .get());
		final Iterator<BrilNode> iterator = blocks.iterator();
		assertEqualsCfg("block 0", new BrilInstructions()
				                .constant("v", 4)
				                .jump(".loop")
				                .get(),
		                List.of(),
		                List.of(".loop"),
		                iterator.next());
		assertEqualsCfg(".loop", new BrilInstructions()
				                .printi("v")
				                .constant("one", 1)
				                .sub("v", "v", "one")
				                .lessThan("cond", "v", "one")
				                .branch("cond", ".end", ".loop")
				                .get(),
		                List.of("block 0", ".loop"),
		                List.of(".end", ".loop"),
		                iterator.next());
		assertEqualsCfg(".end", new BrilInstructions()
				                .jump("exit 3")
				                .get(),
		                List.of(".loop"),
		                List.of("exit 3"),
		                iterator.next());
		assertEqualsCfg("exit 3", List.of(),
		                List.of(".end"),
		                List.of(),
		                iterator.next());
		Assert.assertFalse(iterator.hasNext());
	}

	@Test
	public void testBuildBlocksRetSum() throws Exception {
		final List<BrilNode> blocks = BrilCfg.buildBlocks(new BrilInstructions()
				                                                  .add("sum", "a", "b")
				                                                  .reti("sum")
				                                                  .get());
		final Iterator<BrilNode> iterator = blocks.iterator();
		assertEqualsCfg("block 0", new BrilInstructions()
				                .add("sum", "a", "b")
				                .reti("sum")
				                .jump("exit 1")
				                .get(),
		                List.of(),
		                List.of("exit 1"),
		                iterator.next());
		assertEqualsCfg("exit 1", List.of(),
		                List.of("block 0"),
		                List.of(),
		                iterator.next());
		Assert.assertFalse(iterator.hasNext());
	}

	@Test
	public void testBuildBlocksExit1() throws Exception {
		final List<BrilNode> blocks = BrilCfg.buildBlocks(
				new BrilInstructions()
						.constant("i", 1)

						.label("exit")
						.get()
		);
		final Iterator<BrilNode> iterator = blocks.iterator();
		assertEqualsCfg("block 0", new BrilInstructions()
				                .constant("i", 1)
				                .jump("exit")
				                .get(),
		                List.of(),
		                List.of("exit"),
		                iterator.next());
		assertEqualsCfg("exit", List.of(),
		                List.of("block 0"),
		                List.of(),
		                iterator.next());
		Assert.assertFalse(iterator.hasNext());
	}

	@Test
	public void testBuildBlocksExit2() throws Exception {
		final List<BrilNode> blocks = BrilCfg.buildBlocks(createLoopInstructions());
		assertLoopInstructions(blocks.iterator());

		final BrilNode function = BrilCfg.buildBlocks(BrilFactory.createFunctionV(
				"test", List.of(),
				createLoopInstructions()
		));
		assertLoopInstructions(BrilCfg.getBlocks(function).iterator());
	}

	@Test
	public void testBuildBlocksAddJumpFromFallThroughBlock() throws Exception {
		final List<BrilNode> blocks = BrilCfg.buildBlocks(new BrilInstructions()
				                                                  .constant("zero", 0)
				                                                  .lessThan("cond", "input", "zero")
				                                                  .branch("cond", "then", "endif")
				                                                  .label("then")
				                                                  .idi("input", "zero")
				                                                  .label("endif")
				                                                  .printi("input")
				                                                  .get());
		final Iterator<BrilNode> iterator = blocks.iterator();
		assertEqualsCfg("block 0", new BrilInstructions()
				                .constant("zero", 0)
				                .lessThan("cond", "input", "zero")
				                .branch("cond", "then", "endif")
				                .get(),
		                List.of(),
		                List.of("then", "endif"),
		                iterator.next());
		assertEqualsCfg("then", new BrilInstructions()
				                .idi("input", "zero")
				                .jump("endif")
				                .get(),
		                List.of("block 0"),
		                List.of("endif"),
		                iterator.next());
		assertEqualsCfg("endif", new BrilInstructions()
				                .printi("input")
				                .get(),
		                List.of("block 0", "then"),
		                List.of(),
		                iterator.next());
		Assert.assertFalse(iterator.hasNext());
	}

	@Test
	public void testBuildBlocksRemoveUnused() throws Exception {
		final List<BrilNode> blocks = BrilCfg.buildBlocks(new BrilInstructions()
				                                                  .constant("v", 4)
				                                                  .jump(".somewhere")

				                                                  .constant("v", 2)

				                                                  .label(".somewhere")
				                                                  .printi("v")
				                                                  .get()
		);
		Iterator<BrilNode> iterator = blocks.iterator();
		assertEqualsCfg("block 0", new BrilInstructions()
				                .constant("v", 4)
				                .jump(".somewhere")
				                .get(),
		                List.of(),
		                List.of(".somewhere"),
		                iterator.next());
		assertEqualsCfg("block 1", new BrilInstructions()
				                .constant("v", 2)
				                .jump(".somewhere")
				                .get(),
		                List.of(),
		                List.of(".somewhere"),
		                iterator.next());
		assertEqualsCfg(".somewhere", new BrilInstructions()
				                .printi("v")
				                .get(),
		                List.of("block 0", "block 1"),
		                List.of(),
		                iterator.next());
		Assert.assertFalse(iterator.hasNext());

		BrilCfg.removeUnusedBlocks(blocks);

		iterator = blocks.iterator();
		assertEqualsCfg("block 0", new BrilInstructions()
				                .constant("v", 4)
				                .jump(".somewhere")
				                .get(),
		                List.of(),
		                List.of(".somewhere"),
		                iterator.next());

		assertEqualsCfg(".somewhere", new BrilInstructions()
				                .printi("v")
				                .get(),
		                List.of("block 0"),
		                List.of(),
		                iterator.next());
		Assert.assertFalse(iterator.hasNext());
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

	private void assertLoopInstructions(Iterator<BrilNode> iterator) {
		assertEqualsCfg("block 0", new BrilInstructions()
				                .constant("i", 0x20)
				                .jump("loop")
				                .get(),
		                List.of(), List.of("loop"),
		                iterator.next());
		assertEqualsCfg("loop", new BrilInstructions()
				                .constant("max", 0x80)
				                .lessThan("cond", "i", "max")
				                .branch("cond", "body", "exit")
				                .get(),
		                List.of("block 0", "endif"), List.of("body", "exit"),
		                iterator.next());
		assertEqualsCfg("body", new BrilInstructions()
				                .constant("mask", 0x0f)
				                .and("value", "i", "mask")
				                .constant("zero", 0)
				                .lessThan("cond", "value", "zero")
				                .branch("cond", "print_i", "endif")
				                .get(),
		                List.of("loop"), List.of("print_i", "endif"),
		                iterator.next());
		assertEqualsCfg("print_i", new BrilInstructions()
				                .printi("i")
				                .jump("endif")
				                .get(),
		                List.of("body"), List.of("endif"),
		                iterator.next());
		assertEqualsCfg("endif", new BrilInstructions()
				                .call("printAscii", List.of(BrilFactory.argi("i")))
				                .constant("one", 1)
				                .add("i", "i", "one")
				                .jump("loop")
				                .get(),
		                List.of("body", "print_i"), List.of("loop"),
		                iterator.next());
		assertEqualsCfg("exit", List.of(),
		                List.of("loop"), List.of(),
		                iterator.next());
		Assert.assertFalse(iterator.hasNext());
	}

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
