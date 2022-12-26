// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import java.util.ArrayList;

import javamop.parser.ast.aspectj.*;

public class ConvertPointcutToCNFVisitor extends PointCutVisitorImpl {

	public PointCut visit(CombinedPointCut p, Object arg){
		ArrayList<PointCut> list = new ArrayList<>();

		for (PointCut p2 : p.getPointcuts()) {
			list.add(p2.accept(this, arg));
		}
		
		if(p.getType().equals("&&")){
			//flattening 
			for (PointCut p2 : list) {
				if(p2 instanceof CombinedPointCut && ((CombinedPointCut)p2).getType().equals("&&")){
					list.remove(p2);
					for(PointCut p3 : ((CombinedPointCut)p2).getPointcuts()){
						list.add(p3);
					}
				}
			}
			
			//just return as it is.
			return new CombinedPointCut(p.getTokenRange().get(), p.getType(), list);
		} else if(p.getType().equals("||")){
			//flattening 
			for (PointCut p2 : list) {
				if(p2 instanceof CombinedPointCut && ((CombinedPointCut)p2).getType().equals("||")){
					list.remove(p2);
					for(PointCut p3 : ((CombinedPointCut)p2).getPointcuts()){
						list.add(p3);
					}
				}
			}
			
			//replace (a && b) || c patterns into (a || c) && (b || c)  
			ArrayList<PointCut> list_top = new ArrayList<PointCut>();
			for (PointCut p2 : list) {
				if(p2 instanceof CombinedPointCut && ((CombinedPointCut)p2).getType().equals("&&")){
					list.remove(p2); //then, list is c
					
					for(PointCut p3 : ((CombinedPointCut)p2).getPointcuts()){
						ArrayList<PointCut> list2 = new ArrayList<PointCut>();
						
						list2.add(p3);
						list2.addAll(list);
						
						PointCut p4 = new CombinedPointCut(p2.getTokenRange().get(), "||", list2);
						p4 = p4.accept(this, arg);
						
						list_top.add(p4);
					}
					
					PointCut ret = new CombinedPointCut(p.getTokenRange().get(), "&&", list_top);
					ret = ret.accept(this, arg);
					return ret;
				}
			}
			
			return new CombinedPointCut(p.getTokenRange().get(), p.getType(), list);
		}
		
		return p; //it should not happen.
	}

	@Override
	public PointCut visit(PointCut p, Object arg) {
		return p;
	}

	@Override
	public PointCut visit(ArgsPointCut p, Object arg) {
		return p;
	}

	@Override
	public PointCut visit(NotPointCut p, Object arg) {
		return p;
	}

	@Override
	public PointCut visit(ConditionPointCut p, Object arg) {
		return p;
	}

	@Override
	public PointCut visit(FieldPointCut p, Object arg) {
		return p;
	}

	@Override
	public PointCut visit(MethodPointCut p, Object arg) {
		return p;
	}

	@Override
	public PointCut visit(TargetPointCut p, Object arg) {
		return p;
	}

	@Override
	public PointCut visit(ThisPointCut p, Object arg) {
		return p;
	}

	@Override
	public PointCut visit(CFlowPointCut p, Object arg) {
		return p;
	}

	@Override
	public PointCut visit(IFPointCut p, Object arg) {
		return p;
	}

	@Override
	public PointCut visit(IDPointCut p, Object arg) {
		return p;
	}

	@Override
	public PointCut visit(WithinPointCut p, Object arg) {
		return p;
	}

	@Override
	public PointCut visit(ThreadPointCut p, Object arg) {
		return p;
	}

	@Override
	public PointCut visit(ThreadNamePointCut p, Object arg) {
		return p;
	}

	@Override
	public PointCut visit(ThreadBlockedPointCut p, Object arg) {
		return p;
	}

	@Override
	public PointCut visit(EndProgramPointCut p, Object arg) {
		return p;
	}

	@Override
	public PointCut visit(EndThreadPointCut p, Object arg) {
		return p;
	}

	@Override
	public PointCut visit(EndObjectPointCut p, Object arg) {
		return p;
	}

	@Override
	public PointCut visit(StartThreadPointCut p, Object arg) {
		return p;
	}
}
