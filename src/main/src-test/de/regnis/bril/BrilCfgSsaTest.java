package de.regnis.bril;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

/**
 * @author Thomas Singer
 */
public class BrilCfgSsaTest {

	// Static =================================================================

	public static void assertEqualsFunctionBlocks(List<BrilNode> expectedBlocks, BrilNode function) {
		final Iterator<BrilNode> expectedIt = expectedBlocks.iterator();
		final Iterator<BrilNode> currentIt = BrilCfg.getBlocks(function).iterator();
		while (true) {
			final boolean expectedHasNext = expectedIt.hasNext();
			final boolean currentHasNext = currentIt.hasNext();
			Assert.assertEquals(expectedHasNext, currentHasNext);
			if (!expectedHasNext) {
				break;
			}

			final BrilNode expectedBlock = expectedIt.next();
			final BrilNode currentBlock = currentIt.next();
			assertBlock(expectedBlock, currentBlock);
		}
	}

	// Accessing ==============================================================

	@Test
	public void testSimple() {
		assertSsa(List.of(
				          BrilCfg.createBlock("start",
				                              new BrilInstructions()
						                              .constant("a", 1)
						                              .constant("b", 2)
						                              .add("c", "a", "b")
						                              .constant("a.1", 3)
						                              .add("c.1", "a.1", "b")
						                              .print("c.1")
						                              .get()
				          )),
		          BrilCfg.createFunction("main", "void", List.of(),
		                                 List.of(BrilCfg.createBlock("start",
		                                                             new BrilInstructions()
				                                                             .constant("a", 1)
				                                                             .constant("b", 2)
				                                                             .add("c", "a", "b")
				                                                             .constant("a", 3)
				                                                             .add("c", "a", "b")
				                                                             .print("c")
				                                                             .get())
		                                 )
		          )
		);
	}

	@Test
	public void testReassignedVar() {
		assertSsa(List.of(
				          BrilCfg.createBlock("start",
				                              new BrilInstructions()
						                              .constant("a", 1)
						                              .constant("a.1", 2)
						                              .print("a.1")
						                              .get()
				          )
		          ),
		          BrilCfg.createFunction("main", "void", List.of(),
		                                 List.of(BrilCfg.createBlock("start",
		                                                             new BrilInstructions()
				                                                             .constant("a", 1)
				                                                             .constant("a", 2)
				                                                             .print("a")
				                                                             .get()
		                                         )
		                                 )
		          )
		);
	}

	@Test
	public void testParameters() {
		assertSsa(List.of(BrilCfg.createBlock("start",
		                                      new BrilInstructions()
				                                      .id("result", "a")
				                                      .lessThan("lt", "a", "b")
				                                      .branch("lt", "b>a", "exit")
				                                      .get()
		                  ),
		                  BrilCfg.createBlock("b>a",
		                                      new BrilInstructions()
				                                      .id("result.1", "b")
				                                      .get()
		                  ),
		                  BrilCfg.createBlock("exit",
		                                      new BrilCfgSsa.Instructions()
				                                      .phi("result.2", List.of("result", "result.1"))
				                                      .print("result.2")
				                                      .get()
		                  )
		          ),
		          BrilCfgDetectVarLivenessTest.createPrintMaxCfg()
		);
	}

