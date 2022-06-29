package de.regnis.b;

import de.regnis.b.node.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class DetermineTypes extends NodeVisitor<Object> {

	// Static =================================================================

	/**
	 * @throws InvalidTypeException
	 * @throws SymbolScope.AlreadyDefinedException
	 */
	@NotNull
	public static SymbolScope run(DeclarationList root) {
		final DetermineTypes determineTypes = new DetermineTypes();
		determineTypes.visitDeclarationList(root);
		return determineTypes.symbolMap;
	}

	// Fields =================================================================

	private SymbolScope symbolMap = SymbolScope.createRootInstance();
	@Nullable
	private Type functionReturnType;

	// Setup ==================================================================

	private DetermineTypes() {
	}

	// Implemented ============================================================

	@Override
	public Object visitGlobalVarDeclaration(GlobalVarDeclaration node) {
		super.visitGlobalVarDeclaration(node);
		symbolMap.declareVariable(node.node.var, node.node.expression.getType(), SymbolScope.VariableKind.Global);
		return this;
	}

	@Override
	public Object visitFunctionDeclaration(FuncDeclaration node) {
		final List<Type> parameterTypes = new ArrayList<>();
		for (FuncDeclarationParameter parameter : node.parameters.getParameters()) {
			parameterTypes.add(parameter.type);
		}

		symbolMap.declareFunction(node.name, node.type, parameterTypes);

		final SymbolScope outerSymbolMap = symbolMap;
		try {
			symbolMap = symbolMap.createChildMap(SymbolScope.ScopeKind.Parameter);
			functionReturnType = node.type;

			for (FuncDeclarationParameter parameter : node.parameters.getParameters()) {
				symbolMap.declareVariable(parameter.name, parameter.type, SymbolScope.VariableKind.Parameter);
			}

			node.statement.visit(this);
		}
		finally {
			symbolMap = outerSymbolMap;
			functionReturnType = null;
		}
		return this;
	}

	@Override
	public Object visitStatementList(StatementList node) {
		final SymbolScope outerSymbolMap = symbolMap;
		try {
			symbolMap = symbolMap.createChildMap(SymbolScope.ScopeKind.Local);

			super.visitStatementList(node);
		}
		finally {
			symbolMap = outerSymbolMap;
		}
		return this;
	}

	@Override
	public Object visitLocalVarDeclaration(VarDeclaration node) {
		super.visitLocalVarDeclaration(node);
		symbolMap.declareVariable(node.var, node.expression.getType(), SymbolScope.VariableKind.Local);
		return this;
	}

	@Override
	public Object visitAssignment(Assignment node) {
		final Type variableType = symbolMap.getVariableType(node.var);
		super.visitAssignment(node);
		final Type expressionType = node.expression.getType();
		if (!BasicTypes.canBeAssignedFrom(variableType, expressionType)) {
			throw new InvalidTypeException("Can't assign type " + expressionType + " to " + variableType);
		}
		return this;
	}

	@Override
	public Object visitBinary(BinaryExpression node) {
		super.visitBinary(node);
		final Type leftType = node.left.getType();
		final Type rightType = node.right.getType();
		final Type type;
		if (leftType instanceof final BasicTypes.NumericType lnt
				&& rightType instanceof final BasicTypes.NumericType rnt) {
			if (lnt.isSigned() || rnt.isSigned()) {
				type = lnt.min > rnt.min ? rnt : lnt;
			}
			else {
				type = lnt.max > rnt.max ? lnt : rnt;
			}
		}
		else {
			throw new InvalidTypeException("Operator " + node.operator + " can't work on " + leftType + " and " + rightType);
		}
		node.setType(type);
		return this;
	}

	@Override
	public Object visitFunctionCall(FuncCall node) {
		super.visitFunctionCall(node);

		final SymbolScope.Function function = symbolMap.getFunction(node.name);
		final List<Expression> expressions = node.getParameters();
		final List<Type> parameterTypes = function.parameterTypes;
		if (expressions.size() != parameterTypes.size()) {
			throw new InvalidTypeException("Function " + node.name + " expects " + parameterTypes.size() + " expressions, but got " + expressions.size());
		}

		for (int i = 0; i < parameterTypes.size(); i++) {
			final Type expressionType = expressions.get(i).getType();
			final Type parameterType = parameterTypes.get(i);
			if (!BasicTypes.canBeAssignedFrom(parameterType, expressionType)) {
				throw new InvalidTypeException("Function " + node.name + ": the " + (i + 1) + ". parameter expects " + parameterType + " which can't be assigned from " + expressionType);
			}
		}

		node.setType(function.type);
		return this;
	}

	@Override
	public Object visitReturn(ReturnStatement node) {
		if (functionReturnType == null) {
			throw new IllegalStateException("Missing function return type");
		}

		super.visitReturn(node);

		if (!BasicTypes.canBeAssignedFrom(functionReturnType, node.expression.getType())) {
			throw new InvalidTypeException("The expected return type is " + functionReturnType + " which can't be assigned from " + node.expression.getType());
		}
		return this;
	}

	@Override
	public Object visitVarRead(VarRead node) {
		final Type type = symbolMap.getVariableType(node.var);
		node.setType(type);
		return this;
	}

	@Override
	public Object visitNumber(NumberLiteral node) {
		return this;
	}
}
