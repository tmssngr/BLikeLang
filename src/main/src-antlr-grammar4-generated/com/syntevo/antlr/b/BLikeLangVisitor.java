// Generated from grammar/BLikeLang.g4 by ANTLR 4.7.2
package com.syntevo.antlr.b;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link BLikeLangParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface BLikeLangVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link BLikeLangParser#root}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRoot(BLikeLangParser.RootContext ctx);
	/**
	 * Visit a parse tree produced by {@link BLikeLangParser#declarations}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclarations(BLikeLangParser.DeclarationsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code globalVarDeclaration}
	 * labeled alternative in {@link BLikeLangParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGlobalVarDeclaration(BLikeLangParser.GlobalVarDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code funcDeclaration}
	 * labeled alternative in {@link BLikeLangParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncDeclaration(BLikeLangParser.FuncDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BLikeLangParser#functionDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDeclaration(BLikeLangParser.FunctionDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BLikeLangParser#parameterDeclarations}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterDeclarations(BLikeLangParser.ParameterDeclarationsContext ctx);
	/**
	 * Visit a parse tree produced by {@link BLikeLangParser#parameterDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterDeclaration(BLikeLangParser.ParameterDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code localVarDeclaration}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocalVarDeclaration(BLikeLangParser.LocalVarDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code assignStatement}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignStatement(BLikeLangParser.AssignStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code blockStatement}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockStatement(BLikeLangParser.BlockStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code returnStatement}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(BLikeLangParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link BLikeLangParser#varDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDeclaration(BLikeLangParser.VarDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BLikeLangParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(BLikeLangParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code readVariable}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReadVariable(BLikeLangParser.ReadVariableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code functionCall}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(BLikeLangParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binaryExpressionPoint}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryExpressionPoint(BLikeLangParser.BinaryExpressionPointContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expressionInParenthesis}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionInParenthesis(BLikeLangParser.ExpressionInParenthesisContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numberLiteral}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberLiteral(BLikeLangParser.NumberLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binaryExpressionDash}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryExpressionDash(BLikeLangParser.BinaryExpressionDashContext ctx);
	/**
	 * Visit a parse tree produced by {@link BLikeLangParser#functionCallParameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCallParameters(BLikeLangParser.FunctionCallParametersContext ctx);
}