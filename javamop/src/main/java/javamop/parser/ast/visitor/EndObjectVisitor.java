// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import javamop.parser.ast.aspectj.EndObjectPointCut;

public class EndObjectVisitor extends BaseStringVisitor {
	public String visit(EndObjectPointCut p, Object arg){
		return p.getId();
	}
}
