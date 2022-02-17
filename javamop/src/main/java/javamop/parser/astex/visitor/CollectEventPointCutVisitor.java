// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.visitor;

import java.util.ArrayList;
import java.util.List;

import javamop.parser.ast.aspectj.CFlowPointCut;
import javamop.parser.ast.aspectj.CombinedPointCut;
import javamop.parser.ast.aspectj.NotPointCut;
import javamop.parser.ast.aspectj.PointCut;
import javamop.parser.astex.aspectj.EventPointCut;

public class CollectEventPointCutVisitor extends MOPPointcutVisitorImpl<List<EventPointCut>, Object> {

	@Override
	public List<EventPointCut> visit(CombinedPointCut p, Object arg) {
		List<EventPointCut> ret = new ArrayList<EventPointCut>();
		
		for(PointCut p2 : p.getPointcuts()){
			List<EventPointCut> temp = (List<EventPointCut>)p2.accept(this, arg);
			if(temp != null){
				ret.addAll(temp);
			}
		}
		return ret;
	}

	@Override
	public List<EventPointCut> visit(NotPointCut p, Object arg) {
		return (List<EventPointCut>) p.getPointCut().accept(this, arg);
	}

	@Override
	public List<EventPointCut> visit(CFlowPointCut p, Object arg) {
		return (List<EventPointCut>) p.getPointCut().accept(this, arg);
	}

	@Override
	public List<EventPointCut> visit(EventPointCut p, Object arg) {
		List<EventPointCut> ret = new ArrayList<EventPointCut>();
		ret.add(p);
		return ret;
	}
}
