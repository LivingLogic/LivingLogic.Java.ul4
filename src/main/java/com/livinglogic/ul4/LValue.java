/*
** Copyright 2012-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

interface LValue
{
	public void evaluateSet(EvaluationContext context, Object value);
	public void evaluateAdd(EvaluationContext context, Object value);
	public void evaluateSub(EvaluationContext context, Object value);
	public void evaluateMul(EvaluationContext context, Object value);
	public void evaluateFloorDiv(EvaluationContext context, Object value);
	public void evaluateTrueDiv(EvaluationContext context, Object value);
	public void evaluateMod(EvaluationContext context, Object value);
}