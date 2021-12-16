// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.visitor;

import javamop.parser.ast.visitor.PointCutVisitorImpl;
import javamop.parser.astex.aspectj.EventPointCut;
import javamop.parser.astex.aspectj.HandlerPointCut;

public class MOPPointcutVisitorImpl<R, A> extends PointCutVisitorImpl {

    public R visit(EventPointCut p, A arg) { return null; }
    
    public R visit(HandlerPointCut p, A arg) { return null; }
	
}
