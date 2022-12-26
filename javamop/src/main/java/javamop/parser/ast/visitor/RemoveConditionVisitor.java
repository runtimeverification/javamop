// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import javamop.parser.ast.aspectj.*;

public class RemoveConditionVisitor extends BasePointCutVisitor {

	public RemoveConditionVisitor(String className) {
		super(className);
	}

	public PointCut visit(ConditionPointCut p, Integer arg) {
		return null;
	}

}
