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

	// Accessing ==============================================================

	@Test
	public void testFibonacci() {
		final BrilNode function = createFibonacci();
		final BrilInterpreter interpreter = new BrilInterpreter();
		assertEquals(1, interpreter.run(function, List.of(0)));
		assertEquals(1, interpreter.run(function, List.of(1)));
		assertEquals(2, interpreter.run(function, List.of(2)));
		assertEquals(3, interpreter.run(function, List.of(3)));
		assertEquals(5, interpreter.run(function, List.of(4)));
		assertEquals(8, interpreter.run(function, List.of(5)));
		assertEquals(13, interpreter.run(function, List.of(6)));
	}

	@Test
	public void testIfBoolean() {
		final BrilNode function = createGetLeftOrRight();
		final BrilInterpreter interpreter = new BrilInterpreter();
		assertEquals(0, interpreter.run(function, List.of(true, 0, 1)));
		assertEquals(1, interpreter.run(function, List.of(false, 0, 1)));
		assertEquals(-1000, interpreter.run(function, List.of(true, -1000, 2000)));
		assertEquals(2000, interpreter.run(function, List.of(false, -1000, 2000)));
	}

	@Test
	public void testAverage() {
		final BrilNode function = createAverage();
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
