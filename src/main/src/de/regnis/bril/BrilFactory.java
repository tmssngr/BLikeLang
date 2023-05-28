package de.regnis.bril;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * according to https://capra.cs.cornell.edu/bril/lang/
 *
 * @author Thomas Singer
 */
public class BrilFactory {

	// Constants ==============================================================

	private static final String OP = "op";

	public static final String JMP = "jmp";

	// Static =================================================================

	@NotNull
	public static BrilNode call(String name, List<String> args) {
		return new BrilNode()
				.set(OP, "call")
				.set("name", name)
				.set("args", args);
	}

	@NotNull
	public static BrilNode ret() {
		return new BrilNode()
				.set(OP, "ret");
	}

	@NotNull
	public static BrilNode jump(String label) {
		return new BrilNode()
				.set(OP, JMP)
				.set("label", label);
	}

	@NotNull
	public static BrilNode branch(String var, String thenLabel, String elseLabel) {
		return new BrilNode()
				.set(OP, "br")
				.set("cond", var)
				.set("then", thenLabel)
				.set("else", elseLabel);
	}

	@NotNull
	public static BrilNode lessThan(String dest, String var1, String var2) {
		return new BrilNode()
				.set("dest", dest)
				.set(OP, "lt")
				.set("var1", var1)
				.set("var2", var2);
	}

	@NotNull
	public static BrilNode add(String dest, String var1, String var2) {
		return new BrilNode()
				.set("dest", dest)
				.set(OP, "add")
				.set("var1", var1)
				.set("var2", var2);
	}

	@NotNull
	public static BrilNode constant(String dest, int value) {
		return new BrilNode()
				.set("dest", dest)
				.set(OP, "const")
				.set("value", value);
	}

	@NotNull
	public static BrilNode id(String dest, String src) {
		return new BrilNode()
				.set("dest", dest)
				.set(OP, "id")
				.set("var", src);
	}

	@NotNull
	public static BrilNode label(String name) {
		return new BrilNode()
				.set("label", name);
	}

	public static BrilNode print(String var) {
		return new BrilNode()
				.set(OP, "print")
				.set("var", var);
	}

	@Nullable
	public static String getOp(BrilNode node) {
		return node.getString(OP);
	}

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
		private final List<BrilNode> args;
		private final BrilFactory factory;

		private FunctionFactory(BrilFactory factory, String name, String type) {
			this.factory = factory;

			node.set("name", name);
			node.set("type", type);
			args = node.getOrCreateNodeList("args");
		}

		public FunctionFactory addArgument(String name, String type) {
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
			append(BrilFactory.label(name));
		}

		public void id(String dest, String src) {
			append(BrilFactory.id(dest, src));
		}

		public void constant(String dest, int value) {
			append(BrilFactory.constant(dest, value));
		}

		public void add(String dest, String var1, String var2) {
			append(BrilFactory.add(dest, var1, var2));
		}

		public void lt(String dest, String var1, String var2) {
			append(lessThan(dest, var1, var2));
		}

		public void br(String var, String thenLabel, String elseLabel) {
			append(branch(var, thenLabel, elseLabel));
		}

		public void jmp(String label) {
			append(jump(label));
		}

		public void ret() {
			append(BrilFactory.ret());
		}

		public void call(String name, List<String> args) {
			append(BrilFactory.call(name, args));
		}

		private void append(BrilNode node) {
			final List<BrilNode> instrs = functionNode.getOrCreateNodeList("instrs");
			instrs.add(node);
		}
	}
}
