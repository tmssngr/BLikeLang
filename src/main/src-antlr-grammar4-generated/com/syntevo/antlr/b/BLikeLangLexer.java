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
		Comma=1, Semicolon=2, Plus=3, Minus=4, Multiply=5, Divide=6, Modulo=7, 
		ShiftL=8, ShiftR=9, Lt=10, Le=11, Eq=12, Ge=13, Gt=14, Ne=15, BitAnd=16, 
		BitOr=17, BitXor=18, Assign=19, AndAssign=20, OrAssign=21, XorAssign=22, 
		PlusAssign=23, MinusAssign=24, MultiplyAssign=25, DivideAssign=26, ModuloAssign=27, 
		ShiftLAssign=28, ShiftRAssign=29, ParenOpen=30, ParenClose=31, CurlyOpen=32, 
		CurlyClose=33, Break=34, Const=35, Else=36, If=37, Return=38, Var=39, 
		While=40, CharLiteral=41, BooleanLiteral=42, Number=43, Identifier=44, 
		Whitespace=45, NL=46, LineComment=47, BlockComment=48;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"Comma", "Semicolon", "Plus", "Minus", "Multiply", "Divide", "Modulo", 
			"ShiftL", "ShiftR", "Lt", "Le", "Eq", "Ge", "Gt", "Ne", "BitAnd", "BitOr", 
			"BitXor", "Assign", "AndAssign", "OrAssign", "XorAssign", "PlusAssign", 
			"MinusAssign", "MultiplyAssign", "DivideAssign", "ModuloAssign", "ShiftLAssign", 
			"ShiftRAssign", "ParenOpen", "ParenClose", "CurlyOpen", "CurlyClose", 
			"Break", "Const", "Else", "If", "Return", "Var", "While", "SingleQuote", 
			"Char", "CharLiteral", "BooleanLiteral", "Number", "Identifier", "Whitespace", 
			"NL", "LineComment", "BlockComment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "','", "';'", "'+'", "'-'", "'*'", "'/'", "'%'", "'<<'", "'>>'", 
			"'<'", "'<='", "'=='", "'>='", "'>'", "'!='", "'&'", "'|'", "'^'", "'='", 
			"'&='", "'|='", "'^='", "'+='", "'-='", "'*='", "'/='", "'%='", "'<<='", 
			"'>>='", "'('", "')'", "'{'", "'}'", "'break'", "'const'", "'else'", 
			"'if'", "'return'", "'var'", "'while'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Comma", "Semicolon", "Plus", "Minus", "Multiply", "Divide", "Modulo", 
			"ShiftL", "ShiftR", "Lt", "Le", "Eq", "Ge", "Gt", "Ne", "BitAnd", "BitOr", 
			"BitXor", "Assign", "AndAssign", "OrAssign", "XorAssign", "PlusAssign", 
			"MinusAssign", "MultiplyAssign", "DivideAssign", "ModuloAssign", "ShiftLAssign", 
			"ShiftRAssign", "ParenOpen", "ParenClose", "CurlyOpen", "CurlyClose", 
			"Break", "Const", "Else", "If", "Return", "Var", "While", "CharLiteral", 
			"BooleanLiteral", "Number", "Identifier", "Whitespace", "NL", "LineComment", 
			"BlockComment"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\62\u0138\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\3\2"+
		"\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\n\3"+
		"\n\3\n\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\20"+
		"\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\25\3\26"+
		"\3\26\3\26\3\27\3\27\3\27\3\30\3\30\3\30\3\31\3\31\3\31\3\32\3\32\3\32"+
		"\3\33\3\33\3\33\3\34\3\34\3\34\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3\36"+
		"\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3#\3#\3#\3#\3$\3$\3$\3$\3$\3$\3%"+
		"\3%\3%\3%\3%\3&\3&\3&\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3(\3(\3(\3(\3)\3)\3"+
		")\3)\3)\3)\3*\3*\3+\3+\3+\5+\u00e6\n+\3,\3,\3,\3,\3-\3-\3-\3-\3-\3-\3"+
		"-\3-\3-\5-\u00f5\n-\3.\5.\u00f8\n.\3.\6.\u00fb\n.\r.\16.\u00fc\3.\3.\3"+
		".\3.\6.\u0103\n.\r.\16.\u0104\5.\u0107\n.\3/\3/\7/\u010b\n/\f/\16/\u010e"+
		"\13/\3\60\6\60\u0111\n\60\r\60\16\60\u0112\3\60\3\60\3\61\3\61\5\61\u0119"+
		"\n\61\3\61\5\61\u011c\n\61\3\61\3\61\3\62\3\62\3\62\3\62\7\62\u0124\n"+
		"\62\f\62\16\62\u0127\13\62\3\62\3\62\3\63\3\63\3\63\3\63\7\63\u012f\n"+
		"\63\f\63\16\63\u0132\13\63\3\63\3\63\3\63\3\63\3\63\3\u0130\2\64\3\3\5"+
		"\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21"+
		"!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!"+
		"A\"C#E$G%I&K\'M(O)Q*S\2U\2W+Y,[-]._/a\60c\61e\62\3\2\f\3\2))\3\2^^\7\2"+
		"))^^ppttvv\3\2//\3\2\62;\5\2\62;CHch\4\2C\\c|\6\2\62;C\\aac|\4\2\13\13"+
		"\"\"\4\2\f\f\17\17\2\u0141\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2"+
		"\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2"+
		"\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3"+
		"\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2"+
		"\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67"+
		"\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2"+
		"\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2"+
		"\2Q\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a"+
		"\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\3g\3\2\2\2\5i\3\2\2\2\7k\3\2\2\2\tm\3\2"+
		"\2\2\13o\3\2\2\2\rq\3\2\2\2\17s\3\2\2\2\21u\3\2\2\2\23x\3\2\2\2\25{\3"+
		"\2\2\2\27}\3\2\2\2\31\u0080\3\2\2\2\33\u0083\3\2\2\2\35\u0086\3\2\2\2"+
		"\37\u0088\3\2\2\2!\u008b\3\2\2\2#\u008d\3\2\2\2%\u008f\3\2\2\2\'\u0091"+
		"\3\2\2\2)\u0093\3\2\2\2+\u0096\3\2\2\2-\u0099\3\2\2\2/\u009c\3\2\2\2\61"+
		"\u009f\3\2\2\2\63\u00a2\3\2\2\2\65\u00a5\3\2\2\2\67\u00a8\3\2\2\29\u00ab"+
		"\3\2\2\2;\u00af\3\2\2\2=\u00b3\3\2\2\2?\u00b5\3\2\2\2A\u00b7\3\2\2\2C"+
		"\u00b9\3\2\2\2E\u00bb\3\2\2\2G\u00c1\3\2\2\2I\u00c7\3\2\2\2K\u00cc\3\2"+
		"\2\2M\u00cf\3\2\2\2O\u00d6\3\2\2\2Q\u00da\3\2\2\2S\u00e0\3\2\2\2U\u00e5"+
		"\3\2\2\2W\u00e7\3\2\2\2Y\u00f4\3\2\2\2[\u0106\3\2\2\2]\u0108\3\2\2\2_"+
		"\u0110\3\2\2\2a\u011b\3\2\2\2c\u011f\3\2\2\2e\u012a\3\2\2\2gh\7.\2\2h"+
		"\4\3\2\2\2ij\7=\2\2j\6\3\2\2\2kl\7-\2\2l\b\3\2\2\2mn\7/\2\2n\n\3\2\2\2"+
		"op\7,\2\2p\f\3\2\2\2qr\7\61\2\2r\16\3\2\2\2st\7\'\2\2t\20\3\2\2\2uv\7"+
		">\2\2vw\7>\2\2w\22\3\2\2\2xy\7@\2\2yz\7@\2\2z\24\3\2\2\2{|\7>\2\2|\26"+
		"\3\2\2\2}~\7>\2\2~\177\7?\2\2\177\30\3\2\2\2\u0080\u0081\7?\2\2\u0081"+
		"\u0082\7?\2\2\u0082\32\3\2\2\2\u0083\u0084\7@\2\2\u0084\u0085\7?\2\2\u0085"+
		"\34\3\2\2\2\u0086\u0087\7@\2\2\u0087\36\3\2\2\2\u0088\u0089\7#\2\2\u0089"+
		"\u008a\7?\2\2\u008a \3\2\2\2\u008b\u008c\7(\2\2\u008c\"\3\2\2\2\u008d"+
		"\u008e\7~\2\2\u008e$\3\2\2\2\u008f\u0090\7`\2\2\u0090&\3\2\2\2\u0091\u0092"+
		"\7?\2\2\u0092(\3\2\2\2\u0093\u0094\7(\2\2\u0094\u0095\7?\2\2\u0095*\3"+
		"\2\2\2\u0096\u0097\7~\2\2\u0097\u0098\7?\2\2\u0098,\3\2\2\2\u0099\u009a"+
		"\7`\2\2\u009a\u009b\7?\2\2\u009b.\3\2\2\2\u009c\u009d\7-\2\2\u009d\u009e"+
		"\7?\2\2\u009e\60\3\2\2\2\u009f\u00a0\7/\2\2\u00a0\u00a1\7?\2\2\u00a1\62"+
		"\3\2\2\2\u00a2\u00a3\7,\2\2\u00a3\u00a4\7?\2\2\u00a4\64\3\2\2\2\u00a5"+
		"\u00a6\7\61\2\2\u00a6\u00a7\7?\2\2\u00a7\66\3\2\2\2\u00a8\u00a9\7\'\2"+
		"\2\u00a9\u00aa\7?\2\2\u00aa8\3\2\2\2\u00ab\u00ac\7>\2\2\u00ac\u00ad\7"+
		">\2\2\u00ad\u00ae\7?\2\2\u00ae:\3\2\2\2\u00af\u00b0\7@\2\2\u00b0\u00b1"+
		"\7@\2\2\u00b1\u00b2\7?\2\2\u00b2<\3\2\2\2\u00b3\u00b4\7*\2\2\u00b4>\3"+
		"\2\2\2\u00b5\u00b6\7+\2\2\u00b6@\3\2\2\2\u00b7\u00b8\7}\2\2\u00b8B\3\2"+
		"\2\2\u00b9\u00ba\7\177\2\2\u00baD\3\2\2\2\u00bb\u00bc\7d\2\2\u00bc\u00bd"+
		"\7t\2\2\u00bd\u00be\7g\2\2\u00be\u00bf\7c\2\2\u00bf\u00c0\7m\2\2\u00c0"+
		"F\3\2\2\2\u00c1\u00c2\7e\2\2\u00c2\u00c3\7q\2\2\u00c3\u00c4\7p\2\2\u00c4"+
		"\u00c5\7u\2\2\u00c5\u00c6\7v\2\2\u00c6H\3\2\2\2\u00c7\u00c8\7g\2\2\u00c8"+
		"\u00c9\7n\2\2\u00c9\u00ca\7u\2\2\u00ca\u00cb\7g\2\2\u00cbJ\3\2\2\2\u00cc"+
		"\u00cd\7k\2\2\u00cd\u00ce\7h\2\2\u00ceL\3\2\2\2\u00cf\u00d0\7t\2\2\u00d0"+
		"\u00d1\7g\2\2\u00d1\u00d2\7v\2\2\u00d2\u00d3\7w\2\2\u00d3\u00d4\7t\2\2"+
		"\u00d4\u00d5\7p\2\2\u00d5N\3\2\2\2\u00d6\u00d7\7x\2\2\u00d7\u00d8\7c\2"+
		"\2\u00d8\u00d9\7t\2\2\u00d9P\3\2\2\2\u00da\u00db\7y\2\2\u00db\u00dc\7"+
		"j\2\2\u00dc\u00dd\7k\2\2\u00dd\u00de\7n\2\2\u00de\u00df\7g\2\2\u00dfR"+
		"\3\2\2\2\u00e0\u00e1\t\2\2\2\u00e1T\3\2\2\2\u00e2\u00e6\n\3\2\2\u00e3"+
		"\u00e4\t\3\2\2\u00e4\u00e6\t\4\2\2\u00e5\u00e2\3\2\2\2\u00e5\u00e3\3\2"+
		"\2\2\u00e6V\3\2\2\2\u00e7\u00e8\5S*\2\u00e8\u00e9\5U+\2\u00e9\u00ea\5"+
		"S*\2\u00eaX\3\2\2\2\u00eb\u00ec\7v\2\2\u00ec\u00ed\7t\2\2\u00ed\u00ee"+
		"\7w\2\2\u00ee\u00f5\7g\2\2\u00ef\u00f0\7h\2\2\u00f0\u00f1\7c\2\2\u00f1"+
		"\u00f2\7n\2\2\u00f2\u00f3\7u\2\2\u00f3\u00f5\7g\2\2\u00f4\u00eb\3\2\2"+
		"\2\u00f4\u00ef\3\2\2\2\u00f5Z\3\2\2\2\u00f6\u00f8\t\5\2\2\u00f7\u00f6"+
		"\3\2\2\2\u00f7\u00f8\3\2\2\2\u00f8\u00fa\3\2\2\2\u00f9\u00fb\t\6\2\2\u00fa"+
		"\u00f9\3\2\2\2\u00fb\u00fc\3\2\2\2\u00fc\u00fa\3\2\2\2\u00fc\u00fd\3\2"+
		"\2\2\u00fd\u0107\3\2\2\2\u00fe\u00ff\7\62\2\2\u00ff\u0100\7z\2\2\u0100"+
		"\u0102\3\2\2\2\u0101\u0103\t\7\2\2\u0102\u0101\3\2\2\2\u0103\u0104\3\2"+
		"\2\2\u0104\u0102\3\2\2\2\u0104\u0105\3\2\2\2\u0105\u0107\3\2\2\2\u0106"+
		"\u00f7\3\2\2\2\u0106\u00fe\3\2\2\2\u0107\\\3\2\2\2\u0108\u010c\t\b\2\2"+
		"\u0109\u010b\t\t\2\2\u010a\u0109\3\2\2\2\u010b\u010e\3\2\2\2\u010c\u010a"+
		"\3\2\2\2\u010c\u010d\3\2\2\2\u010d^\3\2\2\2\u010e\u010c\3\2\2\2\u010f"+
		"\u0111\t\n\2\2\u0110\u010f\3\2\2\2\u0111\u0112\3\2\2\2\u0112\u0110\3\2"+
		"\2\2\u0112\u0113\3\2\2\2\u0113\u0114\3\2\2\2\u0114\u0115\b\60\2\2\u0115"+
		"`\3\2\2\2\u0116\u0118\7\17\2\2\u0117\u0119\7\f\2\2\u0118\u0117\3\2\2\2"+
		"\u0118\u0119\3\2\2\2\u0119\u011c\3\2\2\2\u011a\u011c\7\f\2\2\u011b\u0116"+
		"\3\2\2\2\u011b\u011a\3\2\2\2\u011c\u011d\3\2\2\2\u011d\u011e\b\61\2\2"+
		"\u011eb\3\2\2\2\u011f\u0120\7\61\2\2\u0120\u0121\7\61\2\2\u0121\u0125"+
		"\3\2\2\2\u0122\u0124\n\13\2\2\u0123\u0122\3\2\2\2\u0124\u0127\3\2\2\2"+
		"\u0125\u0123\3\2\2\2\u0125\u0126\3\2\2\2\u0126\u0128\3\2\2\2\u0127\u0125"+
		"\3\2\2\2\u0128\u0129\b\62\2\2\u0129d\3\2\2\2\u012a\u012b\7\61\2\2\u012b"+
		"\u012c\7,\2\2\u012c\u0130\3\2\2\2\u012d\u012f\13\2\2\2\u012e\u012d\3\2"+
		"\2\2\u012f\u0132\3\2\2\2\u0130\u0131\3\2\2\2\u0130\u012e\3\2\2\2\u0131"+
		"\u0133\3\2\2\2\u0132\u0130\3\2\2\2\u0133\u0134\7,\2\2\u0134\u0135\7\61"+
		"\2\2\u0135\u0136\3\2\2\2\u0136\u0137\b\63\2\2\u0137f\3\2\2\2\17\2\u00e5"+
		"\u00f4\u00f7\u00fc\u0104\u0106\u010c\u0112\u0118\u011b\u0125\u0130\3\b"+
		"\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}