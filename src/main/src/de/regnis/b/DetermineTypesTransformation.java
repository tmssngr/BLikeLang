package de.regnis.b;

import de.regnis.b.ast.*;
import de.regnis.b.out.StringOutput;
import de.regnis.b.type.BasicTypes;
import de.regnis.b.type.Type;
import de.regnis.utils.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
		newRoot = transformation.reportAndRemoveUnusedFunctions(newRoot);
		return newRoot;
	}

	// Fields =================================================================

	private final Map<String, Function> functions = new LinkedHashMap<>();
	private final StringOutput warningOutput;

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
				public Object visitFunctionDeclaration(FuncDeclaration node) {
					final int parameterCount = node.parameters.getParameters().size();
					if (functions.containsKey(node.name)) {
						throw new TransformationFailedException(Messages.errorFunctionAlreadyDeclared(node.position.line(), node.position.column(), node.name));
					}

					functions.put(node.name, new Function(node.type, parameterCount, node.position.line(), node.position.column()));

					return node;
				}
			});
		}
	}

	private DeclarationList visitDeclarationList(DeclarationList root) {
		final DeclarationList newRoot = root.transform(declaration -> declaration.visit(this::visitFunctionDeclaration));

		symbolMap.reportUnusedVariables(warningOutput);

		return newRoot;
	}

	private Declaration visitFunctionDeclaration(FuncDeclaration node) {
		final SymbolScope outerSymbolMap = symbolMap;
		functionReturnType = node.type;
		symbolMap          = symbolMap.createChildMap(SymbolScope.ScopeKind.Parameter);
		localVarCount      = 0;
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
			symbolMap          = outerSymbolMap;
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

	private FuncDeclarationParameters declareParameters(FuncDeclarationParameters parameters) {
		final List<FuncDeclarationParameter> renamedParameters = new ArrayList<>();
		int i = 0;
		for (FuncDeclarationParameter parameter : parameters.getParameters()) {
			final String newName = "p" + i;
			i++;

			symbolMap.declareVariable(parameter.name, newName, parameter.position.line(), parameter.position.column());

			renamedParameters.add(new FuncDeclarationParameter(newName));
		}
		return FuncDeclarationParameters.of(renamedParameters);
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
		final Expression newExpression = visitExpression(varDeclaration.expression);

		symbolMap.declareVariable(varDeclaration.name, newName, varDeclaration.position.line(), varDeclaration.position.column());
		return new VarDeclaration(newName, newExpression);
	}

	private Assignment visitAssignment(Assignment node) {
		final SymbolScope.Variable variable = symbolMap.variableRead(node.name, node.position);
		final Expression newExpression = visitExpression(node.expression);

		return new Assignment(node.operation, variable.newName, newExpression);
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
		});
	}

	private BinaryExpression visitBinary(BinaryExpression node) {
		final Expression newLeft = visitExpression(node.left);
		final Expression newRight = visitExpression(node.right);

		return new BinaryExpression(newLeft, node.operator, newRight);
	}

	private FuncCall visitFunctionCall(FuncCall node) {
		final Tuple<Function, FuncCallParameters> function = handleCall(node.name, node.parameters, node.position.line(), node.position.column());
		final Type type = function.first.type;
		if (type == BasicTypes.VOID) {
			throw new TransformationFailedException(Messages.errorFunctionDoesNotReturnAValue(node.position.line(), node.position.column(), node.name));
		}

		return new FuncCall(node.name, function.second);
	}

	private CallStatement visitCall(CallStatement node) {
		final Tuple<Function, FuncCallParameters> function = handleCall(node.name, node.parameters, node.position.line(), node.position.column());
		final Type type = function.first.type;
		if (type != BasicTypes.VOID) {
			warning(Messages.warningIgnoredReturnValue(node.position.line(), node.position.column(), node.name, type));
		}

		return new CallStatement(node.name, function.second);
	}

	@NotNull
	private Tuple<Function, FuncCallParameters> handleCall(String name, FuncCallParameters callParameters, int line, int column) {
		final Function function = functions.get(name);
		if (function == null) {
			throw new TransformationFailedException(Messages.errorUndeclaredFunction(line, column, name));
		}

		function.setUsed();

		final List<Expression> parameters = callParameters.getExpressions();
		if (parameters.size() != function.parameterCount) {
			throw new TransformationFailedException("Function " + name + " expects " + function.parameterCount + " expressions, but got " + parameters.size());
		}

		final FuncCallParameters newParameters = callParameters.transform(expression -> visitExpression(expression));
		return new Tuple<>(function, newParameters);
	}

	private ReturnStatement visitReturn(ReturnStatement node) {
		if (functionReturnType == null) {
			throw new IllegalStateException();
		}

		if (functionReturnType == BasicTypes.VOID) {
			if (node.expression != null) {
				throw new TransformationFailedException(Messages.errorNoReturnExpressionExpectedForVoid(node.position.line(), node.position.column()));
			}
			return node;
		}

		if (node.expression == null) {
			throw new TransformationFailedException(Messages.errorReturnExpressionExpected(node.position.line(), node.position.column(), functionReturnType));
		}

		final Expression newExpression = visitExpression(node.expression);
		return new ReturnStatement(newExpression);
	}

	private IfStatement visitIf(IfStatement node) {
		final Expression newExpression = visitExpression(node.expression);
		return new IfStatement(newExpression, visitStatementList(node.trueStatements), visitStatementList(node.falseStatements));
	}

	private WhileStatement visitWhile(WhileStatement node) {
		final Expression newExpression = visitExpression(node.expression);
		return new WhileStatement(newExpression, visitStatementList(node.statements));
	}

	private VarRead visitVarRead(VarRead node) {
		final SymbolScope.Variable typeName = symbolMap.variableRead(node.name, node.position);
		return new VarRead(typeName.newName);
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
					throw new TransformationFailedException(Messages.errorBreakStatementNotInWhile(node.position.line(), node.position.column()));
				}
			});
		}
	}

	private DeclarationList reportAndRemoveUnusedFunctions(DeclarationList root) {
		return root.transform(declaration -> declaration.visit(new DeclarationVisitor<>() {
			@Nullable
			@Override
			public Declaration visitFunctionDeclaration(FuncDeclaration node) {
				final Function function = functions.get(node.name);
				if (function.used) {
					return node;
				}

				warning(Messages.warningUnusedFunction(function.line, function.column, node.name));
				return null;
			}
		}));
	}

	private boolean isMainFunction(String name, Function function) {
		return "main".equals(name)
				&& function.type == BasicTypes.VOID
				&& function.parameterCount == 0;
	}

	private void warning(String message) {
		warningOutput.print(message);
		warningOutput.println();
	}

	// Inner Classes ==========================================================

	private static final class Function {
		public final Type type;
		private final int parameterCount;
		private final int line;
		private final int column;

		private boolean used;

		private Function(Type type, int parameterCount, int line, int column) {
			this.type           = type;
			this.parameterCount = parameterCount;
			this.line           = line;
			this.column         = column;
		}

		public void setUsed() {
			used = true;
		}
	}
}
