package de.regnis.bril;

import org.junit.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

/**
 * @author Thomas Singer
 */
public class BrilAsmSimplifierTest {

	@Test
	public void testFixJumpToNextLabel() {
		final Function<List<BrilCommand>, List<BrilCommand>> transformation = BrilAsmSimplifier.create();

		assertEquals(List.of(new BrilCommand.Label("test"),
		                     BrilCommand.NOP,
		                     BrilCommand.NOP,
		                     BrilCommand.NOP),
		             transformation.apply(List.of(new BrilCommand.Label("test"),
		                                          BrilCommand.NOP,
		                                          new BrilCommand.Jump("a"),
		                                          new BrilCommand.Label("a"),
		                                          BrilCommand.NOP,
		                                          new BrilCommand.Branch(BrilCommand.BranchCondition.Z, "b"),
		                                          new BrilCommand.Label("b"),
		                                          BrilCommand.NOP))
		);
		assertEquals(List.of(new BrilCommand.Label("a"),
		                     new BrilCommand.Jump("a")),
		             transformation.apply(List.of(new BrilCommand.Label("a"),
		                                          new BrilCommand.Jump("a")))
		);
	}

	@Test
	public void testObsoleteLabels() {
		final Function<List<BrilCommand>, List<BrilCommand>> transformation = BrilAsmSimplifier.create();

		assertEquals(List.of(new BrilCommand.Label("test"),
		                     new BrilCommand.Branch(BrilCommand.BranchCondition.Z, "a"),
		                     new BrilCommand.Jump("a"),
		                     BrilCommand.NOP,
		                     new BrilCommand.Label("a")),
		             transformation.apply(List.of(new BrilCommand.Label("test"),
		                                          new BrilCommand.Branch(BrilCommand.BranchCondition.Z, "b"),
		                                          new BrilCommand.Jump("a"),
		                                          BrilCommand.NOP,
		                                          new BrilCommand.Label("a"),
		                                          new BrilCommand.Label("b")))
		);
	}
}
