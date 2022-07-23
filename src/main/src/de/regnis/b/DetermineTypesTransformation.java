package de.regnis.b;

import de.regnis.b.ast.*;
import de.regnis.b.out.StringOutput;
import de.regnis.b.type.BasicTypes;
import de.regnis.b.type.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Thomas Singer
 */
public final class DetermineTypesTransformation {

	// Static =================================================================

	@NotNull
	public static DeclarationList transform(DeclarationList root, StringOutput warningOutput) {
		final DetermineTypesTransformation transformation = new DetermineTypesTransformation(warningOutput);
		transformation.determineFunctions(root);
		transformation.reportMissingMainFunction();
		transformation.reportIllegalBreakStatement(root);

		DeclarationList newRoot = transformation.visitDeclarationList(root);
		AssertTypes.assertAllExpressionsHaveType(newRoot);
		newRoot = transformation.reportAndRemoveUnusedFunctions(newRoot);
		AssertTypes.assertAllExpressionsHaveType(newRoot);
		return newRoot;
	}

	// Fields =================================================================

	private final Map<String, Function> functions = new LinkedHashMap<>();
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

	private void determineFunctions(DeclarationList root) {
		for (Declaration declaration : root.getDeclarations()) {
			declaration.visit(new DeclarationVisitor<>() {
				@Override
				public Object visitGlobalVarDeclaration(GlobalVarDeclaration node) {
					return node;
				}

				@Override
				public Object visitFunctionDeclaration(FuncDeclaration node) {
					final List<Type> parameterTypes = getParameterTypes(node);
					if (functions.containsKey(node.name)) {
						throw new TransformationFailedException(Messages.errorFunctionAlreadyDeclared(node.line, node.column, node.name));
					}

					functions.put(node.name, new Function(node.type, parameterTypes, node.line, node.column));

					return node;
				}
			});
		}
	}

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
		final SymbolScope outerSymbolMap = symbolMap;
		functionReturnType = node.type;
		symbolMap = symbolMap.createChildMap(SymbolScope.ScopeKind.Parameter);
		localVarCount = 0;
		try {
			final FuncDeclarationParameters renamedParameters = declareParameters(node.parameters);

			final StatementList newStatementList = visitStatementList(node.statementList);
			symbolMap.reportUnusedVariables(warningOutput);

			if (functionReturnType != BasicTypes.VOID) {
				if (!hasReturnStatement(newStatementList)) {
					throw new TransformationFailedException(Messages.errorMissingReturnStatement(node.name));
				}
			}

			return new FuncDeclaration(node.type, node.name, renamedParameters, newStatementList);
		}
		finally {
			symbolMap = outerSymbolMap;
			functionReturnType = null;
		}
	}

	private boolean hasReturnStatement(StatementList statementList) {
		final List<? extends Statement> statements = statementList.getStatements();
		if (statements.isEmpty()) {
			return false;
		}

		final Statement lastStatement = statements.get(statements.size() - 1);
		if (lastStatement instanceof ReturnStatement) {
			return true;
		}

		if (lastStatement instanceof IfStatement) {
			final IfStatement ifStatement = (IfStatement) lastStatement;
			return hasReturnStatement(ifStatement.trueStatements)
					&& hasReturnStatement(ifStatement.falseStatements);
		}

		return false;
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
			public Statement visitMemAssignment(MemAssignment node) {
				return DetermineTypesTransformation.this.visitMemAssignment(node);
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
			public Statement visitCall(CallStatement node) {
				return DetermineTypesTransformation.this.visitCall(node);
			}

			@Override
			public Statement visitReturn(ReturnStatement node) {
				return DetermineTypesTransformation.this.visitReturn(node);
			}

			@Override
			public Statement visitIf(IfStatement node) {
				return DetermineTypesTransformation.this.visitIf(node);
			}

			@Override
			public Statement visitWhile(WhileStatement node) {
				return DetermineTypesTransformation.this.visitWhile(node);
			}

			@Override
			public Statement visitBreak(BreakStatement node) {
				return node;
			}
		});
	}

	private StatementList visitStatementList(StatementList list) {
		final SymbolScope outerSymbolMap = symbolMap;
		symbolMap = symbolMap.createChildMap(SymbolScope.ScopeKind.Local);
		try {
			final List<Statement> newStatements = new ArrayList<>();
			for (Statement statement : list.getStatements()) {
				final Statement newStatement = visitStatement(statement);
				flattenNestedStatementList(newStatement, newStatements);
			}

			final StatementList newList = new StatementList();
			boolean wasReturn = false;
			boolean wasBreak = false;
			for (Statement newStatement : newStatements) {
				if (wasBreak) {
					warning(Messages.warningStatementAfterBreak());
					break;
				}

				if (wasReturn) {
					warning(Messages.warningStatementAfterReturn());
					break;
				}

				newList.add(newStatement);
				if (newStatement instanceof BreakStatement) {
					wasBreak = true;
				}
				else if (newStatement instanceof ReturnStatement) {
					wasReturn = true;
				}
			}

			symbolMap.reportUnusedVariables(warningOutput);

			return newList;
		}
		finally {
			symbolMap = outerSymbolMap;
		}
	}

	private void flattenNestedStatementList(Statement statement, List<Statement> list) {
		statement.visit(new StatementVisitor<>() {
			@Override
			public Object visitAssignment(Assignment node) {
				list.add(statement);
				return node;
			}

			@Override
			public Object visitMemAssignment(MemAssignment node) {
				list.add(statement);
				return node;
			}

			@Override
			public Object visitStatementList(StatementList node) {
				list.addAll(node.getStatements());
				return node;
			}

			@Override
			public Object visitLocalVarDeclaration(VarDeclaration node) {
				list.add(statement);
				return node;
			}

			@Override
			public Object visitCall(CallStatement node) {
				list.add(statement);
				return node;
			}

			@Override
			public Object visitReturn(ReturnStatement node) {
				list.add(statement);
				return node;
			}

			@Override
			public Object visitIf(IfStatement node) {
				list.add(statement);
				return node;
			}

			@Override
			public Object visitWhile(WhileStatement node) {
				list.add(statement);
				return node;
			}

			@Override
			public Object visitBreak(BreakStatement node) {
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
				throw new TransformationFailedException(Messages.errorCantAssignType(varDeclaration.line, varDeclaration.column, varDeclaration.name, expressionType, type));
			}
		}
		else {
			type = expressionType;
		}

		symbolMap.declareVariable(varDeclaration.name, newName, type, varDeclaration.line, varDeclaration.column);
		return new VarDeclaration(newName, type, newExpression);
	}

	private Assignment visitAssignment(Assignment node) {
		final SymbolScope.Variable variable = symbolMap.variableRead(node.name, node.line, node.column);
		final Expression newExpression = visitExpression(node.expression);

		final Type expressionType = newExpression.getType();
		if (!BasicTypes.canBeAssignedFrom(variable.type, expressionType)) {
			throw new TransformationFailedException(Messages.errorCantAssignType(node.line, node.column, node.name, expressionType, variable.type));
		}

		return new Assignment(variable.newName, newExpression);
	}

	private MemAssignment visitMemAssignment(MemAssignment node) {
		final SymbolScope.Variable variable = symbolMap.variableRead(node.name, node.line, node.column);
		if (variable.type != BasicTypes.UINT16) {
			throw new TransformationFailedException(Messages.errorMemAccessNeedsU16(node.line, node.column, node.name, variable.type));
		}

		final Expression newExpression = visitExpression(node.expression);

		final Type expressionType = newExpression.getType();
		if (expressionType != BasicTypes.UINT8) {
			throw new TransformationFailedException(Messages.errorMemWriteNeedsU8(node.line, node.column, expressionType));
		}

		return new MemAssignment(variable.newName, newExpression);
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
			public Expression visitBoolean(BooleanLiteral node) {
				return node;
			}

			@Override
			public Expression visitVarRead(VarRead node) {
				return DetermineTypesTransformation.this.visitVarRead(node);
			}

			@Override
			public Expression visitMemRead(MemRead node) {
				return DetermineTypesTransformation.this.visitMemRead(node);
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
		final Type type = getBinaryExpressionType(leftType, node.operator, rightType);
		if (type == null) {
			throw new TransformationFailedException("Operator " + node.operator + " can't work on " + leftType + " and " + rightType);
		}
		return new BinaryExpression(newLeft, node.operator, newRight, type);
	}

	@Nullable
	private Type getBinaryExpressionType(Type left, BinaryExpression.Op operator, Type right) {
		if (left instanceof final BasicTypes.NumericType lnt
				&& right instanceof final BasicTypes.NumericType rnt) {
			if (BinaryExpression.isComparison(operator)) {
				return BasicTypes.BOOLEAN;
			}

			if (lnt.isSigned() || rnt.isSigned()) {
				return lnt.min > rnt.min ? rnt : lnt;
			}
			return lnt.max > rnt.max ? lnt : rnt;
		}

		if (left == BasicTypes.BOOLEAN && right == BasicTypes.BOOLEAN) {
			if (operator == BinaryExpression.Op.equal || operator == BinaryExpression.Op.notEqual) {
				return BasicTypes.BOOLEAN;
			}
		}

		return null;
	}

	private FuncCall visitFunctionCall(FuncCall node) {
		final FuncCallParameters newParameters = new FuncCallParameters();

		final Function function = handleCall(node.name, node.getParameters(), node.line, node.column, newParameters);

		if (function.type == BasicTypes.VOID) {
			throw new TransformationFailedException(Messages.errorFunctionDoesNotReturnAValue(node.line, node.column, node.name));
		}

		return new FuncCall(function.type, node.name, newParameters);
	}

	private CallStatement visitCall(CallStatement node) {
		final FuncCallParameters newParameters = new FuncCallParameters();

		final Function function = handleCall(node.name, node.getParameters(), node.line, node.column, newParameters);

		if (function.type != BasicTypes.VOID) {
			warning(Messages.warningIgnoredReturnValue(node.line, node.column, node.name, function.type));
		}

		return new CallStatement(node.name, newParameters);
	}

	@NotNull
	private Function handleCall(String name, List<Expression> parameters, int line, int column, FuncCallParameters newParameters) {
		final List<Expression> expressions = new ArrayList<>();
		for (Expression expression : parameters) {
			final Expression newExpression = visitExpression(expression);
			expressions.add(newExpression);
			newParameters.add(newExpression);
		}

		final Function function = functions.get(name);
		if (function == null) {
			throw new TransformationFailedException(Messages.errorUndeclaredFunction(line, column, name));
		}

		function.setUsed();

		final List<Type> parameterTypes = function.parameterTypes;
		if (expressions.size() != parameterTypes.size()) {
			throw new TransformationFailedException("Function " + name + " expects " + parameterTypes.size() + " expressions, but got " + expressions.size());
		}

		for (int i = 0; i < parameterTypes.size(); i++) {
			final Type expressionType = expressions.get(i).getType();
			final Type parameterType = parameterTypes.get(i);
			if (!BasicTypes.canBeAssignedFrom(parameterType, expressionType)) {
				throw new TransformationFailedException("Function " + name + ": the " + (i + 1) + ". parameter expects " + parameterType + " which can't be assigned from " + expressionType);
			}
		}
		return function;
	}

	private ReturnStatement visitReturn(ReturnStatement node) {
		if (functionReturnType == null) {
			throw new IllegalStateException();
		}

		if (functionReturnType == BasicTypes.VOID) {
			if (node.expression != null) {
				throw new TransformationFailedException(Messages.errorNoReturnExpressionExpectedForVoid(node.line, node.column));
			}
			return node;
		}

		if (node.expression == null) {
			throw new TransformationFailedException(Messages.errorReturnExpressionExpected(node.line, node.column, functionReturnType));
		}

		final Expression newExpression = visitExpression(node.expression);

		if (!BasicTypes.canBeAssignedFrom(functionReturnType, newExpression.getType())) {
			throw new TransformationFailedException(Messages.errorCantAssignReturnType(node.line, node.column, node.expression.getType(), functionReturnType));
		}

		return new ReturnStatement(newExpression);
	}

	private IfStatement visitIf(IfStatement node) {
		final Expression newExpression = visitExpression(node.expression);
		if (newExpression.getType() != BasicTypes.BOOLEAN) {
			throw new TransformationFailedException(Messages.errorBooleanExpected(node.line, node.column, newExpression.getType()));
		}

		return new IfStatement(newExpression, visitStatementList(node.trueStatements), visitStatementList(node.falseStatements));
	}

	private WhileStatement visitWhile(WhileStatement node) {
		final Expression newExpression = visitExpression(node.expression);
		if (newExpression.getType() != BasicTypes.BOOLEAN) {
			throw new TransformationFailedException(Messages.errorBooleanExpected(node.line, node.column, newExpression.getType()));
		}

		return new WhileStatement(newExpression, visitStatementList(node.statements));
	}

	private VarRead visitVarRead(VarRead node) {
		final SymbolScope.Variable typeName = symbolMap.variableRead(node.name, node.line, node.column);
		return new VarRead(typeName.type, typeName.newName);
	}

	private MemRead visitMemRead(MemRead node) {
		final SymbolScope.Variable variable = symbolMap.variableRead(node.name, node.line, node.column);
		if (variable.type != BasicTypes.UINT16) {
			throw new TransformationFailedException(Messages.errorMemAccessNeedsU16(node.line, node.column, node.name, variable.type));
		}

		return new MemRead(BasicTypes.UINT8, variable.newName);
	}

	private Expression visitTypeCast(TypeCast node) {
		final Expression newExpression = visitExpression(node.expression);
		final Type expressionType = newExpression.getType();
		final Type type = BasicTypes.getType(node.typeName, false);
		if (expressionType == type) {
			warning(Messages.warningUnnecessaryCastTo(node.line, node.column, type));
			return newExpression;
		}

		if (BasicTypes.canBeAssignedFrom(type, expressionType)) {
			warning(Messages.warningUnnecessaryCastTo(node.line, node.column, type));
		}

		return new TypeCast(type, newExpression);
	}

	private void reportMissingMainFunction() {
		for (Map.Entry<String, Function> entry : functions.entrySet()) {
			final String name = entry.getKey();
			final Function function = entry.getValue();
			if (isMainFunction(name, function)) {
				function.setUsed();
				return;
			}
		}

		throw new TransformationFailedException(Messages.errorMissingMain());
	}

	private void reportIllegalBreakStatement(DeclarationList root) {
		for (Declaration declaration : root.getDeclarations()) {
			declaration.visit(new DeclarationVisitor<>() {
				@Override
				public Object visitGlobalVarDeclaration(GlobalVarDeclaration node) {
					return node;
				}

				@Override
				public Object visitFunctionDeclaration(FuncDeclaration node) {
					reportIllegalBreakStatement(node.statementList);
					return node;
				}
			});
		}
	}

	private void reportIllegalBreakStatement(StatementList statementList) {
		for (Statement statement : statementList.getStatements()) {
			statement.visit(new StatementVisitor<>() {
				@Override
				public Object visitAssignment(Assignment node) {
					return node;
				}

				@Override
				public Object visitMemAssignment(MemAssignment node) {
					return node;
				}

				@Override
				public Object visitStatementList(StatementList node) {
					reportIllegalBreakStatement(node);
					return node;
				}

				@Override
				public Object visitLocalVarDeclaration(VarDeclaration node) {
					return node;
				}

				@Override
				public Object visitCall(CallStatement node) {
					return node;
				}

				@Override
				public Object visitReturn(ReturnStatement node) {
					return node;
				}

				@Override
				public Object visitIf(IfStatement node) {
					reportIllegalBreakStatement(node.trueStatements);
					reportIllegalBreakStatement(node.falseStatements);
					return node;
				}

				@Override
				public Object visitWhile(WhileStatement node) {
					return node;
				}

				@Override
				public Object visitBreak(BreakStatement node) {
					throw new TransformationFailedException(Messages.errorBreakStatementNotInWhile(node.line, node.column));
				}
			});
		}
	}

	private DeclarationList reportAndRemoveUnusedFunctions(DeclarationList root) {
		final List<GlobalVarDeclaration> varDeclarations = new ArrayList<>();
		final List<FuncDeclaration> functionDeclarations = new ArrayList<>();

		for (Declaration declaration : root.getDeclarations()) {
			declaration.visit(new DeclarationVisitor<>() {
				@Override
				public Object visitGlobalVarDeclaration(GlobalVarDeclaration node) {
					varDeclarations.add(node);
					return node;
				}

				@Override
				public Object visitFunctionDeclaration(FuncDeclaration node) {
					final Function function = functions.get(node.name);
					if (function.used) {
						functionDeclarations.add(node);
					}
					else {
						warning(Messages.warningUnusedFunction(function.line, function.column, node.name));
					}
					return node;
				}
			});
		}

		final DeclarationList newRoot = new DeclarationList();
		for (GlobalVarDeclaration declaration : varDeclarations) {
			newRoot.add(declaration);
		}
		for (FuncDeclaration declaration : functionDeclarations) {
			newRoot.add(declaration);
		}
		return newRoot;
	}

	private boolean isMainFunction(String name, Function function) {
		return "main".equals(name)
				&& function.type == BasicTypes.VOID
				&& function.parameterTypes.isEmpty();
	}

	private void warning(String message) {
		warningOutput.print(message);
		warningOutput.println();
	}

	// Inner Classes ==========================================================

	private static final class Function {
		public final Type type;
		public final List<Type> parameterTypes;
		private final int line;
		private final int column;

		private boolean used;

		private Function(Type type, List<Type> parameterTypes, int line, int column) {
			this.type = type;
			this.parameterTypes = Collections.unmodifiableList(parameterTypes);
			this.line = line;
			this.column = column;
		}

		public void setUsed() {
			used = true;
		}
	}
}
