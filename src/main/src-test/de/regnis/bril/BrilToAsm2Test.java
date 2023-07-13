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
				                    .label("sum")
				                    .ipush(4)
				                    .iload(4, ARG0_REGISTER)
				                    .iadd(4, ARG1_REGISTER)
				                    .iload(ARG0_REGISTER, 4)
				                    .jump("exit 1")
				                    .label("exit 1")
				                    .ipop(4)
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
/*
				                    .ipush(VAR0_REGISTER)
				                    .ipush(VAR1_REGISTER)
				                    //.constant("value1", 2)
				                    .iconst(VAR0_REGISTER, 2)
				                    //.constant("value2", 3)
				                    .iconst(VAR1_REGISTER, 3)

				                    //.call("result", "sum", List.of("value1", "value2"))
				                    .call("sum")
				                    // .iload(BrilVarMapping.ARG0_REGISTER, BrilVarMapping.VAR1_REGISTER)

				                    //.print("result")
				                    .call("print")
				                    // .label("main exit")
				                    .ipop(VAR1_REGISTER)
				                    .ipop(VAR0_REGISTER)
*/
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
				                    //.lessThan("cond", "a", "b")
/*
				                    .ilt(A_REGISTER, ARG0_REGISTER, ARG1_REGISTER)
				                    .bload(VAR0_REGISTER, A_REGISTER)
				                    //.branch("cond", "takeB", "takeA")
				                    .brElse(VAR0_REGISTER, "takeA")
				                    .jump("takeB")
				                    //.ret("b")
				                    .label("takeB")
				                    .iload(ARG0_REGISTER, ARG1_REGISTER)
				                    .jump("exit 3")

				                    .label("takeA")
				                    //.ret("a")
				                    .jump("exit 3")
				                    .label("exit 3")
				                    .ipop(VAR0_REGISTER)
*/
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
/*
				                    .ipush(VAR0_REGISTER)
				                    .ipush(VAR1_REGISTER)
				                    .ipush(VAR2_REGISTER)
				                    //.constant("a", 0)
				                    .iconst(VAR0_REGISTER, 0)
				                    //.constant("b", 1)
				                    .iconst(VAR1_REGISTER, 1)
				                    .jump("while")

				                    .label("while")
				                    //.constant("one", 1)
				                    .iconst(VAR2_REGISTER, 1)
				                    //.lessThan("cond", "n", "one")
				                    .ilt(A_REGISTER, ARG0_REGISTER, VAR2_REGISTER)
				                    .bload(VAR2_REGISTER, A_REGISTER)
				                    //.branch("cond", "exit", "body")
				                    .brIfElse(VAR2_REGISTER, "exit", "body")

				                    .label("body")
				                    //.add("sum", "a", "b")
				                    .iload(VAR2_REGISTER, VAR0_REGISTER)
				                    .iadd(VAR2_REGISTER, VAR1_REGISTER)
				                    //.id("a", "b")
				                    .iload(VAR0_REGISTER, VAR1_REGISTER)
				                    //.id("b", "sum")
				                    .iload(VAR1_REGISTER, VAR2_REGISTER)
				                    //.constant("one", 1)
				                    .iconst(VAR2_REGISTER, 1)
				                    //.sub("n", "n", "one")
				                    .isub(ARG0_REGISTER, VAR2_REGISTER)
				                    //.jump("while")
				                    .jump("while")

				                    .label("exit")
				                    //.ret("b")
				                    .iload(ARG0_REGISTER, VAR1_REGISTER)

				                    // .label("fibonacci exit")
				                    .jump("exit 4")
				                    .label("exit 4")
				                    .ipop(VAR2_REGISTER)
				                    .ipop(VAR1_REGISTER)
				                    .ipop(VAR0_REGISTER)
*/
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
				                    .allocSpace(2)
/*
				                    .ipush(VAR0_REGISTER)
				                    .ipush(VAR1_REGISTER)
				                    .ipush(VAR2_REGISTER)
				                    //    .constant("n", 0)
				                    .iconst(VAR0_REGISTER, 0)
				                    //    .constant("sum", 0)
				                    .iconst(VAR1_REGISTER, 0)
				                    .jump("loop")

				                    .label("loop")
				                    //    .call("value", "getInt", List.of())
				                    .call("getInt")
				                    //    .constant("zero", 0)
				                    .iconst(A_REGISTER, 0)
				                    //    .lessThan("cond", "value", "zero")
				                    .iloadFromStack(B_REGISTER, FP_REGISTER, 6)
				                    .ilt(A_REGISTER, VAR2_REGISTER, B_REGISTER)
				                    //    .branch("cond", "exit", "body")
				                    .bload(VAR2_REGISTER, A_REGISTER)
				                    .brIfElse(VAR2_REGISTER, "exit", "body")

				                    .label("body")
				                    //    .constant("one", 1)
				                    .iconst(VAR2_REGISTER, 1)
				                    //    .add("n", "n", "one")
				                    .iadd(VAR0_REGISTER, VAR2_REGISTER)
				                    //    .add("sum", "sum", "value")
				                    .iloadFromStack(B_REGISTER, FP_REGISTER, 6)
				                    .iadd(VAR1_REGISTER, B_REGISTER)
				                    //    .div("average", "sum", "n")
				                    .iload(VAR2_REGISTER, VAR1_REGISTER)
				                    .istoreToStack(VAR0_REGISTER, FP_REGISTER, 6)
				                    .call("div")
				                    .istoreToStack(VAR2_REGISTER, FP_REGISTER, 6)
				                    //    .print("average")
				                    .iloadFromStack(VAR2_REGISTER, FP_REGISTER, 6)
				                    .call("print")
				                    .jump("loop")

				                    .label("exit")
				                    .jump("exit 4")
				                    .label("exit 4")
				                    .ipop(VAR2_REGISTER)
				                    .ipop(VAR1_REGISTER)
				                    .ipop(VAR0_REGISTER)
*/
				                    .freeSpace(2)
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
