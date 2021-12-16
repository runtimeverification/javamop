// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;


import javamop.parser.ast.aspectj.EndThreadPointCut;

public class EndThreadVisitor extends BaseStringVisitor {
	public String visit(EndThreadPointCut p, Object arg){
		return "exist";
	}
}
