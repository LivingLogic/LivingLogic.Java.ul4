/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

grammar UL4;

options
{
	language=Java;
	backtrack=true;
	TokenLabelType=CommonToken;
}

@header
{
	package com.livinglogic.ul4;

	import java.util.Date;

	import com.livinglogic.ul4.Utils;
	import com.livinglogic.ul4.Color;
}

@lexer::header
{
	package com.livinglogic.ul4;
}


@lexer::members
{
	private Tag tag;

	public UL4Lexer(Tag tag, CharStream input)
	{
		this(input);
		this.tag = tag;
	}

	@Override
	public void displayRecognitionError(String[] tokenNames, RecognitionException e)
	{
		String message = getErrorMessage(e, tokenNames) + " (at index " + e.index + ")";
		throw new SyntaxException(message, e);
	}
}

@parser::members
{
	private Tag tag;

	public UL4Parser(Tag tag, TokenStream input)
	{
		this(input);
		this.tag = tag;
	}

	@Override
	public void displayRecognitionError(String[] tokenNames, RecognitionException e)
	{
		String message = getErrorMessage(e, tokenNames) + " (at index " + e.index + ")";
		throw new SyntaxException(message, e);
	}

	public Slice getPos(CommonToken token)
	{
		int start = tag.getCodePos().getStart();
		return new Slice(start + token.getStartIndex(), start + token.getStopIndex() + 1);
	}

	public int getStartPos(CommonToken token)
	{
		return tag.getCodePos().getStart() + token.getStartIndex();
	}

	public int getStopPos(CommonToken token)
	{
		return tag.getCodePos().getStart() + token.getStopIndex() + 1;
	}
}

NONE
	: 'None'
	;

TRUE
	: 'True'
	;

FALSE
	: 'False'
	;

NAME
	: ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
	;

fragment
DIGIT
	: '0'..'9'
	;

fragment
BIN_DIGIT
	: ('0'|'1')
	;

fragment
OCT_DIGIT
	: '0'..'7'
	;

fragment
HEX_DIGIT
	: ('0'..'9'|'a'..'f'|'A'..'F')
	;

/* We don't have negative ints (as this would tokenize "1-2" wrong) */
INT
	: DIGIT+
	| '0' ('b'|'B') BIN_DIGIT+
	| '0' ('o'|'O') OCT_DIGIT+
	| '0' ('x'|'X') HEX_DIGIT+
	;

fragment
EXPONENT
	: ('e'|'E') ('+'|'-')? DIGIT+
	;

FLOAT
	: DIGIT+ '.' DIGIT* EXPONENT?
	| '.' DIGIT+ EXPONENT?
	| DIGIT+ EXPONENT
	;

fragment
TIME
	: DIGIT DIGIT ':' DIGIT DIGIT ( ':' DIGIT DIGIT ( '.' DIGIT DIGIT DIGIT DIGIT DIGIT DIGIT)?)?;

DATE
	: '@' '(' DIGIT DIGIT DIGIT DIGIT '-' DIGIT DIGIT '-' DIGIT DIGIT ')';

DATETIME
	: '@' '(' DIGIT DIGIT DIGIT DIGIT '-' DIGIT DIGIT '-' DIGIT DIGIT 'T' TIME? ')';

COLOR
	: '#' HEX_DIGIT HEX_DIGIT HEX_DIGIT
	| '#' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
	| '#' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
	| '#' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
	;

WS
	: (' '|'\t'|'\r'|'\n') { $channel=HIDDEN; }
	;

STRING
	: '"' ( ESC_SEQ | ~('\\'|'"'|'\r'|'\n') )* '"'
	| '\'' ( ESC_SEQ | ~('\\'|'\''|'\r'|'\n') )* '\''
	;

STRING3
	: '"""' (options {greedy=false;}:TRIQUOTE)* '"""'
	|  '\'\'\'' (options {greedy=false;}:TRIAPOS)* '\'\'\''
	;

fragment
TRIQUOTE
	: ('"'|'""')? (ESC_SEQ|~('\\'|'"'))+
	;

