package de.regnis.b.ir.command;

import de.regnis.b.ast.*;
import de.regnis.b.ir.StackPositionProvider;
import de.regnis.b.type.BasicTypes;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static de.regnis.b.ir.command.CommandFactory.*;

/**
 * @author Thomas Singer
 */
@SuppressWarnings("PointlessArithmeticExpression")
public class CommandFactoryTest {

	// Constants ==============================================================

	private static final int STACKPOS_A = 10;
	private static final int STACKPOS_B = 12;

	// Accessing ==============================================================

	@Test
	public void testAssign() {
		test(new ExpectedCommands()
				     .add(new TempLdLiteral(workingRegister(REG_A), 1))

				     .varAddress(STACKPOS_A)
				     .save(REG_A)
				     .commands, new Assignment(Assignment.Op.assign, "a", new NumberLiteral(1)));

		test(new ExpectedCommands()
				     .varAddress(STACKPOS_B)
				     .load(REG_A)

				     .varAddress(STACKPOS_A)
				     .save(REG_A)
				     .commands, new Assignment(Assignment.Op.assign, "a", new VarRead("b")));
	}

	@Test
	public void testArithmetic() {
		testArithmetic(ArithmeticOp.add, Assignment.Op.add);
		testArithmetic(ArithmeticOp.sub, Assignment.Op.sub);
		testArithmetic(ArithmeticOp.and, Assignment.Op.bitAnd);
		testArithmetic(ArithmeticOp.or, Assignment.Op.bitOr);
		testArithmetic(ArithmeticOp.xor, Assignment.Op.bitXor);
	}

	@Test
	public void testIf() {
		final CommandList commandList = new CommandList();
		final CommandFactory factory = createCommandFactory(commandList);
		factory.addIf(new VarRead("a"), "trueLabel", "falseLabel");
		Assert.assertEquals(new ExpectedCommands()
				                    .varAddress(STACKPOS_A)
				                    .load(REG_A)
				                    .add(new ArithmeticLiteral(ArithmeticOp.cp, workingRegister(REG_A), 0))
				                    .add(new JumpCommand(JumpCondition.nz, "trueLabel"))
				                    .add(new ArithmeticLiteral(ArithmeticOp.cp, workingRegister(REG_A + 1), 0))
				                    .add(new JumpCommand(JumpCondition.z, "falseLabel"))
				                    .commands, commandList.getCommands());

		testIf(BinaryExpression.Op.lessThan, List.of(new JumpCommand(JumpCondition.lt, "trueLabel"),
		                                             new JumpCommand(JumpCondition.nz, "falseLabel")), JumpCondition.uge);
		testIf(BinaryExpression.Op.lessEqual, List.of(new JumpCommand(JumpCondition.lt, "trueLabel"),
		                                              new JumpCommand(JumpCondition.nz, "falseLabel")), JumpCondition.ugt);
		testIf(BinaryExpression.Op.greaterEqual, List.of(new JumpCommand(JumpCondition.gt, "trueLabel"),
		                                                 new JumpCommand(JumpCondition.nz, "falseLabel")), JumpCondition.ult);
		testIf(BinaryExpression.Op.greaterThan, List.of(new JumpCommand(JumpCondition.gt, "trueLabel"),
		                                                new JumpCommand(JumpCondition.nz, "falseLabel")), JumpCondition.ule);
		testIf(BinaryExpression.Op.equal, List.of(new JumpCommand(JumpCondition.nz, "falseLabel")), JumpCondition.nz);
		testIf(BinaryExpression.Op.notEqual, List.of(new JumpCommand(JumpCondition.nz, "trueLabel")), JumpCondition.z);
	}

	// Utils ==================================================================

	private void testIf(BinaryExpression.Op operator, List<Command> msbJumps, JumpCondition lsbFalseCondition) {
		CommandList commandList = new CommandList();
		CommandFactory factory = createCommandFactory(commandList);
		factory.addIf(new BinaryExpression(new VarRead("a"),
		                                   operator,
		                                   new VarRead("b")), "trueLabel", "falseLabel");

		Assert.assertEquals(new ExpectedCommands()
				                    .varAddress(STACKPOS_A)
				                    .load(REG_A)

				                    .varAddress(STACKPOS_B)
				                    .load(REG_B)

				                    .add(new Arithmetic(ArithmeticOp.cp, workingRegister(REG_A), workingRegister(REG_B)))
				                    .addAll(msbJumps)

				                    .add(new Arithmetic(ArithmeticOp.cp, workingRegister(REG_A + 1), workingRegister(REG_B + 1)))
				                    .add(new JumpCommand(lsbFalseCondition, "falseLabel"))
				                    .commands, commandList.getCommands());

		commandList = new CommandList();
		factory     = createCommandFactory(commandList);
		factory.addIf(new BinaryExpression(new VarRead("a"),
		                                   operator,
		                                   new NumberLiteral(100)), "trueLabel", "falseLabel");
		Assert.assertEquals(new ExpectedCommands()
				                    .varAddress(STACKPOS_A)
				                    .load(REG_A)

				                    .add(new ArithmeticLiteral(ArithmeticOp.cp, workingRegister(REG_A), 100 >> 8))
				                    .addAll(msbJumps)

				                    .add(new ArithmeticLiteral(ArithmeticOp.cp, workingRegister(REG_A + 1), 100))
				                    .add(new JumpCommand(lsbFalseCondition, "falseLabel"))
				                    .commands, commandList.getCommands());
	}

