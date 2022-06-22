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
		End=1, Assign=2, Plus=3, Minus=4, Multiply=5, ParenOpen=6, ParenClose=7, 
		CurlyOpen=8, CurlyClose=9, Number=10, Identifier=11, Whitespace=12, NL=13, 
		LineComment=14, BlockComment=15;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"End", "Assign", "Plus", "Minus", "Multiply", "ParenOpen", "ParenClose", 
			"CurlyOpen", "CurlyClose", "DecimalNumber", "Number", "Identifier", "Whitespace", 
			"NL", "LineComment", "BlockComment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'='", "'+'", "'-'", "'*'", "'('", "')'", "'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "End", "Assign", "Plus", "Minus", "Multiply", "ParenOpen", "ParenClose", 
			"CurlyOpen", "CurlyClose", "Number", "Identifier", "Whitespace", "NL", 
			"LineComment", "BlockComment"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\21p\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\3\2\3\2\3"+
		"\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\6"+
		"\13\67\n\13\r\13\16\138\3\f\3\f\3\r\3\r\7\r?\n\r\f\r\16\rB\13\r\3\16\6"+
		"\16E\n\16\r\16\16\16F\3\16\3\16\3\17\3\17\5\17M\n\17\3\17\5\17P\n\17\3"+
		"\20\3\20\3\20\3\20\7\20V\n\20\f\20\16\20Y\13\20\3\20\5\20\\\n\20\3\20"+
		"\3\20\3\21\3\21\3\21\3\21\7\21d\n\21\f\21\16\21g\13\21\3\21\3\21\3\21"+
		"\3\21\5\21m\n\21\3\21\3\21\3e\2\22\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n"+
		"\23\13\25\2\27\f\31\r\33\16\35\17\37\20!\21\3\2\7\3\2\62;\4\2C\\c|\6\2"+
		"\62;C\\aac|\4\2\13\13\"\"\4\2\f\f\17\17\2w\2\3\3\2\2\2\2\5\3\2\2\2\2\7"+
		"\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2"+
		"\2\2\23\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2"+
		"\37\3\2\2\2\2!\3\2\2\2\3#\3\2\2\2\5%\3\2\2\2\7\'\3\2\2\2\t)\3\2\2\2\13"+
		"+\3\2\2\2\r-\3\2\2\2\17/\3\2\2\2\21\61\3\2\2\2\23\63\3\2\2\2\25\66\3\2"+
		"\2\2\27:\3\2\2\2\31<\3\2\2\2\33D\3\2\2\2\35O\3\2\2\2\37Q\3\2\2\2!_\3\2"+
		"\2\2#$\7=\2\2$\4\3\2\2\2%&\7?\2\2&\6\3\2\2\2\'(\7-\2\2(\b\3\2\2\2)*\7"+
		"/\2\2*\n\3\2\2\2+,\7,\2\2,\f\3\2\2\2-.\7*\2\2.\16\3\2\2\2/\60\7+\2\2\60"+
		"\20\3\2\2\2\61\62\7}\2\2\62\22\3\2\2\2\63\64\7\177\2\2\64\24\3\2\2\2\65"+
		"\67\t\2\2\2\66\65\3\2\2\2\678\3\2\2\28\66\3\2\2\289\3\2\2\29\26\3\2\2"+
		"\2:;\5\25\13\2;\30\3\2\2\2<@\t\3\2\2=?\t\4\2\2>=\3\2\2\2?B\3\2\2\2@>\3"+
		"\2\2\2@A\3\2\2\2A\32\3\2\2\2B@\3\2\2\2CE\t\5\2\2DC\3\2\2\2EF\3\2\2\2F"+
		"D\3\2\2\2FG\3\2\2\2GH\3\2\2\2HI\b\16\2\2I\34\3\2\2\2JL\7\17\2\2KM\7\f"+
		"\2\2LK\3\2\2\2LM\3\2\2\2MP\3\2\2\2NP\7\f\2\2OJ\3\2\2\2ON\3\2\2\2P\36\3"+
		"\2\2\2QR\7\61\2\2RS\7\61\2\2SW\3\2\2\2TV\n\6\2\2UT\3\2\2\2VY\3\2\2\2W"+
		"U\3\2\2\2WX\3\2\2\2X[\3\2\2\2YW\3\2\2\2Z\\\5\35\17\2[Z\3\2\2\2[\\\3\2"+
		"\2\2\\]\3\2\2\2]^\b\20\2\2^ \3\2\2\2_`\7\61\2\2`a\7,\2\2ae\3\2\2\2bd\13"+
		"\2\2\2cb\3\2\2\2dg\3\2\2\2ef\3\2\2\2ec\3\2\2\2fh\3\2\2\2ge\3\2\2\2hi\7"+
		",\2\2ij\7\61\2\2jl\3\2\2\2km\5\35\17\2lk\3\2\2\2lm\3\2\2\2mn\3\2\2\2n"+
		"o\b\21\2\2o\"\3\2\2\2\f\28@FLOW[el\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}