// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import java.util.ArrayList;
import java.util.List;

import javamop.parser.ast.aspectj.*;

public class BasePointCutVisitor extends BaseVisitor<PointCut, Integer>{

	public PointCut visit(ArgsPointCut p, Integer arg) {
		return p;
	}

	public PointCut visit(CombinedPointCut p, Integer arg){
		if(arg == 0){
			List<PointCut> pointcuts = new ArrayList<PointCut>();
			for(PointCut p2 : p.getPointcuts()){
				PointCut temp = p2.accept(this, new Integer(0));
				
				if(temp != null)
					pointcuts.add(temp);
				else
					return null;
			}
			return new CombinedPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), pointcuts);
		} else {
			boolean andType = (p.getType().compareTo("&&") == 0);
			boolean alreadySeen = false;
			
			List<PointCut> pointcuts = new ArrayList<PointCut>();
			for(PointCut p2 : p.getPointcuts()){
				if(andType && p2 instanceof ConditionPointCut){
					if(alreadySeen)
						return null;
					alreadySeen = true;
					continue;
				}
				
				PointCut temp = p2.accept(this, new Integer(0));
				
				if(temp != null)
					pointcuts.add(temp);
				else
					return null;
			}
			return new CombinedPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), pointcuts);
		}		
	}

	public PointCut visit(ConditionPointCut p, Integer arg) {
		return p;
	}

	public PointCut visit(NotPointCut p, Integer arg){
		return new NotPointCut(p.getBeginLine(), p.getBeginColumn(), p.getPointCut().accept(this, new Integer(0)));
	}

	public PointCut visit(FieldPointCut p, Integer arg){
		return p;
	}
	
	public PointCut visit(CountCondPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(MethodPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(TargetPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(ThisPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(CFlowPointCut p, Integer arg){
		return new CFlowPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), p.getPointCut().accept(this, new Integer(0)));
	}

	public PointCut visit(IFPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(IDPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(WithinPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(ThreadPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(ThreadNamePointCut p, Integer arg){
		return p;
	}
	
	public PointCut visit(ThreadBlockedPointCut p, Integer arg){
		return p;
	}
	
	public PointCut visit(EndProgramPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(EndThreadPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(EndObjectPointCut p, Integer arg){
		return p;
	}
	
	public PointCut visit(StartThreadPointCut p, Integer arg){
		return p;
	}
}
