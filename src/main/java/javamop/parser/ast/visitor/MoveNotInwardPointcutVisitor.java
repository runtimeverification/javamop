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

public class MoveNotInwardPointcutVisitor implements PointcutVisitor<PointCut, Object> {

	public PointCut visit(PointCut p, Object arg) {
		return p;
	}

	public PointCut visit(ArgsPointCut p, Object arg) {
		return p;
	}

	public PointCut visit(CombinedPointCut p, Object arg) {
		ArrayList<PointCut> list = new ArrayList<PointCut>();

		for (PointCut p2 : p.getPointcuts()) {
			list.add(p2.accept(this, arg));
		}

		if (list.size() == 1)
			return list.get(0);
		else 
			return new CombinedPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), list);
	}

	public PointCut visit(NotPointCut p, Object arg) {
		PointCut sub = p.getPointCut();

		if (sub instanceof NotPointCut) {
			return ((NotPointCut) sub).getPointCut().accept(this, arg);
		} else if (sub instanceof CombinedPointCut) {
			String type = p.getType().equals("&&") ? "||" : "&&";

			ArrayList<PointCut> list = new ArrayList<PointCut>();

			for (PointCut p2 : ((CombinedPointCut) sub).getPointcuts()) {
				PointCut p3 = new NotPointCut(p2.getBeginLine(), p2.getBeginColumn(), p2.accept(this, arg));
				p3 = p3.accept(this, arg);

				list.add(p3);
			}

			return new CombinedPointCut(p.getBeginLine(), p.getBeginColumn(), type, list);
		} else {
			return p;
		}
	}

	public PointCut visit(ConditionPointCut p, Object arg) {
		return p;
	}

	public PointCut visit(FieldPointCut p, Object arg) {
		return p;
	}

	public PointCut visit(MethodPointCut p, Object arg) {
		return p;
	}

	public PointCut visit(TargetPointCut p, Object arg) {
		return p;
	}

	public PointCut visit(ThisPointCut p, Object arg) {
		return p;
	}

	public PointCut visit(CFlowPointCut p, Object arg) {
		PointCut p2 = p.getPointCut().accept(this, arg);

		return new CFlowPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), p2);
	}

	public PointCut visit(IFPointCut p, Object arg) {
		return p;
	}

	public PointCut visit(IDPointCut p, Object arg) {
		return p;
	}

	public PointCut visit(WithinPointCut p, Object arg) {
		return p;
	}

	public PointCut visit(ThreadPointCut p, Object arg) {
		return p;
	}
	
	public PointCut visit(ThreadNamePointCut p, Object arg) {
		return p;
	}
	
	public PointCut visit(ThreadBlockedPointCut p, Object arg) {
		return p;
	}

	public PointCut visit(EndProgramPointCut p, Object arg) {
		return p;
	}

	public PointCut visit(EndThreadPointCut p, Object arg) {
		return p;
	}

	public PointCut visit(EndObjectPointCut p, Object arg) {
		return p;
	}

	public PointCut visit(StartThreadPointCut p, Object arg) {
		return p;
	}
}
