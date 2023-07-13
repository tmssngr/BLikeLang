package de.regnis.bril;

import de.regnis.utils.Utils;

import java.util.*;

/**
 * @author Thomas Singer
 */
final class BrilVars2 {

	// Fields =================================================================

	private final Map<String, InternalVarStore> varToStore = new HashMap<>();
	private final String prefixVirtualRegister;
	private final String prefixRegister;
	private final String prefixStackParameter;
	private final int maxParametersInRegisters;

	private int registers;
	private int registerParameterCount;
	private int pushedParameters;
	private int spilledVariables;

	// Setup ==================================================================

	public BrilVars2(String prefixVirtualRegister, String prefixRegister, String prefixStackParameter, int maxParametersInRegisters) {
		this.prefixVirtualRegister    = prefixVirtualRegister;
		this.prefixRegister           = prefixRegister;
		this.prefixStackParameter     = prefixStackParameter;
		this.maxParametersInRegisters = maxParametersInRegisters;
	}

	// Accessing ==============================================================

	public void assignArguments(List<String> arguments) {
		for (int i = 0; i < arguments.size(); i++) {
			final String argName = arguments.get(i);
			final InternalVarStore varStore;
			if (i < maxParametersInRegisters) {
				Utils.assertTrue(argName.equals(prefixRegister + i));
				varStore = new InternalRegister(i);
				registerParameterCount++;
			}
			else {
				Utils.assertTrue(argName.equals(prefixStackParameter + i));
				varStore = new PushedParameter(i - maxParametersInRegisters);
				pushedParameters++;
			}
			varToStore.put(argName, varStore);
		}
	}

	public void assignLocalVariables(Set<String> localVars) {
		int nextRegister = registerParameterCount;
		for (String localVar : localVars) {
			if (varToStore.containsKey(localVar)) {
				continue;
			}

			if (localVar.startsWith(prefixVirtualRegister)) {
				varToStore.put(localVar, new InternalRegister(nextRegister));
				nextRegister++;
				continue;
			}

			throw new UnsupportedOperationException(localVar);
/*

			if (registers < 3) {
				varStore = new InternalRegister(BrilVarMapping.VAR0_REGISTER + 2 * registers);
				registers++;
				varToStore.put(virtualRegister, varStore);
			}
			else {
				varStore = new SpilledRegister(2 * spilledVariables);
				spilledVariables++;
				varToStore.put(virtualRegister, varStore);
			}
			this.varToStore.put(localVar, varStore);
*/
		}
	}

	public Map<String, VarLocation> getVarToLocationMapping() {
		final Map<String, VarLocation> varToLocation = new HashMap<>();
		for (Map.Entry<String, InternalVarStore> entry : varToStore.entrySet()) {
			final String var = entry.getKey();
			final VarLocation location = getLocation(var);
			varToLocation.put(var, location);
		}
		return varToLocation;
	}

	public int getByteCountForSpilledLocalVars() {
		return 2 * spilledVariables;
	}

	public List<Integer> getUsedNonArgumentRegisters() {
		final Set<Integer> usedRegisters = new HashSet<>();
		for (Map.Entry<String, InternalVarStore> entry : varToStore.entrySet()) {
			final InternalVarStore store = entry.getValue();
			if (store instanceof InternalRegister register) {
				if (register.reg < registerParameterCount) {
					continue;
				}

				usedRegisters.add(2 * register.reg);
			}
		}
		final List<Integer> usedRegistersSorted = new ArrayList<>(usedRegisters);
		usedRegistersSorted.sort(Comparator.naturalOrder());
		return Collections.unmodifiableList(usedRegistersSorted);
	}

	// Utils ==================================================================

	private VarLocation getLocation(String var) {
		final InternalVarStore varStore = Utils.notNull(varToStore.get(var));
		if (varStore instanceof InternalRegister register) {
			return new VarLocation(true, 2 * register.reg);
		}

		if (true) {
			throw new UnsupportedOperationException(var);
		}
		// The subroutine receives the parameters on the stack. The stack pointer (SP, register pair FE+FF on Z8) points
		// to the previously pushed byte.
		//
		// PCH PCL  High(3rd parameter) Low(3rd parameter)  High(2nd parameter) Low(2nd parameter)
		//  ^
		//   \_ top of stack
		if (varStore instanceof SpilledRegister spilledRegister) {
			return new VarLocation(false, 2 * registers + spilledRegister.i);
		}
		if (varStore instanceof PushedParameter parameter) {
			//noinspection OverlyComplexArithmeticExpression
			return new VarLocation(false, 2 * registers + 2 * spilledVariables + 2 + 2 * pushedParameters - parameter.i);
		}
		throw new IllegalStateException(var + ": " + varStore);
	}

	// Inner Classes ==========================================================

	private interface InternalVarStore {
	}

	private record InternalRegister(int reg) implements InternalVarStore {
	}

	private record PushedParameter(int i) implements InternalVarStore {
	}

	private record SpilledRegister(int i) implements InternalVarStore {
	}

	public record VarLocation(boolean isRegister, int value) {
		public boolean isStackOffset() {
			return !isRegister;
		}

		public int reg() {
			Utils.assertTrue(isRegister);
			return value;
		}

		public int offset() {
			Utils.assertTrue(!isRegister);
			return value;
		}
	}
}
