grammar BLikeLang;

root: declarations;

declarations: declaration* ;
declaration: functionDeclaration
           ;

functionDeclaration: type=Identifier name=Identifier ParenOpen parameterDeclarations ParenClose statement Semicolon?;
parameterDeclarations: parameterDeclaration?
                     | parameterDeclaration (Comma parameterDeclaration)+;
parameterDeclaration: type=Identifier name=Identifier;

statement: varDeclaration                                                   #localVarDeclaration
         | var=Identifier operator=(Assign|AndAssign|OrAssign|XorAssign|PlusAssign|MinusAssign|MultiplyAssign|DivideAssign|ModuloAssign|ShiftLAssign|ShiftRAssign) expression  #assignStatement
         | func=Identifier ParenOpen functionCallParameters ParenClose      #callStatement
         | CurlyOpen statement* CurlyClose                                  #blockStatement
         | Return expression?                                               #returnStatement
         | If expression
             trueStatement=statement
           (Else
             falseStatement=statement)?                                     #ifStatement
         | While expression statement                                       #whileStatement
         | Break                                                            #breakStatement
         | Semicolon                                                        #emptyStatement
         ;
varDeclaration: Var             var=Identifier Assign expression  #inferVarDeclaration
              | type=Identifier var=Identifier Assign expression  #typeVarDeclaration
              ;
subexpr: Number                                                             #numberLiteral
       | CharLiteral                                                        #charLiteral
       | BooleanLiteral                                                     #booleanLiteral
       | Identifier                                                         #readVariable
       | func=Identifier ParenOpen functionCallParameters ParenClose        #functionCall
       |                 ParenOpen expression ParenClose                    #expressionInParenthesis
       ;
expression: subexpr                                                            #subExpression
          | left=expression operator=(BitAnd|BitOr|BitXor) right=expression    #binaryExpressionBits
          | left=expression operator=(Multiply|Divide|Modulo|ShiftL|ShiftR) right=expression #binaryExpressionPoint
          | left=expression operator=(Plus|Minus) right=expression             #binaryExpressionDash
          | left=expression operator=(Lt|Le|Eq|Ge|Gt|Ne) right=expression      #binaryExpressionBool
          ;

functionCallParameters: expression?
                      | expression (Comma expression)+
                      ;

Comma    : ',';
Semicolon: ';';

Plus    : '+';
Minus   : '-';
Multiply: '*';
Divide  : '/';
Modulo  : '%';
ShiftL  : '<<';
ShiftR  : '>>';

Lt : '<' ;
Le : '<=';
Eq : '==';
Ge : '>=';
Gt : '>';
Ne : '!=';

BitAnd : '&';
BitOr  : '|';
BitXor : '^';

Assign: '=' ;
AndAssign: '&=';
OrAssign: '|=';
XorAssign: '^=';
PlusAssign: '+=';
MinusAssign: '-=';
MultiplyAssign: '*=';
DivideAssign: '/=';
ModuloAssign: '%=';
ShiftLAssign: '<<=';
ShiftRAssign: '>>=';

ParenOpen : '(';
ParenClose: ')';
CurlyOpen : '{';
CurlyClose: '}';

Break : 'break';
Else  : 'else';
If    : 'if';
Return: 'return';
Var   : 'var';
While : 'while';

fragment SingleQuote : ['];

fragment Char: ~[\\]
             | [\\] ['nrt\\]
             ;

CharLiteral: SingleQuote Char SingleQuote;

BooleanLiteral: 'true' | 'false';

Number: ( [-]? [0-9]+
        | '0x' [0-9A-Fa-f]+
        );

Identifier
    : [a-zA-Z] [0-9a-zA-Z_]*
    ;

Whitespace
	: [ \t]+
	  -> skip
	;

NL
	: ('\r' '\n'?
	  | '\n'
	  )
	-> skip
	;

LineComment
	: ('//' ~[\r\n]*
	  )
	  -> skip
	;

BlockComment
	: '/*' .*? '*/'
	  -> skip
	;
