// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.visitor;

import javamop.parser.astex.aspectj.EventPointCut;
import javamop.parser.astex.aspectj.HandlerPointCut;

public interface PointcutVisitor<R, A> extends javamop.parser.ast.visitor.PointcutVisitor<R, A>{

//	public R visit(PointCut p, A arg);
//	
//	public R visit(ArgsPointCut p, A arg);
//
//	public R visit(CombinedPointCut p, A arg);
//
//	public R visit(NotPointCut p, A arg);
//
//	public R visit(ConditionPointCut p, A arg);
//
//	public R visit(FieldPointCut p, A arg);
//
//	public R visit(MethodPointCut p, A arg);
//
//	public R visit(TargetPointCut p, A arg);
//
//	public R visit(ThisPointCut p, A arg);
//
//	public R visit(CFlowPointCut p, A arg);
//
//	public R visit(IFPointCut p, A arg);
//
//	public R visit(IDPointCut p, A arg);
//
//	public R visit(WithinPointCut p, A arg);
//
//	public R visit(ThreadPointCut p, A arg);
//
//	public R visit(EndProgramPointCut p, A arg);
//
//	public R visit(EndThreadPointCut p, A arg);
//	
//	public R visit(EndObjectPointCut p, A arg);
//
//	public R visit(StartThreadPointCut p, A arg);
	
	// All extended componenets
    
    //- AspectJ components --------------------
    
    public R visit(EventPointCut p, A arg);
    
    public R visit(HandlerPointCut p, A arg);
	
}
