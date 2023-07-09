package de.regnis.bril;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Thomas Singer
 */
public class BrilCommonSubexpressionEliminationTest {

	// Accessing ==============================================================

	@Test
	public void testCopyPropagation() {
		assertTransform(new BrilInstructions()
				                .constant("a", 1)
				                .constant("b", 1)
				                .constant("c", 1)
				                .printi("a")
				                .get(),
		                new BrilInstructions()
				                .constant("a", 1)
				                .idi("b", "a")
				                .idi("c", "b")
				                .printi("c")
				                .get());
	}

	@Test
	public void testConstantFolding() {
		assertTransform(new BrilInstructions()
				                .constant("a", 1)
				                .constant("b", 2)
				                .constant("sum1", 3)
				                .get(),
		                new BrilInstructions()
				                .constant("a", 1)
				                .constant("b", 2)
				                .add("sum1", "a", "b")
				                .get());
	}

	@Test
	public void testBinReplacement() {
		assertTransform(new BrilInstructions()
				                .constant("b", 2)
				                .add("sum1", "a", "b")
				                .idi("sum2", "sum1")
				                .get(),
		                new BrilInstructions()
				                .constant("b", 2)
				                .add("sum1", "a", "b")
				                .add("sum2", "a", "b")
				                .get());
	}

	@Test
	public void testSwappedBinReplacement() {
		assertTransform(new BrilInstructions()
				                .constant("b", 2)
				                .add("sum1", "a", "b")
				                .idi("sum2", "sum1")
				                .get(),
		                new BrilInstructions()
				                .constant("b", 2)
				                .add("sum1", "a", "b")
				                .add("sum2", "b", "a")
				                .get());
	}

	@Test
	public void testIndirectBinReplacement() {
		assertTransform(new BrilInstructions()
				                .idi("c", "b")
				                .add("sum1", "a", "b")
				                .idi("sum2", "sum1")
				                .get(),
		                new BrilInstructions()
				                .idi("c", "b")
				                .add("sum1", "a", "b")
				                .add("sum2", "a", "c")
				                .get());
	}

	@Test
	public void testReassignment() {
		assertTransform(new BrilInstructions()
				                .constant("one", 1)
				                .add("anext", "a", "one")
				                .idi("a", "b")
				                .add("anext", "b", "one")
				                .get(),
		                new BrilInstructions()
				                .constant("one", 1)
				                .add("anext", "a", "one")
				                .idi("a", "b")
				                .add("anext", "a", "one")
				                .get());
	}

	// Utils ==================================================================

	private static void assertTransform(List<BrilNode> expected, List<BrilNode> input) {
		final var t = new BrilCommonSubexpressionElimination();
		final List<BrilNode> result = t.transform(input);
		Assert.assertEquals(expected, result);
	}
}
