package de.regnis.b.ir.command;

import de.regnis.b.ir.ControlFlowGraphPrinter;
import de.regnis.b.out.StringOutput;
import de.regnis.b.out.StringStringOutput;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Thomas Singer
 */
public final class CommandList {

	// Fields =================================================================

	private final List<Command> commands = new ArrayList<>();

	// Setup ==================================================================

	public CommandList() {
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return print(new StringStringOutput()).toString();
	}

	// Accessing ==============================================================

	public void add(@NotNull Command command) {
		commands.add(command);
	}

	@NotNull
	public List<Command> getCommands() {
		return Collections.unmodifiableList(commands);
	}

	@NotNull
	public StringOutput print(@NotNull StringOutput output) {
		for (Command command : commands) {
			if (!(command instanceof Label)) {
				output.print(ControlFlowGraphPrinter.INDENTATION);
			}
			output.print(command.toString());
			output.println();
		}
		return output;
	}
}
