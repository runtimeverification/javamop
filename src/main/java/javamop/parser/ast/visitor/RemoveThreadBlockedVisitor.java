// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import javamop.parser.ast.aspectj.PointCut;
import javamop.parser.ast.aspectj.ThreadBlockedPointCut;

public class RemoveThreadBlockedVisitor extends BasePointCutVisitor {

	public RemoveThreadBlockedVisitor(String className) {
		super(className);
	}

	public PointCut visit(ThreadBlockedPointCut p, Integer arg){
		return null;
	}
}
