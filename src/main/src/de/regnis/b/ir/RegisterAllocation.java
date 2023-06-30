package de.regnis.b.ir;

import de.regnis.b.ast.FuncDeclarationParameter;
import de.regnis.b.ast.SimpleStatement;
import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static de.regnis.utils.Utils.assertTrue;
import static de.regnis.utils.Utils.notNull;

/**
 * @author Thomas Singer
 */
public final class RegisterAllocation {

	// Static =================================================================

	@NotNull
	public static Result run(@NotNull ControlFlowGraph graph) {
		final RegisterAllocation registerAllocation = new RegisterAllocation(graph);
		return registerAllocation.run();
	}

	// Fields =================================================================

	private final RegisterAllocation2 registerAllocation = new RegisterAllocation2();
	private final int parameterCount;

	// Setup ==================================================================

	private RegisterAllocation(@NotNull ControlFlowGraph flowGraph) {
		final List<FuncDeclarationParameter> parameters = flowGraph.getParameters();
		parameterCount = parameters.size();

		final Set<String> parameterNames = Utils.convert(parameters, new LinkedHashSet<>(), FuncDeclarationParameter::name);
		assertTrue(parameterNames.size() == parameters.size());
		registerAllocation.addEdgesBetween(parameterNames);

		final ControlFlowGraphVarUsageDetector usages = ControlFlowGraphVarUsageDetector.detectVarUsages(flowGraph);
		flowGraph.iterate(block -> {
			registerAllocation.addEdgesBetween(usages.getVarsBefore(block));
			if (block instanceof StatementsBlock sblock) {
				for (SimpleStatement statement : sblock.getStatements()) {
					registerAllocation.addEdgesBetween(usages.getVarsAfter(statement));
				}
			}
		});

		int register = 0;
		for (FuncDeclarationParameter parameter : parameters) {
			registerAllocation.setRegister(parameter.name(), register);
			register++;
		}
	}

	// Utils ==================================================================

	@NotNull
	private Result run() {
		registerAllocation.build();

		final int maxUsedRegister = registerAllocation.getMaxUsedRegister();

		final int returnCount = registerAllocation.containsVar(ControlFlowGraph.RESULT) ? 1 : 0;
		final int localVarRegisterCount = maxUsedRegister + 1 - Math.max(parameterCount, returnCount);

		final Map<String, Integer> varToRegisterInOriginalOrder = registerAllocation.getMappingInOrder();
		return new Result(parameterCount, returnCount, localVarRegisterCount, varToRegisterInOriginalOrder);
	}

	// Inner Classes ==========================================================

	public static final class Result {
		public final int parameterCount;
		public final int returnVarCount;
		public final int localVarRegisterCount;
		private final Map<String, Integer> varToRegister;

		public Result(int parameterCount, int returnVarCount, int localVarRegisterCount, @NotNull Map<String, Integer> varToRegister) {
			assertTrue(parameterCount >= 0);
			assertTrue(returnVarCount == 0 || returnVarCount == 1);
			assertTrue(localVarRegisterCount >= 0);

			this.parameterCount        = parameterCount;
			this.returnVarCount        = returnVarCount;
			this.localVarRegisterCount = localVarRegisterCount;
			this.varToRegister         = varToRegister;
		}

		public int get(@NotNull String key) {
			return notNull(varToRegister.get(key));
		}

		@NotNull
		public Set<String> getVarNames() {
			return Collections.unmodifiableSet(varToRegister.keySet());
		}

		@NotNull
		public Map<String, Integer> debugToMap() {
			return new HashMap<>(varToRegister);
		}
	}
}
