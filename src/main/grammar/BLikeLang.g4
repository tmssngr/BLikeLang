grammar BLikeLang;

root: statements;

statements: statement* ;
statement: assignment     #statementAssign
         | varDeclaration #statementDeclaration
         | NL             #statementEmpty
         ;
varDeclaration: type=Identifier var=Identifier Assign expression End;
assignment    :                 var=Identifier Assign expression End;
expression: value=Number                                             #exprNumber
          | var=Identifier                                           #exprVar
          | ParenOpen expression ParenClose                          #exprParen
          | left=expression operator=Multiply     right=expression   #exprMultiply
          | left=expression operator=(Plus|Minus) right=expression   #exprAddSub
          ;

End   : ';';
Assign: '=' ;

Plus    : '+';
Minus   : '-';
Multiply: '*';

ParenOpen : '(';
ParenClose: ')';

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
