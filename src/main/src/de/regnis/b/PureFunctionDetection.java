package de.regnis.b;

import de.regnis.b.ast.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A function is considered pure if it only operates on its parameters.
 * Consequently, it is considered non-pure if it operates on global variables.
 *
 * @author Thomas Singer
 */
public final class PureFunctionDetection {

	// Static =================================================================

	/**
	 * Note, that variable and parameter names already need to be unified.
	 */
	public static Set<String> detectPureFunctions(@NotNull DeclarationList root) {
		final PureFunctionDetection detection = new PureFunctionDetection();
		detection.determineGlobalVariables(root);
		detection.detectNonPureFunctions(root);
		return detection.getPureFunctions();
	}

	// Fields =================================================================

	private final Set<String> globalVariables = new HashSet<>();
	private final Map<String, Set<String>> functionCalledByFunctions = new HashMap<>();
	private final Set<String> nonPureFunctions = new HashSet<>();

	private String currentFunction;

	// Setup ==================================================================

	private PureFunctionDetection() {
	}

	// Utils ==================================================================

	private void determineGlobalVariables(DeclarationList root) {
		for (Declaration declaration : root.getDeclarations()) {
			declaration.visit(new DeclarationVisitor<>() {
				@Override
				public Object visitFunctionDeclaration(FuncDeclaration node) {
					if (functionCalledByFunctions.put(node.name, new HashSet<>()) != null) {
						throw new IllegalStateException("Duplicate function " + node.name);
					}
					return node;
				}
			});
		}
	}

	private void detectNonPureFunctions(@NotNull DeclarationList root) {
		for (Declaration declaration : root.getDeclarations()) {
			declaration.visit(new DeclarationVisitor<>() {
				@Override
				public Object visitFunctionDeclaration(FuncDeclaration node) {
					currentFunction = node.name;

					visitStatements(node.statementList);
					return node;
				}
			});
		}

		markFunctionsCalledByNonPureFunctionsNonPure();
	}

	private void markFunctionsCalledByNonPureFunctionsNonPure() {
		while (true) {
			boolean unchanged = true;
			for (Map.Entry<String, Set<String>> entry : functionCalledByFunctions.entrySet()) {
				final String function = entry.getKey();
				if (isNonPure(function)) {
					final Set<String> callingFunctions = entry.getValue();
					if (nonPureFunctions.addAll(callingFunctions)) {
						unchanged = false;
					}
				}
			}
			if (unchanged) {
				break;
			}
		}
	}

	private boolean isNonPure(String function) {
		return nonPureFunctions.contains(function);
	}

	private Set<String> getPureFunctions() {
		final Set<String> pureFunctions = new HashSet<>(functionCalledByFunctions.keySet());
		pureFunctions.removeAll(nonPureFunctions);
		return Collections.unmodifiableSet(pureFunctions);
	}

	private void visitStatements(StatementList statementList) {
		for (Statement statement : statementList.getStatements()) {
			statement.visit(new StatementVisitor<>() {
				@Override
				public Object visitAssignment(Assignment node) {
					visitExpression(node.expression);
					return node;
				}

				@Override
				public Object visitStatementList(StatementList node) {
					visitStatements(statementList);
					return node;
				}

				@Override
				public Object visitLocalVarDeclaration(VarDeclaration node) {
					visitExpression(node.expression);
					return node;
				}

				@Override
				public Object visitCall(CallStatement node) {
					functionCalled(node.name, node.getParameters());
					return node;
				}

				@Override
				public Object visitReturn(ReturnStatement node) {
					if (node.expression != null) {
						visitExpression(node.expression);
					}
					return node;
				}

				@Override
				public Object visitIf(IfStatement node) {
					visitExpression(node.expression);
					visitStatements(node.trueStatements);
					visitStatements(node.falseStatements);
					return node;
				}

				@Override
				public Object visitWhile(WhileStatement node) {
					visitExpression(node.expression);
					visitStatements(node.statements);
					return node;
				}

				@Override
				public Object visitBreak(BreakStatement node) {
					return node;
				}
			});
		}
	}

	private void visitExpression(Expression expression) {
		expression.visit(new ExpressionVisitor<>() {
			@Override
			public Object visitBinary(BinaryExpression node) {
				visitExpression(node.left);
				visitExpression(node.right);
				return node;
			}

			@Override
			public Object visitFunctionCall(FuncCall node) {
				functionCalled(node.name, node.getParameters());
				return node;
			}

			@Override
			public Object visitNumber(NumberLiteral node) {
				return node;
			}

			@Override
			public Object visitVarRead(VarRead node) {
				if (globalVariables.contains(node.name)) {
					nonPureFunctions.add(currentFunction);
				}
				return node;
			}
		});
	}

	private void functionCalled(String name, List<Expression> parameters) {
		if (!name.equals(currentFunction)) {
			final Set<String> calledByFunctions = functionCalledByFunctions.get(name);
			calledByFunctions.add(currentFunction);
		}

		for (Expression parameter : parameters) {
			visitExpression(parameter);
		}
	}
}
