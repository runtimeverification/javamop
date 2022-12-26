// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import javamop.parser.ast.aspectj.PointCut;
import javamop.parser.ast.aspectj.ThreadPointCut;

public class RemoveThreadVisitor extends BasePointCutVisitor {

	public RemoveThreadVisitor(String className) {
		super(className);
	}

	public PointCut visit(ThreadPointCut p, Integer arg){
		return null;
	}

}
