package de.regnis.bril;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Thomas Singer
 */
public class BrilToAsmTest {

	@Test
	public void testSimple() {
		Assert.assertEquals(new BrilAsm()
				                    .label("main")
				                    .ipush(BrilVarMapping.VAR0_REGISTER)
				                    .ipush(BrilVarMapping.VAR1_REGISTER)
				                    //.constant("value1", 2)
				                    .iconst(BrilVarMapping.VAR0_REGISTER, 2)
				                    //.constant("value2", 3)
				                    .iconst(BrilVarMapping.VAR1_REGISTER, 3)

				                    //.call("result", "sum", List.of("value1", "value2"))
				                    .iload(BrilVarMapping.ARG0_REGISTER, BrilVarMapping.VAR0_REGISTER)
				                    .iload(BrilVarMapping.ARG1_REGISTER, BrilVarMapping.VAR1_REGISTER)
				                    .call("sum")
				                    .iload(BrilVarMapping.VAR1_REGISTER, BrilVarMapping.ARG0_REGISTER)
				                    // .iload(BrilVarMapping.ARG0_REGISTER, BrilVarMapping.VAR1_REGISTER)

				                    //.print("result")
				                    .call("print")
				                    // .label("main exit")
				                    .ipop(BrilVarMapping.VAR1_REGISTER)
				                    .ipop(BrilVarMapping.VAR0_REGISTER)
				                    .ret()
				                    //==========
				                    .label("sum")
				                    //.add("sum", "a", "b")
				                    .iadd(BrilVarMapping.ARG0_REGISTER, BrilVarMapping.ARG1_REGISTER)
				                    //.ret("sum")
				                    // .label("sum exit")
				                    .ret()
				                    .toLines(),
		                    BrilToAsm.convertToAsm(List.of(
				                                           BrilFactory.createFunction("main", "void", List.of(),
				                                                                      new BrilInstructions()
						                                                                      .constant("value1", 2)
						                                                                      .constant("value2", 3)
						                                                                      .call("result", "sum", List.of("value1", "value2"))
						                                                                      .print("result")
						                                                                      .get()
				                                           ),
				                                           BrilFactory.createFunction("sum", "int", List.of(BrilFactory.argument("a", "int"), BrilFactory.argument("b", "int")),
				                                                                      new BrilInstructions()
						                                                                      .add("sum", "a", "b")
						                                                                      .ret("sum")
						                                                                      .get()
				                                           )
		                                           )
		                    )
		);
	}

	@Test
	public void testIfBoolean() {
		Assert.assertEquals(new BrilAsm()
				                    .label("getLeftOrRight")
				                    .brElse(BrilVarMapping.ARG0_REGISTER, "takeRight")
				                    // .jump("takeLeft")
				                    //.ret("left")
				                    // .label("takeLeft")
				                    .iload(BrilVarMapping.ARG0_REGISTER, BrilVarMapping.ARG1_REGISTER)
				                    .jump("getLeftOrRight exit")
				                    .label("takeRight")
				                    //.ret("right")
				                    .iloadFromStack(BrilVarMapping.ARG0_REGISTER, BrilVarMapping.FP_REGISTER, 4)
				                    .label("getLeftOrRight exit")
				                    .ret()
				                    .toLines(),
		                    BrilToAsm.convertToAsm(List.of(
				                                           BrilFactory.createFunction("getLeftOrRight", BrilInstructions.INT, List.of(
						                                                                      BrilFactory.argument("leftOrRight", BrilInstructions.BOOL),
						                                                                      BrilFactory.argument("left", BrilInstructions.INT),
						                                                                      BrilFactory.argument("right", BrilInstructions.INT)
				                                                                      ),
				                                                                      new BrilInstructions()
						                                                                      .branch("leftOrRight", "takeLeft", "takeRight")
						                                                                      .label("takeLeft")
						                                                                      .ret("left")
						                                                                      .label("takeRight")
						                                                                      .ret("right")
						                                                                      .get()
				                                           )
		                                           )
		                    )
		);
	}

