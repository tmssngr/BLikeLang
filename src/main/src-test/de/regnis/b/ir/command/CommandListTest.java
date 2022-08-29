package de.regnis.b.ir.command;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Thomas Singer
 */
public final class CommandListTest {

	@Test
	public void testJumpsToNextCommand() {
		final CommandList list = new CommandList();
		list.add(new Label("start"));
		list.add(new JumpCommand("useless1"));
		list.add(new Label("useless1"));
		list.add(new LoadC(0, 1));
		list.add(new JumpCommand("useless2"));
		list.add(new Label("useless2"));
		list.add(NoArgCommand.Return);
		list.compact();
		Assert.assertEquals(List.of(new Label("start"),
		                            new LoadC(0, 1),
		                            NoArgCommand.Return), list.getCommands());
	}

	@Test
	public void testReplaceLabel() {
		final CommandList list = new CommandList();
		list.add(new Label("start"));
		list.add(new ArithmeticC(ArithmeticOp.cmp, 0, 1));
		list.add(new JumpCommand(JumpCondition.nz, "if_else"));
		list.add(new JumpCommand("if_then"));
		list.add(new Label("if_then"));
		list.add(new LoadC(0, 1));
		list.add(new JumpCommand("next1"));
		list.add(new Label("if_else"));
		list.add(new LoadC(0, 2));
		list.add(new Label("next1"));
		list.add(new Label("next"));
		list.add(NoArgCommand.Return);
		list.compact();
		Assert.assertEquals(List.of(new Label("start"),
		                            new ArithmeticC(ArithmeticOp.cmp, 0, 1),
		                            new JumpCommand(JumpCondition.nz, "if_else"),
		                            new LoadC(0, 1),
		                            new JumpCommand("next"),
		                            new Label("if_else"),
		                            new LoadC(0, 2),
		                            new Label("next"),
		                            NoArgCommand.Return), list.getCommands());
	}

	@Test
	public void testReplaceLabelIfElse() {
		final CommandList list = new CommandList();
		list.add(new Label("start"));
		list.add(new ArithmeticC(ArithmeticOp.cmp, 0, 1));
		list.add(new JumpCommand(JumpCondition.nz, "if_else"));
		list.add(new JumpCommand("if_then"));
		list.add(new Label("if_then"));
		list.add(new LoadC(0, 1));
		list.add(new JumpCommand("next"));
		list.add(new Label("if_else"));
		list.add(new Label("next"));
		list.add(NoArgCommand.Return);
		list.compact();
		Assert.assertEquals(List.of(new Label("start"),
		                            new ArithmeticC(ArithmeticOp.cmp, 0, 1),
		                            new JumpCommand(JumpCondition.nz, "next"),
		                            new LoadC(0, 1),
		                            new Label("next"),
		                            NoArgCommand.Return), list.getCommands());
	}

	@Test
	public void testRemoveUnusedLabels() {
		final CommandList list = new CommandList();
		list.add(new Label("start"));
		list.add(new Label("unused"));
		list.add(new LoadC(0, 1));
		list.add(new Label("if_else"));
		list.add(new Label("next"));
		list.add(NoArgCommand.Return);
		list.compact();
		Assert.assertEquals(List.of(new Label("start"),
		                            new LoadC(0, 1),
		                            NoArgCommand.Return), list.getCommands());
	}
}
