package de.regnis.b.ast;

import de.regnis.b.type.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record FunctionSignature(@NotNull Type returnType, int parameterCount) {
}
