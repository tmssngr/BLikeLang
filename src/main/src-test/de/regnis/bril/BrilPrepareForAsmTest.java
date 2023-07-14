package de.regnis.bril;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author Thomas Singer
 */
public class BrilPrepareForAsmTest {

	// Accessing ==============================================================

	@Test
	public void testPrepare() {
		final BrilPrepareForAsm prepare = new BrilPrepareForAsm("v.", "r.", "s.", 8, 2);
		final BrilNode function = BrilFactory.createFunctionV("average", List.of(),
		                                                      new BrilInstructions()
				                                                      .constant("n", 0)
				                                                      .constant("sum", 0)

				                                                      .label("loop")
				                                                      .calli("value", "getInt", List.of())
				                                                      .constant("zero", 0)
				                                                      .lessThan("cond", "value", "zero")
				                                                      .branch("cond", "exit", "body")

				                                                      .label("body")
				                                                      .constant("one", 1)
				                                                      .add("n", "n", "one")
				                                                      .add("sum", "sum", "value")
				                                                      .div("average", "sum", "n")
				                                                      .printi("average")
				                                                      .jump("loop")

				                                                      .label("exit")
				                                                      .ret()
				                                                      .get()
		);
		final BrilNode cfg = prepare.prepare(function);
		final Iterator<BrilNode> blocks = BrilCfg.getBlocks(cfg).iterator();
		assertEqualsCfg("block 0", new BrilInstructions()
				                .constant("v.2", 0)
				                .constant("v.1", 0)
				                .jump("loop")
				                .get(),
		                List.of(), List.of("loop"),
		                blocks.next());
		assertEqualsCfg("loop", new BrilInstructions()
				                .calli("r.0", "getInt", List.of())
				                .idi("v.0", "r.0")
				                .constant("v.3", 0)
				                .lessThan("v.3", "v.0", "v.3")
				                .branch("v.3", "exit", "body")
				                .get(),
		                List.of("block 0", "body"), List.of("exit", "body"),
		                blocks.next());
		assertEqualsCfg("body", new BrilInstructions()
				                .constant("v.3", 1)
				                .add("v.2", "v.2", "v.3")
				                .add("v.1", "v.1", "v.0")
				                .div("v.0", "v.1", "v.2")
				                .idi("r.0", "v.0")
				                .printi("r.0")
				                .jump("loop")
				                .get(),
		                List.of("loop"), List.of("loop"),
		                blocks.next());
		assertEqualsCfg("exit", new BrilInstructions()
				                .jump("exit 4")
				                .get(),
		                List.of("loop"), List.of("exit 4"),
		                blocks.next());
		assertEqualsCfg("exit 4", List.of(),
		                List.of("exit"), List.of(),
		                blocks.next());
		assertFalse(blocks.hasNext());
	}

	// Utils ==================================================================

	private void assertEqualsCfg(String expectedName,
	                             List<BrilNode> expectedInstructions,
	                             List<String> expectedPredecessors,
	                             List<String> expectedSuccessors,
	                             BrilNode blockNode) {
		Assert.assertEquals(expectedName, BrilCfg.getName(blockNode));
		assertInstructions(expectedInstructions, BrilCfg.getInstructions(blockNode));
		Assert.assertEquals(expectedPredecessors, BrilCfg.getPredecessors(blockNode));
		Assert.assertEquals(expectedSuccessors, BrilCfg.getSuccessors(blockNode));
	}

	private static void assertInstructions(List<BrilNode> expectedInstructions, List<BrilNode> instructions) {
		final Iterator<BrilNode> expectedIt = expectedInstructions.iterator();
		final Iterator<BrilNode> it = instructions.iterator();
		while (true) {
			final boolean expectedHasNext = expectedIt.hasNext();
			final boolean hasNext = it.hasNext();
			assertEquals(expectedHasNext, hasNext);
			if (!expectedHasNext) {
				break;
			}

			final BrilNode expectedInstruction = expectedIt.next();
			final BrilNode instruction = it.next();
			assertInstruction(expectedInstruction, instruction);
		}
	}

	private static void assertInstruction(BrilNode expectedInstruction, BrilNode instruction) {
		final Map<String, Object> expectedMap = BrilInstructions.getMap(expectedInstruction);
		final Map<String, Object> map = BrilInstructions.getMap(instruction);
		assertEquals(expectedMap, map);
	}
}
