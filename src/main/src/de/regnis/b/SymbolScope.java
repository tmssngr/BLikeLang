package de.regnis.b;

import de.regnis.b.node.Type;
import de.regnis.b.out.StringOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
	public static String msgVarIsUnused(int line, int column, String name) {
		return line + ":" + column + ": Variable " + name + " is unused";
	}

	@NotNull
	public static String msgParamIsUnused(int line, int column, String name) {
		return line + ":" + column + ": Parameter " + name + " is unused";
	}

	// Fields =================================================================

	private final Map<String, Variable> variables = new HashMap<>();
	private final Map<String, Function> functions = new HashMap<>();
	private final SymbolScope parentScope;
	private final ScopeKind scopeKind;

	// Setup ==================================================================

	private SymbolScope(@Nullable SymbolScope parentScope, ScopeKind scopeKind) {
		this.parentScope = parentScope;
		this.scopeKind = scopeKind;
	}

	// Accessing ==============================================================

	public void declareVariable(@NotNull String name, @NotNull Type type, int line, int column, @NotNull VariableKind kind) {
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

		variables.put(name, new Variable(type, kind, line, column));
	}

	public void declareFunction(@NotNull String name, @NotNull Type type, @NotNull List<Type> parameterTypes) {
		if (scopeKind != ScopeKind.Global) {
			throw new UnsupportedOperationException("functions only are allowed for the global scope");
		}

		if (functions.containsKey(name)) {
			throw new AlreadyDefinedException(name);
		}

		functions.put(name, new Function(type, parameterTypes));
	}

	public SymbolScope createChildMap(ScopeKind scopeKind) {
		return new SymbolScope(this, scopeKind);
	}

	/**
	 * @throws UndeclaredException
	 */
	@NotNull
	public Type variableRead(@NotNull String name) {
		SymbolScope scope = this;
		while (scope != null) {
			final Variable variable = scope.variables.get(name);
			if (variable != null) {
				variable.used = true;
				return variable.type;
			}

			scope = scope.parentScope;
		}
		throw new UndeclaredException(name);
	}

	@NotNull
	public Function getFunction(@NotNull String name) {
		final SymbolScope rootScope = getRootScope();
		final Function function = rootScope.functions.get(name);
		if (function == null) {
			throw new UndeclaredException(name);
		}
		return function;
	}

	public void reportUnusedVariables(@NotNull StringOutput output) {
		for (Map.Entry<String, Variable> entry : variables.entrySet()) {
			final String name = entry.getKey();
			final Variable variable = entry.getValue();
			if (!variable.used) {
				final int line = variable.line;
				final int column = variable.column;
				output.print(scopeKind == ScopeKind.Parameter
						             ? msgParamIsUnused(line, column, name)
						             :msgVarIsUnused(line, column, name));
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

	public enum VariableKind {
		Global, Parameter, Local, Temp
	}

	private static final class Variable {
		private final Type type;
		private final VariableKind kind;
		private final int line;
		private final int column;

		private boolean used;

		private Variable(@NotNull Type type, @NotNull VariableKind kind, int line, int column) {
			this.type = type;
			this.kind = kind;
			this.line = line;
			this.column = column;
		}
	}

	public static final class Function {
		public final Type type;
		public final List<Type> parameterTypes;

		private Function(Type type, List<Type> parameterTypes) {
			this.type = type;
			this.parameterTypes = Collections.unmodifiableList(parameterTypes);
		}
	}

	public static final class AlreadyDefinedException extends RuntimeException {
		public AlreadyDefinedException(String message) {
			super(message);
		}
	}

	public static final class UndeclaredException extends RuntimeException {
		public UndeclaredException(String message) {
			super(message);
		}
	}
}
