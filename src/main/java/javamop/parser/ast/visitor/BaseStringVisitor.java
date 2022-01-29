// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import javamop.parser.ast.aspectj.*;

public class BaseStringVisitor extends BaseVisitor<String, Object> {

	public String visit(CombinedPointCut p,  Object arg){
		String goal = "";

		for(PointCut p2 : p.getPointcuts()){
			String temp = p2.accept(this, arg);
			if(temp != null){
				if(temp.length() != 0 && goal.length() != 0)
					return null;

				if(temp.length() != 0)
					goal = temp;
			} else
				return null;
		}

		return goal;
	}

	public String visit(ArgsPointCut p, Object arg){
		return "";
	}

	public String visit(NotPointCut p, Object arg) {
		return p.getPointCut().accept(this, arg);
	}

	public String visit(ConditionPointCut p, Object arg) {
		return "";
	}
	
	public String visit(CountCondPointCut p, Object arg) {
		return "";
	}
	
	public String visit(FieldPointCut p, Object arg) {
		return "";
	}

	public String visit(MethodPointCut p, Object arg) {
		return "";
	}

	public String visit(TargetPointCut p, Object arg) {
		return "";
	}

	public String visit(ThisPointCut p, Object arg) {
		return "";
	}

	public String visit(CFlowPointCut p, Object arg) {
		return p.getPointCut().accept(this, arg);
	}

	public String visit(IFPointCut p, Object arg) {
		return "";
	}

	public String visit(IDPointCut p, Object arg) {
		return "";
	}

	public String visit(WithinPointCut p, Object arg) {
		return "";
	}

	public String visit(ThreadPointCut p, Object arg) {
		return "";
	}
	
	public String visit(ThreadNamePointCut p, Object arg) {
		return "";
	}
	
	public String visit(ThreadBlockedPointCut p, Object arg) {
		return "";
	}

	public String visit(EndProgramPointCut p, Object arg) {
		return "";
	}

	public String visit(EndThreadPointCut p, Object arg) {
		return "";
	}
	
	public String visit(EndObjectPointCut p, Object arg) {
		return "";
	}

	public String visit(StartThreadPointCut p, Object arg) {
		return "";
	}
}
