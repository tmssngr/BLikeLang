package de.regnis.bril;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static de.regnis.bril.BrilVarMapping2.*;

/**
 * @author Thomas Singer
 */
public class BrilToAsm2Test {

	// Accessing ==============================================================

	@Test
	public void testBasicRetValue() {
		Assert.assertEquals(new BrilAsm()
				                    .label("pi10000")
				                    .iconst(0, 31415)
				                    .jump("exit 1")
				                    .label("exit 1")
				                    .ret()
				                    .toLines(),
		                    brilToAsm(BrilFactory.createFunctionI("pi10000", List.of(),
		                                                          new BrilInstructions()
				                                                          .constant("pi10000", 31415)
				                                                          .reti("pi10000")
				                                                          .get()
		                    ))
		);
	}

	@Test
	public void test2ParamRetValue() {
		Assert.assertEquals(new BrilAsm()
				                    .label("sum")
				                    .iadd(ARG0_REGISTER, ARG1_REGISTER)
				                    .jump("exit 1")
				                    .label("exit 1")
				                    .ret()
				                    .toLines(),
		                    brilToAsm(BrilFactory.createFunctionI("sum", List.of(BrilFactory.argi("a"), BrilFactory.argi("b")),
		                                                          new BrilInstructions()
				                                                          .add("sum", "a", "b")
				                                                          .reti("sum")
				                                                          .get()
		                    ))
		);
	}

	@Test
	public void testCall() {
		Assert.assertEquals(new BrilAsm()
				                    .label("main")
				                    .ipush(ARG0_REGISTER)
				                    .ipush(ARG1_REGISTER)
				                    //.constant("value1", 2)
				                    .iconst(ARG0_REGISTER, 2)
				                    //.constant("value2", 3)
				                    .iconst(ARG1_REGISTER, 3)

				                    //.call("result", "sum", List.of("value1", "value2"))
				                    .call("sum")

				                    //.print("result")
				                    .call("print")
				                    // .label("main exit")
				                    .ipop(ARG1_REGISTER)
				                    .ipop(ARG0_REGISTER)
				                    .ret()
				                    .toLines(),
		                    brilToAsm(BrilFactory.createFunctionV("main", List.of(),
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
		Assert.assertEquals(new BrilAsm()
				                    .label("getLeftOrRight")
				                    .brElse(ARG0_REGISTER, "takeRight")
				                    .jump("takeLeft")
				                    .label("takeLeft")
				                    .iload(ARG0_REGISTER, ARG1_REGISTER)
				                    .jump("exit 3")

				                    .label("takeRight")
				                    .iloadFromStack(ARG0_REGISTER, FP_REGISTER, 4)
				                    .jump("exit 3")

				                    .label("exit 3")
				                    .ret()
				                    .toLines(),
		                    brilToAsm(BrilFactory.createFunctionI("getLeftOrRight", List.of(
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
		Assert.assertEquals(new BrilAsm()
				                    .label("max")
				                    .ipush(4)
				                    //.lessThan("cond", "a", "b")
				                    .ilt(4, ARG0_REGISTER, ARG1_REGISTER)
				                    //.branch("cond", "takeB", "takeA")
				                    .brElse(4, "takeA")
				                    .jump("takeB")
				                    //.ret("b")
				                    .label("takeB")
				                    .iload(ARG0_REGISTER, ARG1_REGISTER)
				                    .jump("exit 3")

				                    .label("takeA")
				                    //.ret("a")
				                    .jump("exit 3")
				                    .label("exit 3")
				                    .ipop(4)
				                    .ret()
				                    .toLines(),
		                    brilToAsm(BrilFactory.createFunctionI("max", List.of(
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
		Assert.assertEquals(new BrilAsm()
				                    .label("fibonacci")
				                    .ipush(ARG1_REGISTER)
				                    .ipush(4)
				                    .ipush(6)
				                    //.constant("a", 0)
				                    .iconst(ARG1_REGISTER, 0)
				                    //.constant("b", 1)
				                    .iconst(6, 1)
				                    .jump("while")

				                    .label("while")
				                    //.constant("one", 1)
				                    .iconst(4, 1)
				                    //.lessThan("cond", "n", "one")
				                    .ilt(4, ARG0_REGISTER, 4)
				                    //.branch("cond", "exit", "body")
				                    .brIfElse(4, "exit", "body")

				                    .label("body")
				                    //.add("sum", "a", "b")
				                    .iload(4, ARG1_REGISTER)
				                    .iadd(4, 6)
				                    //.id("a", "b")
				                    .iload(ARG1_REGISTER, 6)
				                    //.id("b", "sum")
				                    .iload(6, 4)
				                    //.constant("one", 1)
				                    .iconst(4, 1)
				                    //.sub("n", "n", "one")
				                    .isub(ARG0_REGISTER, 4)
				                    //.jump("while")
				                    .jump("while")

				                    .label("exit")
				                    //.ret("b")
				                    .iload(ARG0_REGISTER, 6)

				                    // .label("fibonacci exit")
				                    .jump("exit 4")
				                    .label("exit 4")
				                    .ipop(6)
				                    .ipop(4)
				                    .ipop(ARG1_REGISTER)
				                    .ret()
				                    .toLines(),
		                    brilToAsm(BrilFactory.createFunctionI("fibonacci", List.of(
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
		Assert.assertEquals(new BrilAsm()
				                    .label("average")
				                    .ipush(0)
				                    .ipush(2)
				                    .ipush(4)
				                    //    .constant("n", 0)
				                    .iconst(4, 0)
				                    //    .constant("sum", 0)
				                    .iconst(0, 0)
				                    .jump("loop")

				                    .label("loop")
				                    //    .call("value", "getInt", List.of())
				                    .call("getInt")
				                    //    .constant("zero", 0)
				                    .iconst(2, 0)
				                    //    .lessThan("cond", "value", "zero")
				                    .ilt(2, 0, 2)
				                    //    .branch("cond", "exit", "body")
				                    .brIfElse(2, "exit", "body")

				                    .label("body")
				                    //    .constant("one", 1)
				                    .iconst(2, 1)
				                    //    .add("n", "n", "one")
				                    .iadd(4, 2)
				                    //    .add("sum", "sum", "value")
				                    .iadd(0, 0) // ****************************************
				                    //    .div("average", "sum", "n")
				                    .idiv(0, 4)
				                    //    .print("average")
				                    .call("print")
				                    .jump("loop")

				                    .label("exit")
				                    .jump("exit 4")
				                    .label("exit 4")
				                    .ipop(4)
				                    .ipop(2)
				                    .ipop(0)
				                    .ret()
				                    .toLines(),
		                    brilToAsm(BrilFactory.createFunctionV("average", List.of(),
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
		                    ))
		);
	}

	// Utils ==================================================================

	private static List<String> brilToAsm(BrilNode function) {
		final BrilAsm asm = new BrilAsm();
		BrilToAsm2.convertToAsm(function, asm);
		return asm.toLines();
	}
}
