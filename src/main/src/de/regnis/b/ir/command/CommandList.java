package de.regnis.b.ir.command;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class CommandList {

	// Fields =================================================================

	private final List<Command> commands = new ArrayList<>();

	// Setup ==================================================================

	public CommandList() {
	}

	// Accessing ==============================================================

	public void add(@NotNull Command command) {
		commands.add(command);
	}

	@NotNull
	public List<Command> getCommands() {
		return Collections.unmodifiableList(commands);
	}
}
