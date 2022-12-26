// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.aspectj;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import javamop.parser.ast.aspectj.PointCut;
import javamop.parser.ast.visitor.BaseVisitor;
import javamop.parser.ast.visitor.MOPVoidVisitor;
import javamop.parser.astex.mopspec.ReferenceSpec;

public class HandlerPointCut extends PointCut{
    
    private ReferenceSpec r;
    private final String state;
    
    public HandlerPointCut(TokenRange tokenRange, String type, String specName, String referenceElement, String state) {
        super(tokenRange, type);
        this.r = new ReferenceSpec(tokenRange, specName, referenceElement, "property") ;
        this.state = state;
    }
    
    public void setReference(ReferenceSpec r){
        this.r = r;
    }
    
    public ReferenceSpec getReferenceSpec(){return r;}
    
    public String getState(){
        return state;
    }

    public <A> void accept(VoidVisitor<A> v, A arg) {
        if (v instanceof MOPVoidVisitor) {
            ((MOPVoidVisitor)v).visit(this, arg);
        }
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        if (v instanceof BaseVisitor) {
            return ((BaseVisitor<R, A>) v).visit(this, arg);
        } else {
            return null;
        }
    }
    
}
