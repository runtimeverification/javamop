// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import javamop.parser.ast.aspectj.*;

public class PointCutVisitorImpl extends BasePointCutVisitorImpl<PointCut, Object> {

	//TODO: This is being added to work with Legacy code. Should be removed eventually.
	public int getBeginColumn(PointCut p) {
		return p.getRange().get().begin.column;
	}


	//TODO: This is being added to work with Legacy code. Should be removed eventually.
	public int getBeginLine(PointCut p) {
		return p.getRange().get().begin.line;
	}


	public PointCut visit(NotPointCut p, Object arg){
		PointCut p2 = p.getPointCut().accept(this, arg);

		return new NotPointCut(getBeginLine(p), getBeginColumn(p), p2);
	}

	public PointCut visit(CFlowPointCut p, Object arg){
		PointCut p2 = p.getPointCut().accept(this, arg);

		return new CFlowPointCut(getBeginLine(p), getBeginColumn(p), p.getType(), p2);
	}

}
