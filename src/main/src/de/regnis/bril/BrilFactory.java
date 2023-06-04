package de.regnis.bril;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * according to https://capra.cs.cornell.edu/bril/lang/
 *
 * @author Thomas Singer
 */
public class BrilFactory {

	// Constants ==============================================================

	private static final String KEY_NAME = "name";
	private static final String KEY_TYPE = "type";
	private static final String KEY_ARGS = "args";
	private static final String KEY_INSTRS = "instrs";
	private static final String KEY_ARG_NAME = "name";

	// Static =================================================================

	@NotNull
	public static String getName(BrilNode function) {
		return function.getString(KEY_NAME);
	}

	@NotNull
	public static String getType(BrilNode function) {
		return function.getString(KEY_TYPE);
	}

	@NotNull
	public static BrilNode createFunction(String name, String type, List<BrilNode> arguments) {
		final BrilNode node = new BrilNode();
		node.set(KEY_NAME, name);
		node.set(KEY_TYPE, type);
		node.getOrCreateNodeList(KEY_ARGS)
				.addAll(arguments);
		return node;
	}

	@NotNull
	public static List<BrilNode> getArguments(BrilNode function) {
		return function.getOrCreateNodeList(KEY_ARGS);
	}

	@NotNull
	public static List<BrilNode> getInstructions(BrilNode function) {
		return function.getOrCreateNodeList(KEY_INSTRS);
	}

	@NotNull
	public static String getArgName(BrilNode argument) {
		return argument.getString(KEY_ARG_NAME);
	}

	// Fields =================================================================

	private final BrilNode root = new BrilNode();

	// Setup ==================================================================

	public BrilFactory() {
	}

	public static BrilNode argument(String name, String type) {
		return new BrilNode()
				.set("name", name)
				.set("type", type);
	}

	// Accessing ==============================================================

	public void addFunction(String name, String type,
	                        List<BrilNode> arguments,
	                        List<BrilNode> instructions) {
		final List<BrilNode> functions = root.getOrCreateNodeList("functions");
		for (BrilNode function : functions) {
			if (getName(function).equals(name)) {
				throw new IllegalArgumentException("a function with name " + name + " already exists");
			}
		}

		final Set<String> argumentNames = new HashSet<>();
		for (BrilNode argument : arguments) {
			final String argName = argument.getString(KEY_ARG_NAME);
			if (!argumentNames.add(argName)) {
				throw new IllegalArgumentException("an argument with name " + argName + " already exists");
			}
		}

		final BrilNode node = createFunction(name, type, arguments);
		node.getOrCreateNodeList(KEY_INSTRS)
				.addAll(instructions);
		functions.add(node);
	}
}
