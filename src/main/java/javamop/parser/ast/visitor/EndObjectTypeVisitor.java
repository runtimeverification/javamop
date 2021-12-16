// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import javamop.parser.ast.aspectj.CFlowPointCut;
import javamop.parser.ast.aspectj.CombinedPointCut;
import javamop.parser.ast.aspectj.EndObjectPointCut;
import javamop.parser.ast.aspectj.NotPointCut;
import javamop.parser.ast.aspectj.PointCut;
import javamop.parser.ast.aspectj.TypePattern;

public class EndObjectTypeVisitor extends BaseVisitor<TypePattern, Object> {

	public TypePattern visit(CombinedPointCut p, Object arg) {
		TypePattern endObjectType = null;

		for (PointCut p2 : p.getPointcuts()) {
			TypePattern temp = p2.accept(this, arg);
			if (temp != null && endObjectType != null)
				return null;

			endObjectType = temp;
		}

		return endObjectType;
	}

	public TypePattern visit(NotPointCut p, Object arg) {
		return p.getPointCut().accept(this, arg);
	}

	public TypePattern visit(CFlowPointCut p, Object arg) {
		return p.getPointCut().accept(this, arg);
	}

	public TypePattern visit(EndObjectPointCut p, Object arg) {
		return p.getTargetType();
	}

}