	@Test
	public void testIf() {
		final BrilNode function = BrilCfg.createFunction(
				"test", "void", List.of(BrilFactory.argument("cond", "bool")),
				List.of(BrilCfg.createBlock("entry",
				                            new BrilInstructions()
						                            .constant("a", 47)
						                            .branch("cond", "left", "right")
						                            .get(),
				                            List.of(), List.of("left", "right")
				        ),
				        BrilCfg.createBlock("left",
				                            new BrilInstructions()
						                            .add("a", "a", "a")
						                            .jump("exit")
						                            .get(),
				                            List.of("entry"), List.of("exit")
				        ),
				        BrilCfg.createBlock("right",
				                            new BrilInstructions()
						                            .mul("a", "a", "a")
						                            .jump("exit")
						                            .get(),
				                            List.of("entry"), List.of("exit")
				        ),
				        BrilCfg.createBlock("exit",
				                            new BrilInstructions()
						                            .print("a")
						                            .ret()
						                            .get(),
				                            List.of("left", "right"), List.of()
				        )
				)
		);
		BrilCfgSsa.transformToSsaWithPhiFunctions(function);
		assertEqualsFunctionBlocks(List.of(
				BrilCfg.createBlock("entry",
				                    new BrilInstructions()
						                    .constant("a", 47)
						                    .branch("cond", "left", "right")
						                    .get()
				),
				BrilCfg.createBlock("left",
				                    new BrilInstructions()
						                    .add("a.1", "a", "a")
						                    .jump("exit")
						                    .get()
				),
				BrilCfg.createBlock("right",
				                    new BrilInstructions()
						                    .mul("a.2", "a", "a")
						                    .jump("exit")
						                    .get()
				),
				BrilCfg.createBlock("exit",
				                    new BrilCfgSsa.Instructions()
						                    .phi("a.3", List.of("a.1", "a.2"))
						                    .print("a.3")
						                    .ret()
						                    .get()
				)
		), function);

		BrilCfgSsa.inlinePhiFunctions(function);
		assertEqualsFunctionBlocks(List.of(
				BrilCfg.createBlock("entry",
				                    new BrilInstructions()
						                    .constant("a", 47)
						                    .branch("cond", "left", "right")
						                    .get()
				),
				BrilCfg.createBlock("left",
				                    new BrilInstructions()
						                    .add("a.1", "a", "a")
						                    .id("a.3", "a.1")
						                    .jump("exit")
						                    .get()
				),
				BrilCfg.createBlock("right",
				                    new BrilInstructions()
						                    .mul("a.2", "a", "a")
						                    .id("a.3", "a.2")
						                    .jump("exit")
						                    .get()
				),
				BrilCfg.createBlock("exit",
				                    new BrilInstructions()
						                    .print("a.3")
						                    .ret()
						                    .get()
				)
		), function);
	}

	@Test
	public void testLoop1() {
		final BrilNode function = BrilCfg.createFunction(
				"test", "void", List.of(),
				List.of(BrilCfg.createBlock("entry",
				                            new BrilInstructions()
						                            .constant("i", 1)
						                            .jump("loop")
						                            .get(),
				                            List.of(), List.of("loop")
				        ),
				        BrilCfg.createBlock("loop",
				                            new BrilInstructions()
						                            .constant("max", 10)
						                            .lessThan("cond", "i", "max")
						                            .branch("cond", "body", "exit")
						                            .get(),
				                            List.of("entry", "body"), List.of("body", "exit")
				        ),
				        BrilCfg.createBlock("body",
				                            new BrilInstructions()
						                            .add("i", "i", "i")
						                            .jump("loop")
						                            .get(),
				                            List.of("loop"), List.of("loop")
				        ),
				        BrilCfg.createBlock("exit",
				                            new BrilInstructions()
						                            .print("i")
						                            .ret()
						                            .get(),
				                            List.of("loop"), List.of()
				        )
				)
		);
		BrilCfgSsa.transformToSsaWithPhiFunctions(function);
		assertEqualsFunctionBlocks(List.of(
				BrilCfg.createBlock("entry",
				                    new BrilInstructions()
						                    .constant("i", 1)
						                    .jump("loop")
						                    .get()
				),
				BrilCfg.createBlock("loop",
				                    new BrilCfgSsa.Instructions()
						                    .phi("i.1", List.of("i", "i.2"))
						                    .constant("max", 10)
						                    .lessThan("cond", "i.1", "max")
						                    .branch("cond", "body", "exit")
						                    .get()
				),
				BrilCfg.createBlock("body",
				                    new BrilInstructions()
						                    .add("i.2", "i.1", "i.1")
						                    .jump("loop")
						                    .get()
				),
				BrilCfg.createBlock("exit",
				                    new BrilInstructions()
						                    .print("i.1")
						                    .ret()
						                    .get()
				)
		), function);

		BrilCfgSsa.inlinePhiFunctions(function);
		assertEqualsFunctionBlocks(List.of(
				BrilCfg.createBlock("entry",
				                    new BrilInstructions()
						                    .constant("i", 1)
						                    .id("i.1", "i")
						                    .jump("loop")
						                    .get()
				),
				BrilCfg.createBlock("loop",
				                    new BrilInstructions()
						                    .constant("max", 10)
						                    .lessThan("cond", "i.1", "max")
						                    .branch("cond", "body", "exit")
						                    .get()
				),
				BrilCfg.createBlock("body",
				                    new BrilInstructions()
						                    .add("i.2", "i.1", "i.1")
						                    .id("i.1", "i.2")
						                    .jump("loop")
						                    .get()
				),
				BrilCfg.createBlock("exit",
				                    new BrilInstructions()
						                    .print("i.1")
						                    .ret()
						                    .get()
				)
		), function);
	}

