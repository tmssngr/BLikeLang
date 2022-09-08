package de.regnis.b.ir.command;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Thomas Singer
 */
public final class CommandListOptimizationsTest {

	// Accessing ==============================================================

	@Test
	public void testJumpsToNextCommand() {
		final CommandList list = new CommandList();
		list.add(new Label("start"));
		list.add(new JumpCommand("useless1"));
		list.add(new Label("useless1"));
		list.add(new LdLiteral(CommandFactory.workingRegister(0), 0));
		list.add(new JumpCommand("useless2"));
		list.add(new Label("useless2"));
		list.add(NoArgCommand.Ret);
		final CommandList compact = CommandListOptimizations.optimize(list);
		Assert.assertEquals(List.of(new Label("start"),
		                            new LdLiteral(CommandFactory.workingRegister(0), 0),
		                            NoArgCommand.Ret), compact.getCommands());
	}

	@Test
	public void testReplaceLabel() {
		final CommandList list = new CommandList();
		list.add(new Label("start"));
		list.add(new ArithmeticLiteral(ArithmeticOp.cp, 0, 1));
		list.add(new JumpCommand(JumpCondition.nz, "if_else"));
		list.add(new JumpCommand("if_then"));
		list.add(new Label("if_then"));
		list.add(new LdLiteral(CommandFactory.workingRegister(0), 1));
		list.add(new JumpCommand("next1"));
		list.add(new Label("if_else"));
		list.add(new LdLiteral(CommandFactory.workingRegister(0), 2));
		list.add(new Label("next1"));
		list.add(new Label("next"));
		list.add(NoArgCommand.Ret);
		final CommandList compact = CommandListOptimizations.optimize(list);
		Assert.assertEquals(List.of(new Label("start"),
		                            new ArithmeticLiteral(ArithmeticOp.cp, 0, 1),
		                            new JumpCommand(JumpCondition.nz, "if_else"),
		                            new LdLiteral(CommandFactory.workingRegister(0), 1),
		                            new JumpCommand("next"),
		                            new Label("if_else"),
		                            new LdLiteral(CommandFactory.workingRegister(0), 2),
		                            new Label("next"),
		                            NoArgCommand.Ret), compact.getCommands());
	}

	@Test
	public void testReplaceLabelIfElse() {
		final CommandList list = new CommandList();
		list.add(new Label("start"));
		list.add(new ArithmeticLiteral(ArithmeticOp.cp, 0, 1));
		list.add(new JumpCommand(JumpCondition.nz, "if_else"));
		list.add(new JumpCommand("if_then"));
		list.add(new Label("if_then"));
		list.add(new LdLiteral(CommandFactory.workingRegister(0), 1));
		list.add(new JumpCommand("next"));
		list.add(new Label("if_else"));
		list.add(new Label("next"));
		list.add(NoArgCommand.Ret);
		final CommandList compact = CommandListOptimizations.optimize(list);
		Assert.assertEquals(List.of(new Label("start"),
		                            new ArithmeticLiteral(ArithmeticOp.cp, 0, 1),
		                            new JumpCommand(JumpCondition.nz, "next"),
		                            new LdLiteral(CommandFactory.workingRegister(0), 1),
		                            new Label("next"),
		                            NoArgCommand.Ret), compact.getCommands());
	}

	@Test
	public void testRemoveUnusedLabels() {
		final CommandList list = new CommandList();
		list.add(new Label("start"));
		list.add(new Label("unused"));
		list.add(new LdLiteral(CommandFactory.workingRegister(0), 1));
		list.add(new Label("if_else"));
		list.add(new Label("next"));
		list.add(NoArgCommand.Ret);
		final CommandList compact = CommandListOptimizations.optimize(list);
		Assert.assertEquals(List.of(new Label("start"),
		                            new LdLiteral(CommandFactory.workingRegister(0), 1),
		                            NoArgCommand.Ret), compact.getCommands());
	}

