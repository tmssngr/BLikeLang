package de.regnis.b.ir;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Thomas Singer
 */
public class RegisterAllocation2 {

	// Fields =================================================================

	private final Map<String, Integer> varToRegister = new HashMap<>();
	private final UndirectedGraph<String> interferenceGraph = new UndirectedGraph<>();

	// Setup ==================================================================

	public RegisterAllocation2() {
	}

	// Accessing ==============================================================

	public void addEdgesBetween(@NotNull Set<String> objects) {
		interferenceGraph.addEdgesBetween(objects);
	}

	public void setRegister(@NotNull String var, int register) {
		if (varToRegister.containsKey(var)) {
			throw new IllegalArgumentException();
		}

		varToRegister.put(var, register);
	}

	public void build() {
		final Set<String> pendingVars = new HashSet<>(interferenceGraph.getObjects());
		pendingVars.removeAll(varToRegister.keySet());

		while (pendingVars.size() > 0) {
			final String var = getVarWithHighestEdgeCount(pendingVars, interferenceGraph);
			pendingVars.remove(var);

			final int register = getNextFreeRegister(var);
			setRegister(var, register);
		}
	}

	public int getMaxUsedRegister() {
		int maxUsedRegister = -1;
		for (Map.Entry<String, Integer> entry : varToRegister.entrySet()) {
			maxUsedRegister = Math.max(maxUsedRegister, entry.getValue());
		}
		return maxUsedRegister;
	}

	public boolean containsVar(@NotNull String name) {
		return varToRegister.containsKey(name);
	}

	public Map<String, Integer> getMappingInOrder() {
		final LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
		interferenceGraph.getObjects().forEach(var -> result.put(var, getVirtualRegister(var)));
		return result;
	}

	public int getVirtualRegister(@NotNull String var) {
		return varToRegister.get(var);
	}

	// Utils ==================================================================

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
}
