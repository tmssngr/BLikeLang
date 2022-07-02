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
	public static DeclarationList transform(DeclarationList root, StringOutput warningOutput) {
		final DetermineTypesTransformation determineTypes = new DetermineTypesTransformation(warningOutput);
		return determineTypes.visitDeclarationList(root);
	}

	@NotNull
	public static String msgCantAssignType(int line, int column, String name, Type currentType, Type expectedType) {
		return line + ":" + column + ": Variable " + name + ": Can't assign type " + currentType + " to " + expectedType;
	}

	// Fields =================================================================

	private final StringOutput warningOutput;

	private int globalVarCount;
	private SymbolScope symbolMap = SymbolScope.createRootInstance();
	@Nullable
	private Type functionReturnType;
	private int localVarCount;

	// Setup ==================================================================

	private DetermineTypesTransformation(StringOutput warningOutput) {
		this.warningOutput = warningOutput;
	}

	// Utils ==================================================================

	private DeclarationList visitDeclarationList(DeclarationList root) {
		final DeclarationList newRoot = new DeclarationList();
		for (Declaration declaration : root.getDeclarations()) {
			final Declaration newDeclaration = declaration.visit(new DeclarationVisitor<>() {
				@Override
				public Declaration visitGlobalVarDeclaration(GlobalVarDeclaration node) {
					return DetermineTypesTransformation.this.visitGlobalVarDeclaration(node);
				}

				@Override
				public Declaration visitFunctionDeclaration(FuncDeclaration node) {
					return DetermineTypesTransformation.this.visitFunctionDeclaration(node);
				}
			});
			newRoot.add(newDeclaration);
		}

		symbolMap.reportUnusedVariables(warningOutput);

		return newRoot;
	}

	private Declaration visitGlobalVarDeclaration(GlobalVarDeclaration node) {
		final String newName = "g" + globalVarCount;
		globalVarCount++;

		final VarDeclaration newVarDeclaration = visitVarDeclaration(node.node, newName);
		return new GlobalVarDeclaration(newVarDeclaration);
	}

	private Declaration visitFunctionDeclaration(FuncDeclaration node) {
		final List<Type> parameterTypes = getParameterTypes(node);

		symbolMap.declareFunction(node.name, node.type, parameterTypes);

		final SymbolScope outerSymbolMap = symbolMap;
		functionReturnType = node.type;
		symbolMap = symbolMap.createChildMap(SymbolScope.ScopeKind.Parameter);
		localVarCount = 0;
		try {
			final FuncDeclarationParameters renamedParameters = declareParameters(node.parameters);

			final StatementList newStatementList = visitStatementList(node.statementList);
			symbolMap.reportUnusedVariables(warningOutput);

			return new FuncDeclaration(node.type, node.name, renamedParameters, newStatementList);
		}
		finally {
			symbolMap = outerSymbolMap;
			functionReturnType = null;
		}
	}

	@NotNull
	private List<Type> getParameterTypes(FuncDeclaration node) {
		final List<Type> parameterTypes = new ArrayList<>();
		for (FuncDeclarationParameter parameter : node.parameters.getParameters()) {
			parameterTypes.add(parameter.type);
		}
		return parameterTypes;
	}

	private FuncDeclarationParameters declareParameters(FuncDeclarationParameters parameters) {
		final FuncDeclarationParameters renamedParameters = new FuncDeclarationParameters();
		int i = 0;
		for (FuncDeclarationParameter parameter : parameters.getParameters()) {

			final String newName = "p" + i;
			i++;

			symbolMap.declareVariable(parameter.name, newName, parameter.type, parameter.line, parameter.column);

			renamedParameters.add(new FuncDeclarationParameter(parameter.type, newName));
		}
		return renamedParameters;
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
				final Statement newStatement = visitStatement(statement);
				flattenNestedStatementList(newStatement, newList);
			}

			symbolMap.reportUnusedVariables(warningOutput);

			return newList;
		}
		finally {
			symbolMap = outerSymbolMap;
		}
	}

	private void flattenNestedStatementList(Statement statement, StatementList list) {
		statement.visit(new StatementVisitor<>() {
			@Override
			public Object visitAssignment(Assignment node) {
				list.add(statement);
				return node;
			}

			@Override
			public Object visitStatementList(StatementList node) {
				for (Statement subStatement : node.getStatements()) {
					list.add(subStatement);
				}
				return node;
			}

			@Override
			public Object visitLocalVarDeclaration(VarDeclaration node) {
				list.add(statement);
				return node;
			}

			@Override
			public Object visitReturn(ReturnStatement node) {
				list.add(statement);
				return node;
			}
		});
	}

	private VarDeclaration visitLocalVarDeclaration(VarDeclaration node) {
		final String newName = "v" + localVarCount;
		localVarCount++;

		return visitVarDeclaration(node, newName);
	}

	@NotNull
	private VarDeclaration visitVarDeclaration(VarDeclaration varDeclaration, String newName) {
		Type type = null;
		if (varDeclaration.typeName != null) {
			type = BasicTypes.getType(varDeclaration.typeName, false);
		}

		final Expression newExpression = visitExpression(varDeclaration.expression);

		final Type expressionType = newExpression.getType();
		if (type != null) {
			if (!BasicTypes.canBeAssignedFrom(type, expressionType)) {
				throw new InvalidTypeException(msgCantAssignType(varDeclaration.line, varDeclaration.column, varDeclaration.name, expressionType, type));
			}
		}
		else {
			type = expressionType;
		}

		symbolMap.declareVariable(varDeclaration.name, newName, type, varDeclaration.line, varDeclaration.column);
		return new VarDeclaration(newName, type, newExpression);
	}

	private Assignment visitAssignment(Assignment node) {
		final SymbolScope.Variable variable = symbolMap.variableRead(node.var);
		final Expression newExpression = visitExpression(node.expression);

		final Type expressionType = newExpression.getType();
		if (!BasicTypes.canBeAssignedFrom(variable.type, expressionType)) {
			throw new InvalidTypeException("Can't assign type " + expressionType + " to " + variable.type);
		}

		return new Assignment(variable.newName, newExpression);
	}

	private Expression visitExpression(Expression expression) {
		return expression.visit(new ExpressionVisitor<>() {
			@Override
			public Expression visitBinary(BinaryExpression node) {
				return DetermineTypesTransformation.this.visitBinary(node);
			}

			@Override
			public Expression visitFunctionCall(FuncCall node) {
				return DetermineTypesTransformation.this.visitFunctionCall(node);
			}

			@Override
			public Expression visitNumber(NumberLiteral node) {
				return node;
			}

			@Override
			public Expression visitVarRead(VarRead node) {
				return DetermineTypesTransformation.this.visitVarRead(node);
			}

			@Override
			public Expression visitTypeCast(TypeCast node) {
				return DetermineTypesTransformation.this.visitTypeCast(node);
			}
		});
	}

	private BinaryExpression visitBinary(BinaryExpression node) {
		final Expression newLeft = visitExpression(node.left);
		final Expression newRight = visitExpression(node.right);

		final Type leftType = newLeft.getType();
		final Type rightType = newRight.getType();
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
		return node.createNew(type, newLeft, newRight);
	}

	private FuncCall visitFunctionCall(FuncCall node) {
		final FuncCallParameters newParameters = new FuncCallParameters();
		final List<Expression> expressions = new ArrayList<>();
		for (Expression expression : node.getParameters()) {
			final Expression newExpression = visitExpression(expression);
			expressions.add(newExpression);
			newParameters.add(newExpression);
		}

		final SymbolScope.Function function = symbolMap.getFunction(node.name);
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

		return new FuncCall(function.type, node.name, newParameters);
	}

	private ReturnStatement visitReturn(ReturnStatement node) {
		if (functionReturnType == null) {
			throw new IllegalStateException("Missing function return type");
		}

		final Expression newExpression = visitExpression(node.expression);

		if (!BasicTypes.canBeAssignedFrom(functionReturnType, newExpression.getType())) {
			throw new InvalidTypeException("The expected return type is " + functionReturnType + " which can't be assigned from " + node.expression.getType());
		}

		return new ReturnStatement(newExpression);
	}

	private VarRead visitVarRead(VarRead node) {
		final SymbolScope.Variable typeName = symbolMap.variableRead(node.var);
		return new VarRead(typeName.type, typeName.newName);
	}

	private TypeCast visitTypeCast(TypeCast node) {
		final Expression newExpression = visitExpression(node.expression);
		final Type expressionType = newExpression.getType();
		final Type type = BasicTypes.getType(node.typeName, false);
		if (expressionType == type || BasicTypes.canBeAssignedFrom(type, expressionType)) {
			warningOutput.print("Unnecessary cast to " + type);
			warningOutput.println();
		}
		return new TypeCast(type, newExpression);
	}
}
