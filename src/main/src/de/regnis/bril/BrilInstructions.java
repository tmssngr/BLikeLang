package de.regnis.bril;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Thomas Singer
 */
public class BrilInstructions {

	// Constants ==============================================================

	public static final String ADD = "add";
	public static final String SUB = "sub";
	public static final String MUL = "mul";
	public static final String AND = "and";
	public static final String LT = "lt";
	public static final String BR = "br";
	public static final String CALL = "call";
	public static final String CONST = "const";
	public static final String ID = "id";
	public static final String JMP = "jmp";
	private static final String LABEL = "label";
	public static final String PRINT = "print";
	public static final String RET = "ret";
	public static final String KEY_OP = "op";
	private static final String KEY_JMP_TARGET = "target";
	private static final String KEY_IF_TARGET = "ifTarget";
	private static final String KEY_ELSE_TARGET = "elseTarget";
	public static final String KEY_DEST = "dest";
	public static final String KEY_TYPE = "type";
	private static final String KEY_ARGS = "args";
	private static final String KEY_VAR1 = "var1";
	private static final String KEY_VAR2 = "var2";
	private static final String KEY_VAR = "var";
	public static final String KEY_VALUE = "value";
	public static final String VOID = "void";
	public static final String INT = "int";
	public static final String BOOL = "bool";
	public static final String KEY_NAME = "name";

