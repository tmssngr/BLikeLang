package de.regnis.b.ir;

import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * push parameter1
 * push parameter2
 * call function
 * <p>
 * function:
 *   push local var1
 *   push local var2
 * <p>
 * results in
 * SP+0 -> local var 2
 * SP+2 -> local var 1
 * SP+4 -> return address
 * SP+6 -> parameter2
 * SP+8 -> parameter1
 *
 * @author Thomas Singer
 */
public final class StackPositionProviderImpl implements StackPositionProvider {

	// Fields =================================================================

	private final Map<String, VariableInfo> varToStackPosition = new HashMap<>();
	private final RegistersToPush registersToPush;

	// Setup ==================================================================

	public StackPositionProviderImpl(@NotNull RegisterAllocation.Result registers, int maxVarsInRegisters, int startRegister) {
		Utils.assertTrue(maxVarsInRegisters >= 0);
		Utils.assertTrue(startRegister >= 0);
		Utils.assertTrue(startRegister % 2 == 0);

		final int parameterOrReturnCount = Math.max(registers.parameterCount, registers.returnVarCount);
		final int allCount = parameterOrReturnCount + registers.localVarRegisterCount;

		maxVarsInRegisters = Math.min(maxVarsInRegisters, registers.localVarRegisterCount);

		final int localVarsStoredOnStack = registers.localVarRegisterCount - maxVarsInRegisters;
		registersToPush = new RegistersToPush(startRegister, maxVarsInRegisters, localVarsStoredOnStack);

		for (String varName : registers.getVarNames()) {
			final int i = registers.get(varName);

			int stackPosition = -1;
			int register = -1;
			if (i >= allCount - maxVarsInRegisters) {
				register = i - (allCount - maxVarsInRegisters);
				register = startRegister + 2 * register;
			}
			else {
				int reverse = allCount - i - 1;
				if (reverse >= registers.localVarRegisterCount) {
					reverse++;
				}
				stackPosition = reverse * 2;
			}
			varToStackPosition.put(varName, new VariableInfo(stackPosition, register));
		}
	}

	// Implemented ============================================================

	@Override
	public RegistersToPush getRegistersToPush() {
		return registersToPush;
	}

	@Override
	public int getRegister(@NotNull String varName) {
		return varToStackPosition.get(varName).register;
	}

	@Override
	public int getStackPosition(@NotNull String varName) {
		return varToStackPosition.get(varName).stackPosition;
	}

	// Inner Classes ==========================================================

	private record VariableInfo(int stackPosition, int register) {
	}
}