	@Test
	public void testAndOrXor() {
		final CommandList list = new CommandList();
		list.add(new Label("start"));
		list.add(new ArithmeticLiteral(ArithmeticOp.and, 0, 0xFF));
		list.add(new ArithmeticLiteral(ArithmeticOp.and, 1, 0));
		list.add(new ArithmeticLiteral(ArithmeticOp.or, 2, 0xFF));
		list.add(new ArithmeticLiteral(ArithmeticOp.or, 3, 0));
		list.add(new ArithmeticLiteral(ArithmeticOp.xor, 4, 0xFF));
		list.add(new ArithmeticLiteral(ArithmeticOp.xor, 5, 0));
		list.add(NoArgCommand.Ret);
		final CommandList compact = CommandListOptimizations.optimize(list);
		Assert.assertEquals(List.of(new Label("start"),
		                            new LdLiteral(1, 0),
		                            new LdLiteral(2, 0xFF),
		                            new RegisterCommand(RegisterCommand.Op.com, 4),
		                            NoArgCommand.Ret), compact.getCommands());
	}

	@Test
	public void testTempLd() {
		final CommandList list = new CommandList();
		list.add(new Label("start"));
		list.add(new TempLd(8, 0));
		list.add(new TempLd(0, 8));
		list.add(NoArgCommand.Ret);
		final CommandList compact = CommandListOptimizations.optimize(list);
		Assert.assertEquals(List.of(new Label("start"),
		                            new Ld(8, 0),
		                            new Ld(9, 1),
		                            NoArgCommand.Ret), compact.getCommands());
	}

	@Test
	public void finishTempLd() {
		final CommandList list = new CommandList();
		list.add(new Label("start"));
		list.add(new TempLd(8, 0));
		list.add(new TempLdLiteral(2, 0x405));
		list.add(NoArgCommand.Ret);
		final CommandList compact = CommandListOptimizations.optimize(list);
		Assert.assertEquals(List.of(new Label("start"),
		                            new Ld(8, 0),
		                            new Ld(9, 1),
		                            new LdLiteral(2, 4),
		                            new LdLiteral(3, 5),
		                            NoArgCommand.Ret), compact.getCommands());
	}

	@Test
	public void finishTempArithmetic() {
		final CommandList list = new CommandList();
		list.add(new Label("start"));
		list.add(new TempArithmetic(ArithmeticOp.add, 0, 2));
		list.add(new TempArithmeticLiteral(ArithmeticOp.add, 2, 0x10a));
		list.add(new TempArithmeticLiteral(ArithmeticOp.add, 4, 1));

		list.add(new TempArithmetic(ArithmeticOp.sub, 0, 2));
		list.add(new TempArithmeticLiteral(ArithmeticOp.sub, 2, 0x10b));
		list.add(new TempArithmeticLiteral(ArithmeticOp.sub, 4, 1));

		list.add(NoArgCommand.Ret);
		final CommandList compact = CommandListOptimizations.optimize(list);
		Assert.assertEquals(List.of(new Label("start"),
									new Arithmetic(ArithmeticOp.add, 1, 3),
									new Arithmetic(ArithmeticOp.adc, 0, 2),
									new ArithmeticLiteral(ArithmeticOp.add, 3, 10),
									new ArithmeticLiteral(ArithmeticOp.adc, 2, 1),
									new RegisterCommand(RegisterCommand.Op.incw, 4),

									new Arithmetic(ArithmeticOp.sub, 1, 3),
									new Arithmetic(ArithmeticOp.sbc, 0, 2),
									new ArithmeticLiteral(ArithmeticOp.sub, 3, 11),
									new ArithmeticLiteral(ArithmeticOp.sbc, 2, 1),
									new RegisterCommand(RegisterCommand.Op.decw, 4),

		                            NoArgCommand.Ret), compact.getCommands());
	}
}
