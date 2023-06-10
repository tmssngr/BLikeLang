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
				                              List.of(
						                              BrilInstructions.constant("a", 1),
						                              BrilInstructions.constant("b", 2),
						                              BrilInstructions.add("c", "a", "b"),
						                              BrilInstructions.constant("a.1", 3),
						                              BrilInstructions.add("c.1", "a.1", "b"),
						                              BrilInstructions.print("c.1")
				                              ))
		          ),
		          BrilCfg.createFunction("main", "void", List.of(),
		                                 List.of(BrilCfg.createBlock("start",
		                                                             List.of(
				                                                             BrilInstructions.constant("a", 1),
				                                                             BrilInstructions.constant("b", 2),
				                                                             BrilInstructions.add("c", "a", "b"),
				                                                             BrilInstructions.constant("a", 3),
				                                                             BrilInstructions.add("c", "a", "b"),
				                                                             BrilInstructions.print("c")
		                                                             )
		                                         )
		                                 )
		          )
		);
	}

	@Test
	public void testReassignedVar() {
		assertSsa(List.of(
				          BrilCfg.createBlock("start",
				                              List.of(
						                              BrilInstructions.constant("a", 1),
						                              BrilInstructions.constant("a.1", 2),
						                              BrilInstructions.print("a.1")
				                              ))
		          ),
		          BrilCfg.createFunction("main", "void", List.of(),
		                                 List.of(BrilCfg.createBlock("start",
		                                                             List.of(
				                                                             BrilInstructions.constant("a", 1),
				                                                             BrilInstructions.constant("a", 2),
				                                                             BrilInstructions.print("a")
		                                                             )
		                                         )
		                                 )
		          )
		);
	}

	@Test
	public void testParameters() {
		assertSsa(List.of(BrilCfg.createBlock("start",
		                                      List.of(
				                                      BrilInstructions.id("result", "a"),
				                                      BrilInstructions.lessThan("lt", "a", "b"),
				                                      BrilInstructions.branch("lt", "b>a", "exit")
		                                      )
		                  ),
		                  BrilCfg.createBlock("b>a",
		                                      List.of(
				                                      BrilInstructions.id("result.1", "b")
		                                      )
		                  ),
		                  BrilCfg.createBlock("exit",
		                                      List.of(
				                                      BrilCfgSsa.phiFunction("result.2", List.of("result", "result.1")),
				                                      BrilInstructions.print("result.2")
		                                      )
		                  )
		          ),
		          BrilCfgDetectVarUsagesTest.createPrintMaxCfg()
		);
	}

	@Test
	public void testIf() {
		assertSsa(List.of(BrilCfg.createBlock("entry",
		                                      List.of(
				                                      BrilInstructions.constant("a", 47),
				                                      BrilInstructions.branch("cond", "left", "right")
		                                      )
		                  ),
		                  BrilCfg.createBlock("left",
		                                      List.of(
				                                      BrilInstructions.add("a.1", "a", "a"),
				                                      BrilInstructions.jump("exit")
		                                      )
		                  ),
		                  BrilCfg.createBlock("right",
		                                      List.of(
				                                      BrilInstructions.mul("a.2", "a", "a"),
				                                      BrilInstructions.jump("exit")
		                                      )
		                  ),
		                  BrilCfg.createBlock("exit",
		                                      List.of(
				                                      BrilCfgSsa.phiFunction("a.3", List.of("a.1", "a.2")),
				                                      BrilInstructions.print("a.3"),
				                                      BrilInstructions.ret()
		                                      )
		                  )
		          ),
		          BrilCfg.createFunction("test", "void", List.of(BrilFactory.argument("cond", "bool")),
		                                 List.of(BrilCfg.createBlock("entry",
		                                                             List.of(
				                                                             BrilInstructions.constant("a", 47),
				                                                             BrilInstructions.branch("cond", "left", "right")
		                                                             ),
		                                                             List.of(), List.of("left", "right")
		                                         ),
		                                         BrilCfg.createBlock("left",
		                                                             List.of(
				                                                             BrilInstructions.add("a", "a", "a"),
				                                                             BrilInstructions.jump("exit")
		                                                             ),
		                                                             List.of("entry"), List.of("exit")
		                                         ),
		                                         BrilCfg.createBlock("right",
		                                                             List.of(
				                                                             BrilInstructions.mul("a", "a", "a"),
				                                                             BrilInstructions.jump("exit")
		                                                             ),
		                                                             List.of("entry"), List.of("exit")
		                                         ),
		                                         BrilCfg.createBlock("exit",
		                                                             List.of(
				                                                             BrilInstructions.print("a"),
				                                                             BrilInstructions.ret()
		                                                             ),
		                                                             List.of("left", "right"), List.of()
		                                         )
		                                 )
		          )
		);
	}

	@Test
	public void testLoop() {
		assertSsa(List.of(BrilCfg.createBlock("entry",
		                                      List.of(
				                                      BrilInstructions.constant("i", 1),
				                                      BrilInstructions.jump("loop")
		                                      )
		                  ),
		                  BrilCfg.createBlock("loop",
		                                      List.of(
				                                      BrilCfgSsa.phiFunction("i.1", List.of("i", "i.2")),
				                                      BrilInstructions.constant("max", 10),
				                                      BrilInstructions.lessThan("cond", "i.1", "max"),
				                                      BrilInstructions.branch("cond", "body", "exit")
		                                      )
		                  ),
		                  BrilCfg.createBlock("body",
		                                      List.of(
				                                      BrilInstructions.add("i.2", "i.1", "i.1"),
				                                      BrilInstructions.jump("loop")
		                                      )
		                  ),
		                  BrilCfg.createBlock("exit",
		                                      List.of(
				                                      BrilInstructions.print("i.1"),
				                                      BrilInstructions.ret()
		                                      )
		                  )
		          ),
		          BrilCfg.createFunction("test", "void", List.of(),
		                                 List.of(BrilCfg.createBlock("entry",
		                                                             List.of(
				                                                             BrilInstructions.constant("i", 1),
				                                                             BrilInstructions.jump("loop")
		                                                             ),
		                                                             List.of(), List.of("loop")
		                                         ),
		                                         BrilCfg.createBlock("loop",
		                                                             List.of(
				                                                             BrilInstructions.constant("max", 10),
				                                                             BrilInstructions.lessThan("cond", "i", "max"),
				                                                             BrilInstructions.branch("cond", "body", "exit")
		                                                             ),
		                                                             List.of("entry", "body"), List.of("body", "exit")
		                                         ),
		                                         BrilCfg.createBlock("body",
		                                                             List.of(
				                                                             BrilInstructions.add("i", "i", "i"),
				                                                             BrilInstructions.jump("loop")
		                                                             ),
		                                                             List.of("loop"), List.of("loop")
		                                         ),
		                                         BrilCfg.createBlock("exit",
		                                                             List.of(
				                                                             BrilInstructions.print("i"),
				                                                             BrilInstructions.ret()
		                                                             ),
		                                                             List.of("loop"), List.of()
		                                         )
		                                 )
		          )
		);
	}

	// Utils ==================================================================

	private static void assertSsa(List<BrilNode> expectedBlocks, BrilNode function) {
		BrilCfgSsa.transform(function);
		assertEqualsFunctionBlocks(expectedBlocks, function);
	}

	private static void assertBlock(BrilNode expectedBlock, BrilNode block) {
		Assert.assertEquals(BrilCfg.getName(expectedBlock), BrilCfg.getName(block));
		Assert.assertEquals(BrilCfg.getInstructions(expectedBlock), BrilCfg.getInstructions(block));
	}
}
