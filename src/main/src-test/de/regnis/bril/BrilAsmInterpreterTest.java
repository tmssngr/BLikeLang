package de.regnis.bril;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Thomas Singer
 */
public class BrilAsmInterpreterTest {

	// Accessing ==============================================================

	@Test
	public void testSimple() {
		run(List.of(
				    new BrilAsm.Label("main"),
				    new BrilAsm.LoadConst8(0, 10),
				    BrilAsm.RET
		    ), 3, 2,
		    (register, value) -> {
			    //noinspection SwitchStatementWithTooFewBranches
			    final int expectedValue = switch (register) {
				    case 0 -> 10;
				    default -> BrilAsmInterpreter.UNKNOWN;
			    };
			    Assert.assertEquals(expectedValue, value);
		    });

		run(List.of(
				    new BrilAsm.Label("main"),
				    new BrilAsm.LoadConst8(0, 10),
				    new BrilAsm.LoadConst8(1, 6),
				    new BrilAsm.AddConst16(0, 4000),
				    BrilAsm.RET
		    ), 5, 4,
		    (register, value) -> {
			    final int expectedValue = switch (register) {
				    case 0 -> 25;
				    case 1 -> 166;
				    default -> BrilAsmInterpreter.UNKNOWN;
			    };
			    Assert.assertEquals(expectedValue, value);
		    });

		run(List.of(
				    new BrilAsm.Label("main"),
				    new BrilAsm.Call("test"),
				    BrilAsm.RET,

				    new BrilAsm.Label("test"),
				    new BrilAsm.LoadConst8(2, 10),
				    BrilAsm.RET
		    ), 3, 4,
		    (register, value) -> {
			    //noinspection SwitchStatementWithTooFewBranches
			    final int expectedValue = switch (register) {
				    case 2 -> 10;
				    default -> BrilAsmInterpreter.UNKNOWN;
			    };
			    Assert.assertEquals(expectedValue, value);
		    });

		run(List.of(
				    new BrilAsm.Label("main"),
				    new BrilAsm.Push8(0),
				    new BrilAsm.Push8(1),
				    new BrilAsm.LoadConst16(0, 1000),
				    new BrilAsm.Pop8(1),
				    new BrilAsm.Pop8(0),
				    BrilAsm.RET
		    ), 7, 6,
		    (register, value) ->
				    Assert.assertEquals(BrilAsmInterpreter.UNKNOWN, value));
	}

	@Test
	public void testFlags() {
		runCpFlags(false, false, false, true, 0, 0);
		runCpFlags(false, false, false, true, 1, 1);
		runCpFlags(false, false, false, true, 127, 127);
		runCpFlags(false, false, false, true, 128, 128);
		runCpFlags(false, false, false, true, 255, 255);
		runCpFlags(true, true, false, false, 10, 11);
		runCpFlags(false, false, false, false, 11, 10);
		runCpFlags(false, true, false, false, 255, 1);
		runCpFlags(true, false, false, false, 1, 255);
		runCpFlags(true, true, false, false, 0, 127);
		runCpFlags(true, true, true, false, 0, 128);
		runCpFlags(false, true, false, false, 128, 0);
		runCpFlags(false, false, true, false, 128, 1);
	}

