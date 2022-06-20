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
		Assign=1, Plus=2, Minus=3, Multiply=4, EOL=5, Number=6, Identifier=7, 
		Whitespace=8, NL=9, LineComment=10, BlockComment=11;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"Assign", "Plus", "Minus", "Multiply", "EOL", "DecimalNumber", "Number", 
			"Identifier", "Whitespace", "NL", "LineComment", "BlockComment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'='", "'+'", "'-'", "'*'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Assign", "Plus", "Minus", "Multiply", "EOL", "Number", "Identifier", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\rk\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\5\6&\n\6\3"+
		"\7\6\7)\n\7\r\7\16\7*\3\b\3\b\3\t\3\t\7\t\61\n\t\f\t\16\t\64\13\t\3\n"+
		"\6\n\67\n\n\r\n\16\n8\3\n\3\n\3\13\3\13\5\13?\n\13\3\13\5\13B\n\13\3\f"+
		"\3\f\3\f\3\f\7\fH\n\f\f\f\16\fK\13\f\3\f\3\f\7\fO\n\f\f\f\16\fR\13\f\5"+
		"\fT\n\f\3\f\5\fW\n\f\3\f\3\f\3\r\3\r\3\r\3\r\7\r_\n\r\f\r\16\rb\13\r\3"+
		"\r\3\r\3\r\3\r\5\rh\n\r\3\r\3\r\3`\2\16\3\3\5\4\7\5\t\6\13\7\r\2\17\b"+
		"\21\t\23\n\25\13\27\f\31\r\3\2\7\3\2\62;\4\2C\\c|\6\2\62;C\\aac|\4\2\13"+
		"\13\"\"\4\2\f\f\17\17\2u\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2"+
		"\2\2\13\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2"+
		"\27\3\2\2\2\2\31\3\2\2\2\3\33\3\2\2\2\5\35\3\2\2\2\7\37\3\2\2\2\t!\3\2"+
		"\2\2\13%\3\2\2\2\r(\3\2\2\2\17,\3\2\2\2\21.\3\2\2\2\23\66\3\2\2\2\25A"+
		"\3\2\2\2\27S\3\2\2\2\31Z\3\2\2\2\33\34\7?\2\2\34\4\3\2\2\2\35\36\7-\2"+
		"\2\36\6\3\2\2\2\37 \7/\2\2 \b\3\2\2\2!\"\7,\2\2\"\n\3\2\2\2#&\5\25\13"+
		"\2$&\7\2\2\3%#\3\2\2\2%$\3\2\2\2&\f\3\2\2\2\')\t\2\2\2(\'\3\2\2\2)*\3"+
		"\2\2\2*(\3\2\2\2*+\3\2\2\2+\16\3\2\2\2,-\5\r\7\2-\20\3\2\2\2.\62\t\3\2"+
		"\2/\61\t\4\2\2\60/\3\2\2\2\61\64\3\2\2\2\62\60\3\2\2\2\62\63\3\2\2\2\63"+
		"\22\3\2\2\2\64\62\3\2\2\2\65\67\t\5\2\2\66\65\3\2\2\2\678\3\2\2\28\66"+
		"\3\2\2\289\3\2\2\29:\3\2\2\2:;\b\n\2\2;\24\3\2\2\2<>\7\17\2\2=?\7\f\2"+
		"\2>=\3\2\2\2>?\3\2\2\2?B\3\2\2\2@B\7\f\2\2A<\3\2\2\2A@\3\2\2\2B\26\3\2"+
		"\2\2CD\7\61\2\2DE\7\61\2\2EI\3\2\2\2FH\n\6\2\2GF\3\2\2\2HK\3\2\2\2IG\3"+
		"\2\2\2IJ\3\2\2\2JT\3\2\2\2KI\3\2\2\2LP\7=\2\2MO\n\6\2\2NM\3\2\2\2OR\3"+
		"\2\2\2PN\3\2\2\2PQ\3\2\2\2QT\3\2\2\2RP\3\2\2\2SC\3\2\2\2SL\3\2\2\2TV\3"+
		"\2\2\2UW\5\25\13\2VU\3\2\2\2VW\3\2\2\2WX\3\2\2\2XY\b\f\2\2Y\30\3\2\2\2"+
		"Z[\7\61\2\2[\\\7,\2\2\\`\3\2\2\2]_\13\2\2\2^]\3\2\2\2_b\3\2\2\2`a\3\2"+
		"\2\2`^\3\2\2\2ac\3\2\2\2b`\3\2\2\2cd\7,\2\2de\7\61\2\2eg\3\2\2\2fh\5\25"+
		"\13\2gf\3\2\2\2gh\3\2\2\2hi\3\2\2\2ij\b\r\2\2j\32\3\2\2\2\17\2%*\628>"+
		"AIPSV`g\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}