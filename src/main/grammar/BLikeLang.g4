grammar BLikeLang;

root: statements;

statements: statement* ;
statement: assignment     #statementAssign
         | varDeclaration #statementDeclaration
         ;
varDeclaration: type=Identifier var=Identifier Assign expression EOL;
assignment: var=Identifier Assign expression EOL;
expression: value=Number                                             #exprNumber
          | var=Identifier                                           #exprVar
          | left=expression operator=(Plus|Minus) right=expression   #exprBinary
          ;

Assign: '=' ;
Plus: '+';
Minus: '-';

EOL: NL | EOF;

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
	  | ';' ~[\r\n]*
	  ) NL?
	  -> skip
	;

BlockComment
	: '/*' .*? '*/' NL?
	  -> skip
	;