	@Test
	public void testBranchZ() {
		testCpBranch(10, 11, BrilAsm.BranchCondition.C, BrilAsm.BranchCondition.NC);
		testCpBranch(11, 10, BrilAsm.BranchCondition.NC, BrilAsm.BranchCondition.C);

		testCpBranch(10, 11, BrilAsm.BranchCondition.MI, BrilAsm.BranchCondition.PL);
		testCpBranch(11, 10, BrilAsm.BranchCondition.PL, BrilAsm.BranchCondition.MI);

		testCpBranch(0, 128, BrilAsm.BranchCondition.OV, BrilAsm.BranchCondition.NOV);
		testCpBranch(128, 0, BrilAsm.BranchCondition.NOV, BrilAsm.BranchCondition.OV);

		testCpBranch(10, 11, BrilAsm.BranchCondition.NZ, BrilAsm.BranchCondition.Z);
		testCpBranch(10, 10, BrilAsm.BranchCondition.Z, BrilAsm.BranchCondition.NZ);

		testCpBranch(10, 11, BrilAsm.BranchCondition.LT, BrilAsm.BranchCondition.GE);
		testCpBranch(10, 11, BrilAsm.BranchCondition.LT, BrilAsm.BranchCondition.UGE);
		testCpBranch(10, 11, BrilAsm.BranchCondition.LT, BrilAsm.BranchCondition.EQ);
		testCpBranch(10, 11, BrilAsm.BranchCondition.LT, BrilAsm.BranchCondition.GT);
		testCpBranch(10, 11, BrilAsm.BranchCondition.LT, BrilAsm.BranchCondition.UGT);

		testCpBranch(255, 1, BrilAsm.BranchCondition.LT, BrilAsm.BranchCondition.GE);
		testCpBranch(255, 1, BrilAsm.BranchCondition.LT, BrilAsm.BranchCondition.EQ);
		testCpBranch(255, 1, BrilAsm.BranchCondition.LT, BrilAsm.BranchCondition.GT);

		testCpBranch(10, 11, BrilAsm.BranchCondition.LE, BrilAsm.BranchCondition.GE);
		testCpBranch(10, 11, BrilAsm.BranchCondition.LE, BrilAsm.BranchCondition.UGE);
		testCpBranch(10, 11, BrilAsm.BranchCondition.LE, BrilAsm.BranchCondition.EQ);
		testCpBranch(10, 11, BrilAsm.BranchCondition.LE, BrilAsm.BranchCondition.GT);
		testCpBranch(10, 11, BrilAsm.BranchCondition.LE, BrilAsm.BranchCondition.UGT);

		testCpBranch(255, 1, BrilAsm.BranchCondition.LE, BrilAsm.BranchCondition.GE);
		testCpBranch(255, 1, BrilAsm.BranchCondition.LE, BrilAsm.BranchCondition.EQ);
		testCpBranch(255, 1, BrilAsm.BranchCondition.LE, BrilAsm.BranchCondition.GT);

		testCpBranch(10, 10, BrilAsm.BranchCondition.LE, BrilAsm.BranchCondition.LT);
		testCpBranch(10, 10, BrilAsm.BranchCondition.LE, BrilAsm.BranchCondition.ULT);
		testCpBranch(10, 10, BrilAsm.BranchCondition.LE, BrilAsm.BranchCondition.NEQ);
		testCpBranch(10, 10, BrilAsm.BranchCondition.LE, BrilAsm.BranchCondition.GT);
		testCpBranch(10, 10, BrilAsm.BranchCondition.LE, BrilAsm.BranchCondition.UGT);

		testCpBranch(128, 128, BrilAsm.BranchCondition.LE, BrilAsm.BranchCondition.LT);
		testCpBranch(128, 128, BrilAsm.BranchCondition.LE, BrilAsm.BranchCondition.ULT);
		testCpBranch(128, 128, BrilAsm.BranchCondition.LE, BrilAsm.BranchCondition.NEQ);
		testCpBranch(128, 128, BrilAsm.BranchCondition.LE, BrilAsm.BranchCondition.GT);
		testCpBranch(128, 128, BrilAsm.BranchCondition.LE, BrilAsm.BranchCondition.UGT);

		testCpBranch(11, 10, BrilAsm.BranchCondition.GE, BrilAsm.BranchCondition.LT);
		testCpBranch(11, 10, BrilAsm.BranchCondition.GE, BrilAsm.BranchCondition.ULT);
		testCpBranch(11, 10, BrilAsm.BranchCondition.GE, BrilAsm.BranchCondition.EQ);

		testCpBranch(11, 255, BrilAsm.BranchCondition.GE, BrilAsm.BranchCondition.LT);
		testCpBranch(11, 255, BrilAsm.BranchCondition.GE, BrilAsm.BranchCondition.EQ);

		testCpBranch(10, 10, BrilAsm.BranchCondition.GE, BrilAsm.BranchCondition.LT);
		testCpBranch(10, 10, BrilAsm.BranchCondition.GE, BrilAsm.BranchCondition.ULT);
		testCpBranch(10, 10, BrilAsm.BranchCondition.GE, BrilAsm.BranchCondition.NEQ);
		testCpBranch(10, 10, BrilAsm.BranchCondition.GE, BrilAsm.BranchCondition.GT);
		testCpBranch(10, 10, BrilAsm.BranchCondition.GE, BrilAsm.BranchCondition.UGT);

		testCpBranch(128, 128, BrilAsm.BranchCondition.GE, BrilAsm.BranchCondition.LT);
		testCpBranch(128, 128, BrilAsm.BranchCondition.GE, BrilAsm.BranchCondition.ULT);
		testCpBranch(128, 128, BrilAsm.BranchCondition.GE, BrilAsm.BranchCondition.NEQ);
		testCpBranch(128, 128, BrilAsm.BranchCondition.GE, BrilAsm.BranchCondition.GT);
		testCpBranch(128, 128, BrilAsm.BranchCondition.GE, BrilAsm.BranchCondition.UGT);

		testCpBranch(11, 10, BrilAsm.BranchCondition.GT, BrilAsm.BranchCondition.LT);
		testCpBranch(11, 10, BrilAsm.BranchCondition.GT, BrilAsm.BranchCondition.ULT);
		testCpBranch(11, 10, BrilAsm.BranchCondition.GT, BrilAsm.BranchCondition.LE);
		testCpBranch(11, 10, BrilAsm.BranchCondition.GT, BrilAsm.BranchCondition.ULE);
		testCpBranch(11, 10, BrilAsm.BranchCondition.GT, BrilAsm.BranchCondition.EQ);

		testCpBranch(10, 11, BrilAsm.BranchCondition.ULT, BrilAsm.BranchCondition.EQ);
		testCpBranch(10, 11, BrilAsm.BranchCondition.ULE, BrilAsm.BranchCondition.EQ);
		testCpBranch(10, 10, BrilAsm.BranchCondition.ULE, BrilAsm.BranchCondition.NEQ);
		testCpBranch(10, 10, BrilAsm.BranchCondition.UGE, BrilAsm.BranchCondition.NEQ);
		testCpBranch(11, 10, BrilAsm.BranchCondition.UGE, BrilAsm.BranchCondition.EQ);
		testCpBranch(11, 10, BrilAsm.BranchCondition.UGT, BrilAsm.BranchCondition.EQ);
	}

