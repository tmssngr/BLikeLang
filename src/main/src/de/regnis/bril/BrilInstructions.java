package de.regnis.bril;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Thomas Singer
 */
public final class BrilInstructions {

	// Constants ==============================================================

	public static final String RET = "ret";
	public static final String JMP = "jmp";
	private static final String KEY_OP = "op";
	private static final String LABEL = "label";
	private static final String BR = "br";
	private static final String KEY_JMP_TARGET = "target";
	private static final String KEY_IF_TARGET = "ifTarget";
	private static final String KEY_ELSE_TARGET = "elseTarget";
	private static final String KEY_DEST = "dest";
	private static final String KEY_COND = "cond";
	private static final String CALL = "call";
	public static final String KEY_ARGS = "args";
	private static final String KEY_VAR1 = "var1";
	private static final String KEY_VAR2 = "var2";
	private static final String KEY_VAR = "var";

	// Static =================================================================

	@NotNull
	public static BrilNode call(String name, List<String> args) {
		return new BrilNode()
				.set(KEY_OP, CALL)
				.set("name", name)
				.set(KEY_ARGS, args);
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
				.set(KEY_COND, var)
				.set(KEY_IF_TARGET, thenLabel)
				.set(KEY_ELSE_TARGET, elseLabel);
	}

	@NotNull
	public static BrilNode lessThan(String dest, String var1, String var2) {
		return new BrilNode()
				.set(KEY_OP, "lt")
				.set(KEY_DEST, dest)
				.set(KEY_VAR1, var1)
				.set(KEY_VAR2, var2);
	}

	@NotNull
	public static BrilNode add(String dest, String var1, String var2) {
		return new BrilNode()
				.set(KEY_OP, "add")
				.set(KEY_DEST, dest)
				.set(KEY_VAR1, var1)
				.set(KEY_VAR2, var2);
	}

	@NotNull
	public static BrilNode constant(String dest, int value) {
		return new BrilNode()
				.set(KEY_DEST, dest)
				.set(KEY_OP, "const")
				.set("value", value);
	}

	@NotNull
	public static BrilNode id(String dest, String src) {
		return new BrilNode()
				.set(KEY_DEST, dest)
				.set(KEY_OP, "id")
				.set(KEY_VAR, src);
	}

	@NotNull
	public static BrilNode label(String name) {
		return new BrilNode()
				.set(LABEL, name);
	}

	public static BrilNode print(String var) {
		return new BrilNode()
				.set(KEY_OP, "print")
				.set(KEY_VAR, var);
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

	@Nullable
	public static String getDest(BrilNode node) {
		return node.getString(KEY_DEST);
	}

	public static Set<String> getRequiredVars(BrilNode node) {
		final Set<String> requiredVars = new HashSet<>();
		requiredVars.add(node.getString(KEY_COND));
		requiredVars.add(node.getString(KEY_VAR));
		requiredVars.add(node.getString(KEY_VAR1));
		requiredVars.add(node.getString(KEY_VAR2));
		requiredVars.addAll(node.getStringList(KEY_ARGS));
		requiredVars.remove(null);
		return requiredVars;
	}
}
