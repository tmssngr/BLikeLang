package de.regnis.bril;

import java.util.List;

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

	public FunctionFactory addFunction(String name, String type) {
		return new FunctionFactory(this, name, type);
	}

	// Utils ==================================================================

	private void addFunction(BrilNode node) {
		final String functionName = node.getString("name");

		final List<BrilNode> functions = root.getOrCreateNodeList("functions");
		for (BrilNode function : functions) {
			if (function.getString("name").equals(functionName)) {
				throw new IllegalArgumentException("a function with name " + functionName + " already exists");
			}
		}

		functions.add(node);
	}

	// Inner Classes ==========================================================

	public static final class FunctionFactory {
		private final BrilNode node = new BrilNode();
		private final BrilFactory factory;

		private FunctionFactory(BrilFactory factory, String name, String type) {
			this.factory = factory;

			node.set("name", name);
			node.set("type", type);
		}

		public FunctionFactory addArgument(String name, String type) {
			final List<BrilNode> args = node.getOrCreateNodeList("args");
			for (BrilNode arg : args) {
				if (arg.getString("name").equals(name)) {
					throw new IllegalArgumentException("argument with name " + name + " already exists");
				}
			}

			args.add(new BrilNode()
					         .set("name", name)
					         .set("type", type));
			return this;
		}

		public void addInstructions(InstructionsProvider provider) {
			provider.create(new InstructionsFactory(node));
			factory.addFunction(node);
		}
	}

	public interface InstructionsProvider {
		void create(InstructionsFactory factory);
	}

	public static final class InstructionsFactory {

		private final BrilNode functionNode;

		public InstructionsFactory(BrilNode functionNode) {
			this.functionNode = functionNode;
		}

		public void label(String name) {
			append(new BrilNode()
					       .set("label", name));
		}

		public void id(String dest, String src) {
			append(new BrilNode()
					       .set("dest", dest)
					       .set("op", "id")
					       .set("var", src));
		}

		public void constant(String dest, int value) {
			append(new BrilNode()
					       .set("dest", dest)
					       .set("op", "const")
					       .set("value", value));
		}

		public void add(String dest, String var1, String var2) {
			append(new BrilNode()
					       .set("dest", dest)
					       .set("op", "add")
					       .set("var1", var1)
					       .set("var2", var2));
		}

		public void lt(String dest, String var1, String var2) {
			append(new BrilNode()
					       .set("dest", dest)
					       .set("op", "lt")
					       .set("var1", var1)
					       .set("var2", var2));
		}

		public void br(String var, String thenLabel, String elseLabel) {
			append(new BrilNode()
					       .set("op", "br")
					       .set("cond", "var")
					       .set("then", thenLabel)
					       .set("else", elseLabel));
		}

		public void jmp(String label) {
			append(new BrilNode()
					       .set("op", "jmp")
					       .set("label", label));
		}

		public void ret() {
			append(new BrilNode()
					       .set("op", "ret"));
		}

		public void call(String name, List<String> args) {
			append(new BrilNode()
					       .set("op", "call")
					       .set("name", name)
					       .set("args", args));
		}

		private void append(BrilNode node) {
			final List<BrilNode> instrs = functionNode.getOrCreateNodeList("instrs");
			instrs.add(node);
		}
	}
}
