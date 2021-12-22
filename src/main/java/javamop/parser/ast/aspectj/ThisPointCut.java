// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import com.github.javaparser.TokenRange;

public class ThisPointCut extends PointCut {
    
    private final TypePattern target;
    
    public ThisPointCut(TokenRange tokenRange, String type, TypePattern target){
        super(tokenRange, type);
        this.target = target;
    }
    public TypePattern getTarget() { return target; }
    
}