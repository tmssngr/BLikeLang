package de.regnis.b;

import de.regnis.b.type.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class Messages {
	@NotNull
	public static String errorBooleanExpected(int line, int column, Type currentType) {
		return line + ":" + column + ": a boolean expression was expected, but got " + currentType;
	}

	@NotNull
	public static String errorBreakStatementNotInWhile(int line, int column) {
		return line + ":" + column + ": The break statement only is allowed inside a while loop.";
	}

	@NotNull
	public static String errorCantAssignType(int line, int column, String name, Type currentType, Type expectedType) {
		return line + ":" + column + ": Variable " + name + ": Can't assign type " + currentType + " to " + expectedType;
	}

	@NotNull
	public static String errorCantAssignReturnType(int line, int column, Type currentType, Type expectedType) {
		return line + ":" + column + ": return statement: Can't assign type " + currentType + " to " + expectedType;
	}

	@NotNull
	public static String errorFunctionAlreadyDeclared(int line, int column, String name) {
		return line + ":" + column + ": function " + name + " already declared";
	}

	@NotNull
	public static String errorFunctionCallsInConstantsNotAllowed(int line, int column) {
		return line + ":" + column + ": function calls are not allowed in constant definitions";
	}

	@NotNull
	public static String errorFunctionDoesNotReturnAValue(int line, int column, String name) {
		return line + ":" + column + ": the call to function " + name + " does not return any value";
	}

	@NotNull
	public static String errorMissingMain() {
		return "Missing function 'void main()'";
	}

	@NotNull
	public static String errorMainHasWrongSignature(int line, int column) {
		return line + ":" + column + ": Main function has wrong signature - needs to be 'void main()'";
	}

	@NotNull
	public static String errorMissingReturnStatement(String functionName) {
		return "Function " + functionName + ": missing return statement";
	}

	@NotNull
	public static String errorNoReturnExpressionExpectedForVoid(int line, int column) {
		return line + ":" + column + ": return statement: no expression expected for void-method";
	}

	@NotNull
	public static String errorParameterAlreadyDeclared(int line, int column, String name) {
		return line + ":" + column + ": parameter " + name + " already declared";
	}

	@NotNull
	public static String errorReturnExpressionExpected(int line, int column, Type expectedType) {
		return line + ":" + column + ": return statement: expression of type " + expectedType + " expected";
	}

	@NotNull
	public static String errorUndeclaredFunction(int line, int column, String name) {
		return line + ":" + column + ": Call to undeclared function " + name;
	}

	public static String errorUndeclaredVariable(int line, int column, String name) {
		return line + ":" + column + ": undeclared variable " + name;
	}

	@NotNull
	public static String errorVarAlreadyDeclared(int line, int column, @NotNull String name) {
		return line + ":" + column + ": variable " + name + " already declared";
	}

	@NotNull
	public static String errorVarAlreadyDeclaredAsParameter(int line, int column, @NotNull String name) {
		return line + ":" + column + ": local variable " + name + " already declared as parameter";
	}

	@NotNull
	public static String warningIgnoredReturnValue(int line, int column, String name, Type functionReturnType) {
		return line + ":" + column + ": the call to function " + name + " ignores its return value of type " + functionReturnType;
	}

	@NotNull
	public static String warningStatementAfterBreak() {
		return "Ignored statements after break";
	}

	@NotNull
	public static String warningStatementAfterReturn() {
		return "Ignored statements after return";
	}

	@NotNull
	public static String warningUnnecessaryCastTo(int line, int column, Type type) {
		return line + ":" + column + ": Unnecessary cast to " + type;
	}

	@NotNull
	public static String warningUnusedFunction(int line, int column, String name) {
		return line + ":" + column + ": Function " + name + " is unused";
	}

	@NotNull
	public static String warningUnusedParameter(int line, int column, String name) {
		return line + ":" + column + ": Parameter " + name + " is unused";
	}

	@NotNull
	public static String warningUnusedVar(int line, int column, String name) {
		return line + ":" + column + ": Variable " + name + " is unused";
	}
}
