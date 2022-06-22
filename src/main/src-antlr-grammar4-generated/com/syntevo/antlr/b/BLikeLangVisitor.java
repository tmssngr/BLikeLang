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
	 * Visit a parse tree produced by {@link BLikeLangParser#statements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatements(BLikeLangParser.StatementsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code statementAssign}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatementAssign(BLikeLangParser.StatementAssignContext ctx);
	/**
	 * Visit a parse tree produced by the {@code statementDeclaration}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatementDeclaration(BLikeLangParser.StatementDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code statementEmpty}
	 * labeled alternative in {@link BLikeLangParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatementEmpty(BLikeLangParser.StatementEmptyContext ctx);
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
	 * Visit a parse tree produced by the {@code exprParen}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprParen(BLikeLangParser.ExprParenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprAddSub}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprAddSub(BLikeLangParser.ExprAddSubContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprVar}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprVar(BLikeLangParser.ExprVarContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprNumber}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprNumber(BLikeLangParser.ExprNumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprMultiply}
	 * labeled alternative in {@link BLikeLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprMultiply(BLikeLangParser.ExprMultiplyContext ctx);
}