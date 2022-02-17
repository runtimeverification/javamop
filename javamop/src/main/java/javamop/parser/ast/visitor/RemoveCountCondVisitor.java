// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import javamop.parser.ast.aspectj.CountCondPointCut;
import javamop.parser.ast.aspectj.PointCut;

public class RemoveCountCondVisitor extends BasePointCutVisitor {

	public RemoveCountCondVisitor(String className) {
		super(className);
	}

	public PointCut visit(CountCondPointCut p, Integer arg){
		return null;
	}

}
