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
		assertEquals(List.of(new BrilAsm.Label("test"),
		                     BrilAsm.NOP,
		                     BrilAsm.NOP,
		                     BrilAsm.NOP),
		             BrilAsmSimplifier.simplify(List.of(new BrilAsm.Label("test"),
		                                                BrilAsm.NOP,
		                                                new BrilAsm.Jump("a"),
		                                                new BrilAsm.Label("a"),
		                                                BrilAsm.NOP,
		                                                new BrilAsm.Branch(BrilAsm.BranchCondition.Z, "b"),
		                                                new BrilAsm.Label("b"),
		                                                BrilAsm.NOP))
		);
		assertEquals(List.of(new BrilAsm.Label("a"),
		                     new BrilAsm.Jump("a")),
		             BrilAsmSimplifier.simplify(List.of(new BrilAsm.Label("a"),
		                                                new BrilAsm.Jump("a")))
		);
	}

	@Test
	public void testObsoleteLabels() {
		assertEquals(List.of(new BrilAsm.Label("test"),
		                     new BrilAsm.Branch(BrilAsm.BranchCondition.Z, "a"),
		                     new BrilAsm.Jump("a"),
		                     BrilAsm.NOP,
		                     new BrilAsm.Label("a")),
		             BrilAsmSimplifier.simplify(List.of(new BrilAsm.Label("test"),
		                                                new BrilAsm.Branch(BrilAsm.BranchCondition.Z, "b"),
		                                                new BrilAsm.Jump("a"),
		                                                BrilAsm.NOP,
		                                                new BrilAsm.Label("a"),
		                                                new BrilAsm.Label("b")))
		);
	}
}
