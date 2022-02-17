// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javamop.parser.ast.aspectj.*;
import javamop.parser.ast.visitor.PointcutVisitor;
import javamop.parser.astex.aspectj.EventPointCut;
import javamop.parser.astex.aspectj.HandlerPointCut;

public class ReplacePointCutVisitor extends BaseMOPVisitor<PointCut, HashMap<PointCut, PointCut>> implements PointcutVisitor<PointCut, HashMap<PointCut, PointCut>> {

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
			PointCut p3 = p2.accept( this, arg);
			
			if(p2 != p3)
				changed = true;
			
			pointcuts.add(p3);
		}
		
		if(!changed)
			return p;
		else
			return new CombinedPointCut(p.getTokenRange().get(), p.getType(), pointcuts);
	}

	@Override
	public PointCut visit(NotPointCut p, HashMap<PointCut, PointCut> arg) {
		PointCut sub = p.getPointCut().accept((PointcutVisitor<PointCut, HashMap<PointCut, PointCut>>) this, arg);
		
		if(p.getPointCut() == sub)
			return p;
		
		return new NotPointCut(p.getTokenRange().get(), sub);
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
		PointCut sub = p.getPointCut().accept((PointcutVisitor<PointCut, HashMap<PointCut, PointCut>>) this, arg);
		
		if(p.getPointCut() == sub)
			return p;
		
		return new CFlowPointCut(p.getTokenRange().get(), p.getType(), sub);
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
