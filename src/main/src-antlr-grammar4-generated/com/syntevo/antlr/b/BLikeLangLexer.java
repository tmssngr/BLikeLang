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
		CurlyOpen=9, CurlyClose=10, Number=11, Identifier=12, Whitespace=13, NL=14, 
		LineComment=15, BlockComment=16;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"Comma", "End", "Assign", "Plus", "Minus", "Multiply", "ParenOpen", "ParenClose", 
			"CurlyOpen", "CurlyClose", "DecimalNumber", "Number", "Identifier", "Whitespace", 
			"NL", "LineComment", "BlockComment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "','", "';'", "'='", "'+'", "'-'", "'*'", "'('", "')'", "'{'", 
			"'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Comma", "End", "Assign", "Plus", "Minus", "Multiply", "ParenOpen", 
			"ParenClose", "CurlyOpen", "CurlyClose", "Number", "Identifier", "Whitespace", 
			"NL", "LineComment", "BlockComment"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\22t\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3"+
		"\n\3\13\3\13\3\f\6\f;\n\f\r\f\16\f<\3\r\3\r\3\16\3\16\7\16C\n\16\f\16"+
		"\16\16F\13\16\3\17\6\17I\n\17\r\17\16\17J\3\17\3\17\3\20\3\20\5\20Q\n"+
		"\20\3\20\5\20T\n\20\3\21\3\21\3\21\3\21\7\21Z\n\21\f\21\16\21]\13\21\3"+
		"\21\5\21`\n\21\3\21\3\21\3\22\3\22\3\22\3\22\7\22h\n\22\f\22\16\22k\13"+
		"\22\3\22\3\22\3\22\3\22\5\22q\n\22\3\22\3\22\3i\2\23\3\3\5\4\7\5\t\6\13"+
		"\7\r\b\17\t\21\n\23\13\25\f\27\2\31\r\33\16\35\17\37\20!\21#\22\3\2\7"+
		"\3\2\62;\4\2C\\c|\6\2\62;C\\aac|\4\2\13\13\"\"\4\2\f\f\17\17\2{\2\3\3"+
		"\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2"+
		"\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\31\3\2\2\2\2\33\3"+
		"\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\3%\3\2\2\2\5\'"+
		"\3\2\2\2\7)\3\2\2\2\t+\3\2\2\2\13-\3\2\2\2\r/\3\2\2\2\17\61\3\2\2\2\21"+
		"\63\3\2\2\2\23\65\3\2\2\2\25\67\3\2\2\2\27:\3\2\2\2\31>\3\2\2\2\33@\3"+
		"\2\2\2\35H\3\2\2\2\37S\3\2\2\2!U\3\2\2\2#c\3\2\2\2%&\7.\2\2&\4\3\2\2\2"+
		"\'(\7=\2\2(\6\3\2\2\2)*\7?\2\2*\b\3\2\2\2+,\7-\2\2,\n\3\2\2\2-.\7/\2\2"+
		".\f\3\2\2\2/\60\7,\2\2\60\16\3\2\2\2\61\62\7*\2\2\62\20\3\2\2\2\63\64"+
		"\7+\2\2\64\22\3\2\2\2\65\66\7}\2\2\66\24\3\2\2\2\678\7\177\2\28\26\3\2"+
		"\2\29;\t\2\2\2:9\3\2\2\2;<\3\2\2\2<:\3\2\2\2<=\3\2\2\2=\30\3\2\2\2>?\5"+
		"\27\f\2?\32\3\2\2\2@D\t\3\2\2AC\t\4\2\2BA\3\2\2\2CF\3\2\2\2DB\3\2\2\2"+
		"DE\3\2\2\2E\34\3\2\2\2FD\3\2\2\2GI\t\5\2\2HG\3\2\2\2IJ\3\2\2\2JH\3\2\2"+
		"\2JK\3\2\2\2KL\3\2\2\2LM\b\17\2\2M\36\3\2\2\2NP\7\17\2\2OQ\7\f\2\2PO\3"+
		"\2\2\2PQ\3\2\2\2QT\3\2\2\2RT\7\f\2\2SN\3\2\2\2SR\3\2\2\2T \3\2\2\2UV\7"+
		"\61\2\2VW\7\61\2\2W[\3\2\2\2XZ\n\6\2\2YX\3\2\2\2Z]\3\2\2\2[Y\3\2\2\2["+
		"\\\3\2\2\2\\_\3\2\2\2][\3\2\2\2^`\5\37\20\2_^\3\2\2\2_`\3\2\2\2`a\3\2"+
		"\2\2ab\b\21\2\2b\"\3\2\2\2cd\7\61\2\2de\7,\2\2ei\3\2\2\2fh\13\2\2\2gf"+
		"\3\2\2\2hk\3\2\2\2ij\3\2\2\2ig\3\2\2\2jl\3\2\2\2ki\3\2\2\2lm\7,\2\2mn"+
		"\7\61\2\2np\3\2\2\2oq\5\37\20\2po\3\2\2\2pq\3\2\2\2qr\3\2\2\2rs\b\22\2"+
		"\2s$\3\2\2\2\f\2<DJPS[_ip\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}