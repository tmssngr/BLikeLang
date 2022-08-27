package de.regnis.b.ir.command;

import de.regnis.b.ast.*;
import de.regnis.b.ir.StackPositionProvider;
import de.regnis.b.type.BasicTypes;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Singer
 */
public class CommandFactoryTest {

	// Constants ==============================================================

	private static final int STACKPOS_A = 10;
	private static final int STACKPOS_B = 12;

	// Accessing ==============================================================

	@Test
	public void testAssign() {
		test(List.of(new LoadC(CommandFactory.REG_A, 1),

		             new Load(CommandFactory.VAR_ACCESS_REGISTER, CommandFactory.SP),
		             new ArithmeticC(ArithmeticOp.add, CommandFactory.VAR_ACCESS_REGISTER, STACKPOS_A),
		             new Store(CommandFactory.VAR_ACCESS_REGISTER_NAME, CommandFactory.REG_A)), new Assignment(Assignment.Op.assign, "a", new NumberLiteral(1)));

		test(List.of(new Load(CommandFactory.VAR_ACCESS_REGISTER, CommandFactory.SP),
		             new ArithmeticC(ArithmeticOp.add, CommandFactory.VAR_ACCESS_REGISTER, STACKPOS_B),
		             new Load(CommandFactory.REG_A, CommandFactory.VAR_ACCESS_REGISTER_NAME),

		             new Load(CommandFactory.VAR_ACCESS_REGISTER, CommandFactory.SP),
		             new ArithmeticC(ArithmeticOp.add, CommandFactory.VAR_ACCESS_REGISTER, STACKPOS_A),
		             new Store(CommandFactory.VAR_ACCESS_REGISTER_NAME, CommandFactory.REG_A)), new Assignment(Assignment.Op.assign, "a", new VarRead("b")));
	}

	@Test
	public void testArithmetic() {
		testArithmetic(ArithmeticOp.add, Assignment.Op.add);
		testArithmetic(ArithmeticOp.sub, Assignment.Op.sub);
		testArithmetic(ArithmeticOp.and, Assignment.Op.bitAnd);
		testArithmetic(ArithmeticOp.or, Assignment.Op.bitOr);
		testArithmetic(ArithmeticOp.xor, Assignment.Op.bitXor);

		testShift(RegisterCommand.Op.shiftL, Assignment.Op.shiftL);
		testShift(RegisterCommand.Op.shiftR, Assignment.Op.shiftR);
	}

	@Test
	public void testIf() {
		final TestCommandFactory factory = new TestCommandFactory();
		factory.addIf(new VarRead("a"), "trueLabel", "falseLabel");
		Assert.assertEquals(List.of(new Load(CommandFactory.VAR_ACCESS_REGISTER, CommandFactory.SP),
		                            new ArithmeticC(ArithmeticOp.add, CommandFactory.VAR_ACCESS_REGISTER, STACKPOS_A),
		                            new Load(CommandFactory.REG_A, CommandFactory.VAR_ACCESS_REGISTER_NAME),

		                            new CmpCJump(CommandFactory.REG_A, 0,
		                                         JumpCondition.nz, "trueLabel",
		                                         JumpCondition.z, "falseLabel")), factory.getCommands());

		testIf(BinaryExpression.Op.lessThan, JumpCondition.lt, JumpCondition.ge);
		testIf(BinaryExpression.Op.lessEqual, JumpCondition.le, JumpCondition.gt);
		testIf(BinaryExpression.Op.equal, JumpCondition.z, JumpCondition.nz);
		testIf(BinaryExpression.Op.notEqual, JumpCondition.nz, JumpCondition.z);
		testIf(BinaryExpression.Op.greaterEqual, JumpCondition.ge, JumpCondition.lt);
		testIf(BinaryExpression.Op.greaterThan, JumpCondition.gt, JumpCondition.le);
	}

	// Utils ==================================================================

	private void testIf(BinaryExpression.Op operator, JumpCondition trueCondition, JumpCondition falseCondition) {
		TestCommandFactory factory = new TestCommandFactory();
		factory.addIf(new BinaryExpression(new VarRead("a"),
		                                   operator,
		                                   new VarRead("b")), "trueLabel", "falseLabel");
		Assert.assertEquals(List.of(new Load(CommandFactory.VAR_ACCESS_REGISTER, CommandFactory.SP),
		                            new ArithmeticC(ArithmeticOp.add, CommandFactory.VAR_ACCESS_REGISTER, STACKPOS_A),
		                            new Load(CommandFactory.REG_A, CommandFactory.VAR_ACCESS_REGISTER_NAME),

		                            new Load(CommandFactory.VAR_ACCESS_REGISTER, CommandFactory.SP),
		                            new ArithmeticC(ArithmeticOp.add, CommandFactory.VAR_ACCESS_REGISTER, STACKPOS_B),
		                            new Load(CommandFactory.REG_B, CommandFactory.VAR_ACCESS_REGISTER_NAME),

		                            new CmpJump(CommandFactory.REG_A, CommandFactory.REG_B,
		                                        trueCondition, "trueLabel",
		                                        falseCondition, "falseLabel")), factory.getCommands());

		factory = new TestCommandFactory();
		factory.addIf(new BinaryExpression(new VarRead("a"),
		                                   operator,
		                                   new NumberLiteral(100)), "trueLabel", "falseLabel");
		Assert.assertEquals(List.of(new Load(CommandFactory.VAR_ACCESS_REGISTER, CommandFactory.SP),
		                            new ArithmeticC(ArithmeticOp.add, CommandFactory.VAR_ACCESS_REGISTER, STACKPOS_A),
		                            new Load(CommandFactory.REG_A, CommandFactory.VAR_ACCESS_REGISTER_NAME),

		                            new CmpCJump(CommandFactory.REG_A, 100,
		                                         trueCondition, "trueLabel",
		                                         falseCondition, "falseLabel")), factory.getCommands());
	}

