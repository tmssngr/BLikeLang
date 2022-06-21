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
		Assign=1, Plus=2, Minus=3, Multiply=4, ParenOpen=5, ParenClose=6, EOL=7, 
		Number=8, Identifier=9, Whitespace=10, NL=11, LineComment=12, BlockComment=13;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"Assign", "Plus", "Minus", "Multiply", "ParenOpen", "ParenClose", "EOL", 
			"DecimalNumber", "Number", "Identifier", "Whitespace", "NL", "LineComment", 
			"BlockComment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'='", "'+'", "'-'", "'*'", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Assign", "Plus", "Minus", "Multiply", "ParenOpen", "ParenClose", 
			"EOL", "Number", "Identifier", "Whitespace", "NL", "LineComment", "BlockComment"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\17s\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3"+
		"\5\3\6\3\6\3\7\3\7\3\b\3\b\5\b.\n\b\3\t\6\t\61\n\t\r\t\16\t\62\3\n\3\n"+
		"\3\13\3\13\7\139\n\13\f\13\16\13<\13\13\3\f\6\f?\n\f\r\f\16\f@\3\f\3\f"+
		"\3\r\3\r\5\rG\n\r\3\r\5\rJ\n\r\3\16\3\16\3\16\3\16\7\16P\n\16\f\16\16"+
		"\16S\13\16\3\16\3\16\7\16W\n\16\f\16\16\16Z\13\16\5\16\\\n\16\3\16\5\16"+
		"_\n\16\3\16\3\16\3\17\3\17\3\17\3\17\7\17g\n\17\f\17\16\17j\13\17\3\17"+
		"\3\17\3\17\3\17\5\17p\n\17\3\17\3\17\3h\2\20\3\3\5\4\7\5\t\6\13\7\r\b"+
		"\17\t\21\2\23\n\25\13\27\f\31\r\33\16\35\17\3\2\7\3\2\62;\4\2C\\c|\6\2"+
		"\62;C\\aac|\4\2\13\13\"\"\4\2\f\f\17\17\2}\2\3\3\2\2\2\2\5\3\2\2\2\2\7"+
		"\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\23\3\2\2"+
		"\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\3"+
		"\37\3\2\2\2\5!\3\2\2\2\7#\3\2\2\2\t%\3\2\2\2\13\'\3\2\2\2\r)\3\2\2\2\17"+
		"-\3\2\2\2\21\60\3\2\2\2\23\64\3\2\2\2\25\66\3\2\2\2\27>\3\2\2\2\31I\3"+
		"\2\2\2\33[\3\2\2\2\35b\3\2\2\2\37 \7?\2\2 \4\3\2\2\2!\"\7-\2\2\"\6\3\2"+
		"\2\2#$\7/\2\2$\b\3\2\2\2%&\7,\2\2&\n\3\2\2\2\'(\7*\2\2(\f\3\2\2\2)*\7"+
		"+\2\2*\16\3\2\2\2+.\5\31\r\2,.\7\2\2\3-+\3\2\2\2-,\3\2\2\2.\20\3\2\2\2"+
		"/\61\t\2\2\2\60/\3\2\2\2\61\62\3\2\2\2\62\60\3\2\2\2\62\63\3\2\2\2\63"+
		"\22\3\2\2\2\64\65\5\21\t\2\65\24\3\2\2\2\66:\t\3\2\2\679\t\4\2\28\67\3"+
		"\2\2\29<\3\2\2\2:8\3\2\2\2:;\3\2\2\2;\26\3\2\2\2<:\3\2\2\2=?\t\5\2\2>"+
		"=\3\2\2\2?@\3\2\2\2@>\3\2\2\2@A\3\2\2\2AB\3\2\2\2BC\b\f\2\2C\30\3\2\2"+
		"\2DF\7\17\2\2EG\7\f\2\2FE\3\2\2\2FG\3\2\2\2GJ\3\2\2\2HJ\7\f\2\2ID\3\2"+
		"\2\2IH\3\2\2\2J\32\3\2\2\2KL\7\61\2\2LM\7\61\2\2MQ\3\2\2\2NP\n\6\2\2O"+
		"N\3\2\2\2PS\3\2\2\2QO\3\2\2\2QR\3\2\2\2R\\\3\2\2\2SQ\3\2\2\2TX\7=\2\2"+
		"UW\n\6\2\2VU\3\2\2\2WZ\3\2\2\2XV\3\2\2\2XY\3\2\2\2Y\\\3\2\2\2ZX\3\2\2"+
		"\2[K\3\2\2\2[T\3\2\2\2\\^\3\2\2\2]_\5\31\r\2^]\3\2\2\2^_\3\2\2\2_`\3\2"+
		"\2\2`a\b\16\2\2a\34\3\2\2\2bc\7\61\2\2cd\7,\2\2dh\3\2\2\2eg\13\2\2\2f"+
		"e\3\2\2\2gj\3\2\2\2hi\3\2\2\2hf\3\2\2\2ik\3\2\2\2jh\3\2\2\2kl\7,\2\2l"+
		"m\7\61\2\2mo\3\2\2\2np\5\31\r\2on\3\2\2\2op\3\2\2\2pq\3\2\2\2qr\b\17\2"+
		"\2r\36\3\2\2\2\17\2-\62:@FIQX[^ho\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}