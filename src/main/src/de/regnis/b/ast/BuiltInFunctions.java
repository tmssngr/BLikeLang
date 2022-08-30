package de.regnis.b.ast;

import de.regnis.b.ir.command.Command;
import de.regnis.b.type.Type;
import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Thomas Singer
 */
public final class BuiltInFunctions {

	// Fields =================================================================

	private final Map<String, FunctionSignature> nameToFunctions = new HashMap<>();
	private final Map<String, FunctionCommandFactory> nameToFactory = new HashMap<>();

	// Setup ==================================================================

	public BuiltInFunctions() {
	}

	// Accessing ==============================================================

	public void add(@NotNull Type returnType, @NotNull String name, int parameterCount, @NotNull FunctionCommandFactory factory) {
		Utils.assertTrue(!nameToFunctions.containsKey(name));

		nameToFunctions.put(name, new FunctionSignature(returnType, parameterCount));
		nameToFactory.put(name, factory);
	}

	@Nullable
	public FunctionSignature get(@NotNull String name) {
		return nameToFunctions.get(name);
	}

	@Nullable
	public FunctionCommandFactory getFactory(@NotNull String name) {
		return nameToFactory.get(name);
	}

	// Inner Classes ==========================================================

	public interface FunctionCommandFactory {
		void handleCall(@NotNull List<Expression> parameters, @Nullable String assignReturnToVar, @NotNull CommandFactory factory);
	}

	public interface CommandFactory {
		void loadToRegister(@NotNull Expression parameterExpression, int register);

		void addCommand(@NotNull Command command);
	}
}
