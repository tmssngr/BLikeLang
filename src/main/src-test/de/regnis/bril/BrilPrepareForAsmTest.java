package de.regnis.bril;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Thomas Singer
 */
public class BrilPrepareForAsmTest {

	// Accessing ==============================================================

	@Test
	public void testBasicRet() {
		assertInstructions(new BrilInstructions()
				                   .constant("r.0", 31415)
				                   .idi("r.0", "r.0")
				                   .reti("r.0")
				                   .jump("exit 1")
				                   .label("exit 1")
				                   .get(),
		                   prepare(BrilFactory.createFunctionI("pi10_000", List.of(),
		                                                       new BrilInstructions()
				                                                       .constant("pi10000", 31415)
				                                                       .reti("pi10000")
				                                                       .get())));
	}

	@Test
	public void test2ParamRetValue() {
		assertInstructions(new BrilInstructions()
				                   .add("r.0", "r.0", "r.1")
				                   .idi("r.0", "r.0")
				                   .reti("r.0")
				                   .jump("exit 1")
				                   .label("exit 1")
				                   .get(),
		                   prepare(BrilFactory.createFunctionI("sum", List.of(BrilFactory.argi("a"), BrilFactory.argi("b")),
		                                                       new BrilInstructions()
				                                                       .add("sum", "a", "b")
				                                                       .reti("sum")
				                                                       .get()
		                   ))
		);
	}

	@Test
	public void testCall() {
		assertInstructions(new BrilInstructions()
				                   .constant("r.0", 2)
				                   .constant("r.1", 3)
				                   .idi("r.0", "r.0")
				                   .idi("r.1", "r.1")
				                   .calli("r.0", "sum", List.of(BrilFactory.argi("r.0"),
				                                                BrilFactory.argi("r.1")))
				                   .idi("r.0", "r.0")
				                   .idi("r.0", "r.0")
				                   .call("print", List.of(BrilFactory.argi("r.0")))
				                   .get(),
		                   prepare(BrilFactory.createFunctionV("main", List.of(),
		                                                       new BrilInstructions()
				                                                       .constant("value1", 2)
				                                                       .constant("value2", 3)
				                                                       .calli("result", "sum", List.of(BrilFactory.argi("value1"),
				                                                                                       BrilFactory.argi("value2")))
				                                                       .printi("result")
				                                                       .get()
		                   ))
		);
	}

	@Test
	public void testIfBoolean() {
		assertInstructions(new BrilInstructions()
				                   .branch("r.0", "takeLeft", "takeRight")
				                   .label("takeLeft")
				                   .idi("r.0", "r.1")
				                   .reti("r.0")
				                   .jump("exit 3")

				                   .label("takeRight")
				                   .idi("r.0", "s.2")
				                   .reti("r.0")
				                   .jump("exit 3")

				                   .label("exit 3")
				                   .get(),
		                   prepare(BrilFactory.createFunctionI("getLeftOrRight", List.of(
				                                                       BrilFactory.argb("leftOrRight"),
				                                                       BrilFactory.argi("left"),
				                                                       BrilFactory.argi("right")
		                                                       ),
		                                                       new BrilInstructions()
				                                                       .branch("leftOrRight", "takeLeft", "takeRight")
				                                                       .label("takeLeft")
				                                                       .reti("left")
				                                                       .label("takeRight")
				                                                       .reti("right")
				                                                       .get()
		                   ))
		);
	}

	@Test
	public void testIfLessThan() {
		assertInstructions(new BrilInstructions()
				                   .lessThan("v.2", "r.0", "r.1")
				                   .branch("v.2", "takeB", "takeA")

				                   .label("takeB")
				                   .idi("r.0", "r.1")
				                   .reti("r.0")
				                   .jump("exit 3")

				                   .label("takeA")
				                   .idi("r.0", "r.0")
				                   .reti("r.0")
				                   .jump("exit 3")

				                   .label("exit 3")
				                   .get(),
		                   prepare(BrilFactory.createFunctionI("max", List.of(
				                                                       BrilFactory.argi("a"),
				                                                       BrilFactory.argi("b")
		                                                       ),
		                                                       new BrilInstructions()
				                                                       .lessThan("cond", "a", "b")
				                                                       .branch("cond", "takeB", "takeA")
				                                                       .label("takeB")
				                                                       .reti("b")
				                                                       .label("takeA")
				                                                       .reti("a")
				                                                       .get()
		                   ))
		);
	}

	@Test
	public void testLoop() {
		assertInstructions(new BrilInstructions()
				                   .constant("v.1", 0)
				                   .constant("v.2", 1)
				                   .jump("while")

				                   .label("while")
				                   .constant("v.3", 1)
				                   .lessThan("v.3", "r.0", "v.3")
				                   .branch("v.3", "exit", "body")

				                   .label("body")
				                   .add("v.3", "v.1", "v.2")
				                   .idi("v.1", "v.2")
				                   .idi("v.2", "v.3")
				                   .constant("v.3", 1)
				                   .sub("r.0", "r.0", "v.3")
				                   .jump("while")

				                   .label("exit")
				                   .idi("r.0", "v.2")
				                   .reti("r.0")
				                   .jump("exit 4")

				                   .label("exit 4")
				                   .get(),
		                   prepare(BrilFactory.createFunctionI("fibonacci", List.of(
				                                                       BrilFactory.argi("n")
		                                                       ),
		                                                       new BrilInstructions()
				                                                       .constant("a", 0)
				                                                       .constant("b", 1)

				                                                       .label("while")
				                                                       .constant("one", 1)
				                                                       .lessThan("cond", "n", "one")
				                                                       .branch("cond", "exit", "body")

				                                                       .label("body")
				                                                       .add("sum", "a", "b")
				                                                       .idi("a", "b")
				                                                       .idi("b", "sum")
				                                                       .constant("one", 1)
				                                                       .sub("n", "n", "one")
				                                                       .jump("while")

				                                                       .label("exit")
				                                                       .reti("b")
				                                                       .get()
		                   ))
		);
	}

	@Test
	public void testAverage() {
		assertInstructions(new BrilInstructions()
				                   .constant("v.2", 0)
				                   .constant("v.1", 0)
				                   .jump("loop")

				                   .label("loop")
				                   .calli("r.0", "getInt", List.of())
				                   .idi("r.0", "r.0")
				                   .constant("v.3", 0)
				                   .lessThan("v.3", "r.0", "v.3")
				                   .branch("v.3", "exit", "body")

				                   .label("body")
				                   .constant("v.3", 1)
				                   .add("v.2", "v.2", "v.3")
				                   .add("v.1", "v.1", "r.0")
				                   .div("r.0", "v.1", "v.2")
				                   .idi("r.0", "r.0")
				                   .printi("r.0")
				                   .jump("loop")

				                   .label("exit")
				                   .jump("exit 4")

				                   .label("exit 4")
				                   .get(),
		                   prepare(BrilFactory.createFunctionV("average", List.of(),
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
		                   )));
	}

	// Utils ==================================================================

	@NotNull
	private static List<BrilNode> prepare(BrilNode function) {
		final BrilPrepareForAsm prepare = new BrilPrepareForAsm("v.", "r.", "s.", 8, 2);
		final BrilNode cfg = prepare.prepare(function);
		final BrilNode functionFromCfg = BrilCfg.flattenBlocks(cfg);
		return BrilFactory.getInstructions(functionFromCfg);
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
