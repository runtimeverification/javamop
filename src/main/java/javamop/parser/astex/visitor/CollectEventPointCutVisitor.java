// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.visitor;

import java.util.ArrayList;
import java.util.List;

import javamop.parser.ast.aspectj.*;
import javamop.parser.astex.aspectj.EventPointCut;
import javamop.parser.astex.aspectj.HandlerPointCut;

public class CollectEventPointCutVisitor implements PointcutVisitor<List<EventPointCut>, Object> {

	@Override
	public List<EventPointCut> visit(PointCut p, Object arg) {
		return null;
	}

	@Override
	public List<EventPointCut> visit(ArgsPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<EventPointCut> visit(CombinedPointCut p, Object arg) {
		List<EventPointCut> ret = new ArrayList<EventPointCut>();
		
		for(PointCut p2 : p.getPointcuts()){
			List<EventPointCut> temp = p2.accept(this, arg);
			if(temp != null){
				ret.addAll(temp);
			}
		}
		return ret;
	}

	@Override
	public List<EventPointCut> visit(NotPointCut p, Object arg) {
		return p.getPointCut().accept(this, arg);
	}

	@Override
	public List<EventPointCut> visit(ConditionPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<EventPointCut> visit(FieldPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<EventPointCut> visit(MethodPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<EventPointCut> visit(TargetPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<EventPointCut> visit(ThisPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<EventPointCut> visit(CFlowPointCut p, Object arg) {
		return p.getPointCut().accept(this, arg);
	}

	@Override
	public List<EventPointCut> visit(IFPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<EventPointCut> visit(IDPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<EventPointCut> visit(WithinPointCut p, Object arg) {
		return null;
	}

    @Override
    public List<EventPointCut> visit(WithincodePointCut p, Object arg) {
        return null;
    }

	@Override
	public List<EventPointCut> visit(ThreadPointCut p, Object arg) {
		return null;
	}
	
	@Override
	public List<EventPointCut> visit(ThreadBlockedPointCut p, Object arg) {
		return null;
	}
	
	@Override
	public List<EventPointCut> visit(ThreadNamePointCut p, Object arg) {
		return null;
	}
	
	@Override
	public List<EventPointCut> visit(EndProgramPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<EventPointCut> visit(EndThreadPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<EventPointCut> visit(EndObjectPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<EventPointCut> visit(StartThreadPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<EventPointCut> visit(EventPointCut p, Object arg) {
		List<EventPointCut> ret = new ArrayList<EventPointCut>();
		ret.add(p);
		return ret;
	}

	@Override
	public List<EventPointCut> visit(HandlerPointCut p, Object arg) {
		return null;
	}
}
