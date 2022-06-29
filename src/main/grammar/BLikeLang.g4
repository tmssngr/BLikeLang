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

statements: (statement | NL)* ;
statement: varDeclaration                  #localVarDeclaration
         | assignment                      #assignStatement
         | CurlyOpen statements CurlyClose #blockStatement
         | Return expression? End          #returnStatement
         ;
varDeclaration: Var var=Identifier Assign expression End;
assignment    :     var=Identifier Assign expression End;
expression: value=Number                                                 #numberLiteral
          | var=Identifier                                               #readVariable
          | func=Identifier ParenOpen functionCallParameters ParenClose  #functionCall
          |                 ParenOpen expression ParenClose              #expressionInParenthesis
          | left=expression operator=Multiply     right=expression       #binaryExpressionPoint
          | left=expression operator=(Plus|Minus) right=expression       #binaryExpressionDash
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

ParenOpen : '(';
ParenClose: ')';
CurlyOpen : '{';
CurlyClose: '}';

Return: 'return';
Var: 'var';

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
