package de.regnis.bril;

import de.regnis.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Thomas Singer
 */
final class BrilVars {

	// Fields =================================================================

	private final Map<String, VarStore> varToStore = new HashMap<>();

	private int registers;
	private int registerParameter;
	private int pushedParameters;
	private int spilledVariables;

	// Setup ==================================================================

	public BrilVars() {
	}

	// Accessing ==============================================================

	public void assign(List<String> arguments, Set<String> localVars, Function<String, Integer> varToVirtualRegister) {
		final Map<Integer, VarStore> virtualVarToStore = new HashMap<>();
		for (int i = 0; i < arguments.size(); i++) {
			final String argName = arguments.get(i);
			final int virtualRegister = varToVirtualRegister.apply(argName);
			Utils.assertTrue(virtualRegister == i);
			final VarStore varStore;
			if (virtualRegister < 2) {
				varStore = new Register(BrilVarMapping.ARG0_REGISTER + 2 * virtualRegister);
				registerParameter++;
			}
			else {
				varStore = new PushedParameter(2 * pushedParameters);
				pushedParameters++;
			}
			virtualVarToStore.put(virtualRegister, varStore);
			varToStore.put(argName, varStore);
		}

		for (String localVar : localVars) {
			final int virtualRegister = varToVirtualRegister.apply(localVar);
			VarStore varStore = virtualVarToStore.get(virtualRegister);
			if (varStore == null) {
				if (registers < 3) {
					varStore = new Register(BrilVarMapping.VAR0_REGISTER + 2 * registers);
					registers++;
					virtualVarToStore.put(virtualRegister, varStore);
				}
				else {
					varStore = new SpilledRegister(2 * spilledVariables);
					spilledVariables++;
					virtualVarToStore.put(virtualRegister, varStore);
				}
			}
			varToStore.put(localVar, varStore);
		}
	}

	public int getOffset(String var) {
		final VarStore varStore = Utils.notNull(varToStore.get(var));
		if (varStore instanceof Register register) {
			return -register.reg;
		}
		if (varStore instanceof SpilledRegister spilledRegister) {
			return 2 * registers + spilledRegister.i;
		}
		if (varStore instanceof PushedParameter parameter) {
			//noinspection OverlyComplexArithmeticExpression
			return 2 * registers + 2 * spilledVariables + 2 + 2 * pushedParameters - parameter.i;
		}
		throw new IllegalStateException(var + ": " + varStore);
	}

	public int getByteCountForSpilledLocalVars() {
		return 2 * spilledVariables;
	}

	// Inner Classes ==========================================================

	private interface VarStore {
	}

	private record Register(int reg) implements VarStore {
	}

	private record PushedParameter(int i) implements VarStore {
	}

	private record SpilledRegister(int i) implements VarStore {
	}
}
