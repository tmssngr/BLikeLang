package de.regnis.bril;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Thomas Singer
 */
public class BrilLoopDetectionTest {

	// Constants ==============================================================

	private static final List<BrilNode> NO_INSTRUCTIONS = List.of();

	// Accessing ==============================================================

	@Test
	public void testSimplest() {
		final List<BrilNode> blocks = List.of(
				BrilCfg.createBlock("loop", NO_INSTRUCTIONS, List.of("loop"), List.of("loop", "exit")),
				BrilCfg.createBlock("exit", NO_INSTRUCTIONS, List.of("loop"), List.of())
		);
		final Map<String, Set<String>> loops = BrilLoopDetection.detectLoopLevels(blocks);
		Assert.assertEquals(Map.of("loop", Set.of("loop")), loops);
	}

	@Test
	public void testSimple() {
		final List<BrilNode> blocks = List.of(
				BrilCfg.createBlock("entry", NO_INSTRUCTIONS, List.of(), List.of("while")),
				BrilCfg.createBlock("while", NO_INSTRUCTIONS, List.of("entry", "body"), List.of("body", "exit")),
				BrilCfg.createBlock("body", NO_INSTRUCTIONS, List.of("while"), List.of("while")),
				BrilCfg.createBlock("exit", NO_INSTRUCTIONS, List.of("while"), List.of())
		);
		final Map<String, Set<String>> loops = BrilLoopDetection.detectLoopLevels(blocks);
		Assert.assertEquals(Map.of("while", Set.of("while", "body")), loops);
	}

	@Test
	public void testIf() {
		final List<BrilNode> blocks = List.of(
				BrilCfg.createBlock("entry", NO_INSTRUCTIONS, List.of(), List.of("then", "else")),
				BrilCfg.createBlock("then", NO_INSTRUCTIONS, List.of("entry"), List.of("exit")),
				BrilCfg.createBlock("else", NO_INSTRUCTIONS, List.of("entry"), List.of("exit")),
				BrilCfg.createBlock("exit", NO_INSTRUCTIONS, List.of("then", "else"), List.of())
		);
		final Map<String, Set<String>> loops = BrilLoopDetection.detectLoopLevels(blocks);
		Assert.assertEquals(Map.of(), loops);
	}

	@Test
	public void testLoopWithIf() {
		final List<BrilNode> blocks = List.of(
				BrilCfg.createBlock("entry", NO_INSTRUCTIONS, List.of(), List.of("loop")),
				BrilCfg.createBlock("loop", NO_INSTRUCTIONS, List.of("entry", "endif"), List.of("body", "exit")),
				BrilCfg.createBlock("body", NO_INSTRUCTIONS, List.of("loop"), List.of("then", "endif")),
				BrilCfg.createBlock("then", NO_INSTRUCTIONS, List.of("body"), List.of("endif")),
				BrilCfg.createBlock("endif", NO_INSTRUCTIONS, List.of("then", "body"), List.of("loop")),
				BrilCfg.createBlock("exit", NO_INSTRUCTIONS, List.of("loop"), List.of())
		);
		final Map<String, Set<String>> loops = BrilLoopDetection.detectLoopLevels(blocks);
		Assert.assertEquals(Map.of("loop", Set.of("loop", "body", "then", "endif")), loops);
	}

	@Test
	public void testNestedLoops() {
		final List<BrilNode> blocks = List.of(
				BrilCfg.createBlock("entry", NO_INSTRUCTIONS, List.of(), List.of("loop")),
				BrilCfg.createBlock("loop", NO_INSTRUCTIONS, List.of("entry", "innerloop"), List.of("innerloop", "exit")),
				BrilCfg.createBlock("innerloop", NO_INSTRUCTIONS, List.of("loop", "innerloop"), List.of("innerloop", "loop")),
				BrilCfg.createBlock("exit", NO_INSTRUCTIONS, List.of("loop"), List.of())
		);
		final Map<String, Set<String>> loops = BrilLoopDetection.detectLoopLevels(blocks);
		Assert.assertEquals(Map.of("loop", Set.of("loop", "innerloop"),
		                           "innerloop", Set.of("innerloop")), loops);
	}
}
