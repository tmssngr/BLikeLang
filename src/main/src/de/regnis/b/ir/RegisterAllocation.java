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

	private final Map<String, Integer> varToRegister = new HashMap<>();
	private final UndirectedGraph<String> interferenceGraph;
	private final int parameterCount;

	// Setup ==================================================================

	private RegisterAllocation(@NotNull ControlFlowGraph flowGraph) {
		interferenceGraph = new UndirectedGraph<>();

		final List<FuncDeclarationParameter> parameters = flowGraph.getParameters();
		parameterCount = parameters.size();

		final Set<String> parameterNames = Utils.convert(parameters, new LinkedHashSet<>(), FuncDeclarationParameter::name);
		assertTrue(parameterNames.size() == parameters.size());
		interferenceGraph.addEdgesBetween(parameterNames);

		final ControlFlowGraphVarUsageDetector usages = ControlFlowGraphVarUsageDetector.detectVarUsages(flowGraph);
		flowGraph.iterate(block -> {
			interferenceGraph.addEdgesBetween(usages.getVarsBefore(block));
			if (block instanceof BasicBlock) {
				final BasicBlock basicBlock = (BasicBlock) block;
				for (SimpleStatement statement : basicBlock.getStatements()) {
					interferenceGraph.addEdgesBetween(usages.getVarsAfter(statement));
				}
			}
		});

		int register = 0;
		for (FuncDeclarationParameter parameter : parameters) {
			setRegister(parameter.name(), register);
			register++;
		}
	}

	// Utils ==================================================================

	@NotNull
	private Result run() {
		final Set<String> pendingVars = new HashSet<>(interferenceGraph.getObjects());
		pendingVars.removeAll(varToRegister.keySet());

		while (pendingVars.size() > 0) {
			final String var = getVarWithHighestEdgeCount(pendingVars, interferenceGraph);
			pendingVars.remove(var);

			final int register = getNextFreeRegister(var);
			setRegister(var, register);
		}

		int maxUsedRegister = -1;
		for (Map.Entry<String, Integer> entry : varToRegister.entrySet()) {
			maxUsedRegister = Math.max(maxUsedRegister, entry.getValue());
		}

		final int returnCount = varToRegister.containsKey(ControlFlowGraph.RESULT) ? 1 : 0;
		final int localVarRegisterCount = maxUsedRegister + 1 - Math.max(parameterCount, returnCount);

		final Map<String, Integer> varToRegisterInOriginalOrder = new LinkedHashMap<>();
		interferenceGraph.getObjects().forEach(var -> varToRegisterInOriginalOrder.put(var, varToRegister.get(var)));
		return new Result(parameterCount, returnCount, localVarRegisterCount, varToRegisterInOriginalOrder);
	}

	private void setRegister(@NotNull String var, int register) {
		if (varToRegister.containsKey(var)) {
			throw new IllegalArgumentException();
		}

		varToRegister.put(var, register);
	}

	private int getNextFreeRegister(@NotNull String var) {
		final Set<String> interferedVars = interferenceGraph.getEdges(var);
		final List<Integer> usedRegisters = getUsedRegisters(interferedVars);
		if (usedRegisters.isEmpty()) {
			return 0;
		}

		usedRegisters.sort(Integer::compareTo);
		int i = 0;
		for (; i < usedRegisters.size(); i++) {
			final int usedRegister = usedRegisters.get(i);
			if (usedRegister != i) {
				break;
			}
		}
		return i;
	}

	private List<Integer> getUsedRegisters(Set<String> interferedVars) {
		final Set<Integer> usedRegisters = new HashSet<>();
		for (String var : interferedVars) {
			final Integer register = varToRegister.get(var);
			if (register != null) {
				usedRegisters.add(register);
			}
		}
		return new ArrayList<>(usedRegisters);
	}

	private String getVarWithHighestEdgeCount(Set<String> varsToHandle, UndirectedGraph<String> graph) {
		int nextSize = -1;
		String nextVar = null;
		for (String var : varsToHandle) {
			final int size = graph.getEdges(var).size();
			if (size > nextSize) {
				nextSize = size;
				nextVar  = var;
			}
		}

		return nextVar;
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
