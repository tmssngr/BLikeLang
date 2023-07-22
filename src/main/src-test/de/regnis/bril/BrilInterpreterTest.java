package de.regnis.bril;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Thomas Singer
 */
public class BrilInterpreterTest {

	// Static =================================================================

	@NotNull
	public static BrilNode createGetLeftOrRight() {
		return BrilFactory.createFunctionI("getLeftOrRight", List.of(
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
		);
	}

	public static void testGetLeftOrRight(BrilNode function) {
		final BrilInterpreter interpreter = new BrilInterpreter();
		assertEquals(0, interpreter.run(function, List.of(true, 0, 1)));
		assertEquals(1, interpreter.run(function, List.of(false, 0, 1)));
		assertEquals(-1000, interpreter.run(function, List.of(true, -1000, 2000)));
		assertEquals(2000, interpreter.run(function, List.of(false, -1000, 2000)));
	}

	@NotNull
	public static BrilNode createFibonacci() {
		return BrilFactory.createFunctionI("fibonacci", List.of(
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
		);
	}

	public static void testFibonacci(BrilNode function) {
		final BrilInterpreter interpreter = new BrilInterpreter();
		assertEquals(1, interpreter.run(function, List.of(0)));
		assertEquals(1, interpreter.run(function, List.of(1)));
		assertEquals(2, interpreter.run(function, List.of(2)));
		assertEquals(3, interpreter.run(function, List.of(3)));
		assertEquals(5, interpreter.run(function, List.of(4)));
		assertEquals(8, interpreter.run(function, List.of(5)));
		assertEquals(13, interpreter.run(function, List.of(6)));
		assertEquals(21, interpreter.run(function, List.of(7)));
		assertEquals(34, interpreter.run(function, List.of(8)));
	}

	@NotNull
	public static BrilNode createAverage() {
		return BrilFactory.createFunctionV("average", List.of(),
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
	}

	public static void testAverage(BrilNode function) {
		assertNull(new BrilInterpreter(new TestCallSupport()
				                               .add("getInt", List.of(), -1)
		).run(function, List.of()));

		assertNull(new BrilInterpreter(new TestCallSupport()
				                               .add("getInt", List.of(), 1)
				                               .add("print", List.of(1), null)
				                               .add("getInt", List.of(), -1)
		).run(function, List.of()));

		assertNull(new BrilInterpreter(new TestCallSupport()
				                               .add("getInt", List.of(), 1)
				                               .add("print", List.of(1), null)
				                               .add("getInt", List.of(), 10)
				                               .add("print", List.of(5), null)
				                               .add("getInt", List.of(), 0)
				                               .add("print", List.of(3), null)
				                               .add("getInt", List.of(), -1)
		).run(function, List.of()));
	}

	@NotNull
	public static BrilNode getWeekDayFunction() {
		return BrilFactory.createFunctionI("getWeekDay", List.of(
				                                   BrilFactory.argi("dayInMonth"),
				                                   BrilFactory.argi("month"),
				                                   BrilFactory.argi("year")
		                                   ),
		                                   new BrilInstructions()
				                                   .constant("hundred", 100)
				                                   .div("h", "year", "hundred")
				                                   .constant("fourhundred", 400)
				                                   .div("v", "year", "fourhundred")
				                                   .constant("four", 4)
				                                   .div("z", "year", "four")
				                                   .add("z", "z", "year")
				                                   .sub("z", "z", "h")
				                                   .add("z", "z", "v")
				                                   .constant("one", 1)
				                                   .add("z", "z", "one")
				                                   .constant("seven", 7)
				                                   .mod("z", "z", "seven")
				                                   .constant("four", 4)
				                                   .mod("i", "year", "four")
				                                   .constant("hundred", 100)
				                                   .mod("h", "year", "hundred")
				                                   .constant("fourhundred", 400)
				                                   .mod("v", "year", "fourhundred")
				                                   .constant("zero", 0)
				                                   .equal("cond", "h", "zero")
				                                   .branch("cond", "if1", "endif1")
				                                   .label("if1")
				                                   .constant("one", 1)
				                                   .add("i", "i", "one")
				                                   .label("endif1")
				                                   .constant("zero", 0)
				                                   .equal("cond", "v", "zero")
				                                   .branch("cond", "if2", "endif2")
				                                   .label("if2")
				                                   .constant("one", 1)
				                                   .sub("i", "i", "one")
				                                   .label("endif2")
				                                   .constant("d", -30)
				                                   .constant("b", 1)
				                                   .constant("a", 0)
				                                   .constant("zero", 0)
				                                   .greaterThan("cond", "i", "zero")
				                                   .branch("cond", "if3", "endif3")
				                                   .label("if3")
				                                   .constant("a", 1)
				                                   .label("endif3")
				                                   .constant("two", 2)
				                                   .greaterThan("cond", "month", "two")
				                                   .branch("cond", "if4", "endif4")
				                                   .label("if4")
				                                   .constant("one", 1)
				                                   .sub("d", "d", "one")
				                                   .sub("d", "d", "a")
				                                   .label("endif4")
				                                   .constant("eight", 8)
				                                   .greaterThan("cond", "month", "eight")
				                                   .branch("cond", "if5", "endif5")
				                                   .label("if5")
				                                   .constant("b", 2)
				                                   .label("endif5")
				                                   .label("while")
				                                   .add("temp", "month", "b")
				                                   .constant("two", 2)
				                                   .mod("temp", "temp", "two")
				                                   .constant("thirty", 30)
				                                   .add("d", "d", "thirty")
				                                   .add("d", "d", "temp")
				                                   .constant("one", 1)
				                                   .sub("month", "month", "one")
				                                   .lessThan("cond", "month", "one")
				                                   .branch("cond", "end", "while")
				                                   .label("end")
				                                   .add("d", "d", "dayInMonth")
				                                   .add("d", "d", "z")
				                                   .add("d", "d", "a")
				                                   .constant("three", 3)
				                                   .add("d", "d", "three")
				                                   .constant("seven", 7)
				                                   .mod("d", "d", "seven")
				                                   .reti("d")
				                                   .get()
		);
	}

	// Accessing ==============================================================

	@Test
	public void testIfBoolean() {
		testGetLeftOrRight(createGetLeftOrRight());
	}

	@Test
	public void testFibonacci() {
		testFibonacci(createFibonacci());
	}

	@Test
	public void testAverage() {
		testAverage(createAverage());
	}

	@Test
	public void testWeekDay() {
		final BrilNode function = getWeekDayFunction();
		final BrilInterpreter interpreter = new BrilInterpreter();
		assertEquals(0, interpreter.run(function, List.of(17, 7, 2023))); // monday
		assertEquals(5, interpreter.run(function, List.of(17, 6, 2023))); // saturday
		assertEquals(0, interpreter.run(function, List.of(24, 6, 1974))); // monday
	}

	// Inner Classes ==========================================================

	private static class TestCallSupport extends BrilInterpreter.CallSupport {
		private final List<ExpectedCall> expectedCalls = new ArrayList<>();

		private int index;

		public TestCallSupport() {
		}

		@Nullable
		@Override
		public Object call(String name, List<Object> arguments) {
			if (index == expectedCalls.size()) {
				throw new BrilInterpreter.InterpretingFailedException("No (further) method call expected. Got " + name + "(" + arguments + ")");
			}

			final ExpectedCall expectedCall = expectedCalls.get(index);
			index++;

			if (!expectedCall.name.equals(name)
					|| !expectedCall.arguments.equals(arguments)) {
				throw new BrilInterpreter.InterpretingFailedException("Expected call " + expectedCall.name + " (" + expectedCall.arguments
						                                                      + "), but got " + name + "(" + arguments + ")");
			}

			return expectedCall.result;
		}

		@Override
		public void beforeRun() {
			index = 0;
		}

		@Override
		public void afterRun() {
			if (index < expectedCalls.size()) {
				final ExpectedCall expectedCall = expectedCalls.get(index);
				throw new BrilInterpreter.InterpretingFailedException("Expected more calls: " + expectedCall.name + "(" + expectedCall.arguments + ")");
			}
		}

		public TestCallSupport add(String name, List<Object> arguments, @Nullable Object result) {
			expectedCalls.add(new ExpectedCall(name, arguments, result));
			return this;
		}

		private record ExpectedCall(String name, List<Object> arguments, @Nullable Object result) {
		}
	}
}
