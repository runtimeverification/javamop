// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import java.util.ArrayList;

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

public class ConvertPointcutToCNFVisitor implements PointcutVisitor<PointCut, Object> {

	public PointCut visit(PointCut p, Object arg){
		return p;
	}
	
	public PointCut visit(ArgsPointCut p, Object arg){
		return p;
	}

	public PointCut visit(CombinedPointCut p, Object arg){
		ArrayList<PointCut> list = new ArrayList<PointCut>();

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
			return new CombinedPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), list);
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
						
						PointCut p4 = new CombinedPointCut(p2.getBeginLine(), p2.getBeginColumn(), "||", list2);
						p4 = p4.accept(this, arg);
						
						list_top.add(p4);
					}
					
					PointCut ret = new CombinedPointCut(p.getBeginLine(), p.getBeginColumn(), "&&", list_top);
					ret = ret.accept(this, arg);
					return ret;
				}
			}
			
			return new CombinedPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), list);
		}
		
		return p; //it should not happen.
	}

	public PointCut visit(NotPointCut p, Object arg){
		PointCut p2 = p.getPointCut().accept(this, arg);

		return new NotPointCut(p.getBeginLine(), p.getBeginColumn(), p2);
	}

	public PointCut visit(ConditionPointCut p, Object arg){
		return p;
	}

	public PointCut visit(FieldPointCut p, Object arg){
		return p;
	}

	public PointCut visit(MethodPointCut p, Object arg){
		return p;
	}

	public PointCut visit(TargetPointCut p, Object arg){
		return p;
	}

	public PointCut visit(ThisPointCut p, Object arg){
		return p;
	}

	public PointCut visit(CFlowPointCut p, Object arg){
		PointCut p2 = p.getPointCut().accept(this, arg);

		return new CFlowPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), p2);
	}

	public PointCut visit(IFPointCut p, Object arg){
		return p;
	}

	public PointCut visit(IDPointCut p, Object arg){
		return p;
	}

	public PointCut visit(WithinPointCut p, Object arg){
		return p;
	}

	public PointCut visit(ThreadPointCut p, Object arg){
		return p;
	}
	
	public PointCut visit(ThreadNamePointCut p, Object arg){
		return p;
	}
	
	public PointCut visit(ThreadBlockedPointCut p, Object arg){
		return p;
	}

	public PointCut visit(EndProgramPointCut p, Object arg){
		return p;
	}

	public PointCut visit(EndThreadPointCut p, Object arg){
		return p;
	}
	
	public PointCut visit(EndObjectPointCut p, Object arg){
		return p;
	}

	public PointCut visit(StartThreadPointCut p, Object arg){
		return p;
	}

	
}
