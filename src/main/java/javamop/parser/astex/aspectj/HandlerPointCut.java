// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.aspectj;

import com.github.javaparser.TokenRange;
import javamop.parser.ast.aspectj.PointCut;
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
    
}
