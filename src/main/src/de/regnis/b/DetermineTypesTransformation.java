package de.regnis.b;

import de.regnis.b.node.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class DetermineTypesTransformation {

	// Static =================================================================

	/**
	 * @throws InvalidTypeException
	 * @throws SymbolScope.AlreadyDefinedException
	 */
	@NotNull
	public static SymbolScope run(DeclarationList root) {
		final DetermineTypesTransformation determineTypes = new DetermineTypesTransformation();
		determineTypes.visitDeclarationList(root);
		return determineTypes.symbolMap;
	}

	// Fields =================================================================

	private SymbolScope symbolMap = SymbolScope.createRootInstance();
	@Nullable
	private Type functionReturnType;

	// Setup ==================================================================

	private DetermineTypesTransformation() {
	}

	// Utils ==================================================================

	private void visitDeclarationList(DeclarationList node) {
		for (Declaration declaration : node.getDeclarations()) {
			declaration.visit(new DeclarationVisitor<>() {
				@Override
				public Object visitGlobalVarDeclaration(GlobalVarDeclaration node) {
					return DetermineTypesTransformation.this.visitGlobalVarDeclaration(node);
				}

				@Override
				public Object visitFunctionDeclaration(FuncDeclaration node) {
					return DetermineTypesTransformation.this.visitFunctionDeclaration(node);
				}
			});
		}
	}

	private Object visitGlobalVarDeclaration(GlobalVarDeclaration node) {
		visitExpression(node.node.expression);
		symbolMap.declareVariable(node.node.var, node.node.expression.getType(), SymbolScope.VariableKind.Global);
		return this;
	}

	private Object visitFunctionDeclaration(FuncDeclaration node) {
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

			visitStatement(node.statement);
		}
		finally {
			symbolMap = outerSymbolMap;
			functionReturnType = null;
		}
		return this;
	}

	private void visitStatement(Statement statement) {
		statement.visit(new StatementVisitor<>() {
			@Override
			public Object visitAssignment(Assignment node) {
				return DetermineTypesTransformation.this.visitAssignment(node);
			}

			@Override
			public Object visitStatementList(StatementList node) {
				return DetermineTypesTransformation.this.visitStatementList(node);
			}

			@Override
			public Object visitLocalVarDeclaration(VarDeclaration node) {
				return DetermineTypesTransformation.this.visitLocalVarDeclaration(node);
			}

			@Override
			public Object visitReturn(ReturnStatement node) {
				return DetermineTypesTransformation.this.visitReturn(node);
			}
		});
	}

	private Object visitStatementList(StatementList node) {
		final SymbolScope outerSymbolMap = symbolMap;
		try {
			symbolMap = symbolMap.createChildMap(SymbolScope.ScopeKind.Local);

			for (Statement statement : node.getStatements()) {
				visitStatement(statement);
			}
		}
		finally {
			symbolMap = outerSymbolMap;
		}
		return this;
	}

	private Object visitLocalVarDeclaration(VarDeclaration node) {
		visitExpression(node.expression);
		symbolMap.declareVariable(node.var, node.expression.getType(), SymbolScope.VariableKind.Local);
		return this;
	}

	private Object visitAssignment(Assignment node) {
		final Type variableType = symbolMap.getVariableType(node.var);
		visitExpression(node.expression);
		final Type expressionType = node.expression.getType();
		if (!BasicTypes.canBeAssignedFrom(variableType, expressionType)) {
			throw new InvalidTypeException("Can't assign type " + expressionType + " to " + variableType);
		}
		return this;
	}

	private void visitExpression(Expression expression) {
		expression.visit(new ExpressionVisitor<>() {
			@Override
			public Object visitBinary(BinaryExpression node) {
				return DetermineTypesTransformation.this.visitBinary(node);
			}

			@Override
			public Object visitFunctionCall(FuncCall node) {
				return DetermineTypesTransformation.this.visitFunctionCall(node);
			}

			@Override
			public Object visitNumber(NumberLiteral node) {
				return this;
			}

			@Override
			public Object visitVarRead(VarRead node) {
				return DetermineTypesTransformation.this.visitVarRead(node);
			}
		});
	}

	private Object visitBinary(BinaryExpression node) {
		visitExpression(node.left);
		visitExpression(node.right);

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

	private Object visitFunctionCall(FuncCall node) {
		for (Expression expression : node.getParameters()) {
			visitExpression(expression);
		}

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

	private Object visitReturn(ReturnStatement node) {
		if (functionReturnType == null) {
			throw new IllegalStateException("Missing function return type");
		}

		visitExpression(node.expression);

		if (!BasicTypes.canBeAssignedFrom(functionReturnType, node.expression.getType())) {
			throw new InvalidTypeException("The expected return type is " + functionReturnType + " which can't be assigned from " + node.expression.getType());
		}
		return this;
	}

	private Object visitVarRead(VarRead node) {
		final Type type = symbolMap.getVariableType(node.var);
		node.setType(type);
		return this;
	}
}
