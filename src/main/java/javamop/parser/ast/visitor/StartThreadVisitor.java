// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import javamop.parser.ast.aspectj.StartThreadPointCut;

public class StartThreadVisitor extends BaseStringVisitor {
	public String visit(StartThreadPointCut p, Object arg){
		return "exist";
	}
}
