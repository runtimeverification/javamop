// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import com.github.javaparser.TokenRange;

public class MethodPointCut extends PointCut {
    
    private final MethodPattern signature;
    
    public MethodPointCut(TokenRange tokenRange, String type, MethodPattern signature) {
        super(tokenRange, type);
        this.signature = signature;
    }
    
    public MethodPattern getSignature() {
        return signature;
    }
    
}
