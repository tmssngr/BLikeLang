package de.regnis.b;

import de.regnis.b.node.Type;
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

	public void declareVariable(@NotNull String name, @NotNull Type type, @NotNull VariableKind kind) {
		if (variables.containsKey(name)) {
			throw new AlreadyDefinedException(name);
		}

		if (scopeKind == ScopeKind.Local) {
			for (SymbolScope scope = parentScope; scope != null; scope = scope.parentScope) {
				if (scope.scopeKind == ScopeKind.Parameter
						&& scope.variables.containsKey(name)) {
					throw new AlreadyDefinedException(name);
				}
			}
		}

		variables.put(name, new Variable(type, kind));
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
	public Type getVariableType(@NotNull String name) {
		SymbolScope scope = this;
		while (scope != null) {
			final Variable variable = scope.variables.get(name);
			if (variable != null) {
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

		private Variable(@NotNull Type type, @NotNull VariableKind kind) {
			this.type = type;
			this.kind = kind;
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
