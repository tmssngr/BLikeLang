grammar BLikeLang;

root: declarations;

declarations: declaration* ;
declaration: varDeclaration      #globalVarDeclaration
           | functionDeclaration #funcDeclaration
           ;

functionDeclaration: type=Identifier name=Identifier ParenOpen parameterDeclarations ParenClose statement;
parameterDeclarations: parameterDeclaration?
                     | parameterDeclaration (Comma parameterDeclaration)+;
parameterDeclaration: type=Identifier name=Identifier;

statement: varDeclaration                         #localVarDeclaration
         | var=Identifier Assign expression End   #assignStatement
         | func=Identifier ParenOpen functionCallParameters ParenClose End  #callStatement
         | CurlyOpen statement* CurlyClose        #blockStatement
         | Return expression? End                 #returnStatement
         | If expression
             trueStatement=statement
           (Else
             falseStatement=statement)?           #ifStatement
         | While expression statement             #whileStatement
         | Break End                              #breakStatement
         ;
varDeclaration: Var             var=Identifier Assign expression End #inferVarDeclaration
              | type=Identifier var=Identifier Assign expression End #typeVarDeclaration
              ;
expression: Number                                                             #numberLiteral
          | CharLiteral                                                        #charLiteral
          | BooleanLiteral                                                     #booleanLiteral
          | Identifier                                                         #readVariable
          | Identifier Deref                                                   #readMemory
          | func=Identifier ParenOpen functionCallParameters ParenClose        #functionCall
          |                 ParenOpen expression ParenClose                    #expressionInParenthesis
          | left=expression operator=(BitAnd|BitOr|BitXor) right=expression    #binaryExpressionBits
          | left=expression operator=(Multiply|ShiftL|ShiftR) right=expression #binaryExpressionPoint
          | left=expression operator=(Plus|Minus) right=expression             #binaryExpressionDash
          | left=expression operator=(Lt|Le|Eq|Ge|Gt|Ne) right=expression      #binaryExpressionBool
          | ParenOpen type=Identifier ParenClose expression                    #typeCast
          ;

functionCallParameters: expression?
                      | expression (Comma expression)+
                      ;

Comma : ',';
End   : ';';
Assign: '=' ;

Plus    : '+';
Minus   : '-';
Multiply: '*';
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

ParenOpen : '(';
ParenClose: ')';
CurlyOpen : '{';
CurlyClose: '}';

Deref: '[]';

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

fragment DecimalNumber
    : [0-9]+
    ;

Number: [-]? DecimalNumber ('_' [ui] ('8' | '16'))?;

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
