package de.regnis.b;

import de.regnis.b.node.Type;
import de.regnis.b.out.StringOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Singer
 */
public final class SymbolScope {

	// Static =================================================================

	public static SymbolScope createRootInstance() {
		return new SymbolScope(null, ScopeKind.Global);
	}

	@NotNull
	public static String msgVarAlreadyDeclared(@NotNull String name, int line, int column) {
		return line + ":" + column + ": variable " + name + " already declared";
	}

	@NotNull
	public static String msgVarAlreadyDeclaredAsParameter(@NotNull String name, int line, int column) {
		return line + ":" + column + ": local variable " + name + " already declared as parameter";
	}

	@NotNull
	public static String warningUnusedVar(int line, int column, String name) {
		return line + ":" + column + ": Variable " + name + " is unused";
	}

	@NotNull
	public static String warningUnusedParameter(int line, int column, String name) {
		return line + ":" + column + ": Parameter " + name + " is unused";
	}

	// Fields =================================================================

	private final Map<String, Variable> variables = new HashMap<>();
	private final SymbolScope parentScope;
	private final ScopeKind scopeKind;

	// Setup ==================================================================

	private SymbolScope(@Nullable SymbolScope parentScope, ScopeKind scopeKind) {
		this.parentScope = parentScope;
		this.scopeKind = scopeKind;
	}

	// Accessing ==============================================================

	public void declareVariable(@NotNull String name, @NotNull String newName, @NotNull Type type, int line, int column) {
		if (variables.containsKey(name)) {
			throw new AlreadyDefinedException(msgVarAlreadyDeclared(name, line, column));
		}

		if (scopeKind == ScopeKind.Local) {
			for (SymbolScope scope = parentScope; scope != null; scope = scope.parentScope) {
				if (scope.scopeKind == ScopeKind.Parameter
						&& scope.variables.containsKey(name)) {
					throw new AlreadyDefinedException(msgVarAlreadyDeclaredAsParameter(name, line, column));
				}
			}
		}

		variables.put(name, new Variable(newName, type, line, column));
	}

	public SymbolScope createChildMap(ScopeKind scopeKind) {
		return new SymbolScope(this, scopeKind);
	}

	/**
	 * @throws DetermineTypesTransformation.UndeclaredException
	 */
	@NotNull
	public Variable variableRead(@NotNull String name) {
		SymbolScope scope = this;
		while (scope != null) {
			final Variable variable = scope.variables.get(name);
			if (variable != null) {
				variable.used = true;
				return variable;
			}

			scope = scope.parentScope;
		}
		throw new DetermineTypesTransformation.UndeclaredException(name);
	}

	public void reportUnusedVariables(@NotNull StringOutput output) {
		for (Map.Entry<String, Variable> entry : variables.entrySet()) {
			final String name = entry.getKey();
			final Variable variable = entry.getValue();
			if (!variable.used) {
				final int line = variable.line;
				final int column = variable.column;
				output.print(scopeKind == ScopeKind.Parameter
						             ? warningUnusedParameter(line, column, name)
						             : warningUnusedVar(line, column, name));
				output.println();
			}
		}
	}

	// Utils ==================================================================

	@NotNull
	private SymbolScope getRootScope() {
		SymbolScope scope = this;
		while (scope.parentScope != null) {
			scope = scope.parentScope;
		}
		return scope;
	}

	// Inner Classes ==========================================================

	public enum ScopeKind {
		Global, Parameter, Local
	}

	public static final class Variable {
		public final String newName;
		public final Type type;
		private final int line;
		private final int column;

		private boolean used;

		private Variable(String newName, @NotNull Type type, int line, int column) {
			this.newName = newName;
			this.type = type;
			this.line = line;
			this.column = column;
		}
	}

	public static final class AlreadyDefinedException extends RuntimeException {
		public AlreadyDefinedException(String message) {
			super(message);
		}
	}
}
