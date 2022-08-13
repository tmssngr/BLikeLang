package de.regnis.b.ir.command;

import de.regnis.b.ast.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Thomas Singer
 */
public class CommandFactoryTest {

	// Accessing ==============================================================

	@Test
	public void testAssignLiteral() {
		assertEqualsAssignDeclare(List.of(new StoreCommand("c.1", 10),
		                                  new StoreCommand("c.0", 0)), "c", new NumberLiteral(10));
		assertEqualsAssignDeclare(List.of(new StoreCommand("c.1", 0xe8),
		                                  new StoreCommand("c.0", 3)), "c", new NumberLiteral(1000));
		assertEqualsAssignDeclare(List.of(new StoreCommand("d.1", 0xfe),
		                                  new StoreCommand("d.0", 0xff)), "d", new NumberLiteral(-2));
	}

	@Test
	public void testAssignVar() {
		assertEqualsAssignDeclare(List.of(new StoreCommand("c.1", "x.1"),
		                                  new StoreCommand("c.0", "x.0")), "c", new VarRead("x"));
		assertEqualsAssignDeclare(List.of(new StoreCommand("d.1", "y.1"),
		                                  new StoreCommand("d.0", "y.0")), "d", new VarRead("y"));
	}

	@Test
	public void testArithmetic() {
		assertEquals(List.of(new ArithmeticCommand(ArithmeticCommand.Operation.add, "a.1", 1),
		                     new ArithmeticCommand(ArithmeticCommand.Operation.adc, "a.0", 0)), new Assignment(Assignment.Op.add,
		                                                                                                       "a",
		                                                                                                       new NumberLiteral(1)));
		assertEquals(List.of(new ArithmeticCommand(ArithmeticCommand.Operation.add, "a.1", "b.1"),
		                     new ArithmeticCommand(ArithmeticCommand.Operation.adc, "a.0", "b.0")), new Assignment(Assignment.Op.add,
		                                                                                                           "a",
		                                                                                                           new VarRead("b")));
		assertEquals(List.of(new SimpleCommand(SimpleCommand.Operation.ccf),
		                     new ModifyRegisterCommand(ModifyRegisterCommand.Operation.rlc, "a.1"),
		                     new ModifyRegisterCommand(ModifyRegisterCommand.Operation.rlc, "a.0")), new Assignment(Assignment.Op.shiftL,
		                                                                                                            "a",
		                                                                                                            new NumberLiteral(1)));
		assertEquals(List.of(new SimpleCommand(SimpleCommand.Operation.ccf),
		                     new ModifyRegisterCommand(ModifyRegisterCommand.Operation.rlc, "a.1"),
		                     new ModifyRegisterCommand(ModifyRegisterCommand.Operation.rlc, "a.0"),
		                     new SimpleCommand(SimpleCommand.Operation.ccf),
		                     new ModifyRegisterCommand(ModifyRegisterCommand.Operation.rlc, "a.1"),
		                     new ModifyRegisterCommand(ModifyRegisterCommand.Operation.rlc, "a.0")), new Assignment(Assignment.Op.shiftL,
		                                                                                                            "a",
		                                                                                                            new NumberLiteral(2)));
		assertEquals(List.of(new SimpleCommand(SimpleCommand.Operation.ccf),
		                     new ModifyRegisterCommand(ModifyRegisterCommand.Operation.rrc, "a.0"),
		                     new ModifyRegisterCommand(ModifyRegisterCommand.Operation.rrc, "a.1")), new Assignment(Assignment.Op.shiftR,
		                                                                                                            "a",
		                                                                                                            new NumberLiteral(1)));
		assertEquals(List.of(new SimpleCommand(SimpleCommand.Operation.ccf),
		                     new ModifyRegisterCommand(ModifyRegisterCommand.Operation.rrc, "a.0"),
		                     new ModifyRegisterCommand(ModifyRegisterCommand.Operation.rrc, "a.1"),
		                     new SimpleCommand(SimpleCommand.Operation.ccf),
		                     new ModifyRegisterCommand(ModifyRegisterCommand.Operation.rrc, "a.0"),
		                     new ModifyRegisterCommand(ModifyRegisterCommand.Operation.rrc, "a.1")), new Assignment(Assignment.Op.shiftR,
		                                                                                                            "a",
		                                                                                                            new NumberLiteral(2)));
	}

	@Test
	public void testIfBoolean() {
		assertEquals(List.of(new TestCommand(TestCommand.Operation.or, "isValid", "isValid"),
		                     new JumpCommand(JumpCommand.Condition.z, () -> "falseLabel")), new IfStatement(new VarRead("isValid"), new StatementList(), new StatementList()));
	}

