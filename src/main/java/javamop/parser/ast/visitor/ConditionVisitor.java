// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import javamop.parser.ast.aspectj.ConditionPointCut;

public class ConditionVisitor extends BaseStringVisitor {

	@Override
	public String visit(ConditionPointCut p, Object arg) {
		String ret = p.getExpression().toString();
		if (ret == null || ret.length() == 0)
			return null;
		return ret;
	}
}
