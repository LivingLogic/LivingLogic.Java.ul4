# -*- coding: utf-8 -*-

## Copyright 2008-2012 by LivingLogic AG, Bayreuth/Germany
## Copyright 2008-2012 by Walter Dörwald
##
## All Rights Reserved
##
## See ll/__init__.py for the license


import sys, re, StringIO

import spark

from com.livinglogic import ul4

from java import lang

###
### helper functions for compiling
###

def _compile(template, tags):
	opcodes = []
	parseexpr = ExprParser().compile
	parsestmt = StmtParser().compile
	parsefor = ForParser().compile
	parserender = RenderParser().compile

	# This stack stores for each nested for/if/elif/else the following information:
	# 1) Which construct we're in (i.e. "if" or "for")
	# 2) The start location of the construct
	# For ifs:
	# 3) How many if's or elif's we have seen (this is used for simulating elif's via nested if's, for each additional elif, we have one more endif to add)
	# 4) Whether we've already seen the else
	stack = []
	for location in tags:
		try:
			if location.type is None:
				template.opcode(ul4.Opcode.OC_TEXT, location)
			elif location.type == "print":
				r = parseexpr(template, location)
				template.opcode(ul4.Opcode.OC_PRINT, r, location)
			elif location.type == "printx":
				r = parseexpr(template, location)
				template.opcode(ul4.Opcode.OC_PRINTX, r, location)
			elif location.type == "code":
				parsestmt(template, location)
			elif location.type == "if":
				r = parseexpr(template, location)
				template.opcode(ul4.Opcode.OC_IF, r, location)
				stack.append(("if", location, 1, False))
			elif location.type == "elif":
				if not stack or stack[-1][0] != "if":
					raise ul4.BlockException("elif doesn't match any if")
				elif stack[-1][3]:
					raise ul4.BlockException("else already seen in elif")
				template.opcode(ul4.Opcode.OC_ELSE, location)
				r = parseexpr(template, location)
				template.opcode(ul4.Opcode.OC_IF, r, location)
				stack[-1] = ("if", stack[-1][1], stack[-1][2]+1, False)
			elif location.type == "else":
				if not stack or stack[-1][0] != "if":
					raise ul4.BlockException("else doesn't match any if")
				elif stack[-1][3]:
					raise ul4.BlockException("duplicate else")
				template.opcode(ul4.Opcode.OC_ELSE, location)
				stack[-1] = ("if", stack[-1][1], stack[-1][2], True)
			elif location.type == "end":
				if not stack:
					raise ul4.BlockException("not in any block")
				code = location.code
				if code:
					if code == "if":
						if stack[-1][0] != "if":
							raise ul4.BlockException("endif doesn't match any if")
					elif code == "for":
						if stack[-1][0] != "for":
							raise ul4.BlockException("endfor doesn't match any for")
					elif code == "def":
						if stack[-1][0] != "def":
							raise ul4.BlockException("enddef doesn't match any def")
					else:
						raise ul4.BlockException("illegal end value %r" % code)
				last = stack.pop()
				if last[0] == "if":
					for i in xrange(last[2]):
						template.opcode(ul4.Opcode.OC_ENDIF, location)
				elif last[0] == "for":
					template.opcode(ul4.Opcode.OC_ENDFOR, location)
				else: # last[0] == "def":
					template.opcode(ul4.Opcode.OC_ENDDEF, location)
			elif location.type == "for":
				parsefor(template, location)
				stack.append(("for", location))
			elif location.type == "break":
				for entry in stack:
					if entry[0] == "for":
						break
				else:
					raise BlockException("break outside of for loop")
				template.opcode(ul4.Opcode.OC_BREAK, location)
			elif location.type == "continue":
				for entry in stack:
					if entry[0] == "for":
						break
				else:
					raise BlockException("continue outside of for loop")
				template.opcode(ul4.Opcode.OC_CONTINUE, location)
			elif location.type == "render":
				parserender(template, location)
			elif location.type == "def":
				template.opcode(ul4.Opcode.OC_DEF, location.code, location)
				stack.append(("def", location))
			else: # Can't happen
				raise ValueError("unknown tag %r" % location.type)
		except ul4.LocationException, exc:
			raise
		except lang.Exception, exc:
			raise ul4.LocationException(exc, location)
	if stack:
		raise ul4.LocationException(ul4.BlockException("block unclosed"), stack[-1][1])
	return opcodes