fragment
TRIAPOS
	: ('\''|'\'\'')? (ESC_SEQ|~('\\'|'\''))+
	;

fragment
ESC_SEQ
	: '\\' ('a'|'b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
	| UNICODE1_ESC
	| UNICODE2_ESC
	| UNICODE4_ESC
	;

fragment
UNICODE1_ESC
	: '\\' 'x' HEX_DIGIT HEX_DIGIT
	;

fragment
UNICODE2_ESC
	: '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
	;

fragment
UNICODE4_ESC
	: '\\' 'U' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
	;


/* Rules common to all tags */

none returns [ConstAST node]
	: NONE { $node = new ConstAST(tag.getTemplate(), getPos($NONE), null); }
	;

true_ returns [ConstAST node]
	: TRUE { $node = new ConstAST(tag.getTemplate(), getPos($TRUE), true); }
	;

false_ returns [ConstAST node]
	: FALSE { $node = new ConstAST(tag.getTemplate(), getPos($FALSE), false); }
	;

int_ returns [ConstAST node]
	: INT { $node = new ConstAST(tag.getTemplate(), getPos($INT), Utils.parseUL4Int($INT.text)); }
	;

float_ returns [ConstAST node]
	: FLOAT { $node = new ConstAST(tag.getTemplate(), getPos($FLOAT), Double.parseDouble($FLOAT.text)); }
	;

string returns [ConstAST node]
	: STRING { $node = new ConstAST(tag.getTemplate(), getPos($STRING), Utils.unescapeUL4String($STRING.text.substring(1, $STRING.text.length()-1))); }
	| STRING3 { $node = new ConstAST(tag.getTemplate(), getPos($STRING3), Utils.unescapeUL4String($STRING3.text.substring(3, $STRING3.text.length()-3))); }
	;

date returns [ConstAST node]
	: DATE { $node = new ConstAST(tag.getTemplate(), getPos($DATE), Utils.isoParseDate($DATE.text.substring(2, $DATE.text.length()-1))); }
	;

datetime returns [ConstAST node]
	: DATETIME { $node = new ConstAST(tag.getTemplate(), getPos($DATETIME), Utils.isoParseDateTime($DATETIME.text.substring(2, $DATETIME.text.length()-1))); }
	;

color returns [ConstAST node]
	: COLOR { $node = new ConstAST(tag.getTemplate(), getPos($COLOR), Color.fromrepr($COLOR.text)); }
	;

name returns [VarAST node]
	: NAME { $node = new VarAST(tag.getTemplate(), getPos($NAME), $NAME.text); }
	;

literal returns [CodeAST node]
	: e_none=none { $node = $e_none.node; }
	| e_false=false_ { $node = $e_false.node; }
	| e_true=true_ { $node = $e_true.node; }
	| e_int=int_ { $node = $e_int.node; }
	| e_float=float_ { $node = $e_float.node; }
	| e_string=string { $node = $e_string.node; }
	| e_date=date { $node = $e_date.node; }
	| e_datetime=datetime { $node = $e_datetime.node; }
	| e_color=color { $node = $e_color.node; }
	| e_name=name { $node = $e_name.node; }
	;

/* List literals */
fragment
seqitem returns [SeqItemASTBase node]
	:
		e=expr_if { $node = new SeqItemAST(tag.getTemplate(), $e.node.getStartPos().withStop($e.node.getStartPos().getStop()), $e.node); }
	|
		star='*'
		es=expr_if { $node = new UnpackSeqItemAST(tag.getTemplate(), new Slice(getStartPos($star), $es.node.getStartPos().getStop()), $es.node); }
	;

list returns [ListAST node]
	:
		open='['
		close=']' { $node = new ListAST(tag.getTemplate(), new Slice(getStartPos($open), getStopPos($close))); }
	|
		open='[' {$node = new ListAST(tag.getTemplate(), new Slice(getStartPos($open))); }
		e1=seqitem { $node.append($e1.node); }
		(
			','
			e2=seqitem { $node.append($e2.node); }
		)*
		','?
		close=']' { $node.setStartPosStop(getStopPos($close)); }
	;

listcomprehension returns [ListComprehensionAST node]
	@init
	{
		CodeAST _condition = null;
	}
	:
		open='['
		item=expr_if
		'for'
		n=nestedlvalue
		'in'
		container=expr_if
		(
			'if'
			condition=expr_if { _condition = $condition.node; }
		)?
		close=']' { $node = new ListComprehensionAST(tag.getTemplate(), new Slice(getStartPos($open), getStopPos($close)), $item.node, $n.lvalue, $container.node, _condition); }
	;

/* Set literals */
set returns [SetAST node]
	:
		open='{'
		'/'
		close='}' { $node = new SetAST(tag.getTemplate(), new Slice(getStartPos($open), getStopPos($close))); }
	|
		open='{' {$node = new SetAST(tag.getTemplate(), new Slice(getStartPos($open))); }
		e1=seqitem { $node.append($e1.node); }
		(
			','
			e2=seqitem { $node.append($e2.node); }
		)*
		','?
		close='}' { $node.setStartPosStop(getStopPos($close)); }
	;

setcomprehension returns [SetComprehensionAST node]
	@init
	{
		CodeAST _condition = null;
	}
	:
		open='{'
		item=expr_if
		'for'
		n=nestedlvalue
		'in'
		container=expr_if
		(
			'if'
			condition=expr_if { _condition = $condition.node; }
		)?
		close='}' { $node = new SetComprehensionAST(tag.getTemplate(), new Slice(getStartPos($open), getStopPos($close)), $item.node, $n.lvalue, $container.node, _condition); }
	;

/* Dict literal */
fragment
dictitem returns [DictItemASTBase node]
	:
		k=expr_if
		':'
		v=expr_if { $node = new DictItemAST(tag.getTemplate(), new Slice($k.node.getStartPos().getStart(), $v.node.getStartPos().getStop()), $k.node, $v.node); }
	|
		star='**'
		e=expr_if { $node = new UnpackDictItemAST(tag.getTemplate(), new Slice(getStartPos($star), $e.node.getStartPos().getStop()), $e.node); }
	;

dict returns [DictAST node]
	:
		open='{'
		close='}' { $node = new DictAST(tag.getTemplate(), new Slice(getStartPos($open), getStopPos($close))); }
	|
		open='{' { $node = new DictAST(tag.getTemplate(), new Slice(getStartPos($open))); }
		i1=dictitem { $node.append($i1.node); }
		(
			','
			i2=dictitem { $node.append($i2.node); }
		)*
		','?
		close='}' { $node.setStartPosStop(getStopPos($close)); }
	;

dictcomprehension returns [DictComprehensionAST node]
	@init
	{
		CodeAST _condition = null;
	}
	:
		open='{'
		key=expr_if
		':'
		value=expr_if
		'for'
		n=nestedlvalue
		'in'
		container=expr_if
		(
			'if'
			condition=expr_if { _condition = $condition.node; }
		)?
		close='}' { $node = new DictComprehensionAST(tag.getTemplate(), new Slice(getStartPos($open), getStopPos($close)), $key.node, $value.node, $n.lvalue, $container.node, _condition); }
	;

generatorexpression returns [GeneratorExpressionAST node]
	@init
	{
		CodeAST _condition = null;
		int _end = -1;
	}
	:
		item=expr_if
		'for'
		n=nestedlvalue
		'in'
		container=expr_if { _end = $container.node.getStartPos().getStop(); }
		(
			'if'
			condition=expr_if { _condition = $condition.node; _end = $condition.node.getStartPos().getStop(); }
		)? { $node = new GeneratorExpressionAST(tag.getTemplate(), new Slice($item.node.getStartPos().getStart(), _end), $item.node, $n.lvalue, $container.node, _condition); }
	;

atom returns [CodeAST node]
	: e_literal=literal { $node = $e_literal.node; }
	| e_list=list { $node = $e_list.node; }
	| e_listcomp=listcomprehension { $node = $e_listcomp.node; }
	| e_set=set { $node = $e_set.node; }
	| e_setcomp=setcomprehension { $node = $e_setcomp.node; }
	| e_dict=dict { $node = $e_dict.node; }
	| e_dictcomp=dictcomprehension { $node = $e_dictcomp.node; }
	| open='(' e_genexpr=generatorexpression close=')' { $node = $e_genexpr.node; $node.setStartPosStart(getStartPos($open)); $node.setStartPosStop(getStopPos($close)); }
	| open='(' e_bracket=expr_if close=')' { $node = $e_bracket.node; $node.setStartPosStart(getStartPos($open)); $node.setStartPosStop(getStopPos($close)); }
	;

/* For variable unpacking in assignments and for loops */
nestedlvalue returns [Object lvalue]
	:
		n=expr_subscript { $lvalue = $n.node; }
	|
		'(' n0=nestedlvalue ',' ')' { $lvalue = java.util.Arrays.asList($n0.lvalue); }
	|
		'('
		n1=nestedlvalue
		','
		n2=nestedlvalue { $lvalue = new ArrayList(2); ((ArrayList)$lvalue).add($n1.lvalue); ((ArrayList)$lvalue).add($n2.lvalue); }
		(
			','
			n3=nestedlvalue { ((ArrayList)$lvalue).add($n3.lvalue); }
		)*
		','?
		')'
	;


/* Slice expression */
slice returns [SliceAST node]
	@init
	{
		CodeAST startIndex = null;
		CodeAST stopIndex = null;
		int startPos = -1;
		int endPos = -1;
	}
	:
		(
			e1=expr_if { startIndex = $e1.node; startPos = $e1.node.getStartPos().getStart(); }
		)?
		colon=':' {
			if (startPos == -1)
				startPos = getStartPos($colon);
			endPos = getStopPos($colon);
		}
		(
			e2=expr_if { stopIndex = $e2.node; endPos = $e2.node.getStartPos().getStop(); }
		)? { $node = new SliceAST(tag.getTemplate(), new Slice(startPos, endPos), startIndex, stopIndex); }
	;


/* Function/method call, attribute access, item access, slice access */
fragment
argument returns [ArgumentASTBase node]
	:
		e=exprarg { $node = new PositionalArgumentAST(tag.getTemplate(), $e.node.getStartPos(), $e.node); }
	|
		en=name '=' ev=exprarg { $node = new KeywordArgumentAST(tag.getTemplate(), new Slice($en.node.getStartPos().getStart(), $ev.node.getStartPos().getStop()), $en.text, $ev.node); }
	|
		star='*'
		es=exprarg { $node = new UnpackListArgumentAST(tag.getTemplate(), new Slice(getStartPos($star), $es.node.getStartPos().getStop()), $es.node); }
	|
		star='**'
		ess=exprarg { $node = new UnpackDictArgumentAST(tag.getTemplate(), new Slice(getStartPos($star), $ess.node.getStartPos().getStop()), $ess.node); }
	;

expr_subscript returns [CodeAST node]
	:
		e1=atom { $node = $e1.node; }
		(
			/* Attribute access */
			'.'
			n=name { $node = new AttrAST(tag.getTemplate(), new Slice($e1.node.getStartPos().getStart(), $n.node.getStartPos().getStop()), $node, $n.text); }
		|
			/* Function/method call */
			'(' { $node = new CallAST(tag.getTemplate(), new Slice($e1.node.getStartPos().getStart()), $node); }
			(
				a1=argument { $a1.node.addToCall((CallAST)$node); }
				(
					','
					a2=argument { $a2.node.addToCall((CallAST)$node); }
				)*
				','?
			)*
			close=')' { $node.setStartPosStop(getStopPos($close)); }
		|
			/* Item access */
			'['
			e2_if=expr_if
			close=']' { $node = new ItemAST(tag.getTemplate(), new Slice($e1.node.getStartPos().getStart(), getStopPos($close)), $node, $e2_if.node); }
		|
			/* Slice access */
			'['
			e2_slice=slice
			close=']' { $node = new ItemAST(tag.getTemplate(), new Slice($e1.node.getStartPos().getStart(), getStopPos($close)), $node, $e2_slice.node); }
		)*
	;

/* Negation/bitwise not */
expr_unary returns [CodeAST node]
	:
		e1=expr_subscript { $node = $e1.node; }
	|
		minus='-' e2=expr_unary { $node = new NegAST(tag.getTemplate(), new Slice(getStartPos($minus), $e2.node.getStartPos().getStop()), $e2.node); }
	|
		bitnot='~' e2=expr_unary { $node = new BitNotAST(tag.getTemplate(), new Slice(getStartPos($bitnot), $e2.node.getStartPos().getStop()), $e2.node); }
	;

/* Multiplication, division, modulo */
expr_mul returns [CodeAST node]
	@init
	{
		int opcode = -1;
		Slice pos = null;
	}
	:
		e1=expr_unary { $node = $e1.node; }
		(
			(
				'*' { opcode = 0; }
			|
				'/' { opcode = 1; }
			|
				'//' { opcode = 2; }
			|
				'%' { opcode = 3; }
			)
			e2=expr_unary {
				pos = new Slice($node.getStartPos().getStart(), $e2.node.getStartPos().getStop());
				switch (opcode)
				{
					case 0:
						$node = new MulAST(tag.getTemplate(), pos, $node, $e2.node);
						break;
					case 1:
						$node = new TrueDivAST(tag.getTemplate(), pos, $node, $e2.node);
						break;
					case 2:
						$node = new FloorDivAST(tag.getTemplate(), pos, $node, $e2.node);
						break;
					case 3:
						$node = new ModAST(tag.getTemplate(), pos, $node, $e2.node);
						break;
				}
			}
		)*
	;

/* Addition, substraction */
expr_add returns [CodeAST node]
	@init
	{
		boolean add = false;
		Slice pos = null;
	}
	:
		e1=expr_mul { $node = $e1.node; }
		(
			(
				'+' { add = true; }
			|
				'-' { add = false; }
			)
			e2=expr_mul {
				pos = new Slice($node.getStartPos().getStart(), $e2.node.getStartPos().getStop());
				$node = add ? new AddAST(tag.getTemplate(), pos, $node, $e2.node) : new SubAST(tag.getTemplate(), pos, $node, $e2.node);
			}
		)*
	;

/* Binary shift */
expr_bitshift returns [CodeAST node]
	@init
	{
		boolean left = false;
		Slice pos = null;
	}
	:
		e1=expr_add { $node = $e1.node; }
		(
			(
				'<<' { left = true; }
			|
				'>>' { left = false; }
			)
			e2=expr_add {
				pos = new Slice($node.getStartPos().getStart(), $e2.node.getStartPos().getStop());
				$node = left ? new ShiftLeftAST(tag.getTemplate(), pos, $node, $e2.node) : new ShiftRightAST(tag.getTemplate(), pos, $node, $e2.node); }
		)*
	;

/* Bitwise and */
expr_bitand returns [CodeAST node]
	:
		e1=expr_bitshift { $node = $e1.node; }
		(
			'&'
			e2=expr_bitshift { $node = new BitAndAST(tag.getTemplate(), new Slice($node.getStartPos().getStart(), $e2.node.getStartPos().getStop()), $node, $e2.node); }
		)*
	;

/* Bitwise exclusive or */
expr_bitxor returns [CodeAST node]
	:
		e1=expr_bitand { $node = $e1.node; }
		(
			'^'
			e2=expr_bitand { $node = new BitXOrAST(tag.getTemplate(), new Slice($node.getStartPos().getStart(), $e2.node.getStartPos().getStop()), $node, $e2.node); }
		)*
	;

/* Bitwise or */
expr_bitor returns [CodeAST node]
	:
		e1=expr_bitxor { $node = $e1.node; }
		(
			'|'
			e2=expr_bitxor { $node = new BitOrAST(tag.getTemplate(), new Slice($node.getStartPos().getStart(), $e2.node.getStartPos().getStop()), $node, $e2.node); }
		)*
	;

/* Comparisons */
expr_cmp returns [CodeAST node]
	@init
	{
		int opcode = -1;
		Slice pos = null;
	}
	:
		e1=expr_bitor { $node = $e1.node; }
		(
			(
				'==' { opcode = 0; }
			|
				'!=' { opcode = 1; }
			|
				'<' { opcode = 2; }
			|
				'<=' { opcode = 3; }
			|
				'>' { opcode = 4; }
			|
				'>=' { opcode = 5; }
			|
				'in' { opcode = 6; }
			|
				'not' 'in' { opcode = 7; }
			|
				'is' { opcode = 8; }
			|
				'is' 'not' { opcode = 9; }
			)
			e2=expr_bitor {
				pos = new Slice($node.getStartPos().getStart(), $e2.node.getStartPos().getStop());
				switch (opcode)
					{
						case 0:
							$node = new EQAST(tag.getTemplate(), pos, $node, $e2.node);
							break;
						case 1:
							$node = new NEAST(tag.getTemplate(), pos, $node, $e2.node);
							break;
						case 2:
							$node = new LTAST(tag.getTemplate(), pos, $node, $e2.node);
							break;
						case 3:
							$node = new LEAST(tag.getTemplate(), pos, $node, $e2.node);
							break;
						case 4:
							$node = new GTAST(tag.getTemplate(), pos, $node, $e2.node);
							break;
						case 5:
							$node = new GEAST(tag.getTemplate(), pos, $node, $e2.node);
							break;
						case 6:
							$node = new ContainsAST(tag.getTemplate(), pos, $node, $e2.node);
							break;
						case 7:
							$node = new NotContainsAST(tag.getTemplate(), pos, $node, $e2.node);
							break;
						case 8:
							$node = new IsAST(tag.getTemplate(), pos, $node, $e2.node);
							break;
						case 9:
							$node = new IsNotAST(tag.getTemplate(), pos, $node, $e2.node);
							break;
					}
			}
		)*
	;

/* Boolean not operator */
expr_not returns [CodeAST node]
	:
		e1=expr_cmp { $node = $e1.node; }
	|
		n='not' e2=expr_not { $node = new NotAST(tag.getTemplate(), new Slice(getStartPos($n), $e2.node.getStartPos().getStop()), $e2.node); }
	;

/* And operator */
expr_and returns [CodeAST node]
	:
		e1=expr_not { $node = $e1.node; }
		(
			'and'
			e2=expr_not { $node = new AndAST(tag.getTemplate(), new Slice($node.getStartPos().getStart(), $e2.node.getStartPos().getStop()), $node, $e2.node); }
		)*
	;

/* Or operator */
expr_or returns [CodeAST node]
	:
		e1=expr_and { $node = $e1.node; }
		(
			'or'
			e2=expr_and { $node = new OrAST(tag.getTemplate(), new Slice($node.getStartPos().getStart(), $e2.node.getStartPos().getStop()), $node, $e2.node); }
		)*
	;

/* If expression operator */
expr_if returns [CodeAST node]
	:
		e1=expr_or { $node = $e1.node; }
		(
			'if'
			e2=expr_or
			'else'
			e3=expr_or { $node = new IfAST(tag.getTemplate(), new Slice($e1.node.getStartPos().getStart(), $e3.node.getStartPos().getStop()), $e1.node, $e2.node, $e3.node); }
		)?
	;

exprarg returns [CodeAST node]
	: ege=generatorexpression { $node = $ege.node; }
	| e1=expr_if { $node = $e1.node; }
	;

expression returns [CodeAST node]
	: ege=generatorexpression EOF { $node = $ege.node; }
	| e=expr_if EOF { $node = $e.node; }
	;


/* Additional rules for "for" tag */

for_ returns [ForBlockAST node]
	:
		n=nestedlvalue
		'in'
		e=expr_if { $node = new ForBlockAST(tag.getTemplate(), tag.getStartPos(), null, $n.lvalue, $e.node); }
		EOF
	;


/* Additional rules for "code" tag */

stmt returns [CodeAST node]
	: nn=nestedlvalue '=' e=expr_if EOF { $node = new SetVarAST(tag.getTemplate(), tag.getCodePos(), $nn.lvalue, $e.node); }
	| n=expr_subscript '+=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new AddVarAST(tag.getTemplate(), tag.getCodePos(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| n=expr_subscript '-=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new SubVarAST(tag.getTemplate(), tag.getCodePos(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| n=expr_subscript '*=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new MulVarAST(tag.getTemplate(), tag.getCodePos(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| n=expr_subscript '//=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new FloorDivVarAST(tag.getTemplate(), tag.getCodePos(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| n=expr_subscript '/=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new TrueDivVarAST(tag.getTemplate(), tag.getCodePos(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| n=expr_subscript '%=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new ModVarAST(tag.getTemplate(), tag.getCodePos(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| n=expr_subscript '<<=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new ShiftLeftVarAST(tag.getTemplate(), tag.getCodePos(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| n=expr_subscript '>>=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new ShiftRightVarAST(tag.getTemplate(), tag.getCodePos(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| n=expr_subscript '&=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new BitAndVarAST(tag.getTemplate(), tag.getCodePos(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| n=expr_subscript '^=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new BitXOrVarAST(tag.getTemplate(), tag.getCodePos(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| n=expr_subscript '|=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new BitOrVarAST(tag.getTemplate(), tag.getCodePos(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| e=expression EOF { $node = $e.node; }
	;


/* Used for parsing signatures */
signature returns [SignatureAST node]
	:
	open='(' { $node = new SignatureAST(tag.getTemplate(), new Slice(getStartPos($open))); }
	(
		/* No paramteers */
	|
		/* "**" parameter only */
		'**' rkwargsname=name { $node.add($rkwargsname.text, ParameterDescription.Type.VAR_KEYWORD, null); }
		','?
	|
		/* "*" parameter only (and maybe **) */
		'*' rargsname=name { $node.add($rargsname.text, ParameterDescription.Type.VAR_POSITIONAL, null); }
		(
			','
			'**' rkwargsname=name { $node.add($rkwargsname.text, ParameterDescription.Type.VAR_KEYWORD, null); }
		)?
		','?
	|
		/* All parameters have a default */
		aname1=name
		'='
		adefault1=exprarg { $node.add($aname1.text, ParameterDescription.Type.POSITIONAL_OR_KEYWORD_DEFAULT, $adefault1.node); }
		(
			','
			aname2=name
			'='
			adefault2=exprarg { $node.add($aname2.text, ParameterDescription.Type.POSITIONAL_OR_KEYWORD_DEFAULT, $adefault2.node); }
		)*
		(
			','
			'*' rargsname=name { $node.add($rargsname.text, ParameterDescription.Type.VAR_POSITIONAL, null); }
		)?
		(
			','
			'**' rkwargsname=name { $node.add($rkwargsname.text, ParameterDescription.Type.VAR_KEYWORD, null); }
		)?
		','?
	|
		/* At least one parameter without a default */
		aname1=name { $node.add($aname1.text, ParameterDescription.Type.POSITIONAL_OR_KEYWORD_REQUIRED, null); }
		(
			','
			aname2=name { $node.add($aname2.text, ParameterDescription.Type.POSITIONAL_OR_KEYWORD_REQUIRED, null); }
		)*
		(
			','
			aname3=name
			'='
			adefault3=exprarg { $node.add($aname3.text, ParameterDescription.Type.POSITIONAL_OR_KEYWORD_DEFAULT, $adefault3.node); }
		)*
		(
			','
			'*' rargsname=name { $node.add($rargsname.text, ParameterDescription.Type.VAR_POSITIONAL, null); }
		)?
		(
			','
			'**' rkwargsname=name { $node.add($rkwargsname.text, ParameterDescription.Type.VAR_KEYWORD, null); }
		)?
		','?
	)
	close=')' { $node.setStartPosStop(getStopPos($close)); }
;


/* Additional rules for "def" tag */

definition returns [Definition node]
	:
		{
			$node = new Definition(null, null);
		}
		(
			n=name { $node.setName($n.text); }
		)?
		(
			sig=signature { $node.setSignature($sig.node); }
		)?
		EOF
	;
