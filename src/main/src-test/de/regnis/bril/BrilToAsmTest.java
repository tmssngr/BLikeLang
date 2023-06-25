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
				                    //.constant("value1", 2)
				                    .iconst(4, 2)
				                    //.constant("value2", 3)
				                    .iconst(6, 3)

				                    //.call("result", "sum", List.of("value1", "value2"))
				                    .iload(10, 4)
				                    .iload(12, 6)
				                    .call("sum")
				                    .iload(8, 10)

				                    //.print("result")
				                    //.iload(10, 8)
				                    //.call("print")
				                    //.ret()
				                    .jump("print")
				                    //==========
				                    .label("sum")
				                    //.add("sum", "a", "b")
				                    .iload(4, 10)
				                    .iadd(4, 12)
				                    //.ret("sum")
				                    .iload(10, 4)
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
				                    .brElse(10, "takeRight")
				                    //.ret("left")
				                    .iload(10, 12)
				                    .jump("getLeftOrRight exit")
				                    .label("takeRight")
				                    //.ret("right")
				                    .iloadFromStack(10, 14, 2)
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
				                    //.lessThan("cond", "a", "b")
				                    .ilt(0, 10, 12)
				                    .bload(4, 0)
				                    //.branch("cond", "takeB", "takeA")
				                    .brElse(4, "takeA")
				                    //.ret("b")
				                    .iload(10, 12)
				                    .label("takeA")
				                    //.ret("a")
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
				                    .allocSpace(6)
									//.constant("a", 0)
				                    .iconst(4, 0)
									//.constant("b", 1)
				                    .iconst(6, 1)

									.label("while")
									//.constant("one", 1)
				                    .iconst(8, 1)
									//.lessThan("cond", "n", "one")
				                    .ilt(0, 10, 8)
				                    .bstoreToStack(0, 14, 0)
									//.branch("cond", "exit", "body")
				                    .bloadFromStack(0, 14, 0)
				                    .brIfElse(0, "exit", "body")

									.label("body")
									//.add("sum", "a", "b")
				                    .iload(0, 4)
				                    .iadd(0, 6)
				                    .istoreToStack(0, 14, 2)
									//.id("a", "b")
				                    .iload(4, 6)
									//.id("b", "sum")
				                    .iloadFromStack(6, 14, 2)
				                    //.constant("one", 1)
				                    .iconst(8, 1)
				                    //.sub("n", "n", "one")
				                    .isub(10, 8)
									//.jump("while")
				                    .jump("while")

									.label("exit")
									//.ret("b")
				                    .iload(10, 6)

				                    //.label("fibonacci exit")
				                    .freeSpace(6)
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
