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
}
