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
		Comma=1, End=2, Plus=3, Minus=4, Multiply=5, Divide=6, Modulo=7, ShiftL=8, 
		ShiftR=9, Lt=10, Le=11, Eq=12, Ge=13, Gt=14, Ne=15, BitAnd=16, BitOr=17, 
		BitXor=18, Assign=19, AndAssign=20, OrAssign=21, XorAssign=22, PlusAssign=23, 
		MinusAssign=24, MultiplyAssign=25, DivideAssign=26, ModuloAssign=27, ShiftLAssign=28, 
		ShiftRAssign=29, ParenOpen=30, ParenClose=31, CurlyOpen=32, CurlyClose=33, 
		Break=34, Else=35, If=36, Return=37, Var=38, While=39, CharLiteral=40, 
		BooleanLiteral=41, Number=42, Identifier=43, Whitespace=44, NL=45, LineComment=46, 
		BlockComment=47;
	public static final int
		RULE_root = 0, RULE_declarations = 1, RULE_declaration = 2, RULE_functionDeclaration = 3, 
		RULE_parameterDeclarations = 4, RULE_parameterDeclaration = 5, RULE_statement = 6, 
		RULE_varDeclaration = 7, RULE_subexpr = 8, RULE_expression = 9, RULE_functionCallParameters = 10;
	private static String[] makeRuleNames() {
		return new String[] {
			"root", "declarations", "declaration", "functionDeclaration", "parameterDeclarations", 
			"parameterDeclaration", "statement", "varDeclaration", "subexpr", "expression", 
			"functionCallParameters"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "','", "';'", "'+'", "'-'", "'*'", "'/'", "'%'", "'<<'", "'>>'", 
			"'<'", "'<='", "'=='", "'>='", "'>'", "'!='", "'&'", "'|'", "'^'", "'='", 
			"'&='", "'|='", "'^='", "'+='", "'-='", "'*='", "'/='", "'%='", "'<<='", 
			"'>>='", "'('", "')'", "'{'", "'}'", "'break'", "'else'", "'if'", "'return'", 
			"'var'", "'while'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Comma", "End", "Plus", "Minus", "Multiply", "Divide", "Modulo", 
			"ShiftL", "ShiftR", "Lt", "Le", "Eq", "Ge", "Gt", "Ne", "BitAnd", "BitOr", 
			"BitXor", "Assign", "AndAssign", "OrAssign", "XorAssign", "PlusAssign", 
			"MinusAssign", "MultiplyAssign", "DivideAssign", "ModuloAssign", "ShiftLAssign", 
			"ShiftRAssign", "ParenOpen", "ParenClose", "CurlyOpen", "CurlyClose", 
			"Break", "Else", "If", "Return", "Var", "While", "CharLiteral", "BooleanLiteral", 
			"Number", "Identifier", "Whitespace", "NL", "LineComment", "BlockComment"
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
			while (_la==Identifier) {
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
			_localctx = new FuncDeclarationContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(30);
			functionDeclaration();
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
			setState(32);
			((FunctionDeclarationContext)_localctx).type = match(Identifier);
			setState(33);
			((FunctionDeclarationContext)_localctx).name = match(Identifier);
			setState(34);
			match(ParenOpen);
			setState(35);
			parameterDeclarations();
			setState(36);
			match(ParenClose);
			setState(37);
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
			setState(49);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(40);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Identifier) {
					{
					setState(39);
					parameterDeclaration();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(42);
				parameterDeclaration();
				setState(45); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(43);
					match(Comma);
					setState(44);
					parameterDeclaration();
					}
					}
					setState(47); 
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
			setState(51);
			((ParameterDeclarationContext)_localctx).type = match(Identifier);
			setState(52);
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
		public Token var;
		public Token operator;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode End() { return getToken(BLikeLangParser.End, 0); }
		public TerminalNode Identifier() { return getToken(BLikeLangParser.Identifier, 0); }
		public TerminalNode Assign() { return getToken(BLikeLangParser.Assign, 0); }
		public TerminalNode AndAssign() { return getToken(BLikeLangParser.AndAssign, 0); }
		public TerminalNode OrAssign() { return getToken(BLikeLangParser.OrAssign, 0); }
		public TerminalNode XorAssign() { return getToken(BLikeLangParser.XorAssign, 0); }
		public TerminalNode PlusAssign() { return getToken(BLikeLangParser.PlusAssign, 0); }
		public TerminalNode MinusAssign() { return getToken(BLikeLangParser.MinusAssign, 0); }
		public TerminalNode MultiplyAssign() { return getToken(BLikeLangParser.MultiplyAssign, 0); }
		public TerminalNode DivideAssign() { return getToken(BLikeLangParser.DivideAssign, 0); }
		public TerminalNode ModuloAssign() { return getToken(BLikeLangParser.ModuloAssign, 0); }
		public TerminalNode ShiftLAssign() { return getToken(BLikeLangParser.ShiftLAssign, 0); }
		public TerminalNode ShiftRAssign() { return getToken(BLikeLangParser.ShiftRAssign, 0); }
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
			setState(92);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				_localctx = new LocalVarDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(54);
				varDeclaration();
				}
				break;
			case 2:
				_localctx = new AssignStatementContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(55);
				((AssignStatementContext)_localctx).var = match(Identifier);
				setState(56);
				((AssignStatementContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Assign) | (1L << AndAssign) | (1L << OrAssign) | (1L << XorAssign) | (1L << PlusAssign) | (1L << MinusAssign) | (1L << MultiplyAssign) | (1L << DivideAssign) | (1L << ModuloAssign) | (1L << ShiftLAssign) | (1L << ShiftRAssign))) != 0)) ) {
					((AssignStatementContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(57);
				expression(0);
				setState(58);
				match(End);
				}
				break;
			case 3:
				_localctx = new CallStatementContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(60);
				((CallStatementContext)_localctx).func = match(Identifier);
				setState(61);
				match(ParenOpen);
				setState(62);
				functionCallParameters();
				setState(63);
				match(ParenClose);
				setState(64);
				match(End);
				}
				break;
			case 4:
				_localctx = new BlockStatementContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(66);
				match(CurlyOpen);
				setState(70);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CurlyOpen) | (1L << Break) | (1L << If) | (1L << Return) | (1L << Var) | (1L << While) | (1L << Identifier))) != 0)) {
					{
					{
					setState(67);
					statement();
					}
					}
					setState(72);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(73);
				match(CurlyClose);
				}
				break;
			case 5:
				_localctx = new ReturnStatementContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(74);
				match(Return);
				setState(76);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ParenOpen) | (1L << CharLiteral) | (1L << BooleanLiteral) | (1L << Number) | (1L << Identifier))) != 0)) {
					{
					setState(75);
					expression(0);
					}
				}

				setState(78);
				match(End);
				}
				break;
			case 6:
				_localctx = new IfStatementContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(79);
				match(If);
				setState(80);
				expression(0);
				setState(81);
				((IfStatementContext)_localctx).trueStatement = statement();
				setState(84);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
				case 1:
					{
					setState(82);
					match(Else);
					setState(83);
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
				setState(86);
				match(While);
				setState(87);
				expression(0);
				setState(88);
				statement();
				}
				break;
			case 8:
				_localctx = new BreakStatementContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(90);
				match(Break);
				setState(91);
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
			setState(106);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Var:
				_localctx = new InferVarDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(94);
				match(Var);
				setState(95);
				((InferVarDeclarationContext)_localctx).var = match(Identifier);
				setState(96);
				match(Assign);
				setState(97);
				expression(0);
				setState(98);
				match(End);
				}
				break;
			case Identifier:
				_localctx = new TypeVarDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(100);
				((TypeVarDeclarationContext)_localctx).type = match(Identifier);
				setState(101);
				((TypeVarDeclarationContext)_localctx).var = match(Identifier);
				setState(102);
				match(Assign);
				setState(103);
				expression(0);
				setState(104);
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

	public static class SubexprContext extends ParserRuleContext {
		public SubexprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subexpr; }
	 
		public SubexprContext() { }
		public void copyFrom(SubexprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ReadVariableContext extends SubexprContext {
		public TerminalNode Identifier() { return getToken(BLikeLangParser.Identifier, 0); }
		public ReadVariableContext(SubexprContext ctx) { copyFrom(ctx); }
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
	public static class FunctionCallContext extends SubexprContext {
		public Token func;
		public TerminalNode ParenOpen() { return getToken(BLikeLangParser.ParenOpen, 0); }
		public FunctionCallParametersContext functionCallParameters() {
			return getRuleContext(FunctionCallParametersContext.class,0);
		}
		public TerminalNode ParenClose() { return getToken(BLikeLangParser.ParenClose, 0); }
		public TerminalNode Identifier() { return getToken(BLikeLangParser.Identifier, 0); }
		public FunctionCallContext(SubexprContext ctx) { copyFrom(ctx); }
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
	public static class CharLiteralContext extends SubexprContext {
		public TerminalNode CharLiteral() { return getToken(BLikeLangParser.CharLiteral, 0); }
		public CharLiteralContext(SubexprContext ctx) { copyFrom(ctx); }
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
	public static class BooleanLiteralContext extends SubexprContext {
		public TerminalNode BooleanLiteral() { return getToken(BLikeLangParser.BooleanLiteral, 0); }
		public BooleanLiteralContext(SubexprContext ctx) { copyFrom(ctx); }
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
	public static class ExpressionInParenthesisContext extends SubexprContext {
		public TerminalNode ParenOpen() { return getToken(BLikeLangParser.ParenOpen, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode ParenClose() { return getToken(BLikeLangParser.ParenClose, 0); }
		public ExpressionInParenthesisContext(SubexprContext ctx) { copyFrom(ctx); }
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
	public static class NumberLiteralContext extends SubexprContext {
		public TerminalNode Number() { return getToken(BLikeLangParser.Number, 0); }
		public NumberLiteralContext(SubexprContext ctx) { copyFrom(ctx); }
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

	public final SubexprContext subexpr() throws RecognitionException {
		SubexprContext _localctx = new SubexprContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_subexpr);
		try {
			setState(121);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				_localctx = new NumberLiteralContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(108);
				match(Number);
				}
				break;
			case 2:
				_localctx = new CharLiteralContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(109);
				match(CharLiteral);
				}
				break;
			case 3:
				_localctx = new BooleanLiteralContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(110);
				match(BooleanLiteral);
				}
				break;
			case 4:
				_localctx = new ReadVariableContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(111);
				match(Identifier);
				}
				break;
			case 5:
				_localctx = new FunctionCallContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(112);
				((FunctionCallContext)_localctx).func = match(Identifier);
				setState(113);
				match(ParenOpen);
				setState(114);
				functionCallParameters();
				setState(115);
				match(ParenClose);
				}
				break;
			case 6:
				_localctx = new ExpressionInParenthesisContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(117);
				match(ParenOpen);
				setState(118);
				expression(0);
				setState(119);
				match(ParenClose);
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
		public TerminalNode Divide() { return getToken(BLikeLangParser.Divide, 0); }
		public TerminalNode Modulo() { return getToken(BLikeLangParser.Modulo, 0); }
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
	public static class SubExpressionContext extends ExpressionContext {
		public SubexprContext subexpr() {
			return getRuleContext(SubexprContext.class,0);
		}
		public SubExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).enterSubExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BLikeLangListener ) ((BLikeLangListener)listener).exitSubExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BLikeLangVisitor ) return ((BLikeLangVisitor<? extends T>)visitor).visitSubExpression(this);
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
			{
			_localctx = new SubExpressionContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(124);
			subexpr();
			}
			_ctx.stop = _input.LT(-1);
			setState(140);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(138);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
					case 1:
						{
						_localctx = new BinaryExpressionBitsContext(new ExpressionContext(_parentctx, _parentState));
						((BinaryExpressionBitsContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(126);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(127);
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
						setState(128);
						((BinaryExpressionBitsContext)_localctx).right = expression(5);
						}
						break;
					case 2:
						{
						_localctx = new BinaryExpressionPointContext(new ExpressionContext(_parentctx, _parentState));
						((BinaryExpressionPointContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(129);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(130);
						((BinaryExpressionPointContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Multiply) | (1L << Divide) | (1L << Modulo) | (1L << ShiftL) | (1L << ShiftR))) != 0)) ) {
							((BinaryExpressionPointContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(131);
						((BinaryExpressionPointContext)_localctx).right = expression(4);
						}
						break;
					case 3:
						{
						_localctx = new BinaryExpressionDashContext(new ExpressionContext(_parentctx, _parentState));
						((BinaryExpressionDashContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(132);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(133);
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
						setState(134);
						((BinaryExpressionDashContext)_localctx).right = expression(3);
						}
						break;
					case 4:
						{
						_localctx = new BinaryExpressionBoolContext(new ExpressionContext(_parentctx, _parentState));
						((BinaryExpressionBoolContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(135);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(136);
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
						setState(137);
						((BinaryExpressionBoolContext)_localctx).right = expression(2);
						}
						break;
					}
					} 
				}
				setState(142);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
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
			setState(153);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(144);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ParenOpen) | (1L << CharLiteral) | (1L << BooleanLiteral) | (1L << Number) | (1L << Identifier))) != 0)) {
					{
					setState(143);
					expression(0);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(146);
				expression(0);
				setState(149); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(147);
					match(Comma);
					setState(148);
					expression(0);
					}
					}
					setState(151); 
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
			return precpred(_ctx, 4);
		case 1:
			return precpred(_ctx, 3);
		case 2:
			return precpred(_ctx, 2);
		case 3:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\61\u009e\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\3\2\3\2\3\3\7\3\34\n\3\f\3\16\3\37\13\3\3\4\3\4\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\6\5\6+\n\6\3\6\3\6\3\6\6\6\60\n\6\r\6\16\6\61\5"+
		"\6\64\n\6\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\7\bG\n\b\f\b\16\bJ\13\b\3\b\3\b\3\b\5\bO\n\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\5\bW\n\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b_\n\b\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\5\tm\n\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\5\n|\n\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\7\13\u008d\n\13\f\13\16\13\u0090\13"+
		"\13\3\f\5\f\u0093\n\f\3\f\3\f\3\f\6\f\u0098\n\f\r\f\16\f\u0099\5\f\u009c"+
		"\n\f\3\f\2\3\24\r\2\4\6\b\n\f\16\20\22\24\26\2\7\3\2\25\37\3\2\22\24\3"+
		"\2\7\13\3\2\5\6\3\2\f\21\2\u00ad\2\30\3\2\2\2\4\35\3\2\2\2\6 \3\2\2\2"+
		"\b\"\3\2\2\2\n\63\3\2\2\2\f\65\3\2\2\2\16^\3\2\2\2\20l\3\2\2\2\22{\3\2"+
		"\2\2\24}\3\2\2\2\26\u009b\3\2\2\2\30\31\5\4\3\2\31\3\3\2\2\2\32\34\5\6"+
		"\4\2\33\32\3\2\2\2\34\37\3\2\2\2\35\33\3\2\2\2\35\36\3\2\2\2\36\5\3\2"+
		"\2\2\37\35\3\2\2\2 !\5\b\5\2!\7\3\2\2\2\"#\7-\2\2#$\7-\2\2$%\7 \2\2%&"+
		"\5\n\6\2&\'\7!\2\2\'(\5\16\b\2(\t\3\2\2\2)+\5\f\7\2*)\3\2\2\2*+\3\2\2"+
		"\2+\64\3\2\2\2,/\5\f\7\2-.\7\3\2\2.\60\5\f\7\2/-\3\2\2\2\60\61\3\2\2\2"+
		"\61/\3\2\2\2\61\62\3\2\2\2\62\64\3\2\2\2\63*\3\2\2\2\63,\3\2\2\2\64\13"+
		"\3\2\2\2\65\66\7-\2\2\66\67\7-\2\2\67\r\3\2\2\28_\5\20\t\29:\7-\2\2:;"+
		"\t\2\2\2;<\5\24\13\2<=\7\4\2\2=_\3\2\2\2>?\7-\2\2?@\7 \2\2@A\5\26\f\2"+
		"AB\7!\2\2BC\7\4\2\2C_\3\2\2\2DH\7\"\2\2EG\5\16\b\2FE\3\2\2\2GJ\3\2\2\2"+
		"HF\3\2\2\2HI\3\2\2\2IK\3\2\2\2JH\3\2\2\2K_\7#\2\2LN\7\'\2\2MO\5\24\13"+
		"\2NM\3\2\2\2NO\3\2\2\2OP\3\2\2\2P_\7\4\2\2QR\7&\2\2RS\5\24\13\2SV\5\16"+
		"\b\2TU\7%\2\2UW\5\16\b\2VT\3\2\2\2VW\3\2\2\2W_\3\2\2\2XY\7)\2\2YZ\5\24"+
		"\13\2Z[\5\16\b\2[_\3\2\2\2\\]\7$\2\2]_\7\4\2\2^8\3\2\2\2^9\3\2\2\2^>\3"+
		"\2\2\2^D\3\2\2\2^L\3\2\2\2^Q\3\2\2\2^X\3\2\2\2^\\\3\2\2\2_\17\3\2\2\2"+
		"`a\7(\2\2ab\7-\2\2bc\7\25\2\2cd\5\24\13\2de\7\4\2\2em\3\2\2\2fg\7-\2\2"+
		"gh\7-\2\2hi\7\25\2\2ij\5\24\13\2jk\7\4\2\2km\3\2\2\2l`\3\2\2\2lf\3\2\2"+
		"\2m\21\3\2\2\2n|\7,\2\2o|\7*\2\2p|\7+\2\2q|\7-\2\2rs\7-\2\2st\7 \2\2t"+
		"u\5\26\f\2uv\7!\2\2v|\3\2\2\2wx\7 \2\2xy\5\24\13\2yz\7!\2\2z|\3\2\2\2"+
		"{n\3\2\2\2{o\3\2\2\2{p\3\2\2\2{q\3\2\2\2{r\3\2\2\2{w\3\2\2\2|\23\3\2\2"+
		"\2}~\b\13\1\2~\177\5\22\n\2\177\u008e\3\2\2\2\u0080\u0081\f\6\2\2\u0081"+
		"\u0082\t\3\2\2\u0082\u008d\5\24\13\7\u0083\u0084\f\5\2\2\u0084\u0085\t"+
		"\4\2\2\u0085\u008d\5\24\13\6\u0086\u0087\f\4\2\2\u0087\u0088\t\5\2\2\u0088"+
		"\u008d\5\24\13\5\u0089\u008a\f\3\2\2\u008a\u008b\t\6\2\2\u008b\u008d\5"+
		"\24\13\4\u008c\u0080\3\2\2\2\u008c\u0083\3\2\2\2\u008c\u0086\3\2\2\2\u008c"+
		"\u0089\3\2\2\2\u008d\u0090\3\2\2\2\u008e\u008c\3\2\2\2\u008e\u008f\3\2"+
		"\2\2\u008f\25\3\2\2\2\u0090\u008e\3\2\2\2\u0091\u0093\5\24\13\2\u0092"+
		"\u0091\3\2\2\2\u0092\u0093\3\2\2\2\u0093\u009c\3\2\2\2\u0094\u0097\5\24"+
		"\13\2\u0095\u0096\7\3\2\2\u0096\u0098\5\24\13\2\u0097\u0095\3\2\2\2\u0098"+
		"\u0099\3\2\2\2\u0099\u0097\3\2\2\2\u0099\u009a\3\2\2\2\u009a\u009c\3\2"+
		"\2\2\u009b\u0092\3\2\2\2\u009b\u0094\3\2\2\2\u009c\27\3\2\2\2\21\35*\61"+
		"\63HNV^l{\u008c\u008e\u0092\u0099\u009b";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}