// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import java.util.ArrayList;
import java.util.List;

import javamop.parser.ast.aspectj.CombinedPointCut;
import javamop.parser.ast.aspectj.EndProgramPointCut;
import javamop.parser.ast.aspectj.PointCut;

public class RemoveEndProgramVisitor extends BasePointCutVisitor {

	public RemoveEndProgramVisitor(String className) {
		super(className);
	}

	public PointCut visit(EndProgramPointCut p, Integer arg){
		if(arg == 0){
			return null;
		} else {
			List<PointCut> pointcuts = new ArrayList<PointCut>();
			return new CombinedPointCut(p.getTokenRange().get(), "&&", pointcuts);
		}
	}

}
