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
	 * Enter a parse tree produced by {@link BLikeLangParser#declarations}.
	 * @param ctx the parse tree
	 */
	void enterDeclarations(BLikeLangParser.DeclarationsContext ctx);
	/**
	 * Exit a parse tree produced by {@link BLikeLangParser#declarations}.
	 * @param ctx the parse tree
	 */
	void exitDeclarations(BLikeLangParser.DeclarationsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code globalVarDeclaration}
	 * labeled alternative in {@link BLikeLangParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterGlobalVarDeclaration(BLikeLangParser.GlobalVarDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code globalVarDeclaration}
	 * labeled alternative in {@link BLikeLangParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitGlobalVarDeclaration(BLikeLangParser.GlobalVarDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code funcDeclaration}
	 * labeled alternative in {@link BLikeLangParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterFuncDeclaration(BLikeLangParser.FuncDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code funcDeclaration}
	 * labeled alternative in {@link BLikeLangParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitFuncDeclaration(BLikeLangParser.FuncDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BLikeLangParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDeclaration(BLikeLangParser.FunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BLikeLangParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDeclaration(BLikeLangParser.FunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BLikeLangParser#parameterDeclarations}.
	 * @param ctx the parse tree
	 */
	void enterParameterDeclarations(BLikeLangParser.ParameterDeclarationsContext ctx);
	/**
	 * Exit a parse tree produced by {@link BLikeLangParser#parameterDeclarations}.
	 * @param ctx the parse tree
	 */
	void exitParameterDeclarations(BLikeLangParser.ParameterDeclarationsContext ctx);
	/**
	 * Enter a parse tree produced by {@link BLikeLangParser#parameterDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterParameterDeclaration(BLikeLangParser.ParameterDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BLikeLangParser#parameterDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitParameterDeclaration(BLikeLangParser.ParameterDeclarationContext ctx);
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
	 * Enter a parse tree produced by the {@code returnStatement}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(BLikeLangParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code returnStatement}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(BLikeLangParser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ifStatement}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(BLikeLangParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ifStatement}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(BLikeLangParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code inferVarDeclaration}
	 * labeled alternative in {@link BLikeLangParser#varDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInferVarDeclaration(BLikeLangParser.InferVarDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code inferVarDeclaration}
	 * labeled alternative in {@link BLikeLangParser#varDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInferVarDeclaration(BLikeLangParser.InferVarDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code typeVarDeclaration}
	 * labeled alternative in {@link BLikeLangParser#varDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterTypeVarDeclaration(BLikeLangParser.TypeVarDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code typeVarDeclaration}
	 * labeled alternative in {@link BLikeLangParser#varDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitTypeVarDeclaration(BLikeLangParser.TypeVarDeclarationContext ctx);
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
	 * Enter a parse tree produced by the {@code binaryExpressionBool}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinaryExpressionBool(BLikeLangParser.BinaryExpressionBoolContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binaryExpressionBool}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinaryExpressionBool(BLikeLangParser.BinaryExpressionBoolContext ctx);
	/**
	 * Enter a parse tree produced by the {@code typeCast}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterTypeCast(BLikeLangParser.TypeCastContext ctx);
	/**
	 * Exit a parse tree produced by the {@code typeCast}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitTypeCast(BLikeLangParser.TypeCastContext ctx);
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
	 * Enter a parse tree produced by the {@code booleanLiteral}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBooleanLiteral(BLikeLangParser.BooleanLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code booleanLiteral}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBooleanLiteral(BLikeLangParser.BooleanLiteralContext ctx);
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
	 * Enter a parse tree produced by {@link BLikeLangParser#functionCallParameters}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCallParameters(BLikeLangParser.FunctionCallParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link BLikeLangParser#functionCallParameters}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCallParameters(BLikeLangParser.FunctionCallParametersContext ctx);
}