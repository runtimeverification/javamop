// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.visitor;

import java.util.ArrayList;
import java.util.List;

import javamop.parser.ast.aspectj.*;
import javamop.parser.astex.aspectj.EventPointCut;
import javamop.parser.astex.aspectj.HandlerPointCut;

public class CollectHandlerPointCutVisitor implements PointcutVisitor<List<HandlerPointCut>, Object> {

	@Override
	public List<HandlerPointCut> visit(PointCut p, Object arg) {
		return null;
	}

	@Override
	public List<HandlerPointCut> visit(ArgsPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<HandlerPointCut> visit(CombinedPointCut p, Object arg) {
		List<HandlerPointCut> ret = new ArrayList<HandlerPointCut>();
		
		for(PointCut p2 : p.getPointcuts()){
			List<HandlerPointCut> temp = p2.accept(this, arg);
			if(temp != null){
				ret.addAll(temp);
			}
		}
		return ret;
	}

	@Override
	public List<HandlerPointCut> visit(NotPointCut p, Object arg) {
		return p.getPointCut().accept(this, arg);
	}

	@Override
	public List<HandlerPointCut> visit(ConditionPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<HandlerPointCut> visit(FieldPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<HandlerPointCut> visit(MethodPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<HandlerPointCut> visit(TargetPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<HandlerPointCut> visit(ThisPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<HandlerPointCut> visit(CFlowPointCut p, Object arg) {
		return p.getPointCut().accept(this, arg);
	}

	@Override
	public List<HandlerPointCut> visit(IFPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<HandlerPointCut> visit(IDPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<HandlerPointCut> visit(WithinPointCut p, Object arg) {
		return null;
	}

    @Override
    public List<HandlerPointCut> visit(WithincodePointCut p, Object arg) {
        return null;
    }

	@Override
	public List<HandlerPointCut> visit(ThreadPointCut p, Object arg) {
		return null;
	}
	
	@Override
	public List<HandlerPointCut> visit(ThreadBlockedPointCut p, Object arg) {
		return null;
	}
	
	@Override
	public List<HandlerPointCut> visit(ThreadNamePointCut p, Object arg) {
		return null;
	}

	@Override
	public List<HandlerPointCut> visit(EndProgramPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<HandlerPointCut> visit(EndThreadPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<HandlerPointCut> visit(EndObjectPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<HandlerPointCut> visit(StartThreadPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<HandlerPointCut> visit(EventPointCut p, Object arg) {
		return null;
	}

	@Override
	public List<HandlerPointCut> visit(HandlerPointCut p, Object arg) {
		List<HandlerPointCut> ret = new ArrayList<HandlerPointCut>();
		ret.add(p);
		return ret;
	}
}