	@Test
	public void testIfComparison() {
		testIfComparison(BinaryExpression.Op.lessThan, JumpCommand.Condition.lt, JumpCommand.Condition.uge);
		testIfComparison(BinaryExpression.Op.lessEqual, JumpCommand.Condition.le, JumpCommand.Condition.ugt);
		testIfComparison(BinaryExpression.Op.greaterEqual, JumpCommand.Condition.ge, JumpCommand.Condition.ult);
		testIfComparison(BinaryExpression.Op.greaterThan, JumpCommand.Condition.gt, JumpCommand.Condition.ule);
	}

	@Test
	public void testIfEqual() {
		final BinaryExpression.Op operator = BinaryExpression.Op.equal;
		assertEquals(List.of(new TestCommand(TestCommand.Operation.cmp, "a.0", "b.0"),
		                     new JumpCommand(JumpCommand.Condition.nz, () -> "falseLabel"),
		                     new TestCommand(TestCommand.Operation.cmp, "a.1", "b.1"),
		                     new JumpCommand(JumpCommand.Condition.nz, () -> "falseLabel")), new IfStatement(new BinaryExpression(new VarRead("a"), operator, new VarRead("b")), new StatementList(), new StatementList()));

		assertEquals(List.of(new TestCommand(TestCommand.Operation.cmp, "a.0", "b.0"),
		                     new JumpCommand(JumpCommand.Condition.nz, () -> "falseLabel"),
		                     new TestCommand(TestCommand.Operation.cmp, "a.1", "b.1"),
		                     new JumpCommand(JumpCommand.Condition.nz, () -> "falseLabel")), new IfStatement(new BinaryExpression(new VarRead("a"), operator, new VarRead("b")), new StatementList(), new StatementList()));

		assertEquals(List.of(new TestCommand(TestCommand.Operation.cmp, "a.0", 1),
		                     new JumpCommand(JumpCommand.Condition.nz, () -> "falseLabel"),
		                     new TestCommand(TestCommand.Operation.cmp, "a.1", 244),
		                     new JumpCommand(JumpCommand.Condition.nz, () -> "falseLabel")), new IfStatement(new BinaryExpression(new VarRead("a"), operator, new NumberLiteral(500)), new StatementList(), new StatementList()));

		assertEquals(List.of(new TestCommand(TestCommand.Operation.cmp, "a.0", 255),
		                     new JumpCommand(JumpCommand.Condition.nz, () -> "falseLabel"),
		                     new TestCommand(TestCommand.Operation.cmp, "a.1", 246),
		                     new JumpCommand(JumpCommand.Condition.nz, () -> "falseLabel")), new IfStatement(new BinaryExpression(new VarRead("a"), operator, new NumberLiteral(-10)), new StatementList(), new StatementList()));
	}

	// Utils ==================================================================

	private void testIfComparison(BinaryExpression.Op operator, JumpCommand.Condition conditionSigned, JumpCommand.Condition inverseUnsigned) {
		assertEquals(List.of(new TestCommand(TestCommand.Operation.cmp, "a.0", "b.0"),
		                     new JumpCommand(conditionSigned, () -> "trueLabel"),
		                     new JumpCommand(JumpCommand.Condition.nz, () -> "falseLabel"),
		                     new TestCommand(TestCommand.Operation.cmp, "a.1", "b.1"),
		                     new JumpCommand(inverseUnsigned, () -> "falseLabel")), new IfStatement(new BinaryExpression(new VarRead("a"), operator, new VarRead("b")), new StatementList(), new StatementList()));

		assertEquals(List.of(new TestCommand(TestCommand.Operation.cmp, "a.0", 255),
		                     new JumpCommand(conditionSigned, () -> "trueLabel"),
		                     new JumpCommand(JumpCommand.Condition.nz, () -> "falseLabel"),
		                     new TestCommand(TestCommand.Operation.cmp, "a.1", 246),
		                     new JumpCommand(inverseUnsigned, () -> "falseLabel")), new IfStatement(new BinaryExpression(new VarRead("a"), operator, new NumberLiteral(-10)), new StatementList(), new StatementList()));
	}

	private void assertEqualsAssignDeclare(List<Command> expectedCommands, String varName, Expression expression) {
		assertEquals(expectedCommands, new Assignment(Assignment.Op.assign, varName, expression));
		assertEquals(expectedCommands, new VarDeclaration(varName, expression));
	}

	private void assertEquals(List<Command> expectedCommands, SimpleStatement statement) {
		final CommandFactory factory = new CommandFactory();
		factory.add(statement);
		final List<Command> commands = factory.getCommands();
		Assert.assertEquals(expectedCommands, commands);
	}

	private void assertEquals(List<Command> expectedCommands, IfStatement statement) {
		final CommandFactory factory = new CommandFactory();
		factory.add(statement, () -> "trueLabel", () -> "falseLabel");
		final List<Command> commands = factory.getCommands();
		Assert.assertEquals(expectedCommands, commands);
	}
}
