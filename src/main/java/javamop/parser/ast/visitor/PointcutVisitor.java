// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import javamop.parser.ast.aspectj.ArgsPointCut;
import javamop.parser.ast.aspectj.CFlowPointCut;
import javamop.parser.ast.aspectj.CombinedPointCut;
import javamop.parser.ast.aspectj.ConditionPointCut;
import javamop.parser.ast.aspectj.EndObjectPointCut;
import javamop.parser.ast.aspectj.EndProgramPointCut;
import javamop.parser.ast.aspectj.EndThreadPointCut;
import javamop.parser.ast.aspectj.FieldPointCut;
import javamop.parser.ast.aspectj.IDPointCut;
import javamop.parser.ast.aspectj.IFPointCut;
import javamop.parser.ast.aspectj.MethodPointCut;
import javamop.parser.ast.aspectj.NotPointCut;
import javamop.parser.ast.aspectj.PointCut;
import javamop.parser.ast.aspectj.StartThreadPointCut;
import javamop.parser.ast.aspectj.TargetPointCut;
import javamop.parser.ast.aspectj.ThisPointCut;
import javamop.parser.ast.aspectj.ThreadBlockedPointCut;
import javamop.parser.ast.aspectj.ThreadNamePointCut;
import javamop.parser.ast.aspectj.ThreadPointCut;
import javamop.parser.ast.aspectj.WithinPointCut;

public interface PointcutVisitor<R, A> {

	public R visit(PointCut p, A arg);
	
	public R visit(ArgsPointCut p, A arg);

	public R visit(CombinedPointCut p, A arg);

	public R visit(NotPointCut p, A arg);

	public R visit(ConditionPointCut p, A arg);

	public R visit(FieldPointCut p, A arg);

	public R visit(MethodPointCut p, A arg);

	public R visit(TargetPointCut p, A arg);

	public R visit(ThisPointCut p, A arg);

	public R visit(CFlowPointCut p, A arg);

	public R visit(IFPointCut p, A arg);

	public R visit(IDPointCut p, A arg);

	public R visit(WithinPointCut p, A arg);

	public R visit(ThreadPointCut p, A arg);
	
	public R visit(ThreadNamePointCut p, A arg);

	public R visit(ThreadBlockedPointCut p, A arg);
	
	public R visit(EndProgramPointCut p, A arg);

	public R visit(EndThreadPointCut p, A arg);
	
	public R visit(EndObjectPointCut p, A arg);

	public R visit(StartThreadPointCut p, A arg);
	
}
