package de.regnis.b.ast.transformation;

import de.regnis.b.Messages;
import de.regnis.b.ast.Position;
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

	// Fields =================================================================

	private final Map<String, Variable> variables = new HashMap<>();
	private final SymbolScope parentScope;
	private final ScopeKind scopeKind;

	// Setup ==================================================================

	private SymbolScope(@Nullable SymbolScope parentScope, ScopeKind scopeKind) {
		this.parentScope = parentScope;
		this.scopeKind   = scopeKind;
	}

	// Accessing ==============================================================

	public void declareVariable(@NotNull String name, @NotNull String newName, @NotNull Position position) {
		if (variables.containsKey(name)) {
			throw new TransformationFailedException(scopeKind == ScopeKind.Parameter
					                                        ? Messages.errorParameterAlreadyDeclared(position.line(), position.column(), name)
					                                        : Messages.errorVarAlreadyDeclared(position.line(), position.column(), name));
		}

		if (scopeKind == ScopeKind.Local) {
			for (SymbolScope scope = parentScope; scope != null; scope = scope.parentScope) {
				if (scope.scopeKind == ScopeKind.Parameter
						&& scope.variables.containsKey(name)) {
					throw new TransformationFailedException(Messages.errorVarAlreadyDeclaredAsParameter(position.line(), position.column(), name));
				}
			}
		}

		variables.put(name, new Variable(newName, position));
	}

	public SymbolScope createChildMap(ScopeKind scopeKind) {
		return new SymbolScope(this, scopeKind);
	}

	@NotNull
	public Variable variableRead(@NotNull String name, @NotNull Position position) {
		SymbolScope scope = this;
		while (scope != null) {
			final Variable variable = scope.variables.get(name);
			if (variable != null) {
				variable.used = true;
				return variable;
			}

			scope = scope.parentScope;
		}
		throw new TransformationFailedException(Messages.errorUndeclaredVariable(position.line(), position.column(), name));
	}

	public void reportUnusedVariables(@NotNull StringOutput output) {
		for (Map.Entry<String, Variable> entry : variables.entrySet()) {
			final String name = entry.getKey();
			final Variable variable = entry.getValue();
			if (!variable.used) {
				final int line = variable.position.line();
				final int column = variable.position.column();
				output.print(scopeKind == ScopeKind.Parameter
						             ? Messages.warningUnusedParameter(line, column, name)
						             : Messages.warningUnusedVar(line, column, name));
				output.println();
			}
		}
	}

	// Inner Classes ==========================================================

	public enum ScopeKind {
		Global, Parameter, Local
	}

	public static final class Variable {
		public final String newName;
		private final Position position;

		private boolean used;

		private Variable(String newName, Position position) {
			this.newName  = newName;
			this.position = position;
		}
	}
}
