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
		assertTransform(List.of(
				BrilInstructions.constant("a", 1),
				BrilInstructions.constant("b", 1),
				BrilInstructions.constant("c", 1),
				BrilInstructions.print("a")
		), List.of(
				BrilInstructions.constant("a", 1),
				BrilInstructions.id("b", "a"),
				BrilInstructions.id("c", "b"),
				BrilInstructions.print("c")
		));
	}

	@Test
	public void testConstantFolding() {
		assertTransform(List.of(
				BrilInstructions.constant("a", 1),
				BrilInstructions.constant("b", 2),
				BrilInstructions.constant("sum1", 3)
		), List.of(
				BrilInstructions.constant("a", 1),
				BrilInstructions.constant("b", 2),
				BrilInstructions.add("sum1", "a", "b")
		));
	}

	@Test
	public void testBinReplacement() {
		assertTransform(List.of(
				BrilInstructions.constant("b", 2),
				BrilInstructions.add("sum1", "a", "b"),
				BrilInstructions.id("sum2", "sum1")
		), List.of(
				BrilInstructions.constant("b", 2),
				BrilInstructions.add("sum1", "a", "b"),
				BrilInstructions.add("sum2", "a", "b")
		));
	}

	@Test
	public void testSwappedBinReplacement() {
		assertTransform(List.of(
				BrilInstructions.constant("b", 2),
				BrilInstructions.add("sum1", "a", "b"),
				BrilInstructions.id("sum2", "sum1")
		), List.of(
				BrilInstructions.constant("b", 2),
				BrilInstructions.add("sum1", "a", "b"),
				BrilInstructions.add("sum2", "b", "a")
		));
	}

	@Test
	public void testIndirectBinReplacement() {
		assertTransform(List.of(
				BrilInstructions.id("c", "b"),
				BrilInstructions.add("sum1", "a", "b"),
				BrilInstructions.id("sum2", "sum1")
		), List.of(
				BrilInstructions.id("c", "b"),
				BrilInstructions.add("sum1", "a", "b"),
				BrilInstructions.add("sum2", "a", "c")
		));
	}

	@Test
	public void testReassignment() {
		assertTransform(List.of(
				BrilInstructions.constant("one", 1),
				BrilInstructions.add("anext", "a", "one"),
				BrilInstructions.id("a", "b"),
				BrilInstructions.add("anext", "b", "one")
		), List.of(
				BrilInstructions.constant("one", 1),
				BrilInstructions.add("anext", "a", "one"),
				BrilInstructions.id("a", "b"),
				BrilInstructions.add("anext", "a", "one")
		));
	}

	// Utils ==================================================================

	private static void assertTransform(List<BrilNode> expected, List<BrilNode> input) {
		final var t = new BrilCommonSubexpressionElimination();
		final List<BrilNode> result = t.transform(input);
		Assert.assertEquals(expected, result);
	}
}