###
### Parsers for different types of code
###

class ExprParser(spark.GenericParser):
	emptyerror = "expression required"

	def __init__(self, start="expr0"):
		spark.GenericParser.__init__(self, start)

	def compile(self, template, location):
		if not location.code:
			raise ValueError(self.emptyerror)
		try:
			ast = self.parse(ul4.InterpretedTemplate.tokenizeCode(location))
			registers = ul4.Registers()
			return ast.compile(template, registers, location)
		except ul4.LocationException, exc:
			raise
		except lang.Exception, exc:
			raise ul4.LocationException(exc, location)

	def typestring(self, token):
		return token.getTokenType()

	def error(self, token):
		raise ul4.SyntaxException(token)

	def makeconst(self, value):
		if value is None:
			return ul4.LoadNone()
		elif value is True:
			return ul4.LoadTrue()
		elif value is False:
			return ul4.LoadFalse()
		elif isinstance(value, (int, long)):
			return ul4.LoadInt(value)
		elif isinstance(value, float):
			return ul4.LoadFloat(value)
		elif isinstance(value, basestring):
			return ul4.LoadStr(value)
		else:
			raise TypeError("can't convert %r" % value)

	# To implement operator precedence, each expression rule has the precedence in its name. The highest precedence is 11 for atomic expressions.
	# Each expression can have only expressions as parts, which have the some or a higher precedence with two exceptions:
	#    1) Expressions where there's no ambiguity, like the index for a getitem/getslice or function/method arguments;
	#    2) Brackets, which can be used to boost the precedence of an expression to the level of an atomic expression.

	def expr_atomic(self, (atom,)):
		return atom
	expr_atomic.spark = [
		'expr11 ::= none',
		'expr11 ::= true',
		'expr11 ::= false',
		'expr11 ::= str',
		'expr11 ::= int',
		'expr11 ::= float',
		'expr11 ::= date',
		'expr11 ::= color',
		'expr11 ::= name',
	]

	def expr_emptylist(self, (_0, _1)):
		return ul4.List()
	expr_emptylist.spark = ['expr11 ::= [ ]']

	def expr_buildlist(self, (_0, expr)):
		list = ul4.List()
		list.append(expr)
		return list
	expr_buildlist.spark = ['buildlist ::= [ expr0']

	def expr_addlist(self, (list, _0, expr)):
		list.append(expr)
		return list
	expr_addlist.spark = ['buildlist ::= buildlist , expr0']

	def expr_finishlist(self, (list, _0)):
		return list
	expr_finishlist.spark = ['expr11 ::= buildlist ]']

	def expr_finishlist1(self, (list, _0, _1)):
		return list
	expr_finishlist1.spark = ['expr11 ::= buildlist , ]']

	def expr_emptydict(self, (_0, _1)):
		return ul4.Dict()
	expr_emptydict.spark = ['expr11 ::= { }']

	def expr_builddict(self, (_0, key, _1, value)):
		dict = ul4.Dict()
		dict.append(key, value)
		return dict
	expr_builddict.spark = ['builddict ::= { expr0 : expr0']

	def expr_builddictupdate(self, (_0, _1, value)):
		dict = ul4.Dict()
		dict.append(value)
		return dict
	expr_builddictupdate.spark = ['builddict ::= { ** expr0']

	def expr_adddict(self, (dict, _0, key, _1, value)):
		dict.append(key, value)
		return dict
	expr_adddict.spark = ['builddict ::= builddict , expr0 : expr0']

	def expr_updatedict(self, (dict, _0, _1, value)):
		dict.append(value)
		return dict
	expr_updatedict.spark = ['builddict ::= builddict , ** expr0']

	def expr_finishdict(self, (dict, _0)):
		return dict
	expr_finishdict.spark = ['expr11 ::= builddict }']

	def expr_finishdict1(self, (dict, _0, _1)):
		return dict
	expr_finishdict1.spark = ['expr11 ::= builddict , }']

	def expr_bracket(self, (_0, expr, _1)):
		return expr
	expr_bracket.spark = ['expr11 ::= ( expr0 )']

	def expr_callfunc0(self, (name, _0, _1)):
		return ul4.CallFunc(name.value)
	expr_callfunc0.spark = ['expr10 ::= name ( )']

	def expr_callfunc1(self, (name, _0, arg0, _1)):
		func = ul4.CallFunc(name.value)
		func.append(arg0)
		return func
	expr_callfunc1.spark = ['expr10 ::= name ( expr0 )']

	def expr_callfunc2(self, (name, _0, arg0, _1, arg1, _2)):
		func = ul4.CallFunc(name.value)
		func.append(arg0)
		func.append(arg1)
		return func
	expr_callfunc2.spark = ['expr10 ::= name ( expr0 , expr0 )']

	def expr_callfunc3(self, (name, _0, arg0, _1, arg1, _2, arg2, _3)):
		func = ul4.CallFunc(name.value)
		func.append(arg0)
		func.append(arg1)
		func.append(arg2)
		return func
	expr_callfunc3.spark = ['expr10 ::= name ( expr0 , expr0 , expr0 )']

	def expr_callfunc4(self, (name, _0, arg0, _1, arg1, _2, arg2, _3, arg3, _4)):
		func = ul4.CallFunc(name.value)
		func.append(arg0)
		func.append(arg1)
		func.append(arg2)
		func.append(arg3)
		return func
	expr_callfunc4.spark = ['expr10 ::= name ( expr0 , expr0 , expr0 , expr0 )']

	def expr_getattr(self, (expr, _0, name)):
		return ul4.GetAttr(expr, name)
	expr_getattr.spark = ['expr9 ::= expr9 . name']

	def expr_callmeth0(self, (expr, _0, name, _1, _2)):
		return ul4.CallMeth(expr, name)
	expr_callmeth0.spark = ['expr9 ::= expr9 . name ( )']

	def expr_callmeth1(self, (expr, _0, name, _1, arg1, _2)):
		callmeth = ul4.CallMeth(expr, name)
		callmeth.append(arg1)
		return callmeth
	expr_callmeth1.spark = ['expr9 ::= expr9 . name ( expr0 )']

	def expr_callmeth2(self, (expr, _0, name, _1, arg1, _2, arg2, _3)):
		callmeth = ul4.CallMeth(expr, name)
		callmeth.append(arg1)
		callmeth.append(arg2)
		return callmeth
	expr_callmeth2.spark = ['expr9 ::= expr9 . name ( expr0 , expr0 )']

	def expr_callmeth3(self, (expr, _0, name, _1, arg1, _2, arg2, _3, arg3, _4)):
		callmeth = ul4.CallMeth(expr, name)
		callmeth.append(arg1)
		callmeth.append(arg2)
		callmeth.append(arg3)
		return callmeth
	expr_callmeth3.spark = ['expr9 ::= expr9 . name ( expr0 , expr0 , expr0 )']

	def methkw_startname(self, (expr, _0, methname, _1, argname, _2, argvalue)):
		call = ul4.CallMethKeywords(methname, expr)
		call.append(argname.value, argvalue)
		return call
	methkw_startname.spark = ['callmethkw ::= expr9 . name ( name = expr0']

	def methkw_startdict(self, (expr, _0, methname, _1, _2, argvalue)):
		call = ul4.CallMethKeywords(methname, expr)
		call.append(argvalue)
		return call
	methkw_startdict.spark = ['callmethkw ::= expr9 . name ( ** expr0']

	def methkw_buildname(self, (call, _0, argname, _1, argvalue)):
		call.args.append(argname.value, argvalue)
		return call
	methkw_buildname.spark = ['callmethkw ::= callmethkw , name = expr0']

	def methkw_builddict(self, (call, _0, _1, argvalue)):
		call.args.append(argvalue)
		return call
	methkw_builddict.spark = ['callmethkw ::= callmethkw , ** expr0']

	def methkw_finish(self, (call, _0)):
		return call
	methkw_finish.spark = ['expr9 ::= callmethkw )']

	def expr_getitem(self, (expr, _0, key, _1)):
		if isinstance(expr, ul4.LoadConst) and isinstance(key, ul4.LoadConst): # Constant folding
			return self.makeconst(ul4.Utils.getItem(expr.value, key.value))
		return ul4.GetItem(expr, key)
	expr_getitem.spark = ['expr9 ::= expr9 [ expr0 ]']

	def expr_getslice12(self, (expr, _0, index1, _1, index2, _2)):
		if isinstance(expr, ul4.LoadConst) and isinstance(index1, ul4.LoadConst) and isinstance(index2, ul4.LoadConst): # Constant folding
			return self.makeconst(ul4.Utils.getSlice(expr.value, index1.value, index2.value))
		return ul4.GetSlice(expr, index1, index2)
	expr_getslice12.spark = ['expr8 ::= expr8 [ expr0 : expr0 ]']

	def expr_getslice1(self, (expr, _0, index1, _1, _2)):
		if isinstance(expr, ul4.LoadConst) and isinstance(index1, ul4.LoadConst): # Constant folding
			return self.makeconst(ul4.Utils.getSlice(expr.value, index1.value, None))
		return ul4.GetSlice(expr, index1, None)
	expr_getslice1.spark = ['expr8 ::= expr8 [ expr0 : ]']

	def expr_getslice2(self, (expr, _0, _1, index2, _2)):
		if isinstance(expr, ul4.LoadConst) and isinstance(index2, ul4.LoadConst): # Constant folding
			return self.makeconst(ul4.Utils.getSlice(expr.value, None, index2.value))
		return ul4.GetSlice(expr, None, index2)
	expr_getslice2.spark = ['expr8 ::= expr8 [ : expr0 ]']

	def expr_getslice(self, (expr, _0, _1, _2)):
		if isinstance(expr, ul4.LoadConst): # Constant folding
			return self.makeconst(ul4.Utils.getSlice(expr.value, None, None))
		return ul4.GetSlice(expr, None, None)
	expr_getslice2.spark = ['expr8 ::= expr8 [ : ]']

	def expr_neg(self, (_0, expr)):
		if isinstance(expr, ul4.LoadConst): # Constant folding
			return self.makeconst(ul4.Utils.neg(expr.value))
		return ul4.Unary(ul4.Opcode.OC_NEG, expr)
	expr_neg.spark = ['expr7 ::= - expr7']

	def expr_mul(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(ul4.Utils.mul(obj1.value, obj2.value))
		return ul4.Binary(ul4.Opcode.OC_MUL, obj1, obj2)
	expr_mul.spark = ['expr6 ::= expr6 * expr6']

	def expr_floordiv(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(ul4.Utils.floordiv(obj1.value, obj2.value))
		return ul4.Binary(ul4.Opcode.OC_FLOORDIV, obj1, obj2)
	expr_floordiv.spark = ['expr6 ::= expr6 // expr6']

	def expr_truediv(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(ul4.Utils.truediv(obj1.value, obj2.value))
		return ul4.Binary(ul4.Opcode.OC_TRUEDIV, obj1, obj2)
	expr_truediv.spark = ['expr6 ::= expr6 / expr6']

	def expr_mod(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(ul4.Utils.mod(obj1.value, obj2.value))
		return ul4.Binary(ul4.Opcode.OC_MOD, obj1, obj2)
	expr_mod.spark = ['expr6 ::= expr6 % expr6']

	def expr_add(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(ul4.Utils.add(obj1.value, obj2.value))
		return ul4.Binary(ul4.Opcode.OC_ADD, obj1, obj2)
	expr_add.spark = ['expr5 ::= expr5 + expr5']

	def expr_sub(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(ul4.Utils.sub(obj1.value, obj2.value))
		return ul4.Binary(ul4.Opcode.OC_SUB, obj1, obj2)
	expr_sub.spark = ['expr5 ::= expr5 - expr5']

	def expr_eq(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(ul4.Utils.eq(obj1.value, obj2.value))
		return ul4.Binary(ul4.Opcode.OC_EQ, obj1, obj2)
	expr_eq.spark = ['expr4 ::= expr4 == expr4']

	def expr_ne(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(ul4.Utils.ne(obj1.value, obj2.value))
		return ul4.Binary(ul4.Opcode.OC_NE, obj1, obj2)
	expr_ne.spark = ['expr4 ::= expr4 != expr4']

	def expr_lt(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(ul4.Utils.lt(obj1.value, obj2.value))
		return ul4.Binary(ul4.Opcode.OC_LT, obj1, obj2)
	expr_lt.spark = ['expr4 ::= expr4 < expr4']

	def expr_le(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(ul4.Utils.le(obj1.value, obj2.value))
		return ul4.Binary(ul4.Opcode.OC_LE, obj1, obj2)
	expr_le.spark = ['expr4 ::= expr4 <= expr4']

	def expr_gt(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(ul4.Utils.gt(obj1.value, obj2.value))
		return ul4.Binary(ul4.Opcode.OC_GT, obj1, obj2)
	expr_gt.spark = ['expr4 ::= expr4 > expr4']

	def expr_ge(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(ul4.Utils.ge(obj1.value, obj2.value))
		return ul4.Binary(ul4.Opcode.OC_GE, obj1, obj2)
	expr_ge.spark = ['expr4 ::= expr4 >= expr4']

	def expr_contains(self, (obj, _0, container)):
		if isinstance(obj, ul4.LoadConst) and isinstance(container, ul4.LoadConst): # Constant folding
			return self.makeconst(ul4.Utils.contains(obj.value, container.value))
		return ul4.Binary(ul4.Opcode.OC_CONTAINS, obj, container)
	expr_contains.spark = ['expr3 ::= expr3 in expr3']

	def expr_notcontains(self, (obj, _0, _1, container)):
		if isinstance(obj, ul4.LoadConst) and isinstance(container, ul4.LoadConst): # Constant folding
			return self.makeconst(ul4.Utils.notcontains(obj.value, container.value))
		return ul4.Binary(ul4.Opcode.OC_NOTCONTAINS, obj, container)
	expr_notcontains.spark = ['expr3 ::= expr3 not in expr3']

	def expr_not(self, (_0, expr)):
		if isinstance(expr, ul4.LoadConst): # Constant folding
			return self.makeconst(not expr.value)
		return ul4.Unary(ul4.Opcode.OC_NOT, expr)
	expr_not.spark = ['expr2 ::= not expr2']

	def expr_and(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(obj1.value and obj2.value)
		return ul4.Binary(ul4.Opcode.OC_AND, obj1, obj2)
	expr_and.spark = ['expr1 ::= expr1 and expr1']

	def expr_or(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(obj1.value or obj2.value)
		return ul4.Binary(ul4.Opcode.OC_OR, obj1, obj2)
	expr_or.spark = ['expr0 ::= expr0 or expr0']

	# These rules make operators of different precedences interoperable, by allowing an expression to "drop" its precedence.
	def expr_dropprecedence(self, (expr, )):
		return expr
	expr_dropprecedence.spark = [
		'expr10 ::= expr11',
		'expr9 ::= expr10',
		'expr8 ::= expr9',
		'expr7 ::= expr8',
		'expr6 ::= expr7',
		'expr5 ::= expr6',
		'expr4 ::= expr5',
		'expr3 ::= expr4',
		'expr2 ::= expr3',
		'expr1 ::= expr2',
		'expr0 ::= expr1',
	]


class ForParser(ExprParser):
	emptyerror = "loop expression required"

	def __init__(self, start="for"):
		ExprParser.__init__(self, start)

	def for0(self, (iter, _0, cont)):
		return ul4.For(iter.value, cont)
	for0.spark = ['for ::= name in expr0']

	def for1(self, (_0, iter, _1, _2, _3, cont)):
		node = ul4.For(cont)
		node.append(iter.value)
		return node
	for1.spark = ['for ::= ( name , ) in expr0']

	def for2a(self, (_0, iter1, _1, iter2, _2, _3, cont)):
		node = ul4.For(cont)
		node.append(iter1.value)
		node.append(iter2.value)
		return node
	for2a.spark = ['for ::= ( name , name ) in expr0']

	def for2b(self, (_0, iter1, _1, iter2, _2, _3, _4, cont)):
		node = ul4.For(cont)
		node.append(iter1.value)
		node.append(iter2.value)
		return node
	for2b.spark = ['for ::= ( name , name , ) in expr0']

	def for3a(self, (_0, iter1, _1, iter2, _2, iter3, _3, _4, cont)):
		node = ul4.For(cont)
		node.append(iter1.value)
		node.append(iter2.value)
		node.append(iter3.value)
		return node
	for3a.spark = ['for ::= ( name , name , name ) in expr0']

	def for3b(self, (_0, iter1, _1, iter2, _2, iter3, _3, _4, _5, cont)):
		node = ul4.For(cont)
		node.append(iter1.value)
		node.append(iter2.value)
		node.append(iter3.value)
		return node
	for3b.spark = ['for ::= ( name , name , name , ) in expr0']

	def for4a(self, (_0, iter1, _1, iter2, _2, iter3, _3, iter4, _4, _5, cont)):
		node = ul4.For(cont)
		node.append(iter1.value)
		node.append(iter2.value)
		node.append(iter3.value)
		node.append(iter4.value)
		return node
	for4a.spark = ['for ::= ( name , name , name , name ) in expr0']

	def for4b(self, (_0, iter1, _1, iter2, _2, iter3, _3, iter4, _4, _5, _6, cont)):
		node = ul4.For(cont)
		node.append(iter1.value)
		node.append(iter2.value)
		node.append(iter3.value)
		node.append(iter4.value)
		return node
	for4b.spark = ['for ::= ( name , name , name , name , ) in expr0']


class StmtParser(ExprParser):
	emptyerror = "statement required"

	def __init__(self, start="stmt"):
		ExprParser.__init__(self, start)

	def stmt_assign(self, (name, _0, value)):
		return ul4.ChangeVar(ul4.Opcode.OC_STOREVAR, opcode.name, value)
	stmt_assign.spark = ['stmt ::= name = expr0']

	def stmt_iadd(self, (name, _0, value)):
		return ul4.ChangeVar(ul4.Opcode.OC_ADDVAR, name, value)
	stmt_iadd.spark = ['stmt ::= name += expr0']

	def stmt_isub(self, (name, _0, value)):
		return ul4.ChangeVar(ul4.Opcode.OC_SUBVAR, name, value)
	stmt_isub.spark = ['stmt ::= name -= expr0']

	def stmt_imul(self, (name, _0, value)):
		return ul4.ChangeVar(ul4.Opcode.OC_MULVAR, name, value)
	stmt_imul.spark = ['stmt ::= name *= expr0']

	def stmt_itruediv(self, (name, _0, value)):
		return ul4.ChangeVar(ul4.Opcode.OC_TRUEDIVVAR, name, value)
	stmt_itruediv.spark = ['stmt ::= name /= expr0']

	def stmt_ifloordiv(self, (name, _0, value)):
		return ul4.ChangeVar(ul4.Opcode.OC_FLOORDIVVAR, name, value)
	stmt_ifloordiv.spark = ['stmt ::= name //= expr0']

	def stmt_imod(self, (name, _0, value)):
		return ul4.ChangeVar(ul4.Opcode.OC_MODVAR, name, value)
	stmt_imod.spark = ['stmt ::= name %= expr0']

	def stmt_del(self, (_0, name)):
		return ul4.DelVar(name)
	stmt_del.spark = ['stmt ::= del name']


class RenderParser(ExprParser):
	emptyerror = "render statement required"

	def __init__(self, start="render"):
		ExprParser.__init__(self, start)

	def emptyrender(self, (template, _1, _2)):
		return ul4.Render(template)
	emptyrender.spark = ['render ::= expr0 ( )']

	def startrender(self, (template, _1, argname, _2, argexpr)):
		render = ul4.Render(template)
		render.append(argname.value, argexpr)
		return render
	startrender.spark = ['buildrender ::= expr0 ( name = expr0 ']

	def startrenderupdate(self, (template, _0, _1, arg)):
		render = ul4.Render(template)
		render.append(arg)
		return render
	startrenderupdate.spark = ['buildrender ::= expr0 ( ** expr0']

	def buildrender(self, (render, _1, argname, _2, argexpr)):
		render.append(argname.value, argexpr)
		return render
	buildrender.spark = ['buildrender ::= buildrender , name = expr0']

	def buildrenderupdate(self, (render, _0, _1, arg)):
		render.append(arg)
		return render
	buildrenderupdate.spark = ['buildrender ::= buildrender , ** expr0']

	def finishrender(self, (render, _0)):
		return render
	finishrender.spark = ['render ::= buildrender )']

	def finishrender1(self, (render, _0, _1)):
		return render
	finishrender1.spark = ['render ::= buildrender , )']


class Compiler(ul4.CompilerType):
	def compile(self, source, name, tags, startdelim, enddelim):
		template = ul4.InterpretedTemplate()
		template.name = name
		template.startdelim = startdelim
		template.enddelim = enddelim
		template.source = source
		_compile(template, tags)
		return template