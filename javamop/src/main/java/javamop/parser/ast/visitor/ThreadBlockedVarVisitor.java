// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import javamop.parser.ast.aspectj.ThreadBlockedPointCut;

/**
 * 
 * Thread name visitor for threadBlocked() pointcut.
 * 
 * */
public class ThreadBlockedVarVisitor extends BaseStringVisitor {

	public String visit(ThreadBlockedPointCut p, Object arg){
		String ret = p.getId();
		
		if(ret == null || ret.length() == 0)
			return null;
		return ret;
	}
}
