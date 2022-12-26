// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import java.util.ArrayList;
import java.util.List;

import javamop.parser.ast.aspectj.ArgsPointCut;
import javamop.parser.ast.aspectj.CombinedPointCut;
import javamop.parser.ast.aspectj.ConditionPointCut;
import javamop.parser.ast.aspectj.CountCondPointCut;
import javamop.parser.ast.aspectj.EndObjectPointCut;
import javamop.parser.ast.aspectj.EndProgramPointCut;
import javamop.parser.ast.aspectj.EndThreadPointCut;
import javamop.parser.ast.aspectj.FieldPointCut;
import javamop.parser.ast.aspectj.IDPointCut;
import javamop.parser.ast.aspectj.IFPointCut;
import javamop.parser.ast.aspectj.MethodPointCut;
import javamop.parser.ast.aspectj.PointCut;
import javamop.parser.ast.aspectj.StartThreadPointCut;
import javamop.parser.ast.aspectj.TargetPointCut;
import javamop.parser.ast.aspectj.ThisPointCut;
import javamop.parser.ast.aspectj.ThreadBlockedPointCut;
import javamop.parser.ast.aspectj.ThreadNamePointCut;
import javamop.parser.ast.aspectj.ThreadPointCut;
import javamop.parser.ast.aspectj.WithinPointCut;

public class RemovePointCutVisitor extends BasePointCutVisitor {
	String targetType;

	public RemovePointCutVisitor(String type) {
		super(type);
		this.targetType = type;
	}

	public PointCut visit(ArgsPointCut p, Integer arg) {
		return removePointCut(p, arg);
	}

	public PointCut visit(CombinedPointCut p, Integer arg) {
		if (arg == 0) {
			List<PointCut> pointcuts = new ArrayList<PointCut>();
			for (PointCut p2 : p.getPointcuts()) {
				PointCut temp = p2.accept(this, new Integer(0));

				if (temp != null)
					pointcuts.add(temp);
				else
					return null;
			}
			return new CombinedPointCut(p.getTokenRange().get(), p.getType(), pointcuts);
		} else {
			boolean andType = (p.getType().compareTo("&&") == 0);
			boolean alreadySeen = false;

			List<PointCut> pointcuts = new ArrayList<PointCut>();
			for (PointCut p2 : p.getPointcuts()) {
				if (andType && p2.getType().equals(this.targetType)) {
					if (alreadySeen)
						return null;
					alreadySeen = true;
					continue;
				}

				PointCut temp = p2.accept(this, new Integer(0));

				if (temp != null)
					pointcuts.add(temp);
				else
					return null;
			}
			return new CombinedPointCut(p.getTokenRange().get(), p.getType(), pointcuts);
		}
	}

	public PointCut visit(ConditionPointCut p, Integer arg) {
		return removePointCut(p, arg);
	}
	
	public PointCut visit(CountCondPointCut p, Integer arg) {
		return removePointCut(p, arg);
	}

	public PointCut visit(FieldPointCut p, Integer arg) {
		return removePointCut(p, arg);
	}

	public PointCut visit(MethodPointCut p, Integer arg) {
		return removePointCut(p, arg);
	}

	public PointCut visit(TargetPointCut p, Integer arg) {
		return removePointCut(p, arg);
	}

	public PointCut visit(ThisPointCut p, Integer arg) {
		return removePointCut(p, arg);
	}

	public PointCut visit(IFPointCut p, Integer arg) {
		return removePointCut(p, arg);
	}

	public PointCut visit(IDPointCut p, Integer arg) {
		return removePointCut(p, arg);
	}

	public PointCut visit(WithinPointCut p, Integer arg) {
		return removePointCut(p, arg);
	}

	public PointCut visit(ThreadPointCut p, Integer arg) {
		return removePointCut(p, arg);
	}
	
	public PointCut visit(ThreadNamePointCut p, Integer arg) {
		return removePointCut(p, arg);
	}
	
	public PointCut visit(ThreadBlockedPointCut p, Integer arg) {
		return removePointCut(p, arg);
	}

	public PointCut visit(EndProgramPointCut p, Integer arg) {
		return removePointCut(p, arg);
	}

	public PointCut visit(EndThreadPointCut p, Integer arg) {
		return removePointCut(p, arg);
	}

	public PointCut visit(EndObjectPointCut p, Integer arg) {
		return removePointCut(p, arg);
	}

	public PointCut visit(StartThreadPointCut p, Integer arg) {
		return removePointCut(p, arg);
	}

	/**
	 * 
	 * Helper method to remove pointcut with a specific type.
	 * 
	 * */
	private PointCut removePointCut(PointCut p, Integer arg) {
		if (p.getType().equals(this.targetType)) {
			if (arg == 0) {
				return null;
			} else {
				List<PointCut> pointcuts = new ArrayList<PointCut>();
				return new CombinedPointCut(p.getTokenRange().get(), "&&", pointcuts);
			}
		} else
			return p;
	}

}
