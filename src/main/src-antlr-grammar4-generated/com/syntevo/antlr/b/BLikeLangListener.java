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
	 * Enter a parse tree produced by the {@code exprBinary}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprBinary(BLikeLangParser.ExprBinaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprBinary}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprBinary(BLikeLangParser.ExprBinaryContext ctx);
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
}