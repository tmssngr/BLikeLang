package de.regnis.b.ir.command;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Thomas Singer
 */
public final class CommandListOptimizationsTest {

	@Test
	public void testJumpsToNextCommand() {
		final CommandList list = new CommandList();
		list.add(new Label("start"));
		list.add(new JumpCommand("useless1"));
		list.add(new Label("useless1"));
		list.add(new LdLiteral(CommandFactory.workingRegister(1), 1));
		list.add(new LdLiteral(CommandFactory.workingRegister(0), 0));
		list.add(new JumpCommand("useless2"));
		list.add(new Label("useless2"));
		list.add(NoArgCommand.Ret);
		final CommandList compact = CommandListOptimizations.optimize(list);
		Assert.assertEquals(List.of(new Label("start"),
		                            new LdLiteral(CommandFactory.workingRegister(1), 1),
		                            new LdLiteral(CommandFactory.workingRegister(0), 0),
		                            NoArgCommand.Ret), compact.getCommands());
	}

	@Test
	public void testReplaceLabel() {
		final CommandList list = new CommandList();
		list.add(new Label("start"));
		list.add(new ArithmeticC(ArithmeticOp.cp, 0, 1));
		list.add(new JumpCommand(JumpCondition.nz, "if_else"));
		list.add(new JumpCommand("if_then"));
		list.add(new Label("if_then"));
		list.add(new LdLiteral(CommandFactory.workingRegister(1), 1));
		list.add(new LdLiteral(CommandFactory.workingRegister(0), 0));
		list.add(new JumpCommand("next1"));
		list.add(new Label("if_else"));
		list.add(new LdLiteral(CommandFactory.workingRegister(1), 2));
		list.add(new LdLiteral(CommandFactory.workingRegister(0), 0));
		list.add(new Label("next1"));
		list.add(new Label("next"));
		list.add(NoArgCommand.Ret);
		final CommandList compact = CommandListOptimizations.optimize(list);
		Assert.assertEquals(List.of(new Label("start"),
		                            new ArithmeticC(ArithmeticOp.cp, 0, 1),
		                            new JumpCommand(JumpCondition.nz, "if_else"),
		                            new LdLiteral(CommandFactory.workingRegister(1), 1),
		                            new LdLiteral(CommandFactory.workingRegister(0), 0),
		                            new JumpCommand("next"),
		                            new Label("if_else"),
		                            new LdLiteral(CommandFactory.workingRegister(1), 2),
		                            new LdLiteral(CommandFactory.workingRegister(0), 0),
		                            new Label("next"),
		                            NoArgCommand.Ret), compact.getCommands());
	}

	@Test
	public void testReplaceLabelIfElse() {
		final CommandList list = new CommandList();
		list.add(new Label("start"));
		list.add(new ArithmeticC(ArithmeticOp.cp, 0, 1));
		list.add(new JumpCommand(JumpCondition.nz, "if_else"));
		list.add(new JumpCommand("if_then"));
		list.add(new Label("if_then"));
		list.add(new LdLiteral(CommandFactory.workingRegister(1), 1));
		list.add(new LdLiteral(CommandFactory.workingRegister(0), 0));
		list.add(new JumpCommand("next"));
		list.add(new Label("if_else"));
		list.add(new Label("next"));
		list.add(NoArgCommand.Ret);
		final CommandList compact = CommandListOptimizations.optimize(list);
		Assert.assertEquals(List.of(new Label("start"),
		                            new ArithmeticC(ArithmeticOp.cp, 0, 1),
		                            new JumpCommand(JumpCondition.nz, "next"),
		                            new LdLiteral(CommandFactory.workingRegister(1), 1),
		                            new LdLiteral(CommandFactory.workingRegister(0), 0),
		                            new Label("next"),
		                            NoArgCommand.Ret), compact.getCommands());
	}

	@Test
	public void testRemoveUnusedLabels() {
		final CommandList list = new CommandList();
		list.add(new Label("start"));
		list.add(new Label("unused"));
		list.add(new LdLiteral(CommandFactory.workingRegister(1), 1));
		list.add(new LdLiteral(CommandFactory.workingRegister(0), 0));
		list.add(new Label("if_else"));
		list.add(new Label("next"));
		list.add(NoArgCommand.Ret);
		final CommandList compact = CommandListOptimizations.optimize(list);
		Assert.assertEquals(List.of(new Label("start"),
		                            new LdLiteral(CommandFactory.workingRegister(1), 1),
		                            new LdLiteral(CommandFactory.workingRegister(0), 0),
		                            NoArgCommand.Ret), compact.getCommands());
	}

	@Test
	public void testAddSub() {
		final CommandList list = new CommandList();
		list.add(new Label("start"));
		list.add(new ArithmeticC(ArithmeticOp.add, 1, 1));
		list.add(new ArithmeticC(ArithmeticOp.adc, 0, 0));
		list.add(new ArithmeticC(ArithmeticOp.sub, 3, 1));
		list.add(new ArithmeticC(ArithmeticOp.sbc, 2, 0));
		list.add(NoArgCommand.Ret);
		final CommandList compact = CommandListOptimizations.optimize(list);
		Assert.assertEquals(List.of(new Label("start"),
									new RegisterCommand(RegisterCommand.Op.incw, 0),
									new RegisterCommand(RegisterCommand.Op.decw, 2),
		                            NoArgCommand.Ret), compact.getCommands());
	}

	@Test
	public void testAndOrXor() {
		final CommandList list = new CommandList();
		list.add(new Label("start"));
		list.add(new ArithmeticC(ArithmeticOp.and, 0, 0xFF));
		list.add(new ArithmeticC(ArithmeticOp.and, 1, 0));
		list.add(new ArithmeticC(ArithmeticOp.or, 2, 0xFF));
		list.add(new ArithmeticC(ArithmeticOp.or, 3, 0));
		list.add(new ArithmeticC(ArithmeticOp.xor, 4, 0xFF));
		list.add(new ArithmeticC(ArithmeticOp.xor, 5, 0));
		list.add(NoArgCommand.Ret);
		final CommandList compact = CommandListOptimizations.optimize(list);
		Assert.assertEquals(List.of(new Label("start"),
		                            new LdLiteral(1, 0),
		                            new LdLiteral(2, 0xFF),
		                            new RegisterCommand(RegisterCommand.Op.com, 4),
		                            NoArgCommand.Ret), compact.getCommands());
	}

	@Test
	public void testLd() {
		final CommandList list = new CommandList();
		list.add(new Label("start"));
		list.add(new Ld(8, 0));
		list.add(new Ld(9, 1));
		list.add(new Ld(0, 8));
		list.add(new Ld(1, 9));
		list.add(NoArgCommand.Ret);
		final CommandList compact = CommandListOptimizations.optimize(list);
		Assert.assertEquals(List.of(new Label("start"),
		                            new Ld(8, 0),
		                          	new Ld(9, 1),
		                            NoArgCommand.Ret), compact.getCommands());
	}
}
