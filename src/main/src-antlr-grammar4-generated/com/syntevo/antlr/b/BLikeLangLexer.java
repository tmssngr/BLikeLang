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
		Assign=1, Plus=2, EOL=3, Number=4, Identifier=5, Whitespace=6, NL=7, LineComment=8, 
		BlockComment=9;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"Assign", "Plus", "EOL", "DecimalNumber", "Number", "Identifier", "Whitespace", 
			"NL", "LineComment", "BlockComment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'='", "'+'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Assign", "Plus", "EOL", "Number", "Identifier", "Whitespace", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\13c\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\3\2\3\2\3\3\3\3\3\4\3\4\5\4\36\n\4\3\5\6\5!\n\5\r\5\16\5\"\3\6\3\6"+
		"\3\7\3\7\7\7)\n\7\f\7\16\7,\13\7\3\b\6\b/\n\b\r\b\16\b\60\3\b\3\b\3\t"+
		"\3\t\5\t\67\n\t\3\t\5\t:\n\t\3\n\3\n\3\n\3\n\7\n@\n\n\f\n\16\nC\13\n\3"+
		"\n\3\n\7\nG\n\n\f\n\16\nJ\13\n\5\nL\n\n\3\n\5\nO\n\n\3\n\3\n\3\13\3\13"+
		"\3\13\3\13\7\13W\n\13\f\13\16\13Z\13\13\3\13\3\13\3\13\3\13\5\13`\n\13"+
		"\3\13\3\13\3X\2\f\3\3\5\4\7\5\t\2\13\6\r\7\17\b\21\t\23\n\25\13\3\2\7"+
		"\3\2\62;\4\2C\\c|\6\2\62;C\\aac|\4\2\13\13\"\"\4\2\f\f\17\17\2m\2\3\3"+
		"\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2"+
		"\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\3\27\3\2\2\2\5\31\3\2\2\2\7\35\3"+
		"\2\2\2\t \3\2\2\2\13$\3\2\2\2\r&\3\2\2\2\17.\3\2\2\2\219\3\2\2\2\23K\3"+
		"\2\2\2\25R\3\2\2\2\27\30\7?\2\2\30\4\3\2\2\2\31\32\7-\2\2\32\6\3\2\2\2"+
		"\33\36\5\21\t\2\34\36\7\2\2\3\35\33\3\2\2\2\35\34\3\2\2\2\36\b\3\2\2\2"+
		"\37!\t\2\2\2 \37\3\2\2\2!\"\3\2\2\2\" \3\2\2\2\"#\3\2\2\2#\n\3\2\2\2$"+
		"%\5\t\5\2%\f\3\2\2\2&*\t\3\2\2\')\t\4\2\2(\'\3\2\2\2),\3\2\2\2*(\3\2\2"+
		"\2*+\3\2\2\2+\16\3\2\2\2,*\3\2\2\2-/\t\5\2\2.-\3\2\2\2/\60\3\2\2\2\60"+
		".\3\2\2\2\60\61\3\2\2\2\61\62\3\2\2\2\62\63\b\b\2\2\63\20\3\2\2\2\64\66"+
		"\7\17\2\2\65\67\7\f\2\2\66\65\3\2\2\2\66\67\3\2\2\2\67:\3\2\2\28:\7\f"+
		"\2\29\64\3\2\2\298\3\2\2\2:\22\3\2\2\2;<\7\61\2\2<=\7\61\2\2=A\3\2\2\2"+
		">@\n\6\2\2?>\3\2\2\2@C\3\2\2\2A?\3\2\2\2AB\3\2\2\2BL\3\2\2\2CA\3\2\2\2"+
		"DH\7=\2\2EG\n\6\2\2FE\3\2\2\2GJ\3\2\2\2HF\3\2\2\2HI\3\2\2\2IL\3\2\2\2"+
		"JH\3\2\2\2K;\3\2\2\2KD\3\2\2\2LN\3\2\2\2MO\5\21\t\2NM\3\2\2\2NO\3\2\2"+
		"\2OP\3\2\2\2PQ\b\n\2\2Q\24\3\2\2\2RS\7\61\2\2ST\7,\2\2TX\3\2\2\2UW\13"+
		"\2\2\2VU\3\2\2\2WZ\3\2\2\2XY\3\2\2\2XV\3\2\2\2Y[\3\2\2\2ZX\3\2\2\2[\\"+
		"\7,\2\2\\]\7\61\2\2]_\3\2\2\2^`\5\21\t\2_^\3\2\2\2_`\3\2\2\2`a\3\2\2\2"+
		"ab\b\13\2\2b\26\3\2\2\2\17\2\35\"*\60\669AHKNX_\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}