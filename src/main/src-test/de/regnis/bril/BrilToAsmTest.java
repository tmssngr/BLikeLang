package de.regnis.bril;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static de.regnis.bril.BrilVarMapping.*;

/**
 * @author Thomas Singer
 */
public class BrilToAsmTest {

	// Accessing ==============================================================

	@Test
	public void testBasicRetValue() {
		final String name = "pi10000";
		Assert.assertEquals(new BrilAsmFactory()
				                    .label(BrilToAsm.label(name))
				                    .iconst(0, 31415)
				                    .jump(BrilToAsm.label(name, 1))
				                    .label(BrilToAsm.label(name, 1))
				                    .ret()
				                    .toLines(),
		                    brilToAsm(BrilFactory.createFunctionI(name, List.of(),
		                                                          new BrilInstructions()
				                                                          .constant("pi10000", 31415)
				                                                          .reti("pi10000")
				                                                          .get()
		                    ))
		);
	}

	@Test
	public void test2ParamRetValue() {
		final String name = "sum";
		Assert.assertEquals(new BrilAsmFactory()
				                    .label(BrilToAsm.label(name))
				                    .iadd(ARG0_REGISTER, ARG1_REGISTER)
				                    .jump(BrilToAsm.label(name, 1))
				                    .label(BrilToAsm.label(name, 1))
				                    .ret()
				                    .toLines(),
		                    brilToAsm(BrilFactory.createFunctionI(name, List.of(BrilFactory.argi("a"), BrilFactory.argi("b")),
		                                                          new BrilInstructions()
				                                                          .add("sum", "a", "b")
				                                                          .reti("sum")
				                                                          .get()
		                    ))
		);
	}

