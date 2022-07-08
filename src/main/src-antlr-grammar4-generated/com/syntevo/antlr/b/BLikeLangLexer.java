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
		Ge=10, Gt=11, Ne=12, BitAnd=13, BitOr=14, BitXor=15, ParenOpen=16, ParenClose=17, 
		CurlyOpen=18, CurlyClose=19, Else=20, If=21, Return=22, Var=23, While=24, 
		BooleanLiteral=25, Number=26, Identifier=27, Whitespace=28, NL=29, LineComment=30, 
		BlockComment=31;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"Comma", "End", "Assign", "Plus", "Minus", "Multiply", "Lt", "Le", "Eq", 
			"Ge", "Gt", "Ne", "BitAnd", "BitOr", "BitXor", "ParenOpen", "ParenClose", 
			"CurlyOpen", "CurlyClose", "Else", "If", "Return", "Var", "While", "BooleanLiteral", 
			"DecimalNumber", "Number", "Identifier", "Whitespace", "NL", "LineComment", 
			"BlockComment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "','", "';'", "'='", "'+'", "'-'", "'*'", "'<'", "'<='", "'=='", 
			"'>='", "'>'", "'!='", "'&'", "'|'", "'^'", "'('", "')'", "'{'", "'}'", 
			"'else'", "'if'", "'return'", "'var'", "'while'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Comma", "End", "Assign", "Plus", "Minus", "Multiply", "Lt", "Le", 
			"Eq", "Ge", "Gt", "Ne", "BitAnd", "BitOr", "BitXor", "ParenOpen", "ParenClose", 
			"CurlyOpen", "CurlyClose", "Else", "If", "Return", "Var", "While", "BooleanLiteral", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2!\u00d7\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3"+
		"\t\3\n\3\n\3\n\3\13\3\13\3\13\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\17\3\17"+
		"\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\25\3\25"+
		"\3\25\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30"+
		"\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\5\32\u0090\n\32\3\33\6\33\u0093\n\33\r\33\16\33\u0094\3\34"+
		"\5\34\u0098\n\34\3\34\3\34\3\34\3\34\3\34\3\34\5\34\u00a0\n\34\5\34\u00a2"+
		"\n\34\3\35\3\35\7\35\u00a6\n\35\f\35\16\35\u00a9\13\35\3\36\6\36\u00ac"+
		"\n\36\r\36\16\36\u00ad\3\36\3\36\3\37\3\37\5\37\u00b4\n\37\3\37\5\37\u00b7"+
		"\n\37\3 \3 \3 \3 \7 \u00bd\n \f \16 \u00c0\13 \3 \5 \u00c3\n \3 \3 \3"+
		"!\3!\3!\3!\7!\u00cb\n!\f!\16!\u00ce\13!\3!\3!\3!\3!\5!\u00d4\n!\3!\3!"+
		"\3\u00cc\2\"\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16"+
		"\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\2\67"+
		"\349\35;\36=\37? A!\3\2\t\3\2\62;\3\2//\4\2kkww\4\2C\\c|\6\2\62;C\\aa"+
		"c|\4\2\13\13\"\"\4\2\f\f\17\17\2\u00e2\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2"+
		"\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2"+
		"\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3"+
		"\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3"+
		"\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\67"+
		"\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\3C\3\2"+
		"\2\2\5E\3\2\2\2\7G\3\2\2\2\tI\3\2\2\2\13K\3\2\2\2\rM\3\2\2\2\17O\3\2\2"+
		"\2\21Q\3\2\2\2\23T\3\2\2\2\25W\3\2\2\2\27Z\3\2\2\2\31\\\3\2\2\2\33_\3"+
		"\2\2\2\35a\3\2\2\2\37c\3\2\2\2!e\3\2\2\2#g\3\2\2\2%i\3\2\2\2\'k\3\2\2"+
		"\2)m\3\2\2\2+r\3\2\2\2-u\3\2\2\2/|\3\2\2\2\61\u0080\3\2\2\2\63\u008f\3"+
		"\2\2\2\65\u0092\3\2\2\2\67\u0097\3\2\2\29\u00a3\3\2\2\2;\u00ab\3\2\2\2"+
		"=\u00b6\3\2\2\2?\u00b8\3\2\2\2A\u00c6\3\2\2\2CD\7.\2\2D\4\3\2\2\2EF\7"+
		"=\2\2F\6\3\2\2\2GH\7?\2\2H\b\3\2\2\2IJ\7-\2\2J\n\3\2\2\2KL\7/\2\2L\f\3"+
		"\2\2\2MN\7,\2\2N\16\3\2\2\2OP\7>\2\2P\20\3\2\2\2QR\7>\2\2RS\7?\2\2S\22"+
		"\3\2\2\2TU\7?\2\2UV\7?\2\2V\24\3\2\2\2WX\7@\2\2XY\7?\2\2Y\26\3\2\2\2Z"+
		"[\7@\2\2[\30\3\2\2\2\\]\7#\2\2]^\7?\2\2^\32\3\2\2\2_`\7(\2\2`\34\3\2\2"+
		"\2ab\7~\2\2b\36\3\2\2\2cd\7`\2\2d \3\2\2\2ef\7*\2\2f\"\3\2\2\2gh\7+\2"+
		"\2h$\3\2\2\2ij\7}\2\2j&\3\2\2\2kl\7\177\2\2l(\3\2\2\2mn\7g\2\2no\7n\2"+
		"\2op\7u\2\2pq\7g\2\2q*\3\2\2\2rs\7k\2\2st\7h\2\2t,\3\2\2\2uv\7t\2\2vw"+
		"\7g\2\2wx\7v\2\2xy\7w\2\2yz\7t\2\2z{\7p\2\2{.\3\2\2\2|}\7x\2\2}~\7c\2"+
		"\2~\177\7t\2\2\177\60\3\2\2\2\u0080\u0081\7y\2\2\u0081\u0082\7j\2\2\u0082"+
		"\u0083\7k\2\2\u0083\u0084\7n\2\2\u0084\u0085\7g\2\2\u0085\62\3\2\2\2\u0086"+
		"\u0087\7v\2\2\u0087\u0088\7t\2\2\u0088\u0089\7w\2\2\u0089\u0090\7g\2\2"+
		"\u008a\u008b\7h\2\2\u008b\u008c\7c\2\2\u008c\u008d\7n\2\2\u008d\u008e"+
		"\7u\2\2\u008e\u0090\7g\2\2\u008f\u0086\3\2\2\2\u008f\u008a\3\2\2\2\u0090"+
		"\64\3\2\2\2\u0091\u0093\t\2\2\2\u0092\u0091\3\2\2\2\u0093\u0094\3\2\2"+
		"\2\u0094\u0092\3\2\2\2\u0094\u0095\3\2\2\2\u0095\66\3\2\2\2\u0096\u0098"+
		"\t\3\2\2\u0097\u0096\3\2\2\2\u0097\u0098\3\2\2\2\u0098\u0099\3\2\2\2\u0099"+
		"\u00a1\5\65\33\2\u009a\u009b\7a\2\2\u009b\u009f\t\4\2\2\u009c\u00a0\7"+
		":\2\2\u009d\u009e\7\63\2\2\u009e\u00a0\78\2\2\u009f\u009c\3\2\2\2\u009f"+
		"\u009d\3\2\2\2\u00a0\u00a2\3\2\2\2\u00a1\u009a\3\2\2\2\u00a1\u00a2\3\2"+
		"\2\2\u00a28\3\2\2\2\u00a3\u00a7\t\5\2\2\u00a4\u00a6\t\6\2\2\u00a5\u00a4"+
		"\3\2\2\2\u00a6\u00a9\3\2\2\2\u00a7\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8"+
		":\3\2\2\2\u00a9\u00a7\3\2\2\2\u00aa\u00ac\t\7\2\2\u00ab\u00aa\3\2\2\2"+
		"\u00ac\u00ad\3\2\2\2\u00ad\u00ab\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae\u00af"+
		"\3\2\2\2\u00af\u00b0\b\36\2\2\u00b0<\3\2\2\2\u00b1\u00b3\7\17\2\2\u00b2"+
		"\u00b4\7\f\2\2\u00b3\u00b2\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4\u00b7\3\2"+
		"\2\2\u00b5\u00b7\7\f\2\2\u00b6\u00b1\3\2\2\2\u00b6\u00b5\3\2\2\2\u00b7"+
		">\3\2\2\2\u00b8\u00b9\7\61\2\2\u00b9\u00ba\7\61\2\2\u00ba\u00be\3\2\2"+
		"\2\u00bb\u00bd\n\b\2\2\u00bc\u00bb\3\2\2\2\u00bd\u00c0\3\2\2\2\u00be\u00bc"+
		"\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf\u00c2\3\2\2\2\u00c0\u00be\3\2\2\2\u00c1"+
		"\u00c3\5=\37\2\u00c2\u00c1\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c3\u00c4\3\2"+
		"\2\2\u00c4\u00c5\b \2\2\u00c5@\3\2\2\2\u00c6\u00c7\7\61\2\2\u00c7\u00c8"+
		"\7,\2\2\u00c8\u00cc\3\2\2\2\u00c9\u00cb\13\2\2\2\u00ca\u00c9\3\2\2\2\u00cb"+
		"\u00ce\3\2\2\2\u00cc\u00cd\3\2\2\2\u00cc\u00ca\3\2\2\2\u00cd\u00cf\3\2"+
		"\2\2\u00ce\u00cc\3\2\2\2\u00cf\u00d0\7,\2\2\u00d0\u00d1\7\61\2\2\u00d1"+
		"\u00d3\3\2\2\2\u00d2\u00d4\5=\37\2\u00d3\u00d2\3\2\2\2\u00d3\u00d4\3\2"+
		"\2\2\u00d4\u00d5\3\2\2\2\u00d5\u00d6\b!\2\2\u00d6B\3\2\2\2\20\2\u008f"+
		"\u0094\u0097\u009f\u00a1\u00a7\u00ad\u00b3\u00b6\u00be\u00c2\u00cc\u00d3"+
		"\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}