package de.regnis.bril;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * according to https://capra.cs.cornell.edu/bril/lang/
 *
 * @author Thomas Singer
 */
public class BrilFactory {

	// Constants ==============================================================

	private static final String KEY_OP = "op";
	private static final String LABEL = "label";
	private static final String BR = "br";
	private static final String KEY_JMP_TARGET = "target";
	private static final String KEY_IF_TARGET = "ifTarget";
	private static final String KEY_ELSE_TARGET = "elseTarget";

	public static final String RET = "ret";
	public static final String JMP = "jmp";

	// Static =================================================================

	@NotNull
	public static BrilNode call(String name, List<String> args) {
		return new BrilNode()
				.set(KEY_OP, "call")
				.set("name", name)
				.set("args", args);
	}

	@NotNull
	public static BrilNode ret() {
		return new BrilNode()
				.set(KEY_OP, RET);
	}

	@NotNull
	public static BrilNode jump(String label) {
		return new BrilNode()
				.set(KEY_OP, JMP)
				.set(KEY_JMP_TARGET, label);
	}

	@NotNull
	public static BrilNode branch(String var, String thenLabel, String elseLabel) {
		return new BrilNode()
				.set(KEY_OP, BR)
				.set("cond", var)
				.set(KEY_IF_TARGET, thenLabel)
				.set(KEY_ELSE_TARGET, elseLabel);
	}

	@NotNull
	public static BrilNode lessThan(String dest, String var1, String var2) {
		return new BrilNode()
				.set(KEY_OP, "lt")
				.set("dest", dest)
				.set("var1", var1)
				.set("var2", var2);
	}

	@NotNull
	public static BrilNode add(String dest, String var1, String var2) {
		return new BrilNode()
				.set(KEY_OP, "add")
				.set("dest", dest)
				.set("var1", var1)
				.set("var2", var2);
	}

	@NotNull
	public static BrilNode constant(String dest, int value) {
		return new BrilNode()
				.set("dest", dest)
				.set(KEY_OP, "const")
				.set("value", value);
	}

	@NotNull
	public static BrilNode id(String dest, String src) {
		return new BrilNode()
				.set("dest", dest)
				.set(KEY_OP, "id")
				.set("var", src);
	}

	@NotNull
	public static BrilNode label(String name) {
		return new BrilNode()
				.set(LABEL, name);
	}

	public static BrilNode print(String var) {
		return new BrilNode()
				.set(KEY_OP, "print")
				.set("var", var);
	}

	@Nullable
	public static String getOp(BrilNode node) {
		return node.getString(KEY_OP);
	}


	@Nullable
	public static String getLabel(BrilNode brilNode) {
		return brilNode.getString(LABEL);
	}

	public static List<String> getJmpTargets(BrilNode node) {
		final List<String> labels = new ArrayList<>();
		final String op = getOp(node);
		if (JMP.equals(op)) {
			labels.add(node.getString(KEY_JMP_TARGET));
		}
		else if (BR.equals(op)) {
			labels.add(node.getString(KEY_IF_TARGET));
			labels.add(node.getString(KEY_ELSE_TARGET));
		}
		return labels;
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
