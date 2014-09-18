// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import javamop.parser.astex.aspectj.EventPointCut;
import javamop.parser.astex.aspectj.HandlerPointCut;

public class ReplacePointCutVisitor implements PointcutVisitor<PointCut, HashMap<PointCut, PointCut>> {

	@Override
	public PointCut visit(PointCut p, HashMap<PointCut, PointCut> arg) {
		return p;
	}

	@Override
	public PointCut visit(ArgsPointCut p, HashMap<PointCut, PointCut> arg) {
		return p;
	}

	@Override
	public PointCut visit(CombinedPointCut p, HashMap<PointCut, PointCut> arg) {
		boolean changed = false;
		
		List<PointCut> pointcuts = new ArrayList<PointCut>();
		
		for(PointCut p2 : p.getPointcuts()){
			PointCut p3 = p2.accept(this, arg);
			
			if(p2 != p3)
				changed = true;
			
			pointcuts.add(p3);
		}
		
		if(!changed)
			return p;
		else
			return new CombinedPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), pointcuts);
	}

	@Override
	public PointCut visit(NotPointCut p, HashMap<PointCut, PointCut> arg) {
		PointCut sub = p.getPointCut().accept(this, arg);
		
		if(p.getPointCut() == sub)
			return p;
		
		return new NotPointCut(p.getBeginLine(), p.getBeginColumn(), sub);
	}

	@Override
	public PointCut visit(ConditionPointCut p, HashMap<PointCut, PointCut> arg) {
		return p;
	}

	@Override
	public PointCut visit(FieldPointCut p, HashMap<PointCut, PointCut> arg) {
		return p;
	}

	@Override
	public PointCut visit(MethodPointCut p, HashMap<PointCut, PointCut> arg) {
		return p;
	}

	@Override
	public PointCut visit(TargetPointCut p, HashMap<PointCut, PointCut> arg) {
		return p;
	}

	@Override
	public PointCut visit(ThisPointCut p, HashMap<PointCut, PointCut> arg) {
		return p;
	}

	@Override
	public PointCut visit(CFlowPointCut p, HashMap<PointCut, PointCut> arg) {
		PointCut sub = p.getPointCut().accept(this, arg);
		
		if(p.getPointCut() == sub)
			return p;
		
		return new CFlowPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), sub);
	}

	@Override
	public PointCut visit(IFPointCut p, HashMap<PointCut, PointCut> arg) {
		return p;
	}

	@Override
	public PointCut visit(IDPointCut p, HashMap<PointCut, PointCut> arg) {
		return p;
	}

	@Override
	public PointCut visit(WithinPointCut p, HashMap<PointCut, PointCut> arg) {
		return p;
	}

	@Override
	public PointCut visit(ThreadPointCut p, HashMap<PointCut, PointCut> arg) {
		return p;
	}
	
	@Override
	public PointCut visit(ThreadNamePointCut p, HashMap<PointCut, PointCut> arg) {
		return p;
	}
	
	@Override
	public PointCut visit(ThreadBlockedPointCut p, HashMap<PointCut, PointCut> arg) {
		return p;
	}
	
	@Override
	public PointCut visit(EndProgramPointCut p, HashMap<PointCut, PointCut> arg) {
		return p;
	}

	@Override
	public PointCut visit(EndThreadPointCut p, HashMap<PointCut, PointCut> arg) {
		return p;
	}

	@Override
	public PointCut visit(EndObjectPointCut p, HashMap<PointCut, PointCut> arg) {
		return p;
	}

	@Override
	public PointCut visit(StartThreadPointCut p, HashMap<PointCut, PointCut> arg) {
		return p;
	}

	@Override
	public PointCut visit(EventPointCut p, HashMap<PointCut, PointCut> arg) {
		PointCut ret = arg.get(p);
		
		if(ret == null)
			return p;
		
		return ret;
	}

	@Override
	public PointCut visit(HandlerPointCut p, HashMap<PointCut, PointCut> arg) {
		PointCut ret = arg.get(p);
		
		if(ret == null)
			return p;
		
		return ret;
	}
}
