// Generated from grammar/BLikeLang.g4 by ANTLR 4.7.2
package com.syntevo.antlr.b;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class BLikeLangLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		Comma=1, End=2, Assign=3, Plus=4, Minus=5, Multiply=6, ParenOpen=7, ParenClose=8, 
		CurlyOpen=9, CurlyClose=10, Return=11, Var=12, Number=13, Identifier=14, 
		Whitespace=15, NL=16, LineComment=17, BlockComment=18;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"Comma", "End", "Assign", "Plus", "Minus", "Multiply", "ParenOpen", "ParenClose", 
			"CurlyOpen", "CurlyClose", "Return", "Var", "DecimalNumber", "Number", 
			"Identifier", "Whitespace", "NL", "LineComment", "BlockComment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "','", "';'", "'='", "'+'", "'-'", "'*'", "'('", "')'", "'{'", 
			"'}'", "'return'", "'var'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Comma", "End", "Assign", "Plus", "Minus", "Multiply", "ParenOpen", 
			"ParenClose", "CurlyOpen", "CurlyClose", "Return", "Var", "Number", "Identifier", 
			"Whitespace", "NL", "LineComment", "BlockComment"
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


	public BLikeLangLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "BLikeLang.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\24\u008e\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7"+
		"\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r"+
		"\3\r\3\r\3\r\3\16\6\16J\n\16\r\16\16\16K\3\17\5\17O\n\17\3\17\3\17\3\17"+
		"\3\17\3\17\3\17\5\17W\n\17\5\17Y\n\17\3\20\3\20\7\20]\n\20\f\20\16\20"+
		"`\13\20\3\21\6\21c\n\21\r\21\16\21d\3\21\3\21\3\22\3\22\5\22k\n\22\3\22"+
		"\5\22n\n\22\3\23\3\23\3\23\3\23\7\23t\n\23\f\23\16\23w\13\23\3\23\5\23"+
		"z\n\23\3\23\3\23\3\24\3\24\3\24\3\24\7\24\u0082\n\24\f\24\16\24\u0085"+
		"\13\24\3\24\3\24\3\24\3\24\5\24\u008b\n\24\3\24\3\24\3\u0083\2\25\3\3"+
		"\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\2\35\17\37\20"+
		"!\21#\22%\23\'\24\3\2\t\3\2\62;\3\2//\4\2kkww\4\2C\\c|\6\2\62;C\\aac|"+
		"\4\2\13\13\"\"\4\2\f\f\17\17\2\u0098\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2"+
		"\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23"+
		"\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\35\3\2\2\2\2\37\3\2"+
		"\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\3)\3\2\2\2\5+\3\2\2"+
		"\2\7-\3\2\2\2\t/\3\2\2\2\13\61\3\2\2\2\r\63\3\2\2\2\17\65\3\2\2\2\21\67"+
		"\3\2\2\2\239\3\2\2\2\25;\3\2\2\2\27=\3\2\2\2\31D\3\2\2\2\33I\3\2\2\2\35"+
		"N\3\2\2\2\37Z\3\2\2\2!b\3\2\2\2#m\3\2\2\2%o\3\2\2\2\'}\3\2\2\2)*\7.\2"+
		"\2*\4\3\2\2\2+,\7=\2\2,\6\3\2\2\2-.\7?\2\2.\b\3\2\2\2/\60\7-\2\2\60\n"+
		"\3\2\2\2\61\62\7/\2\2\62\f\3\2\2\2\63\64\7,\2\2\64\16\3\2\2\2\65\66\7"+
		"*\2\2\66\20\3\2\2\2\678\7+\2\28\22\3\2\2\29:\7}\2\2:\24\3\2\2\2;<\7\177"+
		"\2\2<\26\3\2\2\2=>\7t\2\2>?\7g\2\2?@\7v\2\2@A\7w\2\2AB\7t\2\2BC\7p\2\2"+
		"C\30\3\2\2\2DE\7x\2\2EF\7c\2\2FG\7t\2\2G\32\3\2\2\2HJ\t\2\2\2IH\3\2\2"+
		"\2JK\3\2\2\2KI\3\2\2\2KL\3\2\2\2L\34\3\2\2\2MO\t\3\2\2NM\3\2\2\2NO\3\2"+
		"\2\2OP\3\2\2\2PX\5\33\16\2QR\7a\2\2RV\t\4\2\2SW\7:\2\2TU\7\63\2\2UW\7"+
		"8\2\2VS\3\2\2\2VT\3\2\2\2WY\3\2\2\2XQ\3\2\2\2XY\3\2\2\2Y\36\3\2\2\2Z^"+
		"\t\5\2\2[]\t\6\2\2\\[\3\2\2\2]`\3\2\2\2^\\\3\2\2\2^_\3\2\2\2_ \3\2\2\2"+
		"`^\3\2\2\2ac\t\7\2\2ba\3\2\2\2cd\3\2\2\2db\3\2\2\2de\3\2\2\2ef\3\2\2\2"+
		"fg\b\21\2\2g\"\3\2\2\2hj\7\17\2\2ik\7\f\2\2ji\3\2\2\2jk\3\2\2\2kn\3\2"+
		"\2\2ln\7\f\2\2mh\3\2\2\2ml\3\2\2\2n$\3\2\2\2op\7\61\2\2pq\7\61\2\2qu\3"+
		"\2\2\2rt\n\b\2\2sr\3\2\2\2tw\3\2\2\2us\3\2\2\2uv\3\2\2\2vy\3\2\2\2wu\3"+
		"\2\2\2xz\5#\22\2yx\3\2\2\2yz\3\2\2\2z{\3\2\2\2{|\b\23\2\2|&\3\2\2\2}~"+
		"\7\61\2\2~\177\7,\2\2\177\u0083\3\2\2\2\u0080\u0082\13\2\2\2\u0081\u0080"+
		"\3\2\2\2\u0082\u0085\3\2\2\2\u0083\u0084\3\2\2\2\u0083\u0081\3\2\2\2\u0084"+
		"\u0086\3\2\2\2\u0085\u0083\3\2\2\2\u0086\u0087\7,\2\2\u0087\u0088\7\61"+
		"\2\2\u0088\u008a\3\2\2\2\u0089\u008b\5#\22\2\u008a\u0089\3\2\2\2\u008a"+
		"\u008b\3\2\2\2\u008b\u008c\3\2\2\2\u008c\u008d\b\24\2\2\u008d(\3\2\2\2"+
		"\17\2KNVX^djmuy\u0083\u008a\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}