	@Test
	public void testCall() {
		final String name = "main";
		Assert.assertEquals(new BrilAsmFactory()
				                    .label(BrilToAsm.label(name))
				                    .ipush(ARG0_REGISTER)
				                    .ipush(ARG1_REGISTER)
				                    //.constant("value1", 2)
				                    .iconst(ARG0_REGISTER, 2)
				                    //.constant("value2", 3)
				                    .iconst(ARG1_REGISTER, 3)

				                    //.call("result", "sum", List.of("value1", "value2"))
				                    .call(BrilToAsm.label("sum"))

				                    //.print("result")
				                    .call(BrilToAsm.label("print"))
				                    // .label("main exit")
				                    .ipop(ARG1_REGISTER)
				                    .ipop(ARG0_REGISTER)
				                    .ret()
				                    .toLines(),
		                    brilToAsm(BrilFactory.createFunctionV(name, List.of(),
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
		final BrilAsmFactory asm = new BrilAsmFactory();
		final String nameMain = "main";
		final String nameGLOR = "getLeftOrRight";
		BrilToAsm.convertToAsm(BrilFactory.createFunctionI(nameMain, List.of(),
		                                                   new BrilInstructions()
				                                                   .constant("leftOrRight", true)
				                                                   .constant("left", 10)
				                                                   .constant("right", 20)
				                                                   .calli("result",
				                                                          nameGLOR,
				                                                          List.of(BrilFactory.argb("leftOrRight"),
				                                                                  BrilFactory.argi("left"),
				                                                                  BrilFactory.argi("right")))
				                                                   .reti("result")
				                                                   .get()
		), asm);
		BrilToAsm.convertToAsm(BrilInterpreterTest.createGetLeftOrRight(), asm);

		Assert.assertEquals(new BrilAsmFactory()
				                    .label(BrilToAsm.label(nameMain))
				                    .ipush(2)
				                    .ipush(4)
				                    .bconst(0, true)
				                    .iconst(2, 10)
				                    .iconst(4, 20)
				                    .ipush(8)
				                    .call(BrilToAsm.label(nameGLOR))
				                    .ipop(8)

				                    .jump(BrilToAsm.label(nameMain, 1))

				                    .label(BrilToAsm.label(nameMain, 1))
				                    .ipop(4)
				                    .ipop(2)
				                    .ret()

				                    .label(BrilToAsm.label(nameGLOR))
				                    .brElse(ARG0_REGISTER, BrilToAsm.label(nameGLOR, 2))
				                    .jump(BrilToAsm.label(nameGLOR, 1))
				                    .label(BrilToAsm.label(nameGLOR, 1))
				                    .iload(ARG0_REGISTER, ARG1_REGISTER)
				                    .jump(BrilToAsm.label(nameGLOR, 3))

				                    .label(BrilToAsm.label(nameGLOR, 2))
				                    .iloadFromStack(ARG0_REGISTER, FP_REGISTER, 4)
				                    .jump(BrilToAsm.label(nameGLOR, 3))

				                    .label(BrilToAsm.label(nameGLOR, 3))
				                    .ret()
				                    .toLines(),
		                    asm.toLines()
		);
		final BrilAsmInterpreter interpreter = new BrilAsmInterpreter(asm.getCommands(),
		                                                              (name, access) -> false);
		interpreter.run();
		interpreter.iterateRegisters(new BrilAsmInterpreter.ByteConsumer() {
			@Override
			public void consumer(int register, int value) {
				final int expectedValue = switch (register) {
					case 0 -> 0;
					case 1 -> 10;
					default -> BrilAsmInterpreter.UNKNOWN;
				};
				Assert.assertEquals(expectedValue, value);
			}
		});
	}

	@Test
	public void testIfLessThan() {
		final String max = "max";
		Assert.assertEquals(new BrilAsmFactory()
				                    .label(BrilToAsm.label(max))
				                    .ipush(4)
				                    //.lessThan("cond", "a", "b")
				                    .ilt(4, ARG0_REGISTER, ARG1_REGISTER)
				                    //.branch("cond", "takeB", "takeA")
				                    .brElse(4, BrilToAsm.label(max, 2))
				                    .jump(BrilToAsm.label(max, 1))
				                    //.ret("b")
				                    .label(BrilToAsm.label(max, 1))
				                    .iload(ARG0_REGISTER, ARG1_REGISTER)
				                    .jump(BrilToAsm.label(max, 3))

				                    .label(BrilToAsm.label(max, 2))
				                    //.ret("a")
				                    .jump(BrilToAsm.label(max, 3))
				                    .label(BrilToAsm.label(max, 3))
				                    .ipop(4)
				                    .ret()
				                    .toLines(),
		                    brilToAsm(BrilFactory.createFunctionI(max, List.of(
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
	public void testFibonacci() {
		final BrilAsmFactory asm = new BrilAsmFactory();
		BrilToAsm.convertToAsm(BrilInterpreterTest.createFibonacci(), asm);
		final String name = "fibonacci";
		Assert.assertEquals(new BrilAsmFactory()
				                    .label(BrilToAsm.label(name))
				                    .ipush(ARG1_REGISTER)
				                    .ipush(4)
				                    .ipush(6)
				                    //.constant("a", 0)
				                    .iconst(ARG1_REGISTER, 0)
				                    //.constant("b", 1)
				                    .iconst(6, 1)
				                    .jump(BrilToAsm.label(name, 1))

				                    .label(BrilToAsm.label(name, 1))
				                    //.constant("one", 1)
				                    .iconst(4, 1)
				                    //.lessThan("cond", "n", "one")
				                    .ilt(4, ARG0_REGISTER, 4)
				                    //.branch("cond", "exit", "body")
				                    .brIfElse(4, BrilToAsm.label(name, 3), BrilToAsm.label(name, 2))

				                    .label(BrilToAsm.label(name, 2))
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
				                    .jump(BrilToAsm.label(name, 1))

				                    .label(BrilToAsm.label(name, 3))
				                    //.ret("b")
				                    .iload(ARG0_REGISTER, 6)

				                    // .label("fibonacci exit")
				                    .jump(BrilToAsm.label(name, 4))
				                    .label(BrilToAsm.label(name, 4))
				                    .ipop(6)
				                    .ipop(4)
				                    .ipop(ARG1_REGISTER)
				                    .ret()
				                    .toLines(),
		                    asm.toLines()
		);

		final List<BrilAsm> commands = new ArrayList<>();
		commands.add(new BrilAsm.Label("main"));
		commands.add(new BrilAsm.LoadConst16(0, 8));
		commands.addAll(asm.getCommands());
		final BrilAsmInterpreter interpreter = new BrilAsmInterpreter(commands,
		                                                              (name_, access) -> false);
		interpreter.run();
		interpreter.iterateRegisters(new BrilAsmInterpreter.ByteConsumer() {
			@Override
			public void consumer(int register, int value) {
				final int expectedValue = switch (register) {
					case 0 -> 0;
					case 1 -> 34;
					default -> BrilAsmInterpreter.UNKNOWN;
				};
				Assert.assertEquals(expectedValue, value);
			}
		});
	}

	@Test
	public void testAverage() {
		final String name = "average";
		Assert.assertEquals(new BrilAsmFactory()
				                    .label(BrilToAsm.label(name))
				                    .ipush(0)
				                    .ipush(2)
				                    .ipush(4)
				                    .ipush(6)
				                    // .constant("n", 0)
				                    .iconst(6, 0)
				                    // .constant("sum", 0)
				                    .iconst(2, 0)
				                    .jump(BrilToAsm.label(name, 1))

				                    .label(BrilToAsm.label(name, 1))
				                    // .call("value", "getInt", List.of())
				                    .call(BrilToAsm.label("getInt"))
				                    // .constant("zero", 0)
				                    .iconst(4, 0)
				                    // .lessThan("cond", "value", "zero")
				                    .ilt(4, 0, 4)
				                    // .branch("cond", "exit", "body")
				                    .brIfElse(4, BrilToAsm.label(name, 3), BrilToAsm.label(name, 2))

				                    .label(BrilToAsm.label(name, 2))
				                    // .constant("one", 1)
				                    .iconst(4, 1)
				                    // .add("n", "n", "one")
				                    .iadd(6, 4)
				                    // .add("sum", "sum", "value")
				                    .iadd(2, 0)
				                    // .div("average", "sum", "n")
				                    .iload(0, 2)
				                    .idiv(0, 6)
				                    // .print("average")
				                    .call(BrilToAsm.label("print"))
				                    .jump(BrilToAsm.label(name, 1))

				                    .label(BrilToAsm.label(name, 3))
				                    .jump(BrilToAsm.label(name, 4))
				                    .label(BrilToAsm.label(name, 4))
				                    .ipop(6)
				                    .ipop(4)
				                    .ipop(2)
				                    .ipop(0)
				                    .ret()
				                    .toLines(),
		                    brilToAsm(BrilInterpreterTest.createAverage())
		);
	}

	// Utils ==================================================================

	private static List<String> brilToAsm(BrilNode function) {
		final BrilAsmFactory asm = new BrilAsmFactory();
		BrilToAsm.convertToAsm(function, asm);
		return asm.toLines();
	}
}
