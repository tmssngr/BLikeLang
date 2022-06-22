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
	 * Enter a parse tree produced by the {@code statementAssign}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatementAssign(BLikeLangParser.StatementAssignContext ctx);
	/**
	 * Exit a parse tree produced by the {@code statementAssign}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatementAssign(BLikeLangParser.StatementAssignContext ctx);
	/**
	 * Enter a parse tree produced by the {@code statementDeclaration}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatementDeclaration(BLikeLangParser.StatementDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code statementDeclaration}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatementDeclaration(BLikeLangParser.StatementDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code statementEmpty}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatementEmpty(BLikeLangParser.StatementEmptyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code statementEmpty}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatementEmpty(BLikeLangParser.StatementEmptyContext ctx);
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
	 * Enter a parse tree produced by the {@code exprParen}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprParen(BLikeLangParser.ExprParenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprParen}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprParen(BLikeLangParser.ExprParenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprAddSub}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprAddSub(BLikeLangParser.ExprAddSubContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprAddSub}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprAddSub(BLikeLangParser.ExprAddSubContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprVar}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprVar(BLikeLangParser.ExprVarContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprVar}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprVar(BLikeLangParser.ExprVarContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprNumber}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprNumber(BLikeLangParser.ExprNumberContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprNumber}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprNumber(BLikeLangParser.ExprNumberContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprMultiply}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprMultiply(BLikeLangParser.ExprMultiplyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprMultiply}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprMultiply(BLikeLangParser.ExprMultiplyContext ctx);
}