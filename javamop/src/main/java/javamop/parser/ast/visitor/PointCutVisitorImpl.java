// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import javamop.parser.ast.aspectj.*;

public class PointCutVisitorImpl extends BasePointCutVisitorImpl<PointCut, Object> {

	public PointCut visit(NotPointCut p, Object arg){
		PointCut p2 = p.getPointCut().accept(this, arg);

		return new NotPointCut(p.getTokenRange().get(), p2);
	}

	public PointCut visit(CFlowPointCut p, Object arg){
		PointCut p2 = p.getPointCut().accept(this, arg);

		return new CFlowPointCut(p.getTokenRange().get(), p.getType(), p2);
	}

}
