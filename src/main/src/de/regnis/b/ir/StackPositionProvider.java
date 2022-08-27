package de.regnis.b.ir;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public interface StackPositionProvider {

	int getStackPosition(@NotNull String varName);
}
