package de.regnis.bril;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * according to https://capra.cs.cornell.edu/bril/lang/
 *
 * @author Thomas Singer
 */
public class BrilFactory {

	// Fields =================================================================

	private final BrilNode root = new BrilNode();

	// Setup ==================================================================

	public BrilFactory() {
	}

	// Accessing ==============================================================

	public void addFunction(String name, String type,
	                        List<BrilNode> arguments,
	                        List<BrilNode> instructions) {
		final List<BrilNode> functions = root.getOrCreateNodeList("functions");
		for (BrilNode function : functions) {
			if (function.getString("name").equals(name)) {
				throw new IllegalArgumentException("a function with name " + name + " already exists");
			}
		}

		final Set<String> argumentNames = new HashSet<>();
		for (BrilNode argument : arguments) {
			final String argName = argument.getString("name");
			if (!argumentNames.add(argName)) {
				throw new IllegalArgumentException("an argument with name " + argName + " already exists");
			}
		}

		final BrilNode node = new BrilNode();
		node.set("name", name);
		node.set("type", type);
		node.getOrCreateNodeList(BrilInstructions.KEY_ARGS)
				.addAll(arguments);
		node.getOrCreateNodeList("instrs")
				.addAll(instructions);

		functions.add(node);
	}
}
