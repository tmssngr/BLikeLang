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
		Number=8, Identifier=9, Whitespace=10, NL=11, LineComment=12, BlockComment=13;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"End", "Assign", "Plus", "Minus", "Multiply", "ParenOpen", "ParenClose", 
			"DecimalNumber", "Number", "Identifier", "Whitespace", "NL", "LineComment", 
			"BlockComment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'='", "'+'", "'-'", "'*'", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "End", "Assign", "Plus", "Minus", "Multiply", "ParenOpen", "ParenClose", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\17h\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3"+
		"\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\6\t/\n\t\r\t\16\t\60\3\n\3\n\3\13\3\13"+
		"\7\13\67\n\13\f\13\16\13:\13\13\3\f\6\f=\n\f\r\f\16\f>\3\f\3\f\3\r\3\r"+
		"\5\rE\n\r\3\r\5\rH\n\r\3\16\3\16\3\16\3\16\7\16N\n\16\f\16\16\16Q\13\16"+
		"\3\16\5\16T\n\16\3\16\3\16\3\17\3\17\3\17\3\17\7\17\\\n\17\f\17\16\17"+
		"_\13\17\3\17\3\17\3\17\3\17\5\17e\n\17\3\17\3\17\3]\2\20\3\3\5\4\7\5\t"+
		"\6\13\7\r\b\17\t\21\2\23\n\25\13\27\f\31\r\33\16\35\17\3\2\7\3\2\62;\4"+
		"\2C\\c|\6\2\62;C\\aac|\4\2\13\13\"\"\4\2\f\f\17\17\2o\2\3\3\2\2\2\2\5"+
		"\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2"+
		"\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35"+
		"\3\2\2\2\3\37\3\2\2\2\5!\3\2\2\2\7#\3\2\2\2\t%\3\2\2\2\13\'\3\2\2\2\r"+
		")\3\2\2\2\17+\3\2\2\2\21.\3\2\2\2\23\62\3\2\2\2\25\64\3\2\2\2\27<\3\2"+
		"\2\2\31G\3\2\2\2\33I\3\2\2\2\35W\3\2\2\2\37 \7=\2\2 \4\3\2\2\2!\"\7?\2"+
		"\2\"\6\3\2\2\2#$\7-\2\2$\b\3\2\2\2%&\7/\2\2&\n\3\2\2\2\'(\7,\2\2(\f\3"+
		"\2\2\2)*\7*\2\2*\16\3\2\2\2+,\7+\2\2,\20\3\2\2\2-/\t\2\2\2.-\3\2\2\2/"+
		"\60\3\2\2\2\60.\3\2\2\2\60\61\3\2\2\2\61\22\3\2\2\2\62\63\5\21\t\2\63"+
		"\24\3\2\2\2\648\t\3\2\2\65\67\t\4\2\2\66\65\3\2\2\2\67:\3\2\2\28\66\3"+
		"\2\2\289\3\2\2\29\26\3\2\2\2:8\3\2\2\2;=\t\5\2\2<;\3\2\2\2=>\3\2\2\2>"+
		"<\3\2\2\2>?\3\2\2\2?@\3\2\2\2@A\b\f\2\2A\30\3\2\2\2BD\7\17\2\2CE\7\f\2"+
		"\2DC\3\2\2\2DE\3\2\2\2EH\3\2\2\2FH\7\f\2\2GB\3\2\2\2GF\3\2\2\2H\32\3\2"+
		"\2\2IJ\7\61\2\2JK\7\61\2\2KO\3\2\2\2LN\n\6\2\2ML\3\2\2\2NQ\3\2\2\2OM\3"+
		"\2\2\2OP\3\2\2\2PS\3\2\2\2QO\3\2\2\2RT\5\31\r\2SR\3\2\2\2ST\3\2\2\2TU"+
		"\3\2\2\2UV\b\16\2\2V\34\3\2\2\2WX\7\61\2\2XY\7,\2\2Y]\3\2\2\2Z\\\13\2"+
		"\2\2[Z\3\2\2\2\\_\3\2\2\2]^\3\2\2\2][\3\2\2\2^`\3\2\2\2_]\3\2\2\2`a\7"+
		",\2\2ab\7\61\2\2bd\3\2\2\2ce\5\31\r\2dc\3\2\2\2de\3\2\2\2ef\3\2\2\2fg"+
		"\b\17\2\2g\36\3\2\2\2\f\2\608>DGOS]d\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}