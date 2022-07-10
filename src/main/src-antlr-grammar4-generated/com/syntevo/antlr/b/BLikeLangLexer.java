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
		Comma=1, End=2, Assign=3, Plus=4, Minus=5, Multiply=6, ShiftL=7, ShiftR=8, 
		Lt=9, Le=10, Eq=11, Ge=12, Gt=13, Ne=14, BitAnd=15, BitOr=16, BitXor=17, 
		ParenOpen=18, ParenClose=19, CurlyOpen=20, CurlyClose=21, Break=22, Else=23, 
		If=24, Return=25, Var=26, While=27, BooleanLiteral=28, Number=29, Identifier=30, 
		Whitespace=31, NL=32, LineComment=33, BlockComment=34;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"Comma", "End", "Assign", "Plus", "Minus", "Multiply", "ShiftL", "ShiftR", 
			"Lt", "Le", "Eq", "Ge", "Gt", "Ne", "BitAnd", "BitOr", "BitXor", "ParenOpen", 
			"ParenClose", "CurlyOpen", "CurlyClose", "Break", "Else", "If", "Return", 
			"Var", "While", "BooleanLiteral", "DecimalNumber", "Number", "Identifier", 
			"Whitespace", "NL", "LineComment", "BlockComment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "','", "';'", "'='", "'+'", "'-'", "'*'", "'<<'", "'>>'", "'<'", 
			"'<='", "'=='", "'>='", "'>'", "'!='", "'&'", "'|'", "'^'", "'('", "')'", 
			"'{'", "'}'", "'break'", "'else'", "'if'", "'return'", "'var'", "'while'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Comma", "End", "Assign", "Plus", "Minus", "Multiply", "ShiftL", 
			"ShiftR", "Lt", "Le", "Eq", "Ge", "Gt", "Ne", "BitAnd", "BitOr", "BitXor", 
			"ParenOpen", "ParenClose", "CurlyOpen", "CurlyClose", "Break", "Else", 
			"If", "Return", "Var", "While", "BooleanLiteral", "Number", "Identifier", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2$\u00e5\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3"+
		"\7\3\b\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\f\3\f\3\f\3\r\3\r"+
		"\3\r\3\16\3\16\3\17\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23"+
		"\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\3\30\3\30"+
		"\3\30\3\30\3\30\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\33"+
		"\3\33\3\33\3\33\3\34\3\34\3\34\3\34\3\34\3\34\3\35\3\35\3\35\3\35\3\35"+
		"\3\35\3\35\3\35\3\35\5\35\u00a2\n\35\3\36\6\36\u00a5\n\36\r\36\16\36\u00a6"+
		"\3\37\5\37\u00aa\n\37\3\37\3\37\3\37\3\37\3\37\3\37\5\37\u00b2\n\37\5"+
		"\37\u00b4\n\37\3 \3 \7 \u00b8\n \f \16 \u00bb\13 \3!\6!\u00be\n!\r!\16"+
		"!\u00bf\3!\3!\3\"\3\"\5\"\u00c6\n\"\3\"\5\"\u00c9\n\"\3\"\3\"\3#\3#\3"+
		"#\3#\7#\u00d1\n#\f#\16#\u00d4\13#\3#\3#\3$\3$\3$\3$\7$\u00dc\n$\f$\16"+
		"$\u00df\13$\3$\3$\3$\3$\3$\3\u00dd\2%\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21"+
		"\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30"+
		"/\31\61\32\63\33\65\34\67\359\36;\2=\37? A!C\"E#G$\3\2\t\3\2\62;\3\2/"+
		"/\4\2kkww\4\2C\\c|\6\2\62;C\\aac|\4\2\13\13\"\"\4\2\f\f\17\17\2\u00ee"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2"+
		"\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2"+
		"\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2"+
		"\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2=\3"+
		"\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\3I\3\2\2"+
		"\2\5K\3\2\2\2\7M\3\2\2\2\tO\3\2\2\2\13Q\3\2\2\2\rS\3\2\2\2\17U\3\2\2\2"+
		"\21X\3\2\2\2\23[\3\2\2\2\25]\3\2\2\2\27`\3\2\2\2\31c\3\2\2\2\33f\3\2\2"+
		"\2\35h\3\2\2\2\37k\3\2\2\2!m\3\2\2\2#o\3\2\2\2%q\3\2\2\2\'s\3\2\2\2)u"+
		"\3\2\2\2+w\3\2\2\2-y\3\2\2\2/\177\3\2\2\2\61\u0084\3\2\2\2\63\u0087\3"+
		"\2\2\2\65\u008e\3\2\2\2\67\u0092\3\2\2\29\u00a1\3\2\2\2;\u00a4\3\2\2\2"+
		"=\u00a9\3\2\2\2?\u00b5\3\2\2\2A\u00bd\3\2\2\2C\u00c8\3\2\2\2E\u00cc\3"+
		"\2\2\2G\u00d7\3\2\2\2IJ\7.\2\2J\4\3\2\2\2KL\7=\2\2L\6\3\2\2\2MN\7?\2\2"+
		"N\b\3\2\2\2OP\7-\2\2P\n\3\2\2\2QR\7/\2\2R\f\3\2\2\2ST\7,\2\2T\16\3\2\2"+
		"\2UV\7>\2\2VW\7>\2\2W\20\3\2\2\2XY\7@\2\2YZ\7@\2\2Z\22\3\2\2\2[\\\7>\2"+
		"\2\\\24\3\2\2\2]^\7>\2\2^_\7?\2\2_\26\3\2\2\2`a\7?\2\2ab\7?\2\2b\30\3"+
		"\2\2\2cd\7@\2\2de\7?\2\2e\32\3\2\2\2fg\7@\2\2g\34\3\2\2\2hi\7#\2\2ij\7"+
		"?\2\2j\36\3\2\2\2kl\7(\2\2l \3\2\2\2mn\7~\2\2n\"\3\2\2\2op\7`\2\2p$\3"+
		"\2\2\2qr\7*\2\2r&\3\2\2\2st\7+\2\2t(\3\2\2\2uv\7}\2\2v*\3\2\2\2wx\7\177"+
		"\2\2x,\3\2\2\2yz\7d\2\2z{\7t\2\2{|\7g\2\2|}\7c\2\2}~\7m\2\2~.\3\2\2\2"+
		"\177\u0080\7g\2\2\u0080\u0081\7n\2\2\u0081\u0082\7u\2\2\u0082\u0083\7"+
		"g\2\2\u0083\60\3\2\2\2\u0084\u0085\7k\2\2\u0085\u0086\7h\2\2\u0086\62"+
		"\3\2\2\2\u0087\u0088\7t\2\2\u0088\u0089\7g\2\2\u0089\u008a\7v\2\2\u008a"+
		"\u008b\7w\2\2\u008b\u008c\7t\2\2\u008c\u008d\7p\2\2\u008d\64\3\2\2\2\u008e"+
		"\u008f\7x\2\2\u008f\u0090\7c\2\2\u0090\u0091\7t\2\2\u0091\66\3\2\2\2\u0092"+
		"\u0093\7y\2\2\u0093\u0094\7j\2\2\u0094\u0095\7k\2\2\u0095\u0096\7n\2\2"+
		"\u0096\u0097\7g\2\2\u00978\3\2\2\2\u0098\u0099\7v\2\2\u0099\u009a\7t\2"+
		"\2\u009a\u009b\7w\2\2\u009b\u00a2\7g\2\2\u009c\u009d\7h\2\2\u009d\u009e"+
		"\7c\2\2\u009e\u009f\7n\2\2\u009f\u00a0\7u\2\2\u00a0\u00a2\7g\2\2\u00a1"+
		"\u0098\3\2\2\2\u00a1\u009c\3\2\2\2\u00a2:\3\2\2\2\u00a3\u00a5\t\2\2\2"+
		"\u00a4\u00a3\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6\u00a4\3\2\2\2\u00a6\u00a7"+
		"\3\2\2\2\u00a7<\3\2\2\2\u00a8\u00aa\t\3\2\2\u00a9\u00a8\3\2\2\2\u00a9"+
		"\u00aa\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\u00b3\5;\36\2\u00ac\u00ad\7a"+
		"\2\2\u00ad\u00b1\t\4\2\2\u00ae\u00b2\7:\2\2\u00af\u00b0\7\63\2\2\u00b0"+
		"\u00b2\78\2\2\u00b1\u00ae\3\2\2\2\u00b1\u00af\3\2\2\2\u00b2\u00b4\3\2"+
		"\2\2\u00b3\u00ac\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4>\3\2\2\2\u00b5\u00b9"+
		"\t\5\2\2\u00b6\u00b8\t\6\2\2\u00b7\u00b6\3\2\2\2\u00b8\u00bb\3\2\2\2\u00b9"+
		"\u00b7\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba@\3\2\2\2\u00bb\u00b9\3\2\2\2"+
		"\u00bc\u00be\t\7\2\2\u00bd\u00bc\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf\u00bd"+
		"\3\2\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00c1\3\2\2\2\u00c1\u00c2\b!\2\2\u00c2"+
		"B\3\2\2\2\u00c3\u00c5\7\17\2\2\u00c4\u00c6\7\f\2\2\u00c5\u00c4\3\2\2\2"+
		"\u00c5\u00c6\3\2\2\2\u00c6\u00c9\3\2\2\2\u00c7\u00c9\7\f\2\2\u00c8\u00c3"+
		"\3\2\2\2\u00c8\u00c7\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca\u00cb\b\"\2\2\u00cb"+
		"D\3\2\2\2\u00cc\u00cd\7\61\2\2\u00cd\u00ce\7\61\2\2\u00ce\u00d2\3\2\2"+
		"\2\u00cf\u00d1\n\b\2\2\u00d0\u00cf\3\2\2\2\u00d1\u00d4\3\2\2\2\u00d2\u00d0"+
		"\3\2\2\2\u00d2\u00d3\3\2\2\2\u00d3\u00d5\3\2\2\2\u00d4\u00d2\3\2\2\2\u00d5"+
		"\u00d6\b#\2\2\u00d6F\3\2\2\2\u00d7\u00d8\7\61\2\2\u00d8\u00d9\7,\2\2\u00d9"+
		"\u00dd\3\2\2\2\u00da\u00dc\13\2\2\2\u00db\u00da\3\2\2\2\u00dc\u00df\3"+
		"\2\2\2\u00dd\u00de\3\2\2\2\u00dd\u00db\3\2\2\2\u00de\u00e0\3\2\2\2\u00df"+
		"\u00dd\3\2\2\2\u00e0\u00e1\7,\2\2\u00e1\u00e2\7\61\2\2\u00e2\u00e3\3\2"+
		"\2\2\u00e3\u00e4\b$\2\2\u00e4H\3\2\2\2\16\2\u00a1\u00a6\u00a9\u00b1\u00b3"+
		"\u00b9\u00bf\u00c5\u00c8\u00d2\u00dd\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}