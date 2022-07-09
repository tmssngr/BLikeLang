grammar BLikeLang;

root: declarations;

declarations: (declaration | NL)* ;
declaration: varDeclaration      #globalVarDeclaration
           | functionDeclaration #funcDeclaration
           ;

functionDeclaration: type=Identifier name=Identifier ParenOpen parameterDeclarations ParenClose statement;
parameterDeclarations: parameterDeclaration?
                     | parameterDeclaration (Comma parameterDeclaration)+;
parameterDeclaration: type=Identifier name=Identifier;

statement: varDeclaration                         #localVarDeclaration
         | assignment                             #assignStatement
         | func=Identifier ParenOpen functionCallParameters ParenClose End  #callStatement
         | CurlyOpen (statement | NL)* CurlyClose #blockStatement
         | Return expression? End                 #returnStatement
         | If expression
             ifStatement=statement
           (Else
             elseStatement=statement)?            #ifStatement
         | While expression statement             #whileStatement
         ;
varDeclaration: Var             var=Identifier Assign expression End #inferVarDeclaration
              | type=Identifier var=Identifier Assign expression End #typeVarDeclaration
              ;
assignment    :     var=Identifier Assign expression End;
expression: value=Number                                                       #numberLiteral
          | value=BooleanLiteral                                               #booleanLiteral
          | var=Identifier                                                     #readVariable
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

Else: 'else';
If: 'if';
Return: 'return';
Var: 'var';
While: 'while';

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
	: '\r' '\n'?
	| '\n'
	;

LineComment
	: ('//' ~[\r\n]*
	  ) NL?
	  -> skip
	;

BlockComment
	: '/*' .*? '*/' NL?
	  -> skip
	;
