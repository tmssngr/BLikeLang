package de.regnis.b;

import de.regnis.b.node.*;
import de.regnis.b.out.StringOutput;

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
	public static void run(DeclarationList root, StringOutput warningOutput) {
		final DetermineTypesTransformation determineTypes = new DetermineTypesTransformation(warningOutput);
		final DeclarationList newRoot = determineTypes.visitDeclarationList(root);
//		return determineTypes.symbolMap;
	}

	// Fields =================================================================

	private final StringOutput warningOutput;

	private SymbolScope symbolMap = SymbolScope.createRootInstance();
	@Nullable
	private Type functionReturnType;

	// Setup ==================================================================

	private DetermineTypesTransformation(StringOutput warningOutput) {
		this.warningOutput = warningOutput;
	}

	// Utils ==================================================================

	private DeclarationList visitDeclarationList(DeclarationList root) {
		final DeclarationList newRoot = new DeclarationList();
		for (Declaration declaration : root.getDeclarations()) {
			newRoot.add(declaration.visit(new DeclarationVisitor<>() {
				@Override
				public Declaration visitGlobalVarDeclaration(GlobalVarDeclaration node) {
					return DetermineTypesTransformation.this.visitGlobalVarDeclaration(node);
				}

				@Override
				public Declaration visitFunctionDeclaration(FuncDeclaration node) {
					return DetermineTypesTransformation.this.visitFunctionDeclaration(node);
				}
			}));
		}

		symbolMap.reportUnusedVariables(warningOutput);

		return newRoot;
	}

	private Declaration visitGlobalVarDeclaration(GlobalVarDeclaration node) {
		final VarDeclaration varDeclaration = node.node;
		visitExpression(varDeclaration.expression);
		symbolMap.declareVariable(varDeclaration.var, varDeclaration.expression.getType(), varDeclaration.line, varDeclaration.column, SymbolScope.VariableKind.Global);
		return node;
	}

	private Declaration visitFunctionDeclaration(FuncDeclaration node) {
		final List<Type> parameterTypes = new ArrayList<>();
		for (FuncDeclarationParameter parameter : node.parameters.getParameters()) {
			parameterTypes.add(parameter.type);
		}

		symbolMap.declareFunction(node.name, node.type, parameterTypes);

		final SymbolScope outerSymbolMap = symbolMap;
		functionReturnType = node.type;
		symbolMap = symbolMap.createChildMap(SymbolScope.ScopeKind.Parameter);
		try {
			for (FuncDeclarationParameter parameter : node.parameters.getParameters()) {
				symbolMap.declareVariable(parameter.name, parameter.type, parameter.line, parameter.column, SymbolScope.VariableKind.Parameter);
			}

			final StatementList newStatementList = visitStatementList(node.statementList);
			symbolMap.reportUnusedVariables(warningOutput);

			return new FuncDeclaration(node.type, node.name, node.parameters, newStatementList);
		}
		finally {
			symbolMap = outerSymbolMap;
			functionReturnType = null;
		}
	}

	private Statement visitStatement(Statement statement) {
		return statement.visit(new StatementVisitor<>() {
			@Override
			public Statement visitAssignment(Assignment node) {
				return DetermineTypesTransformation.this.visitAssignment(node);
			}

			@Override
			public Statement visitStatementList(StatementList node) {
				return DetermineTypesTransformation.this.visitStatementList(node);
			}

			@Override
			public Statement visitLocalVarDeclaration(VarDeclaration node) {
				return DetermineTypesTransformation.this.visitLocalVarDeclaration(node);
			}

			@Override
			public Statement visitReturn(ReturnStatement node) {
				return DetermineTypesTransformation.this.visitReturn(node);
			}
		});
	}

	private StatementList visitStatementList(StatementList list) {
		final SymbolScope outerSymbolMap = symbolMap;
		symbolMap = symbolMap.createChildMap(SymbolScope.ScopeKind.Local);
		try {
			final StatementList newList = new StatementList();

			for (Statement statement : list.getStatements()) {
				newList.add(visitStatement(statement));
			}

			symbolMap.reportUnusedVariables(warningOutput);

			return newList;
		}
		finally {
			symbolMap = outerSymbolMap;
		}
	}

	private VarDeclaration visitLocalVarDeclaration(VarDeclaration node) {
		visitExpression(node.expression);
		symbolMap.declareVariable(node.var, node.expression.getType(), node.line, node.column, SymbolScope.VariableKind.Local);
		return node;
	}

	private Assignment visitAssignment(Assignment node) {
		final Type variableType = symbolMap.variableRead(node.var);
		visitExpression(node.expression);
		final Type expressionType = node.expression.getType();
		if (!BasicTypes.canBeAssignedFrom(variableType, expressionType)) {
			throw new InvalidTypeException("Can't assign type " + expressionType + " to " + variableType);
		}
		return node;
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
				return node;
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

	private ReturnStatement visitReturn(ReturnStatement node) {
		if (functionReturnType == null) {
			throw new IllegalStateException("Missing function return type");
		}

		visitExpression(node.expression);

		if (!BasicTypes.canBeAssignedFrom(functionReturnType, node.expression.getType())) {
			throw new InvalidTypeException("The expected return type is " + functionReturnType + " which can't be assigned from " + node.expression.getType());
		}
		return node;
	}

	private Object visitVarRead(VarRead node) {
		final Type type = symbolMap.variableRead(node.var);
		node.setType(type);
		return this;
	}
}
