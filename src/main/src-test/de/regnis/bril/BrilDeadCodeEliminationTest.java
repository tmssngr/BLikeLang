package de.regnis.bril;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Thomas Singer
 */
public class BrilDeadCodeEliminationTest {

	@Test
	public void testSimple() {
		Assert.assertEquals(List.of(
				                    BrilInstructions.constant("a", 4),
				                    BrilInstructions.constant("b", 2),
				                    BrilInstructions.add("d", "a", "b"),
				                    BrilInstructions.print("d")
		                    ),
		                    BrilDeadCodeElimination.simpleDce(
				                    List.of(
						                    BrilInstructions.constant("a", 4),
						                    BrilInstructions.constant("b", 2),
						                    BrilInstructions.constant("c", 1),
						                    BrilInstructions.add("d", "a", "b"),
						                    BrilInstructions.print("d")
				                    )));

		Assert.assertEquals(List.of(
				                    BrilInstructions.constant("a", 4),
				                    BrilInstructions.constant("b", 2),
				                    BrilInstructions.add("d", "a", "b"),
				                    BrilInstructions.print("d")
		                    ),
		                    BrilDeadCodeElimination.simpleDce(
				                    List.of(
						                    BrilInstructions.constant("a", 4),
						                    BrilInstructions.constant("b", 2),
						                    BrilInstructions.constant("c", 1),
						                    BrilInstructions.add("d", "a", "b"),
						                    BrilInstructions.add("e", "c", "d"),
						                    BrilInstructions.print("d")
				                    )));
	}
}
