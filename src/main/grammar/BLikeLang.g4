grammar BLikeLang;

root: statements;

statements: (statement | NL)* ;
statement: assignment                      #assignStatement
         | varDeclaration                  #variableDeclaration
         | CurlyOpen statements CurlyClose #blockStatement
         ;
varDeclaration: type=Identifier var=Identifier Assign expression End;
assignment    :                 var=Identifier Assign expression End;
expression: value=Number                                             #numberLiteral
          | var=Identifier                                           #readVariable
          | func=Identifier ParenOpen parameters ParenClose          #functionCall
          |                 ParenOpen expression ParenClose          #expressionInParenthesis
          | left=expression operator=Multiply     right=expression   #binaryExpressionPoint
          | left=expression operator=(Plus|Minus) right=expression   #binaryExpressionDash
          ;

parameters: expression?
          | expression ( Comma expression )+
          ;

Comma : ',';
End   : ';';
Assign: '=' ;

Plus    : '+';
Minus   : '-';
Multiply: '*';

ParenOpen : '(';
ParenClose: ')';
CurlyOpen : '{';
CurlyClose: '}';

fragment DecimalNumber
    : [0-9]+
    ;

Number: DecimalNumber ;

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