	@Test
	public void testCall() {
		final BrilAsmInterpreter interpreter = new BrilAsmInterpreter(List.of(
				new BrilAsm.Label("main"),
				new BrilAsm.Call("getInt"),
				BrilAsm.RET
		), new BrilAsmInterpreter.CallHandler() {
			@Override
			public boolean handleCall(String name, BrilAsmInterpreter.RegisterAndRamAccess access) {
				if ("getInt".equals(name)) {
					access.setRegisterValue16(0, 258);
					return true;
				}
				return false;
			}
		});
		interpreter.run();
		Assert.assertEquals(3, interpreter.getIp());
		Assert.assertEquals(2, interpreter.getExecutedCommandCount());
		interpreter.iterateRegisters(new BrilAsmInterpreter.ByteConsumer() {
			@Override
			public void consumer(int register, int value) {
				final int expectedValue = switch (register) {
					case 0 -> 1;
					case 1 -> 2;
					default -> BrilAsmInterpreter.UNKNOWN;
				};
				Assert.assertEquals(expectedValue, value);
			}
		});
	}

	// Utils ==================================================================

	private static void run(List<BrilAsm> commands, int expectedIp, int expectedCommandCount, BrilAsmInterpreter.ByteConsumer expectedRegisterValues) {
		final BrilAsmInterpreter interpreter = new BrilAsmInterpreter(commands,
				                                                              (name, access) -> false);
		interpreter.run();
		Assert.assertEquals(expectedIp, interpreter.getIp());
		Assert.assertEquals(expectedCommandCount, interpreter.getExecutedCommandCount());
		interpreter.iterateRegisters(expectedRegisterValues);
	}

	private static void runCpFlags(boolean expectedC, boolean expectedS, boolean expectedV, boolean expectedZ, int destValue, int srcValue) {
		final List<BrilAsm> commands = List.of(
				new BrilAsm.Label("main"),
				new BrilAsm.LoadConst8(0, destValue),
				new BrilAsm.LoadConst8(1, srcValue),
				new BrilAsm.Cp8(0, 1),
				BrilAsm.RET
		);
		final BrilAsmInterpreter interpreter = new BrilAsmInterpreter(commands,
		                                                              (name, access) -> false);
		interpreter.run();
		Assert.assertEquals("c", expectedC, interpreter.isFlagC());
		Assert.assertEquals("s", expectedS, interpreter.isFlagS());
		Assert.assertEquals("v", expectedV, interpreter.isFlagV());
		Assert.assertEquals("z", expectedZ, interpreter.isFlagZ());
	}

	private static void testCpBranch(int left, int right, BrilAsm.BranchCondition trueCondition, BrilAsm.BranchCondition falseCondition) {
		run(List.of(
				    new BrilAsm.Label("main"),
				    new BrilAsm.LoadConst8(0, left),
				    new BrilAsm.LoadConst8(1, right),
				    new BrilAsm.LoadConst8(2, 1),
				    new BrilAsm.Cp8(0, 1),
				    new BrilAsm.Branch(falseCondition, "false"),
				    new BrilAsm.Branch(trueCondition, "L1"),
				    new BrilAsm.Jump("false"),

				    new BrilAsm.Label("false"),
				    new BrilAsm.LoadConst8(2, 0),

				    new BrilAsm.Label("L1"),
				    BrilAsm.RET
		    ), 12, 7,
		    (register, value) -> {
			    final int expectedValue = switch (register) {
				    case 0 -> left;
				    case 1 -> right;
				    case 2 -> 1;
				    default -> BrilAsmInterpreter.UNKNOWN;
			    };
			    Assert.assertEquals(expectedValue, value);
		    });
	}
}
