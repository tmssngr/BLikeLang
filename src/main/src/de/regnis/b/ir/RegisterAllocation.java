package de.regnis.b.ir;

import de.regnis.b.ast.FuncDeclaration;
import de.regnis.b.ast.FuncDeclarationParameter;
import de.regnis.b.ast.SimpleStatement;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Thomas Singer
 */
public final class RegisterAllocation {

	// Fields =================================================================

	private final Map<String, Set<Integer>> varToUsedRegisters = new HashMap<>();
	private final Map<String, Integer> varToRegister = new HashMap<>();
	private final UndirectedGraph<String> interferenceGraph;

	private int maxUsedRegister = -1;

	// Setup ==================================================================

	public RegisterAllocation(@NotNull ControlFlowGraph flowGraph) {
		final ControlFlowGraphVarUsageDetector usages = ControlFlowGraphVarUsageDetector.detectVarUsages(flowGraph);

		interferenceGraph = new UndirectedGraph<>();
		flowGraph.iterate(block -> {
			interferenceGraph.addEdgesBetween(usages.getVarsBefore(block));
			if (block instanceof BasicBlock) {
				final BasicBlock basicBlock = (BasicBlock) block;
				for (SimpleStatement statement : basicBlock.getStatements()) {
					interferenceGraph.addEdgesBetween(usages.getVarsAfter(statement));
				}
			}
		});

		for (String var : interferenceGraph.getObjects()) {
			varToUsedRegisters.put(var, new HashSet<>());
		}
	}

	// Accessing ==============================================================

	public void initializeParameters(@NotNull FuncDeclaration function) {
		int register = 0;
		for (FuncDeclarationParameter parameter : function.parameters().getParameters()) {
			interferenceGraph.addEdgesBetween(parameter.name());
			setRegister(parameter.name(), register);
			register++;
		}
	}

	public Map<String, Integer> run() {
		final Set<String> pendingVars = new HashSet<>(interferenceGraph.getObjects());
		pendingVars.removeAll(varToRegister.keySet());

		while (pendingVars.size() > 0) {
			final String var = getVarWithHighestEdgeCount(pendingVars, interferenceGraph);
			pendingVars.remove(var);

			final int register = getNextFreeRegister(var);
			setRegister(var, register);
		}

		return Collections.unmodifiableMap(varToRegister);
	}

	public int getMaxRegisterCount() {
		return maxUsedRegister + 1;
	}

	// Utils ==================================================================

	private void setRegister(@NotNull String var, int register) {
		if (varToRegister.containsKey(var)) {
			throw new IllegalArgumentException();
		}

		final Set<String> interferedVars = interferenceGraph.getEdges(var);
		varToRegister.put(var, register);
		for (String interferedVar : interferedVars) {
			final Set<Integer> usedRegisters = varToUsedRegisters.get(interferedVar);
			setUsedRegister(register, usedRegisters);
		}
	}

	private void setUsedRegister(int register, Set<Integer> usedRegisters) {
		usedRegisters.add(register);
		maxUsedRegister = Math.max(register, maxUsedRegister);
	}

	private int getNextFreeRegister(@NotNull String var) {
		final Set<Integer> allUsedRegisters = varToUsedRegisters.get(var);

		int register = 0;
		while (!isFree(register, allUsedRegisters)) {
			register += 1;
		}
		return register;
	}

	private boolean isFree(int register, Set<Integer> allUsedRegisters) {
		return !allUsedRegisters.contains(register);
	}

	private String getVarWithHighestEdgeCount(Set<String> varsToHandle, UndirectedGraph<String> graph) {
		int nextSize = -1;
		String nextVar = null;
		for (String var : varsToHandle) {
			final int size = graph.getEdges(var).size();
			if (size > nextSize) {
				nextSize = size;
				nextVar = var;
			}
		}

		return nextVar;
	}
}
