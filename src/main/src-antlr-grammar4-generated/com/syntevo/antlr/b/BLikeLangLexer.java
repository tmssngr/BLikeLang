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
		Else=17, If=18, Return=19, Var=20, While=21, BooleanLiteral=22, Number=23, 
		Identifier=24, Whitespace=25, NL=26, LineComment=27, BlockComment=28;
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
			"Else", "If", "Return", "Var", "While", "BooleanLiteral", "DecimalNumber", 
			"Number", "Identifier", "Whitespace", "NL", "LineComment", "BlockComment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "','", "';'", "'='", "'+'", "'-'", "'*'", "'<'", "'<='", "'=='", 
			"'>='", "'>'", "'!='", "'('", "')'", "'{'", "'}'", "'else'", "'if'", 
			"'return'", "'var'", "'while'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Comma", "End", "Assign", "Plus", "Minus", "Multiply", "Lt", "Le", 
			"Eq", "Ge", "Gt", "Ne", "ParenOpen", "ParenClose", "CurlyOpen", "CurlyClose", 
			"Else", "If", "Return", "Var", "While", "BooleanLiteral", "Number", "Identifier", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\36\u00cb\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\3\2\3\2\3\3\3"+
		"\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3\n\3\13"+
		"\3\13\3\13\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21"+
		"\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27"+
		"\3\27\3\27\3\27\3\27\3\27\3\27\5\27\u0084\n\27\3\30\6\30\u0087\n\30\r"+
		"\30\16\30\u0088\3\31\5\31\u008c\n\31\3\31\3\31\3\31\3\31\3\31\3\31\5\31"+
		"\u0094\n\31\5\31\u0096\n\31\3\32\3\32\7\32\u009a\n\32\f\32\16\32\u009d"+
		"\13\32\3\33\6\33\u00a0\n\33\r\33\16\33\u00a1\3\33\3\33\3\34\3\34\5\34"+
		"\u00a8\n\34\3\34\5\34\u00ab\n\34\3\35\3\35\3\35\3\35\7\35\u00b1\n\35\f"+
		"\35\16\35\u00b4\13\35\3\35\5\35\u00b7\n\35\3\35\3\35\3\36\3\36\3\36\3"+
		"\36\7\36\u00bf\n\36\f\36\16\36\u00c2\13\36\3\36\3\36\3\36\3\36\5\36\u00c8"+
		"\n\36\3\36\3\36\3\u00c0\2\37\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13"+
		"\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\2\61"+
		"\31\63\32\65\33\67\349\35;\36\3\2\t\3\2\62;\3\2//\4\2kkww\4\2C\\c|\6\2"+
		"\62;C\\aac|\4\2\13\13\"\"\4\2\f\f\17\17\2\u00d6\2\3\3\2\2\2\2\5\3\2\2"+
		"\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21"+
		"\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2"+
		"\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3"+
		"\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65"+
		"\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\3=\3\2\2\2\5?\3\2\2\2\7A\3"+
		"\2\2\2\tC\3\2\2\2\13E\3\2\2\2\rG\3\2\2\2\17I\3\2\2\2\21K\3\2\2\2\23N\3"+
		"\2\2\2\25Q\3\2\2\2\27T\3\2\2\2\31V\3\2\2\2\33Y\3\2\2\2\35[\3\2\2\2\37"+
		"]\3\2\2\2!_\3\2\2\2#a\3\2\2\2%f\3\2\2\2\'i\3\2\2\2)p\3\2\2\2+t\3\2\2\2"+
		"-\u0083\3\2\2\2/\u0086\3\2\2\2\61\u008b\3\2\2\2\63\u0097\3\2\2\2\65\u009f"+
		"\3\2\2\2\67\u00aa\3\2\2\29\u00ac\3\2\2\2;\u00ba\3\2\2\2=>\7.\2\2>\4\3"+
		"\2\2\2?@\7=\2\2@\6\3\2\2\2AB\7?\2\2B\b\3\2\2\2CD\7-\2\2D\n\3\2\2\2EF\7"+
		"/\2\2F\f\3\2\2\2GH\7,\2\2H\16\3\2\2\2IJ\7>\2\2J\20\3\2\2\2KL\7>\2\2LM"+
		"\7?\2\2M\22\3\2\2\2NO\7?\2\2OP\7?\2\2P\24\3\2\2\2QR\7@\2\2RS\7?\2\2S\26"+
		"\3\2\2\2TU\7@\2\2U\30\3\2\2\2VW\7#\2\2WX\7?\2\2X\32\3\2\2\2YZ\7*\2\2Z"+
		"\34\3\2\2\2[\\\7+\2\2\\\36\3\2\2\2]^\7}\2\2^ \3\2\2\2_`\7\177\2\2`\"\3"+
		"\2\2\2ab\7g\2\2bc\7n\2\2cd\7u\2\2de\7g\2\2e$\3\2\2\2fg\7k\2\2gh\7h\2\2"+
		"h&\3\2\2\2ij\7t\2\2jk\7g\2\2kl\7v\2\2lm\7w\2\2mn\7t\2\2no\7p\2\2o(\3\2"+
		"\2\2pq\7x\2\2qr\7c\2\2rs\7t\2\2s*\3\2\2\2tu\7y\2\2uv\7j\2\2vw\7k\2\2w"+
		"x\7n\2\2xy\7g\2\2y,\3\2\2\2z{\7v\2\2{|\7t\2\2|}\7w\2\2}\u0084\7g\2\2~"+
		"\177\7h\2\2\177\u0080\7c\2\2\u0080\u0081\7n\2\2\u0081\u0082\7u\2\2\u0082"+
		"\u0084\7g\2\2\u0083z\3\2\2\2\u0083~\3\2\2\2\u0084.\3\2\2\2\u0085\u0087"+
		"\t\2\2\2\u0086\u0085\3\2\2\2\u0087\u0088\3\2\2\2\u0088\u0086\3\2\2\2\u0088"+
		"\u0089\3\2\2\2\u0089\60\3\2\2\2\u008a\u008c\t\3\2\2\u008b\u008a\3\2\2"+
		"\2\u008b\u008c\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u0095\5/\30\2\u008e\u008f"+
		"\7a\2\2\u008f\u0093\t\4\2\2\u0090\u0094\7:\2\2\u0091\u0092\7\63\2\2\u0092"+
		"\u0094\78\2\2\u0093\u0090\3\2\2\2\u0093\u0091\3\2\2\2\u0094\u0096\3\2"+
		"\2\2\u0095\u008e\3\2\2\2\u0095\u0096\3\2\2\2\u0096\62\3\2\2\2\u0097\u009b"+
		"\t\5\2\2\u0098\u009a\t\6\2\2\u0099\u0098\3\2\2\2\u009a\u009d\3\2\2\2\u009b"+
		"\u0099\3\2\2\2\u009b\u009c\3\2\2\2\u009c\64\3\2\2\2\u009d\u009b\3\2\2"+
		"\2\u009e\u00a0\t\7\2\2\u009f\u009e\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\u009f"+
		"\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\u00a4\b\33\2\2"+
		"\u00a4\66\3\2\2\2\u00a5\u00a7\7\17\2\2\u00a6\u00a8\7\f\2\2\u00a7\u00a6"+
		"\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00ab\3\2\2\2\u00a9\u00ab\7\f\2\2\u00aa"+
		"\u00a5\3\2\2\2\u00aa\u00a9\3\2\2\2\u00ab8\3\2\2\2\u00ac\u00ad\7\61\2\2"+
		"\u00ad\u00ae\7\61\2\2\u00ae\u00b2\3\2\2\2\u00af\u00b1\n\b\2\2\u00b0\u00af"+
		"\3\2\2\2\u00b1\u00b4\3\2\2\2\u00b2\u00b0\3\2\2\2\u00b2\u00b3\3\2\2\2\u00b3"+
		"\u00b6\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b5\u00b7\5\67\34\2\u00b6\u00b5\3"+
		"\2\2\2\u00b6\u00b7\3\2\2\2\u00b7\u00b8\3\2\2\2\u00b8\u00b9\b\35\2\2\u00b9"+
		":\3\2\2\2\u00ba\u00bb\7\61\2\2\u00bb\u00bc\7,\2\2\u00bc\u00c0\3\2\2\2"+
		"\u00bd\u00bf\13\2\2\2\u00be\u00bd\3\2\2\2\u00bf\u00c2\3\2\2\2\u00c0\u00c1"+
		"\3\2\2\2\u00c0\u00be\3\2\2\2\u00c1\u00c3\3\2\2\2\u00c2\u00c0\3\2\2\2\u00c3"+
		"\u00c4\7,\2\2\u00c4\u00c5\7\61\2\2\u00c5\u00c7\3\2\2\2\u00c6\u00c8\5\67"+
		"\34\2\u00c7\u00c6\3\2\2\2\u00c7\u00c8\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9"+
		"\u00ca\b\36\2\2\u00ca<\3\2\2\2\20\2\u0083\u0088\u008b\u0093\u0095\u009b"+
		"\u00a1\u00a7\u00aa\u00b2\u00b6\u00c0\u00c7\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}