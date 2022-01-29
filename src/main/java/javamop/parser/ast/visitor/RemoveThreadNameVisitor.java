// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import javamop.parser.ast.aspectj.PointCut;
import javamop.parser.ast.aspectj.ThreadNamePointCut;

public class RemoveThreadNameVisitor extends BasePointCutVisitor {

	public RemoveThreadNameVisitor(String className) {
		super(className);
	}

	public PointCut visit(ThreadNamePointCut p, Integer arg){
		return null;
	}

}
