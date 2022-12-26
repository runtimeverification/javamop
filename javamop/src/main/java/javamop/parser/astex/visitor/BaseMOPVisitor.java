// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.visitor;

import javamop.parser.ast.visitor.BaseVisitor;
import javamop.parser.astex.MOPSpecFileExt;
import javamop.parser.astex.aspectj.EventPointCut;
import javamop.parser.astex.aspectj.HandlerPointCut;
import javamop.parser.astex.mopspec.EventDefinitionExt;
import javamop.parser.astex.mopspec.ExtendedSpec;
import javamop.parser.astex.mopspec.FormulaExt;
import javamop.parser.astex.mopspec.JavaMOPSpecExt;
import javamop.parser.astex.mopspec.PropertyAndHandlersExt;
import javamop.parser.astex.mopspec.ReferenceSpec;

public class BaseMOPVisitor<R, A> extends BaseVisitor<R, A> {

	// All extended componenets
	
    //- JavaMOP components
    
    public R visit(MOPSpecFileExt f, A arg) { return null; }
    
	public R visit(ReferenceSpec r, A arg) { return null; }

    public R visit(JavaMOPSpecExt s, A arg) { return null; }
    
    public R visit(EventDefinitionExt e, A arg) { return null; }
    
    public R visit(PropertyAndHandlersExt p, A arg) { return null; }
    
    public R visit(FormulaExt f, A arg) { return null; }
    
    public R visit(ExtendedSpec extendedSpec, A arg) { return null; }
    
    //- AspectJ components --------------------
    
    public R visit(EventPointCut p, A arg) { return null; }
    
    public R visit(HandlerPointCut p, A arg) { return null; }

}
