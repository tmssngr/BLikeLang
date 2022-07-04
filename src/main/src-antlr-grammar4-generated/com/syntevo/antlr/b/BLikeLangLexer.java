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
		Comma=1, End=2, Assign=3, Plus=4, Minus=5, Multiply=6, Lt=7, Le=8, Eq=9, 
		Ge=10, Gt=11, Ne=12, ParenOpen=13, ParenClose=14, CurlyOpen=15, CurlyClose=16, 
		Return=17, Var=18, BooleanLiteral=19, Number=20, Identifier=21, Whitespace=22, 
		NL=23, LineComment=24, BlockComment=25;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"Comma", "End", "Assign", "Plus", "Minus", "Multiply", "Lt", "Le", "Eq", 
			"Ge", "Gt", "Ne", "ParenOpen", "ParenClose", "CurlyOpen", "CurlyClose", 
			"Return", "Var", "BooleanLiteral", "DecimalNumber", "Number", "Identifier", 
			"Whitespace", "NL", "LineComment", "BlockComment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "','", "';'", "'='", "'+'", "'-'", "'*'", "'<'", "'<='", "'=='", 
			"'>='", "'>'", "'!='", "'('", "')'", "'{'", "'}'", "'return'", "'var'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Comma", "End", "Assign", "Plus", "Minus", "Multiply", "Lt", "Le", 
			"Eq", "Ge", "Gt", "Ne", "ParenOpen", "ParenClose", "CurlyOpen", "CurlyClose", 
			"Return", "Var", "BooleanLiteral", "Number", "Identifier", "Whitespace", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\33\u00b7\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7"+
		"\3\7\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3\13\3\f\3\f\3\r\3\r\3"+
		"\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3"+
		"\22\3\22\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3"+
		"\24\5\24p\n\24\3\25\6\25s\n\25\r\25\16\25t\3\26\5\26x\n\26\3\26\3\26\3"+
		"\26\3\26\3\26\3\26\5\26\u0080\n\26\5\26\u0082\n\26\3\27\3\27\7\27\u0086"+
		"\n\27\f\27\16\27\u0089\13\27\3\30\6\30\u008c\n\30\r\30\16\30\u008d\3\30"+
		"\3\30\3\31\3\31\5\31\u0094\n\31\3\31\5\31\u0097\n\31\3\32\3\32\3\32\3"+
		"\32\7\32\u009d\n\32\f\32\16\32\u00a0\13\32\3\32\5\32\u00a3\n\32\3\32\3"+
		"\32\3\33\3\33\3\33\3\33\7\33\u00ab\n\33\f\33\16\33\u00ae\13\33\3\33\3"+
		"\33\3\33\3\33\5\33\u00b4\n\33\3\33\3\33\3\u00ac\2\34\3\3\5\4\7\5\t\6\13"+
		"\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'"+
		"\25)\2+\26-\27/\30\61\31\63\32\65\33\3\2\t\3\2\62;\3\2//\4\2kkww\4\2C"+
		"\\c|\6\2\62;C\\aac|\4\2\13\13\"\"\4\2\f\f\17\17\2\u00c2\2\3\3\2\2\2\2"+
		"\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2"+
		"\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2"+
		"\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2"+
		"\2\'\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2"+
		"\2\2\65\3\2\2\2\3\67\3\2\2\2\59\3\2\2\2\7;\3\2\2\2\t=\3\2\2\2\13?\3\2"+
		"\2\2\rA\3\2\2\2\17C\3\2\2\2\21E\3\2\2\2\23H\3\2\2\2\25K\3\2\2\2\27N\3"+
		"\2\2\2\31P\3\2\2\2\33S\3\2\2\2\35U\3\2\2\2\37W\3\2\2\2!Y\3\2\2\2#[\3\2"+
		"\2\2%b\3\2\2\2\'o\3\2\2\2)r\3\2\2\2+w\3\2\2\2-\u0083\3\2\2\2/\u008b\3"+
		"\2\2\2\61\u0096\3\2\2\2\63\u0098\3\2\2\2\65\u00a6\3\2\2\2\678\7.\2\28"+
		"\4\3\2\2\29:\7=\2\2:\6\3\2\2\2;<\7?\2\2<\b\3\2\2\2=>\7-\2\2>\n\3\2\2\2"+
		"?@\7/\2\2@\f\3\2\2\2AB\7,\2\2B\16\3\2\2\2CD\7>\2\2D\20\3\2\2\2EF\7>\2"+
		"\2FG\7?\2\2G\22\3\2\2\2HI\7?\2\2IJ\7?\2\2J\24\3\2\2\2KL\7@\2\2LM\7?\2"+
		"\2M\26\3\2\2\2NO\7@\2\2O\30\3\2\2\2PQ\7#\2\2QR\7?\2\2R\32\3\2\2\2ST\7"+
		"*\2\2T\34\3\2\2\2UV\7+\2\2V\36\3\2\2\2WX\7}\2\2X \3\2\2\2YZ\7\177\2\2"+
		"Z\"\3\2\2\2[\\\7t\2\2\\]\7g\2\2]^\7v\2\2^_\7w\2\2_`\7t\2\2`a\7p\2\2a$"+
		"\3\2\2\2bc\7x\2\2cd\7c\2\2de\7t\2\2e&\3\2\2\2fg\7v\2\2gh\7t\2\2hi\7w\2"+
		"\2ip\7g\2\2jk\7h\2\2kl\7c\2\2lm\7n\2\2mn\7u\2\2np\7g\2\2of\3\2\2\2oj\3"+
		"\2\2\2p(\3\2\2\2qs\t\2\2\2rq\3\2\2\2st\3\2\2\2tr\3\2\2\2tu\3\2\2\2u*\3"+
		"\2\2\2vx\t\3\2\2wv\3\2\2\2wx\3\2\2\2xy\3\2\2\2y\u0081\5)\25\2z{\7a\2\2"+
		"{\177\t\4\2\2|\u0080\7:\2\2}~\7\63\2\2~\u0080\78\2\2\177|\3\2\2\2\177"+
		"}\3\2\2\2\u0080\u0082\3\2\2\2\u0081z\3\2\2\2\u0081\u0082\3\2\2\2\u0082"+
		",\3\2\2\2\u0083\u0087\t\5\2\2\u0084\u0086\t\6\2\2\u0085\u0084\3\2\2\2"+
		"\u0086\u0089\3\2\2\2\u0087\u0085\3\2\2\2\u0087\u0088\3\2\2\2\u0088.\3"+
		"\2\2\2\u0089\u0087\3\2\2\2\u008a\u008c\t\7\2\2\u008b\u008a\3\2\2\2\u008c"+
		"\u008d\3\2\2\2\u008d\u008b\3\2\2\2\u008d\u008e\3\2\2\2\u008e\u008f\3\2"+
		"\2\2\u008f\u0090\b\30\2\2\u0090\60\3\2\2\2\u0091\u0093\7\17\2\2\u0092"+
		"\u0094\7\f\2\2\u0093\u0092\3\2\2\2\u0093\u0094\3\2\2\2\u0094\u0097\3\2"+
		"\2\2\u0095\u0097\7\f\2\2\u0096\u0091\3\2\2\2\u0096\u0095\3\2\2\2\u0097"+
		"\62\3\2\2\2\u0098\u0099\7\61\2\2\u0099\u009a\7\61\2\2\u009a\u009e\3\2"+
		"\2\2\u009b\u009d\n\b\2\2\u009c\u009b\3\2\2\2\u009d\u00a0\3\2\2\2\u009e"+
		"\u009c\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a2\3\2\2\2\u00a0\u009e\3\2"+
		"\2\2\u00a1\u00a3\5\61\31\2\u00a2\u00a1\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3"+
		"\u00a4\3\2\2\2\u00a4\u00a5\b\32\2\2\u00a5\64\3\2\2\2\u00a6\u00a7\7\61"+
		"\2\2\u00a7\u00a8\7,\2\2\u00a8\u00ac\3\2\2\2\u00a9\u00ab\13\2\2\2\u00aa"+
		"\u00a9\3\2\2\2\u00ab\u00ae\3\2\2\2\u00ac\u00ad\3\2\2\2\u00ac\u00aa\3\2"+
		"\2\2\u00ad\u00af\3\2\2\2\u00ae\u00ac\3\2\2\2\u00af\u00b0\7,\2\2\u00b0"+
		"\u00b1\7\61\2\2\u00b1\u00b3\3\2\2\2\u00b2\u00b4\5\61\31\2\u00b3\u00b2"+
		"\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00b6\b\33\2\2"+
		"\u00b6\66\3\2\2\2\20\2otw\177\u0081\u0087\u008d\u0093\u0096\u009e\u00a2"+
		"\u00ac\u00b3\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}