	// Static =================================================================

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
			labels.add(getThenTarget(node));
			labels.add(getElseTarget(node));
		}
		return labels;
	}

	public static String getThenTarget(BrilNode node) {
		return node.getString(KEY_IF_TARGET);
	}

	public static String getElseTarget(BrilNode node) {
		return node.getString(KEY_ELSE_TARGET);
	}

	public static String getTarget(BrilNode node) {
		return node.getString(KEY_JMP_TARGET);
	}

	@Nullable
	public static String getDest(BrilNode node) {
		return node.getString(KEY_DEST);
	}

	@Nullable
	public static String getType(BrilNode node) {
		return node.getString(KEY_TYPE);
	}

	public static void setDest(String dest, BrilNode node) {
		node.set(KEY_DEST, dest);
	}

	@NotNull
	public static String getVarNotNull(BrilNode node) {
		return node.getString(KEY_VAR);
	}

	@NotNull
	public static String getVar1NotNull(BrilNode node) {
		return node.getString(KEY_VAR1);
	}

	@NotNull
	public static String getVar2NotNull(BrilNode node) {
		return node.getString(KEY_VAR2);
	}

	public static int getIntValue(BrilNode node) {
		return node.getInt(KEY_VALUE);
	}

	@NotNull
	public static String getName(BrilNode node) {
		return node.getString(KEY_NAME);
	}

	public static Set<String> getRequiredVars(BrilNode node) {
		final Set<String> requiredVars = new HashSet<>();
		requiredVars.add(node.getString(KEY_VAR));
		requiredVars.add(node.getString(KEY_VAR1));
		requiredVars.add(node.getString(KEY_VAR2));
		requiredVars.addAll(getArgs(node));
		requiredVars.remove(null);
		return requiredVars;
	}

	@NotNull
	public static List<String> getArgs(BrilNode node) {
		return node.getStringList(KEY_ARGS);
	}

	public static void replaceVars(Function<String, String> varReplace, BrilNode node) {
		replace(KEY_VAR, varReplace, node);
		replace(KEY_VAR1, varReplace, node);
		replace(KEY_VAR2, varReplace, node);

		final List<String> args = getArgs(node);
		if (args.size() > 0) {
			final List<String> newArgs = new ArrayList<>(args.size());
			for (String arg : args) {
				newArgs.add(varReplace.apply(arg));
			}
			node.set(KEY_ARGS, newArgs);
		}
	}

	@NotNull
	public static BrilNode _id(String dest, String src) {
		return new BrilNode()
				.set(KEY_DEST, dest)
				.set(KEY_OP, ID)
				.set(KEY_VAR, src);
	}

	@NotNull
	public static BrilNode createLabel(String name) {
		return new BrilNode()
				.set(LABEL, name);
	}

	// Fields =================================================================

	private final List<BrilNode> instructions;

	// Setup ==================================================================

	public BrilInstructions() {
		this(new ArrayList<>());
	}

	public BrilInstructions(List<BrilNode> instructions) {
		this.instructions = instructions;
	}

	// Accessing ==============================================================

	public List<BrilNode> get() {
		return instructions;
	}

	@NotNull
	public BrilInstructions label(String name) {
		return add(createLabel(name));
	}

	@NotNull
	public BrilInstructions constant(String dest, int value) {
		return add(new BrilNode()
				           .set(KEY_DEST, dest)
				           .set(KEY_TYPE, INT)
				           .set(KEY_OP, CONST)
				           .set(KEY_VALUE, value));
	}

	@NotNull
	public BrilInstructions id(String dest, String src) {
		return add(_id(dest, src));
	}

	@NotNull
	public BrilInstructions binary(String dest, String type, String op, String var1, String var2) {
		return add(new BrilNode()
				           .set(KEY_OP, op)
				           .set(KEY_DEST, dest)
				           .set(KEY_TYPE, type)
				           .set(KEY_VAR1, var1)
				           .set(KEY_VAR2, var2));
	}

	@NotNull
	public BrilInstructions add(String dest, String var1, String var2) {
		return binary(dest, INT, ADD, var1, var2);
	}

	@NotNull
	public BrilInstructions sub(String dest, String var1, String var2) {
		return binary(dest, INT, SUB, var1, var2);
	}

	@NotNull
	public BrilInstructions mul(String dest, String var1, String var2) {
		return binary(dest, INT, MUL, var1, var2);
	}

	@NotNull
	public BrilInstructions and(String dest, String var1, String var2) {
		return binary(dest, INT, AND, var1, var2);
	}

	@NotNull
	public BrilInstructions lessThan(String dest, String var1, String var2) {
		return binary(dest, BOOL, LT, var1, var2);
	}

	@NotNull
	public BrilInstructions ret() {
		return add(new BrilNode()
				           .set(KEY_OP, RET));
	}

	@NotNull
	public BrilInstructions ret(String var) {
		return add(new BrilNode()
				           .set(KEY_OP, RET)
				           .set(KEY_VAR, var));
	}

	@NotNull
	public BrilInstructions jump(String label) {
		return add(new BrilNode()
				           .set(KEY_OP, JMP)
				           .set(KEY_JMP_TARGET, label));
	}

	@NotNull
	public BrilInstructions branch(String var, String thenLabel, String elseLabel) {
		return add(new BrilNode()
				           .set(KEY_OP, BR)
				           .set(KEY_VAR, var)
				           .set(KEY_IF_TARGET, thenLabel)
				           .set(KEY_ELSE_TARGET, elseLabel));
	}

	@NotNull
	public BrilInstructions call(String name, List<String> args) {
		return add(new BrilNode()
				           .set(KEY_OP, CALL)
				           .set(KEY_NAME, name)
				           .set(KEY_ARGS, args));
	}

	@NotNull
	public BrilInstructions call(String dest, String name, List<String> args) {
		return add(new BrilNode()
				           .set(KEY_OP, CALL)
				           .set(KEY_DEST, dest)
				           .set(KEY_TYPE, INT)
				           .set(KEY_NAME, name)
				           .set(KEY_ARGS, args));
	}

	@NotNull
	public BrilInstructions print(String var) {
		return add(new BrilNode()
				           .set(KEY_OP, PRINT)
				           .set(KEY_VAR, var));
	}

	@NotNull
	public BrilInstructions add(BrilNode brilNode) {
		instructions.add(brilNode);
		return this;
	}

	// Utils ==================================================================

	private static void replace(String key, Function<String, String> varReplace, BrilNode node) {
		final String var = node.getString(key);
		if (var != null) {
			node.set(key, varReplace.apply(var));
		}
	}

	// Inner Classes ==========================================================

	public abstract static class Handler {
		public final void visit(BrilNode instruction) {
			final String op = getOp(instruction);
			final String dest = getDest(instruction);
			if (CONST.equals(op)) {
				constant(dest, getIntValue(instruction));
			}
			else if (ID.equals(op)) {
				id(dest, getVarNotNull(instruction));
			}
			else if (ADD.equals(op)) {
				add(dest, getVar1NotNull(instruction), getVar2NotNull(instruction));
			}
			else if (SUB.equals(op)) {
				sub(dest, getVar1NotNull(instruction), getVar2NotNull(instruction));
			}
			else if (MUL.equals(op)) {
				mul(dest, getVar1NotNull(instruction), getVar2NotNull(instruction));
			}
			else if (AND.equals(op)) {
				and(dest, getVar1NotNull(instruction), getVar2NotNull(instruction));
			}
			else if (LT.equals(op)) {
				lessThan(dest, getVar1NotNull(instruction), getVar2NotNull(instruction));
			}
			else if (RET.equals(op)) {
				final String var = instruction.getString(KEY_VAR);
				if (var != null) {
					ret(var);
				}
				else {
					ret();
				}
			}
			else if (JMP.equals(op)) {
				jump(getTarget(instruction));
			}
			else if (BR.equals(op)) {
				branch(getVarNotNull(instruction), getThenTarget(instruction), getElseTarget(instruction));
			}
			else if (CALL.equals(op)) {
				if (dest != null) {
					call(dest, getName(instruction), getArgs(instruction));
				}
				else {
					call(getName(instruction), getArgs(instruction));
				}
			}
			else if (PRINT.equals(op)) {
				print(getVarNotNull(instruction));
			}
			else if (op == null) {
				label(getLabel(instruction));
			}
			else {
				throw new IllegalArgumentException("unsupported instruction " + instruction);
			}
		}

		protected void label(String name) {
		}

		protected void constant(String dest, int value) {
		}

		protected void id(String dest, String var) {
		}

		protected void add(String dest, String var1, String var2) {
		}

		protected void sub(String dest, String var1, String var2) {
		}

		protected void mul(String dest, String var1, String var2) {
		}

		protected void and(String dest, String var1, String var2) {
		}

		protected void lessThan(String dest, String var1, String var2) {
		}

		protected void ret() {
		}

		protected void ret(String var) {
		}

		protected void jump(String target) {
		}

		protected void branch(String var, String thenLabel, String elseLabel) {
		}

		protected void call(String name, List<String> args) {
		}

		protected void call(String dest, String name, List<String> args) {
		}

		protected void print(String var) {
		}
	}
}