	private void testArithmetic(ArithmeticOp op, Assignment.Op operation) {
		test(new ExpectedCommands()
				     .varAddress(STACKPOS_A)
				     .load(REG_A)

				     .add(new TempArithmeticLiteral(op, workingRegister(REG_A), 1))

				     .varAddress(STACKPOS_A)
				     .save(REG_A)
				     .commands, new Assignment(operation, "a", new NumberLiteral(1)));

		test(new ExpectedCommands()
				     .varAddress(STACKPOS_A)
				     .load(REG_A)

				     .varAddress(STACKPOS_B)
				     .load(REG_B)

				     .add(new TempArithmetic(op, workingRegister(REG_A), workingRegister(REG_B)))

				     .varAddress(STACKPOS_A)
				     .save(REG_A)
				     .commands, new Assignment(operation, "a", new VarRead("b")));
	}

	@Test
	public void testShift() {
		test(List.of(), new Assignment(Assignment.Op.shiftL, "a", new NumberLiteral(0)));

		testShift(List.of(NoArgCommand.Ccf,
		                  new RegisterCommand(RegisterCommand.Op.rlc, workingRegister(REG_A + 1)),
		                  new RegisterCommand(RegisterCommand.Op.rlc, workingRegister(REG_A))),
		          Assignment.Op.shiftL, 1);

		testShift(List.of(NoArgCommand.Ccf,
		                  new RegisterCommand(RegisterCommand.Op.rlc, workingRegister(REG_A + 1)),
		                  new RegisterCommand(RegisterCommand.Op.rlc, workingRegister(REG_A)),
		                  NoArgCommand.Ccf,
		                  new RegisterCommand(RegisterCommand.Op.rlc, workingRegister(REG_A + 1)),
		                  new RegisterCommand(RegisterCommand.Op.rlc, workingRegister(REG_A))),
		          Assignment.Op.shiftL, 2);

		testShift(List.of(new Ld(workingRegister(REG_A), workingRegister(REG_A + 1)),
		                  new LdLiteral(workingRegister(REG_A + 1), 0)),
		          Assignment.Op.shiftL, 8);

		test(List.of(), new Assignment(Assignment.Op.shiftR, "a", new NumberLiteral(0)));

		testShift(List.of(new RegisterCommand(RegisterCommand.Op.sra, workingRegister(REG_A)),
		                  new RegisterCommand(RegisterCommand.Op.rrc, workingRegister(REG_A + 1))),
		          Assignment.Op.shiftR, 1);

		testShift(List.of(new RegisterCommand(RegisterCommand.Op.sra, workingRegister(REG_A)),
		                  new RegisterCommand(RegisterCommand.Op.rrc, workingRegister(REG_A + 1)),
		                  new RegisterCommand(RegisterCommand.Op.sra, workingRegister(REG_A)),
		                  new RegisterCommand(RegisterCommand.Op.rrc, workingRegister(REG_A + 1))),
		          Assignment.Op.shiftR, 2);

		testShift(List.of(new Ld(workingRegister(REG_A + 1), workingRegister(REG_A)),
		                  new LdLiteral(workingRegister(REG_A), 0)),
		          Assignment.Op.shiftR, 8);
	}

	private void testShift(List<Command> expectedShiftCommands, Assignment.Op operation, int number) {
		test(new ExpectedCommands()
				     .varAddress(STACKPOS_A)
				     .load(REG_A)

				     .addAll(expectedShiftCommands)

				     .varAddress(STACKPOS_A)
				     .save(REG_A)
				     .commands, new Assignment(operation, "a", new NumberLiteral(number)));
	}

	private void test(List<Command> expected, SimpleStatement statement) {
		final CommandList commandList = new CommandList();
		final CommandFactory factory = createCommandFactory(commandList);
		factory.add(statement);
		Assert.assertEquals(expected, commandList.getCommands());
	}

	@NotNull
	private CommandFactory createCommandFactory(@NotNull CommandList commandList) {
		return new CommandFactory(new TestStackPositionProvider(), funcName -> BasicTypes.INT16, new BuiltInFunctions(), commandList);
	}

	// Inner Classes ==========================================================

	private static class ExpectedCommands {

		private final List<Command> commands = new ArrayList<>();

		public ExpectedCommands() {
		}

		public ExpectedCommands add(Command command) {
			commands.add(command);
			return this;
		}

		public ExpectedCommands addAll(List<Command> commands) {
			this.commands.addAll(commands);
			return this;
		}

		public ExpectedCommands varAddress(int stackPost) {
			add(new TempLd(workingRegister(VAR_ACCESS_REGISTER), SP_H));
			add(new TempArithmeticLiteral(ArithmeticOp.add, workingRegister(VAR_ACCESS_REGISTER), stackPost));
			return this;
		}

		public ExpectedCommands load(int register) {
			add(new LdFromMem(register, VAR_ACCESS_REGISTER));
			add(new RegisterCommand(RegisterCommand.Op.incw, workingRegister(VAR_ACCESS_REGISTER)));
			add(new LdFromMem(register + 1, VAR_ACCESS_REGISTER));
			return this;
		}

		public ExpectedCommands save(int register) {
			add(new LdToMem(VAR_ACCESS_REGISTER, register));
			add(new RegisterCommand(RegisterCommand.Op.incw, workingRegister(VAR_ACCESS_REGISTER)));
			add(new LdToMem(VAR_ACCESS_REGISTER, register + 1));
			return this;
		}
	}

	private static class TestStackPositionProvider implements StackPositionProvider {

		@Override
		public RegistersToPush getRegistersToPush() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getRegister(@NotNull String varName) {
			return -1;
		}

		@Override
		public int getStackPosition(@NotNull String varName) {
			return switch (varName) {
				case "a" -> STACKPOS_A;
				case "b" -> STACKPOS_B;
				default -> throw new IllegalArgumentException(varName);
			};
		}
	}
}
