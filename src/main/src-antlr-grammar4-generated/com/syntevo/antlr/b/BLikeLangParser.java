// Generated from grammar/BLikeLang.g4 by ANTLR 4.7.2
package com.syntevo.antlr.b;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class BLikeLangParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		Comma=1, End=2, Assign=3, Plus=4, Minus=5, Multiply=6, ShiftL=7, ShiftR=8, 
		Lt=9, Le=10, Eq=11, Ge=12, Gt=13, Ne=14, BitAnd=15, BitOr=16, BitXor=17, 
		ParenOpen=18, ParenClose=19, CurlyOpen=20, CurlyClose=21, Break=22, Else=23, 
		If=24, Return=25, Var=26, While=27, CharLiteral=28, BooleanLiteral=29, 
		Number=30, Identifier=31, Whitespace=32, NL=33, LineComment=34, BlockComment=35;
	public static final int
		RULE_root = 0, RULE_declarations = 1, RULE_declaration = 2, RULE_functionDeclaration = 3, 
		RULE_parameterDeclarations = 4, RULE_parameterDeclaration = 5, RULE_statement = 6, 
		RULE_varDeclaration = 7, RULE_assignment = 8, RULE_expression = 9, RULE_functionCallParameters = 10;
	private static String[] makeRuleNames() {
		return new String[] {
			"root", "declarations", "declaration", "functionDeclaration", "parameterDeclarations", 
			"parameterDeclaration", "statement", "varDeclaration", "assignment", 
			"expression", "functionCallParameters"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "','", "';'", "'='", "'+'", "'-'", "'*'", "'<<'", "'>>'", "'<'", 
			"'<='", "'=='", "'>='", "'>'", "'!='", "'&'", "'|'", "'^'", "'('", "')'", 
			"'{'", "'}'", "'break'", "'else'", "'if'", "'return'", "'var'", "'while'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Comma", "End", "Assign", "Plus", "Minus", "Multiply", "ShiftL", 
			"ShiftR", "Lt", "Le", "Eq", "Ge", "Gt", "Ne", "BitAnd", "BitOr", "BitXor", 
			"ParenOpen", "ParenClose", "CurlyOpen", "CurlyClose", "Break", "Else", 
			"If", "Return", "Var", "While", "CharLiteral", "BooleanLiteral", "Number", 
			"Identifier", "Whitespace", "NL", "LineComment", "BlockComment"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "BLikeLang.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public BLikeLangParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class RootContext extends ParserRuleContext {
		public DeclarationsContext declarations() {
			return getRuleContext(DeclarationsContext.class,0);
		}
		public RootContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_root; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterRoot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitRoot(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitRoot(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RootContext root() throws RecognitionException {
		RootContext _localctx = new RootContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_root);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(22);
			declarations();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DeclarationsContext extends ParserRuleContext {
		public List<DeclarationContext> declaration() {
			return getRuleContexts(DeclarationContext.class);
		}
		public DeclarationContext declaration(int i) {
			return getRuleContext(DeclarationContext.class,i);
		}
		public DeclarationsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declarations; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterDeclarations(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitDeclarations(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitDeclarations(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeclarationsContext declarations() throws RecognitionException {
		DeclarationsContext _localctx = new DeclarationsContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_declarations);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(27);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Var || _la==Identifier) {
				{
				{
				setState(24);
				declaration();
				}
				}
				setState(29);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DeclarationContext extends ParserRuleContext {
		public DeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaration; }
	 
		public DeclarationContext() { }
		public void copyFrom(DeclarationContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class GlobalVarDeclarationContext extends DeclarationContext {
		public VarDeclarationContext varDeclaration() {
			return getRuleContext(VarDeclarationContext.class,0);
		}
		public GlobalVarDeclarationContext(DeclarationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterGlobalVarDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitGlobalVarDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitGlobalVarDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FuncDeclarationContext extends DeclarationContext {
		public FunctionDeclarationContext functionDeclaration() {
			return getRuleContext(FunctionDeclarationContext.class,0);
		}
		public FuncDeclarationContext(DeclarationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterFuncDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitFuncDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitFuncDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeclarationContext declaration() throws RecognitionException {
		DeclarationContext _localctx = new DeclarationContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_declaration);
		try {
			setState(32);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				_localctx = new GlobalVarDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(30);
				varDeclaration();
				}
				break;
			case 2:
				_localctx = new FuncDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(31);
				functionDeclaration();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionDeclarationContext extends ParserRuleContext {
		public Token type;
		public Token name;
		public TerminalNode ParenOpen() { return getToken(BLikeLangParser.ParenOpen, 0); }
		public ParameterDeclarationsContext parameterDeclarations() {
			return getRuleContext(ParameterDeclarationsContext.class,0);
		}
		public TerminalNode ParenClose() { return getToken(BLikeLangParser.ParenClose, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public List<TerminalNode> Identifier() { return getTokens(BLikeLangParser.Identifier); }
		public TerminalNode Identifier(int i) {
			return getToken(BLikeLangParser.Identifier, i);
		}
		public FunctionDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterFunctionDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitFunctionDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitFunctionDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionDeclarationContext functionDeclaration() throws RecognitionException {
		FunctionDeclarationContext _localctx = new FunctionDeclarationContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_functionDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(34);
			((FunctionDeclarationContext)_localctx).type = match(Identifier);
			setState(35);
			((FunctionDeclarationContext)_localctx).name = match(Identifier);
			setState(36);
			match(ParenOpen);
			setState(37);
			parameterDeclarations();
			setState(38);
			match(ParenClose);
			setState(39);
			statement();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParameterDeclarationsContext extends ParserRuleContext {
		public List<ParameterDeclarationContext> parameterDeclaration() {
			return getRuleContexts(ParameterDeclarationContext.class);
		}
		public ParameterDeclarationContext parameterDeclaration(int i) {
			return getRuleContext(ParameterDeclarationContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(BLikeLangParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(BLikeLangParser.Comma, i);
		}
		public ParameterDeclarationsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameterDeclarations; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterParameterDeclarations(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitParameterDeclarations(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitParameterDeclarations(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterDeclarationsContext parameterDeclarations() throws RecognitionException {
		ParameterDeclarationsContext _localctx = new ParameterDeclarationsContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_parameterDeclarations);
		int _la;
		try {
			setState(51);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(42);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Identifier) {
					{
					setState(41);
					parameterDeclaration();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(44);
				parameterDeclaration();
				setState(47); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(45);
					match(Comma);
					setState(46);
					parameterDeclaration();
					}
					}
					setState(49); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==Comma );
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParameterDeclarationContext extends ParserRuleContext {
		public Token type;
		public Token name;
		public List<TerminalNode> Identifier() { return getTokens(BLikeLangParser.Identifier); }
		public TerminalNode Identifier(int i) {
			return getToken(BLikeLangParser.Identifier, i);
		}
		public ParameterDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameterDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterParameterDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitParameterDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitParameterDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterDeclarationContext parameterDeclaration() throws RecognitionException {
		ParameterDeclarationContext _localctx = new ParameterDeclarationContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_parameterDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(53);
			((ParameterDeclarationContext)_localctx).type = match(Identifier);
			setState(54);
			((ParameterDeclarationContext)_localctx).name = match(Identifier);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
	 
		public StatementContext() { }
		public void copyFrom(StatementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class WhileStatementContext extends StatementContext {
		public TerminalNode While() { return getToken(BLikeLangParser.While, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public WhileStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterWhileStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitWhileStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitWhileStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LocalVarDeclarationContext extends StatementContext {
		public VarDeclarationContext varDeclaration() {
			return getRuleContext(VarDeclarationContext.class,0);
		}
		public LocalVarDeclarationContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterLocalVarDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitLocalVarDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitLocalVarDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BlockStatementContext extends StatementContext {
		public TerminalNode CurlyOpen() { return getToken(BLikeLangParser.CurlyOpen, 0); }
		public TerminalNode CurlyClose() { return getToken(BLikeLangParser.CurlyClose, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public BlockStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterBlockStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitBlockStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitBlockStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AssignStatementContext extends StatementContext {
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public AssignStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterAssignStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitAssignStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitAssignStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BreakStatementContext extends StatementContext {
		public TerminalNode Break() { return getToken(BLikeLangParser.Break, 0); }
		public TerminalNode End() { return getToken(BLikeLangParser.End, 0); }
		public BreakStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterBreakStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitBreakStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitBreakStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CallStatementContext extends StatementContext {
		public Token func;
		public TerminalNode ParenOpen() { return getToken(BLikeLangParser.ParenOpen, 0); }
		public FunctionCallParametersContext functionCallParameters() {
			return getRuleContext(FunctionCallParametersContext.class,0);
		}
		public TerminalNode ParenClose() { return getToken(BLikeLangParser.ParenClose, 0); }
		public TerminalNode End() { return getToken(BLikeLangParser.End, 0); }
		public TerminalNode Identifier() { return getToken(BLikeLangParser.Identifier, 0); }
		public CallStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterCallStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitCallStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitCallStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ReturnStatementContext extends StatementContext {
		public TerminalNode Return() { return getToken(BLikeLangParser.Return, 0); }
		public TerminalNode End() { return getToken(BLikeLangParser.End, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ReturnStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterReturnStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitReturnStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitReturnStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IfStatementContext extends StatementContext {
		public StatementContext trueStatement;
		public StatementContext falseStatement;
		public TerminalNode If() { return getToken(BLikeLangParser.If, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public TerminalNode Else() { return getToken(BLikeLangParser.Else, 0); }
		public IfStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterIfStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitIfStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitIfStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_statement);
		int _la;
		try {
			setState(90);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				_localctx = new LocalVarDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(56);
				varDeclaration();
				}
				break;
			case 2:
				_localctx = new AssignStatementContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(57);
				assignment();
				}
				break;
			case 3:
				_localctx = new CallStatementContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(58);
				((CallStatementContext)_localctx).func = match(Identifier);
				setState(59);
				match(ParenOpen);
				setState(60);
				functionCallParameters();
				setState(61);
				match(ParenClose);
				setState(62);
				match(End);
				}
				break;
			case 4:
				_localctx = new BlockStatementContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(64);
				match(CurlyOpen);
				setState(68);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CurlyOpen) | (1L << Break) | (1L << If) | (1L << Return) | (1L << Var) | (1L << While) | (1L << Identifier))) != 0)) {
					{
					{
					setState(65);
					statement();
					}
					}
					setState(70);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(71);
				match(CurlyClose);
				}
				break;
			case 5:
				_localctx = new ReturnStatementContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(72);
				match(Return);
				setState(74);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ParenOpen) | (1L << CharLiteral) | (1L << BooleanLiteral) | (1L << Number) | (1L << Identifier))) != 0)) {
					{
					setState(73);
					expression(0);
					}
				}

				setState(76);
				match(End);
				}
				break;
			case 6:
				_localctx = new IfStatementContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(77);
				match(If);
				setState(78);
				expression(0);
				setState(79);
				((IfStatementContext)_localctx).trueStatement = statement();
				setState(82);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
				case 1:
					{
					setState(80);
					match(Else);
					setState(81);
					((IfStatementContext)_localctx).falseStatement = statement();
					}
					break;
				}
				}
				break;
			case 7:
				_localctx = new WhileStatementContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(84);
				match(While);
				setState(85);
				expression(0);
				setState(86);
				statement();
				}
				break;
			case 8:
				_localctx = new BreakStatementContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(88);
				match(Break);
				setState(89);
				match(End);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarDeclarationContext extends ParserRuleContext {
		public VarDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varDeclaration; }
	 
		public VarDeclarationContext() { }
		public void copyFrom(VarDeclarationContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class InferVarDeclarationContext extends VarDeclarationContext {
		public Token var;
		public TerminalNode Var() { return getToken(BLikeLangParser.Var, 0); }
		public TerminalNode Assign() { return getToken(BLikeLangParser.Assign, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode End() { return getToken(BLikeLangParser.End, 0); }
		public TerminalNode Identifier() { return getToken(BLikeLangParser.Identifier, 0); }
		public InferVarDeclarationContext(VarDeclarationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterInferVarDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitInferVarDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitInferVarDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TypeVarDeclarationContext extends VarDeclarationContext {
		public Token type;
		public Token var;
		public TerminalNode Assign() { return getToken(BLikeLangParser.Assign, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode End() { return getToken(BLikeLangParser.End, 0); }
		public List<TerminalNode> Identifier() { return getTokens(BLikeLangParser.Identifier); }
		public TerminalNode Identifier(int i) {
			return getToken(BLikeLangParser.Identifier, i);
		}
		public TypeVarDeclarationContext(VarDeclarationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterTypeVarDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitTypeVarDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitTypeVarDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarDeclarationContext varDeclaration() throws RecognitionException {
		VarDeclarationContext _localctx = new VarDeclarationContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_varDeclaration);
		try {
			setState(104);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Var:
				_localctx = new InferVarDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(92);
				match(Var);
				setState(93);
				((InferVarDeclarationContext)_localctx).var = match(Identifier);
				setState(94);
				match(Assign);
				setState(95);
				expression(0);
				setState(96);
				match(End);
				}
				break;
			case Identifier:
				_localctx = new TypeVarDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(98);
				((TypeVarDeclarationContext)_localctx).type = match(Identifier);
				setState(99);
				((TypeVarDeclarationContext)_localctx).var = match(Identifier);
				setState(100);
				match(Assign);
				setState(101);
				expression(0);
				setState(102);
				match(End);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssignmentContext extends ParserRuleContext {
		public Token var;
		public TerminalNode Assign() { return getToken(BLikeLangParser.Assign, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode End() { return getToken(BLikeLangParser.End, 0); }
		public TerminalNode Identifier() { return getToken(BLikeLangParser.Identifier, 0); }
		public AssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitAssignment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitAssignment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_assignment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(106);
			((AssignmentContext)_localctx).var = match(Identifier);
			setState(107);
			match(Assign);
			setState(108);
			expression(0);
			setState(109);
			match(End);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	 
		public ExpressionContext() { }
		public void copyFrom(ExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class BinaryExpressionBoolContext extends ExpressionContext {
		public ExpressionContext left;
		public Token operator;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode Lt() { return getToken(BLikeLangParser.Lt, 0); }
		public TerminalNode Le() { return getToken(BLikeLangParser.Le, 0); }
		public TerminalNode Eq() { return getToken(BLikeLangParser.Eq, 0); }
		public TerminalNode Ge() { return getToken(BLikeLangParser.Ge, 0); }
		public TerminalNode Gt() { return getToken(BLikeLangParser.Gt, 0); }
		public TerminalNode Ne() { return getToken(BLikeLangParser.Ne, 0); }
		public BinaryExpressionBoolContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterBinaryExpressionBool(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitBinaryExpressionBool(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitBinaryExpressionBool(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TypeCastContext extends ExpressionContext {
		public Token type;
		public TerminalNode ParenOpen() { return getToken(BLikeLangParser.ParenOpen, 0); }
		public TerminalNode ParenClose() { return getToken(BLikeLangParser.ParenClose, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode Identifier() { return getToken(BLikeLangParser.Identifier, 0); }
		public TypeCastContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterTypeCast(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitTypeCast(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitTypeCast(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ReadVariableContext extends ExpressionContext {
		public Token var;
		public TerminalNode Identifier() { return getToken(BLikeLangParser.Identifier, 0); }
		public ReadVariableContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterReadVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitReadVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitReadVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FunctionCallContext extends ExpressionContext {
		public Token func;
		public TerminalNode ParenOpen() { return getToken(BLikeLangParser.ParenOpen, 0); }
		public FunctionCallParametersContext functionCallParameters() {
			return getRuleContext(FunctionCallParametersContext.class,0);
		}
		public TerminalNode ParenClose() { return getToken(BLikeLangParser.ParenClose, 0); }
		public TerminalNode Identifier() { return getToken(BLikeLangParser.Identifier, 0); }
		public FunctionCallContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterFunctionCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitFunctionCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitFunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BinaryExpressionPointContext extends ExpressionContext {
		public ExpressionContext left;
		public Token operator;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode Multiply() { return getToken(BLikeLangParser.Multiply, 0); }
		public TerminalNode ShiftL() { return getToken(BLikeLangParser.ShiftL, 0); }
		public TerminalNode ShiftR() { return getToken(BLikeLangParser.ShiftR, 0); }
		public BinaryExpressionPointContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterBinaryExpressionPoint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitBinaryExpressionPoint(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitBinaryExpressionPoint(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CharLiteralContext extends ExpressionContext {
		public Token value;
		public TerminalNode CharLiteral() { return getToken(BLikeLangParser.CharLiteral, 0); }
		public CharLiteralContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterCharLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitCharLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitCharLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BinaryExpressionBitsContext extends ExpressionContext {
		public ExpressionContext left;
		public Token operator;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode BitAnd() { return getToken(BLikeLangParser.BitAnd, 0); }
		public TerminalNode BitOr() { return getToken(BLikeLangParser.BitOr, 0); }
		public TerminalNode BitXor() { return getToken(BLikeLangParser.BitXor, 0); }
		public BinaryExpressionBitsContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterBinaryExpressionBits(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitBinaryExpressionBits(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitBinaryExpressionBits(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BooleanLiteralContext extends ExpressionContext {
		public Token value;
		public TerminalNode BooleanLiteral() { return getToken(BLikeLangParser.BooleanLiteral, 0); }
		public BooleanLiteralContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterBooleanLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitBooleanLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitBooleanLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpressionInParenthesisContext extends ExpressionContext {
		public TerminalNode ParenOpen() { return getToken(BLikeLangParser.ParenOpen, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode ParenClose() { return getToken(BLikeLangParser.ParenClose, 0); }
		public ExpressionInParenthesisContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterExpressionInParenthesis(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitExpressionInParenthesis(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitExpressionInParenthesis(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumberLiteralContext extends ExpressionContext {
		public Token value;
		public TerminalNode Number() { return getToken(BLikeLangParser.Number, 0); }
		public NumberLiteralContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterNumberLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitNumberLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitNumberLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BinaryExpressionDashContext extends ExpressionContext {
		public ExpressionContext left;
		public Token operator;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode Plus() { return getToken(BLikeLangParser.Plus, 0); }
		public TerminalNode Minus() { return getToken(BLikeLangParser.Minus, 0); }
		public BinaryExpressionDashContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterBinaryExpressionDash(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitBinaryExpressionDash(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitBinaryExpressionDash(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 18;
		enterRecursionRule(_localctx, 18, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(129);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				_localctx = new NumberLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(112);
				((NumberLiteralContext)_localctx).value = match(Number);
				}
				break;
			case 2:
				{
				_localctx = new CharLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(113);
				((CharLiteralContext)_localctx).value = match(CharLiteral);
				}
				break;
			case 3:
				{
				_localctx = new BooleanLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(114);
				((BooleanLiteralContext)_localctx).value = match(BooleanLiteral);
				}
				break;
			case 4:
				{
				_localctx = new ReadVariableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(115);
				((ReadVariableContext)_localctx).var = match(Identifier);
				}
				break;
			case 5:
				{
				_localctx = new FunctionCallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(116);
				((FunctionCallContext)_localctx).func = match(Identifier);
				setState(117);
				match(ParenOpen);
				setState(118);
				functionCallParameters();
				setState(119);
				match(ParenClose);
				}
				break;
			case 6:
				{
				_localctx = new ExpressionInParenthesisContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(121);
				match(ParenOpen);
				setState(122);
				expression(0);
				setState(123);
				match(ParenClose);
				}
				break;
			case 7:
				{
				_localctx = new TypeCastContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(125);
				match(ParenOpen);
				setState(126);
				((TypeCastContext)_localctx).type = match(Identifier);
				setState(127);
				match(ParenClose);
				setState(128);
				expression(1);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(145);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(143);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
					case 1:
						{
						_localctx = new BinaryExpressionBitsContext(new ExpressionContext(_parentctx, _parentState));
						((BinaryExpressionBitsContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(131);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(132);
						((BinaryExpressionBitsContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BitAnd) | (1L << BitOr) | (1L << BitXor))) != 0)) ) {
							((BinaryExpressionBitsContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(133);
						((BinaryExpressionBitsContext)_localctx).right = expression(6);
						}
						break;
					case 2:
						{
						_localctx = new BinaryExpressionPointContext(new ExpressionContext(_parentctx, _parentState));
						((BinaryExpressionPointContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(134);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(135);
						((BinaryExpressionPointContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Multiply) | (1L << ShiftL) | (1L << ShiftR))) != 0)) ) {
							((BinaryExpressionPointContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(136);
						((BinaryExpressionPointContext)_localctx).right = expression(5);
						}
						break;
					case 3:
						{
						_localctx = new BinaryExpressionDashContext(new ExpressionContext(_parentctx, _parentState));
						((BinaryExpressionDashContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(137);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(138);
						((BinaryExpressionDashContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==Plus || _la==Minus) ) {
							((BinaryExpressionDashContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(139);
						((BinaryExpressionDashContext)_localctx).right = expression(4);
						}
						break;
					case 4:
						{
						_localctx = new BinaryExpressionBoolContext(new ExpressionContext(_parentctx, _parentState));
						((BinaryExpressionBoolContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(140);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(141);
						((BinaryExpressionBoolContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Lt) | (1L << Le) | (1L << Eq) | (1L << Ge) | (1L << Gt) | (1L << Ne))) != 0)) ) {
							((BinaryExpressionBoolContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(142);
						((BinaryExpressionBoolContext)_localctx).right = expression(3);
						}
						break;
					}
					} 
				}
				setState(147);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class FunctionCallParametersContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(BLikeLangParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(BLikeLangParser.Comma, i);
		}
		public FunctionCallParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionCallParameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterFunctionCallParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitFunctionCallParameters(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitFunctionCallParameters(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionCallParametersContext functionCallParameters() throws RecognitionException {
		FunctionCallParametersContext _localctx = new FunctionCallParametersContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_functionCallParameters);
		int _la;
		try {
			setState(158);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(149);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ParenOpen) | (1L << CharLiteral) | (1L << BooleanLiteral) | (1L << Number) | (1L << Identifier))) != 0)) {
					{
					setState(148);
					expression(0);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(151);
				expression(0);
				setState(154); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(152);
					match(Comma);
					setState(153);
					expression(0);
					}
					}
					setState(156); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==Comma );
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 9:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 5);
		case 1:
			return precpred(_ctx, 4);
		case 2:
			return precpred(_ctx, 3);
		case 3:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3%\u00a3\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\3\2\3\2\3\3\7\3\34\n\3\f\3\16\3\37\13\3\3\4\3\4\5\4#\n\4\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\5\6-\n\6\3\6\3\6\3\6\6\6\62\n\6\r\6\16"+
		"\6\63\5\6\66\n\6\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\7"+
		"\bE\n\b\f\b\16\bH\13\b\3\b\3\b\3\b\5\bM\n\b\3\b\3\b\3\b\3\b\3\b\3\b\5"+
		"\bU\n\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b]\n\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\3\t\5\tk\n\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3"+
		"\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\5"+
		"\13\u0084\n\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\7\13\u0092\n\13\f\13\16\13\u0095\13\13\3\f\5\f\u0098\n\f\3\f\3\f"+
		"\3\f\6\f\u009d\n\f\r\f\16\f\u009e\5\f\u00a1\n\f\3\f\2\3\24\r\2\4\6\b\n"+
		"\f\16\20\22\24\26\2\6\3\2\21\23\3\2\b\n\3\2\6\7\3\2\13\20\2\u00b4\2\30"+
		"\3\2\2\2\4\35\3\2\2\2\6\"\3\2\2\2\b$\3\2\2\2\n\65\3\2\2\2\f\67\3\2\2\2"+
		"\16\\\3\2\2\2\20j\3\2\2\2\22l\3\2\2\2\24\u0083\3\2\2\2\26\u00a0\3\2\2"+
		"\2\30\31\5\4\3\2\31\3\3\2\2\2\32\34\5\6\4\2\33\32\3\2\2\2\34\37\3\2\2"+
		"\2\35\33\3\2\2\2\35\36\3\2\2\2\36\5\3\2\2\2\37\35\3\2\2\2 #\5\20\t\2!"+
		"#\5\b\5\2\" \3\2\2\2\"!\3\2\2\2#\7\3\2\2\2$%\7!\2\2%&\7!\2\2&\'\7\24\2"+
		"\2\'(\5\n\6\2()\7\25\2\2)*\5\16\b\2*\t\3\2\2\2+-\5\f\7\2,+\3\2\2\2,-\3"+
		"\2\2\2-\66\3\2\2\2.\61\5\f\7\2/\60\7\3\2\2\60\62\5\f\7\2\61/\3\2\2\2\62"+
		"\63\3\2\2\2\63\61\3\2\2\2\63\64\3\2\2\2\64\66\3\2\2\2\65,\3\2\2\2\65."+
		"\3\2\2\2\66\13\3\2\2\2\678\7!\2\289\7!\2\29\r\3\2\2\2:]\5\20\t\2;]\5\22"+
		"\n\2<=\7!\2\2=>\7\24\2\2>?\5\26\f\2?@\7\25\2\2@A\7\4\2\2A]\3\2\2\2BF\7"+
		"\26\2\2CE\5\16\b\2DC\3\2\2\2EH\3\2\2\2FD\3\2\2\2FG\3\2\2\2GI\3\2\2\2H"+
		"F\3\2\2\2I]\7\27\2\2JL\7\33\2\2KM\5\24\13\2LK\3\2\2\2LM\3\2\2\2MN\3\2"+
		"\2\2N]\7\4\2\2OP\7\32\2\2PQ\5\24\13\2QT\5\16\b\2RS\7\31\2\2SU\5\16\b\2"+
		"TR\3\2\2\2TU\3\2\2\2U]\3\2\2\2VW\7\35\2\2WX\5\24\13\2XY\5\16\b\2Y]\3\2"+
		"\2\2Z[\7\30\2\2[]\7\4\2\2\\:\3\2\2\2\\;\3\2\2\2\\<\3\2\2\2\\B\3\2\2\2"+
		"\\J\3\2\2\2\\O\3\2\2\2\\V\3\2\2\2\\Z\3\2\2\2]\17\3\2\2\2^_\7\34\2\2_`"+
		"\7!\2\2`a\7\5\2\2ab\5\24\13\2bc\7\4\2\2ck\3\2\2\2de\7!\2\2ef\7!\2\2fg"+
		"\7\5\2\2gh\5\24\13\2hi\7\4\2\2ik\3\2\2\2j^\3\2\2\2jd\3\2\2\2k\21\3\2\2"+
		"\2lm\7!\2\2mn\7\5\2\2no\5\24\13\2op\7\4\2\2p\23\3\2\2\2qr\b\13\1\2r\u0084"+
		"\7 \2\2s\u0084\7\36\2\2t\u0084\7\37\2\2u\u0084\7!\2\2vw\7!\2\2wx\7\24"+
		"\2\2xy\5\26\f\2yz\7\25\2\2z\u0084\3\2\2\2{|\7\24\2\2|}\5\24\13\2}~\7\25"+
		"\2\2~\u0084\3\2\2\2\177\u0080\7\24\2\2\u0080\u0081\7!\2\2\u0081\u0082"+
		"\7\25\2\2\u0082\u0084\5\24\13\3\u0083q\3\2\2\2\u0083s\3\2\2\2\u0083t\3"+
		"\2\2\2\u0083u\3\2\2\2\u0083v\3\2\2\2\u0083{\3\2\2\2\u0083\177\3\2\2\2"+
		"\u0084\u0093\3\2\2\2\u0085\u0086\f\7\2\2\u0086\u0087\t\2\2\2\u0087\u0092"+
		"\5\24\13\b\u0088\u0089\f\6\2\2\u0089\u008a\t\3\2\2\u008a\u0092\5\24\13"+
		"\7\u008b\u008c\f\5\2\2\u008c\u008d\t\4\2\2\u008d\u0092\5\24\13\6\u008e"+
		"\u008f\f\4\2\2\u008f\u0090\t\5\2\2\u0090\u0092\5\24\13\5\u0091\u0085\3"+
		"\2\2\2\u0091\u0088\3\2\2\2\u0091\u008b\3\2\2\2\u0091\u008e\3\2\2\2\u0092"+
		"\u0095\3\2\2\2\u0093\u0091\3\2\2\2\u0093\u0094\3\2\2\2\u0094\25\3\2\2"+
		"\2\u0095\u0093\3\2\2\2\u0096\u0098\5\24\13\2\u0097\u0096\3\2\2\2\u0097"+
		"\u0098\3\2\2\2\u0098\u00a1\3\2\2\2\u0099\u009c\5\24\13\2\u009a\u009b\7"+
		"\3\2\2\u009b\u009d\5\24\13\2\u009c\u009a\3\2\2\2\u009d\u009e\3\2\2\2\u009e"+
		"\u009c\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a1\3\2\2\2\u00a0\u0097\3\2"+
		"\2\2\u00a0\u0099\3\2\2\2\u00a1\27\3\2\2\2\22\35\",\63\65FLT\\j\u0083\u0091"+
		"\u0093\u0097\u009e\u00a0";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}