// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.visitor;

import java.util.ArrayList;
import java.util.List;

import javamop.parser.ast.aspectj.CFlowPointCut;
import javamop.parser.ast.aspectj.CombinedPointCut;
import javamop.parser.ast.aspectj.NotPointCut;
import javamop.parser.ast.aspectj.PointCut;
import javamop.parser.astex.aspectj.HandlerPointCut;

public class CollectHandlerPointCutVisitor extends MOPPointcutVisitorImpl<List<HandlerPointCut>, Object> {

	@Override
	public List<HandlerPointCut> visit(CombinedPointCut p, Object arg) {
		List<HandlerPointCut> ret = new ArrayList<>();
		
		for(PointCut p2 : p.getPointcuts()){
			List<HandlerPointCut> temp = (List<HandlerPointCut>) p2.accept(this, arg);
			if(temp != null){
				ret.addAll(temp);
			}
		}
		return ret;
	}

	@Override
	public List<HandlerPointCut> visit(NotPointCut p, Object arg) {
		return (List<HandlerPointCut>) p.getPointCut().accept(this, arg);
	}

	@Override
	public List<HandlerPointCut> visit(CFlowPointCut p, Object arg) {
		return (List<HandlerPointCut>) p.getPointCut().accept(this, arg);
	}

	@Override
	public List<HandlerPointCut> visit(HandlerPointCut p, Object arg) {
		List<HandlerPointCut> ret = new ArrayList<HandlerPointCut>();
		ret.add(p);
		return ret;
	}
}
