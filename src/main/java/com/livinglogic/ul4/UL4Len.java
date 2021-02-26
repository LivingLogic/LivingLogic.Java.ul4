/*
** Copyright 2012-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
Interface that can be implemented to return the length of an object.

/**
<p>Implementing the {@code UL4Len} interface allows to query an object for its
length.</p>

<p>For containers (like lists, set and maps, and even strings) this is supposed
to the number of items in the container.</p>

<p>Like all interfaces that make aspects of objects accessible to UL4 there are
two versions of each method: One that gets passed the {@link EvaluationContext}
and one that doesn't. Passing the {@link EvaluationContext} makes it possible
to implement functionality that is dependent on e.g. the currently defined
local variables etc. The default implementations of the context dependent
method version simply forward the call to the non-context-dependent version.</p>
**/
public interface UL4Len
{
	default int lenUL4(EvaluationContext context)
	{
		return lenUL4();
	}

	int lenUL4();
}
