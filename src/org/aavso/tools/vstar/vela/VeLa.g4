grammar VeLa;

// VeLa: VStar expression Language
//       -     -          -- 

// TODO:
// - regex: x =~ "..."; homage to Perl
// - add exponentiation (use associativity modifier)
// - set membership: x in [ ... ]; see filter from plot descriptions
// - math functions: by reflection from Math class or a targeted selection?
// - selection (e.g. in models): functional-style patterns instead of if-then
// - internal function representation in Models dialog should use VeLa:
//     t -> real-expression-over-t
//   | (boolean-expression : t -> real-expression-over-t ...)+

booleanExpression
:
	conjunctiveExpression
	(
		OR conjunctiveExpression
	)*
;

conjunctiveExpression
:
	logicalNegationExpression
	(
		AND logicalNegationExpression
	)*
;

logicalNegationExpression
:
	NOT? relationalExpression
;

relationalExpression
:
	groupedBooleanExpression
	(
		(
			EQUAL
			| NOT_EQUAL
			| GREATER_THAN
			| LESS_THAN
			| GREATER_THAN_OR_EQUAL
			| LESS_THAN_OR_EQUAL
			| APPROXIMATELY_EQUAL
		) groupedBooleanExpression
	)*
;

groupedBooleanExpression
:
	LPAREN booleanExpression RPAREN
	| expression
;

expression
:
	realExpression
	| stringExpression
;

realExpression
:
	multiplicativeExpression
	(
		(
			PLUS
			| MINUS
		) multiplicativeExpression
	)*
;

multiplicativeExpression
:
	unaryExpression
	(
		(
			MULT
			| DIV
		) unaryExpression
	)*
;

unaryExpression
:
	sign? numericFactor
;

numericFactor
:
	LPAREN realExpression RPAREN
	| real
	| var
	| func
;

stringExpression
:
	stringFactor
	(
		PLUS stringFactor
	)*
;

stringFactor
:
	string
	| var
	| func
;

var
:
	IDENT
;

func
:
	//IDENT LPAREN RPAREN | 
	IDENT LPAREN booleanExpression
	(
		COMMA booleanExpression
	)* RPAREN
;

// TODO: This rule needs to move to the lexer because it allows whitespace and 
// there's also a conflict with comma here and in the real number rule (may be 
// able to handle this via a non-greedy directive); hmm... whitespace in numbers
// may be a nice side effect!

real
:
	DIGIT+
	(
		POINT DIGIT+
	)?
	(
		exponentIndicator MINUS? DIGIT+
	)?
	| POINT DIGIT+?
	(
		exponentIndicator MINUS? DIGIT+
	)?
;

// TODO: should be in lexer; this avoids [Ee] being treated as a subset of 
// LETTER, such that it would never be matched; unsatisfying! make a fragment

exponentIndicator
:
	'E'
	| 'e'
;

// TODO: use body instead of sign above or make a lexer rule

sign
:
	MINUS
	| PLUS
;

string
:
	STRING
;

MINUS
:
	'-'
;

PLUS
:
	'+'
;

MULT
:
	'*'
;

DIV
:
	'/'
;

EQUAL
:
	'='
;

NOT_EQUAL
:
	'<>'
;

GREATER_THAN
:
	'>'
;

LESS_THAN
:
	'<'
;

GREATER_THAN_OR_EQUAL
:
	'>='
;

LESS_THAN_OR_EQUAL
:
	'<='
;

APPROXIMATELY_EQUAL
:
	'=~'
;

LPAREN
:
	'('
;

RPAREN
:
	')'
;

POINT
// Locale-inclusive

:
	'.'
	| ','
;

COMMA
:
	','
;

AND
:
	'AND'
	| 'and'
;

OR
:
	'OR'
	| 'or'
;

NOT
:
	'NOT'
	| 'not'
;

DIGIT
:
	[0-9]
;

fragment LETTER
:
	[A-Z]
	| [a-z]
;

UNDERSCORE
:
	'_'
;

IDENT
:
	(
		LETTER
		| UNDERSCORE
	)
	(
		LETTER
		| DIGIT
		| UNDERSCORE
	)*
;

STRING
:
	'"'
	(
		~'"'
	)* '"'
;

WS
:
	[ \r\t\n]+ -> skip
;