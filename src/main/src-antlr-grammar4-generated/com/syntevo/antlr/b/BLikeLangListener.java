// Generated from grammar/BLikeLang.g4 by ANTLR 4.7.2
package com.syntevo.antlr.b;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link BLikeLangParser}.
 */
public interface BLikeLangListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link BLikeLangParser#root}.
	 * @param ctx the parse tree
	 */
	void enterRoot(BLikeLangParser.RootContext ctx);
	/**
	 * Exit a parse tree produced by {@link BLikeLangParser#root}.
	 * @param ctx the parse tree
	 */
	void exitRoot(BLikeLangParser.RootContext ctx);
	/**
	 * Enter a parse tree produced by {@link BLikeLangParser#statements}.
	 * @param ctx the parse tree
	 */
	void enterStatements(BLikeLangParser.StatementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link BLikeLangParser#statements}.
	 * @param ctx the parse tree
	 */
	void exitStatements(BLikeLangParser.StatementsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code localVarDeclaration}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterLocalVarDeclaration(BLikeLangParser.LocalVarDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code localVarDeclaration}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitLocalVarDeclaration(BLikeLangParser.LocalVarDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assignStatement}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAssignStatement(BLikeLangParser.AssignStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code assignStatement}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAssignStatement(BLikeLangParser.AssignStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code blockStatement}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBlockStatement(BLikeLangParser.BlockStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code blockStatement}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBlockStatement(BLikeLangParser.BlockStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link BLikeLangParser#varDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVarDeclaration(BLikeLangParser.VarDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BLikeLangParser#varDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVarDeclaration(BLikeLangParser.VarDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BLikeLangParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(BLikeLangParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link BLikeLangParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(BLikeLangParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code readVariable}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterReadVariable(BLikeLangParser.ReadVariableContext ctx);
	/**
	 * Exit a parse tree produced by the {@code readVariable}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitReadVariable(BLikeLangParser.ReadVariableContext ctx);
	/**
	 * Enter a parse tree produced by the {@code functionCall}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(BLikeLangParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code functionCall}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(BLikeLangParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binaryExpressionPoint}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinaryExpressionPoint(BLikeLangParser.BinaryExpressionPointContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binaryExpressionPoint}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinaryExpressionPoint(BLikeLangParser.BinaryExpressionPointContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionInParenthesis}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpressionInParenthesis(BLikeLangParser.ExpressionInParenthesisContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionInParenthesis}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpressionInParenthesis(BLikeLangParser.ExpressionInParenthesisContext ctx);
	/**
	 * Enter a parse tree produced by the {@code numberLiteral}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNumberLiteral(BLikeLangParser.NumberLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code numberLiteral}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNumberLiteral(BLikeLangParser.NumberLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binaryExpressionDash}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinaryExpressionDash(BLikeLangParser.BinaryExpressionDashContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binaryExpressionDash}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinaryExpressionDash(BLikeLangParser.BinaryExpressionDashContext ctx);
	/**
	 * Enter a parse tree produced by {@link BLikeLangParser#parameters}.
	 * @param ctx the parse tree
	 */
	void enterParameters(BLikeLangParser.ParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link BLikeLangParser#parameters}.
	 * @param ctx the parse tree
	 */
	void exitParameters(BLikeLangParser.ParametersContext ctx);
}