package de.regnis.b.ir;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public interface StackPositionProvider {

	RegistersToPush getRegistersToPush();

	int getRegister(@NotNull String varName);

	int getStackPosition(@NotNull String varName);

	record RegistersToPush(int startRegister, int count, int localVarsStoredOnStack) {
	}
}
