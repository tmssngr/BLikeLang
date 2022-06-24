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
		CurlyOpen=9, CurlyClose=10, Var=11, Number=12, Identifier=13, Whitespace=14, 
		NL=15, LineComment=16, BlockComment=17;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"Comma", "End", "Assign", "Plus", "Minus", "Multiply", "ParenOpen", "ParenClose", 
			"CurlyOpen", "CurlyClose", "Var", "DecimalNumber", "Number", "Identifier", 
			"Whitespace", "NL", "LineComment", "BlockComment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "','", "';'", "'='", "'+'", "'-'", "'*'", "'('", "')'", "'{'", 
			"'}'", "'var'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Comma", "End", "Assign", "Plus", "Minus", "Multiply", "ParenOpen", 
			"ParenClose", "CurlyOpen", "CurlyClose", "Var", "Number", "Identifier", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\23z\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t"+
		"\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3\f\3\r\6\rA\n\r\r\r\16\rB\3\16\3\16"+
		"\3\17\3\17\7\17I\n\17\f\17\16\17L\13\17\3\20\6\20O\n\20\r\20\16\20P\3"+
		"\20\3\20\3\21\3\21\5\21W\n\21\3\21\5\21Z\n\21\3\22\3\22\3\22\3\22\7\22"+
		"`\n\22\f\22\16\22c\13\22\3\22\5\22f\n\22\3\22\3\22\3\23\3\23\3\23\3\23"+
		"\7\23n\n\23\f\23\16\23q\13\23\3\23\3\23\3\23\3\23\5\23w\n\23\3\23\3\23"+
		"\3o\2\24\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\2\33\16"+
		"\35\17\37\20!\21#\22%\23\3\2\7\3\2\62;\4\2C\\c|\6\2\62;C\\aac|\4\2\13"+
		"\13\"\"\4\2\f\f\17\17\2\u0081\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t"+
		"\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2"+
		"\2\2\25\3\2\2\2\2\27\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2"+
		"!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\3\'\3\2\2\2\5)\3\2\2\2\7+\3\2\2\2\t-\3"+
		"\2\2\2\13/\3\2\2\2\r\61\3\2\2\2\17\63\3\2\2\2\21\65\3\2\2\2\23\67\3\2"+
		"\2\2\259\3\2\2\2\27;\3\2\2\2\31@\3\2\2\2\33D\3\2\2\2\35F\3\2\2\2\37N\3"+
		"\2\2\2!Y\3\2\2\2#[\3\2\2\2%i\3\2\2\2\'(\7.\2\2(\4\3\2\2\2)*\7=\2\2*\6"+
		"\3\2\2\2+,\7?\2\2,\b\3\2\2\2-.\7-\2\2.\n\3\2\2\2/\60\7/\2\2\60\f\3\2\2"+
		"\2\61\62\7,\2\2\62\16\3\2\2\2\63\64\7*\2\2\64\20\3\2\2\2\65\66\7+\2\2"+
		"\66\22\3\2\2\2\678\7}\2\28\24\3\2\2\29:\7\177\2\2:\26\3\2\2\2;<\7x\2\2"+
		"<=\7c\2\2=>\7t\2\2>\30\3\2\2\2?A\t\2\2\2@?\3\2\2\2AB\3\2\2\2B@\3\2\2\2"+
		"BC\3\2\2\2C\32\3\2\2\2DE\5\31\r\2E\34\3\2\2\2FJ\t\3\2\2GI\t\4\2\2HG\3"+
		"\2\2\2IL\3\2\2\2JH\3\2\2\2JK\3\2\2\2K\36\3\2\2\2LJ\3\2\2\2MO\t\5\2\2N"+
		"M\3\2\2\2OP\3\2\2\2PN\3\2\2\2PQ\3\2\2\2QR\3\2\2\2RS\b\20\2\2S \3\2\2\2"+
		"TV\7\17\2\2UW\7\f\2\2VU\3\2\2\2VW\3\2\2\2WZ\3\2\2\2XZ\7\f\2\2YT\3\2\2"+
		"\2YX\3\2\2\2Z\"\3\2\2\2[\\\7\61\2\2\\]\7\61\2\2]a\3\2\2\2^`\n\6\2\2_^"+
		"\3\2\2\2`c\3\2\2\2a_\3\2\2\2ab\3\2\2\2be\3\2\2\2ca\3\2\2\2df\5!\21\2e"+
		"d\3\2\2\2ef\3\2\2\2fg\3\2\2\2gh\b\22\2\2h$\3\2\2\2ij\7\61\2\2jk\7,\2\2"+
		"ko\3\2\2\2ln\13\2\2\2ml\3\2\2\2nq\3\2\2\2op\3\2\2\2om\3\2\2\2pr\3\2\2"+
		"\2qo\3\2\2\2rs\7,\2\2st\7\61\2\2tv\3\2\2\2uw\5!\21\2vu\3\2\2\2vw\3\2\2"+
		"\2wx\3\2\2\2xy\b\23\2\2y&\3\2\2\2\f\2BJPVYaeov\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}