	private void testArithmetic(ArithmeticOp op, Assignment.Op operation) {
		test(List.of(new Load(CommandFactory.VAR_ACCESS_REGISTER, CommandFactory.SP),
		             new ArithmeticC(ArithmeticOp.add, CommandFactory.VAR_ACCESS_REGISTER, STACKPOS_A),
		             new Load(CommandFactory.REG_A, CommandFactory.VAR_ACCESS_REGISTER_NAME),

		             new ArithmeticC(op, CommandFactory.REG_A, 1),

		             new Load(CommandFactory.VAR_ACCESS_REGISTER, CommandFactory.SP),
		             new ArithmeticC(ArithmeticOp.add, CommandFactory.VAR_ACCESS_REGISTER, STACKPOS_A),
		             new Store(CommandFactory.VAR_ACCESS_REGISTER_NAME, CommandFactory.REG_A)), new Assignment(operation, "a", new NumberLiteral(1)));

		test(List.of(new Load(CommandFactory.VAR_ACCESS_REGISTER, CommandFactory.SP),
		             new ArithmeticC(ArithmeticOp.add, CommandFactory.VAR_ACCESS_REGISTER, STACKPOS_A),
		             new Load(CommandFactory.REG_A, CommandFactory.VAR_ACCESS_REGISTER_NAME),

		             new Load(CommandFactory.VAR_ACCESS_REGISTER, CommandFactory.SP),
		             new ArithmeticC(ArithmeticOp.add, CommandFactory.VAR_ACCESS_REGISTER, STACKPOS_B),
		             new Load(CommandFactory.REG_B, CommandFactory.VAR_ACCESS_REGISTER_NAME),

		             new Arithmetic(op, CommandFactory.REG_A, CommandFactory.REG_B),

		             new Load(CommandFactory.VAR_ACCESS_REGISTER, CommandFactory.SP),
		             new ArithmeticC(ArithmeticOp.add, CommandFactory.VAR_ACCESS_REGISTER, STACKPOS_A),
		             new Store(CommandFactory.VAR_ACCESS_REGISTER_NAME, CommandFactory.REG_A)), new Assignment(operation, "a", new VarRead("b")));
	}

	private void testShift(RegisterCommand.Op op, Assignment.Op operation) {
		test(List.of(new Load(CommandFactory.VAR_ACCESS_REGISTER, CommandFactory.SP),
		             new ArithmeticC(ArithmeticOp.add, CommandFactory.VAR_ACCESS_REGISTER, STACKPOS_A),
		             new Load(CommandFactory.REG_A, CommandFactory.VAR_ACCESS_REGISTER_NAME),

		             new RegisterCommand(op, CommandFactory.REG_A),

		             new Load(CommandFactory.VAR_ACCESS_REGISTER, CommandFactory.SP),
		             new ArithmeticC(ArithmeticOp.add, CommandFactory.VAR_ACCESS_REGISTER, STACKPOS_A),
		             new Store(CommandFactory.VAR_ACCESS_REGISTER_NAME, CommandFactory.REG_A)), new Assignment(operation, "a", new NumberLiteral(1)));

		test(List.of(new Load(CommandFactory.VAR_ACCESS_REGISTER, CommandFactory.SP),
		             new ArithmeticC(ArithmeticOp.add, CommandFactory.VAR_ACCESS_REGISTER, STACKPOS_A),
		             new Load(CommandFactory.REG_A, CommandFactory.VAR_ACCESS_REGISTER_NAME),

		             new RegisterCommand(op, CommandFactory.REG_A),
		             new RegisterCommand(op, CommandFactory.REG_A),

		             new Load(CommandFactory.VAR_ACCESS_REGISTER, CommandFactory.SP),
		             new ArithmeticC(ArithmeticOp.add, CommandFactory.VAR_ACCESS_REGISTER, STACKPOS_A),
		             new Store(CommandFactory.VAR_ACCESS_REGISTER_NAME, CommandFactory.REG_A)), new Assignment(operation, "a", new NumberLiteral(2)));
	}

	private void test(List<Command> expected, SimpleStatement statement) {
		final TestCommandFactory factory = new TestCommandFactory();
		factory.add(statement);
		Assert.assertEquals(expected, factory.getCommands());
	}

	// Inner Classes ==========================================================

	private static class TestStackPositionProvider implements StackPositionProvider {
		@Override
		public int getStackPosition(@NotNull String varName) {
			return switch (varName) {
				case "a" -> STACKPOS_A;
				case "b" -> STACKPOS_B;
				default -> throw new IllegalArgumentException(varName);
			};
		}
	}

	private static class TestCommandFactory extends CommandFactory {
		private final List<Command> commands = new ArrayList<>();

		public TestCommandFactory() {
			super(new TestStackPositionProvider(), funcName -> BasicTypes.INT16);
		}

		@Override
		protected void addCommand(@NotNull Command command) {
			commands.add(command);
		}

		public List<Command> getCommands() {
			return Collections.unmodifiableList(commands);
		}
	}
}