	@Test
	public void testLoop2() {
		final BrilNode function = BrilCfg.createFunction(
				"test", "void", List.of(),
				List.of(BrilCfg.createBlock("block 0",
				                            new BrilInstructions()
						                            .constant("i", 0x20)
						                            .jump("loop")
						                            .get(),
				                            List.of(), List.of("loop")),
				        BrilCfg.createBlock("loop",
				                            new BrilInstructions()
						                            .constant("max", 0x80)
						                            .lessThan("cond", "i", "max")
						                            .branch("cond", "body", "exit")
						                            .get(),
				                            List.of("block 0", "endif"), List.of("body", "exit")),
				        BrilCfg.createBlock("body",
				                            new BrilInstructions()
						                            .constant("mask", 0x0f)
						                            .and("value", "i", "mask")
						                            .constant("zero", 0)
						                            .lessThan("cond", "value", "zero")
						                            .branch("cond", "print_i", "endif")
						                            .get(),
				                            List.of("loop"), List.of("print_i", "endif")),
				        BrilCfg.createBlock("print_i",
				                            new BrilInstructions()
						                            .print("i")
						                            .jump("endif")
						                            .get(),
				                            List.of("body"), List.of("endif")),
				        BrilCfg.createBlock("endif",
				                            new BrilInstructions()
						                            .call("printAscii", List.of("i"))
						                            .constant("one", 1)
						                            .add("i", "i", "one")
						                            .jump("loop")
						                            .get(),
				                            List.of("body", "print_i"), List.of("loop")),
				        BrilCfg.createBlock("exit", List.of(),
				                            List.of("loop"), List.of()
				        )
				)
		);
		BrilCfgSsa.transformToSsaWithPhiFunctions(function);
		assertEqualsFunctionBlocks(List.of(
				BrilCfg.createBlock("block 0",
				                    new BrilInstructions()
						                    .constant("i", 0x20)
						                    .jump("loop")
						                    .get()
				),
				BrilCfg.createBlock("loop",
				                    new BrilCfgSsa.Instructions()
						                    .phi("i.1", List.of("i", "i.3"))
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
						                    .jump("loop")
						                    .get()
				),
				BrilCfg.createBlock("exit", List.of())
		), function);

		BrilCfgSsa.inlinePhiFunctions(function);
		assertEqualsFunctionBlocks(List.of(
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
		), function);
	}

	// Utils ==================================================================

	private static void assertSsa(List<BrilNode> expectedBlocks, BrilNode function) {
		BrilCfgSsa.transformToSsaWithPhiFunctions(function);
		assertEqualsFunctionBlocks(expectedBlocks, function);
	}

	private static void assertBlock(BrilNode expectedBlock, BrilNode block) {
		Assert.assertEquals(BrilCfg.getName(expectedBlock), BrilCfg.getName(block));
		Assert.assertEquals(BrilCfg.getInstructions(expectedBlock), BrilCfg.getInstructions(block));
	}
}