	@Test
	public void testIfLessThan() {
		Assert.assertEquals(new BrilAsm()
				                    .label("max")
				                    .ipush(BrilVarMapping.VAR0_REGISTER)
				                    //.lessThan("cond", "a", "b")
				                    .ilt(BrilVarMapping.A_REGISTER, BrilVarMapping.ARG0_REGISTER, BrilVarMapping.ARG1_REGISTER)
				                    .bload(BrilVarMapping.VAR0_REGISTER, BrilVarMapping.A_REGISTER)
				                    //.branch("cond", "takeB", "takeA")
				                    .brElse(BrilVarMapping.VAR0_REGISTER, "takeA")
				                    // .jump("takeB")
				                    //.ret("b")
				                    // .label("takeB")
				                    .iload(BrilVarMapping.ARG0_REGISTER, BrilVarMapping.ARG1_REGISTER)
				                    // .jump("max exit")
				                    .label("takeA")
				                    //.ret("a")
				                    // .label("max exit")
				                    .ipop(BrilVarMapping.VAR0_REGISTER)
				                    .ret()
				                    .toLines(),
		                    BrilToAsm.convertToAsm(List.of(
				                                           BrilFactory.createFunction("max", BrilInstructions.INT, List.of(
						                                                                      BrilFactory.argument("a", BrilInstructions.INT),
						                                                                      BrilFactory.argument("b", BrilInstructions.INT)
				                                                                      ),
				                                                                      new BrilInstructions()
						                                                                      .lessThan("cond", "a", "b")
						                                                                      .branch("cond", "takeB", "takeA")
						                                                                      .label("takeB")
						                                                                      .ret("b")
						                                                                      .label("takeA")
						                                                                      .ret("a")
						                                                                      .get()
				                                           )
		                                           )
		                    )
		);
	}

	@Test
	public void testLoop() {
		Assert.assertEquals(new BrilAsm()
				                    .label("fibonacci")
				                    .ipush(BrilVarMapping.VAR0_REGISTER)
				                    .ipush(BrilVarMapping.VAR1_REGISTER)
				                    .ipush(BrilVarMapping.VAR2_REGISTER)
									//.constant("a", 0)
				                    .iconst(BrilVarMapping.VAR0_REGISTER, 0)
									//.constant("b", 1)
				                    .iconst(BrilVarMapping.VAR1_REGISTER, 1)

									.label("while")
									//.constant("one", 1)
				                    .iconst(BrilVarMapping.VAR2_REGISTER, 1)
									//.lessThan("cond", "n", "one")
				                    .ilt(BrilVarMapping.A_REGISTER, BrilVarMapping.ARG0_REGISTER, BrilVarMapping.VAR2_REGISTER)
				                    .bload(BrilVarMapping.VAR2_REGISTER, BrilVarMapping.A_REGISTER)
									//.branch("cond", "exit", "body")
				                    .brIfElse(BrilVarMapping.VAR2_REGISTER, "exit", "body")

									.label("body")
									//.add("sum", "a", "b")
				                    .iload(BrilVarMapping.VAR2_REGISTER, BrilVarMapping.VAR0_REGISTER)
				                    .iadd(BrilVarMapping.VAR2_REGISTER, BrilVarMapping.VAR1_REGISTER)
									//.id("a", "b")
				                    .iload(BrilVarMapping.VAR0_REGISTER, BrilVarMapping.VAR1_REGISTER)
									//.id("b", "sum")
				                    .iload(BrilVarMapping.VAR1_REGISTER, BrilVarMapping.VAR2_REGISTER)
				                    //.constant("one", 1)
				                    .iconst(BrilVarMapping.VAR2_REGISTER, 1)
				                    //.sub("n", "n", "one")
				                    .isub(BrilVarMapping.ARG0_REGISTER, BrilVarMapping.VAR2_REGISTER)
									//.jump("while")
				                    .jump("while")

									.label("exit")
									//.ret("b")
				                    .iload(BrilVarMapping.ARG0_REGISTER, BrilVarMapping.VAR1_REGISTER)

				                    // .label("fibonacci exit")
				                    .ipop(BrilVarMapping.VAR2_REGISTER)
				                    .ipop(BrilVarMapping.VAR1_REGISTER)
				                    .ipop(BrilVarMapping.VAR0_REGISTER)
				                    .ret()
				                    .toLines(),
		                    BrilToAsm.convertToAsm(List.of(
				                                           BrilFactory.createFunction("fibonacci", BrilInstructions.INT, List.of(
						                                                                      BrilFactory.argument("n", BrilInstructions.INT)
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
						                                                                      .id("a", "b")
						                                                                      .id("b", "sum")
						                                                                      .constant("one", 1)
						                                                                      .sub("n", "n", "one")
						                                                                      .jump("while")

						                                                                      .label("exit")
						                                                                      .ret("b")
						                                                                      .get()
				                                           )
		                                           )
		                    )
		);
	}
}
