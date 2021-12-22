// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import com.github.javaparser.TokenRange;

public class CFlowPointCut extends PointCut {
    
    private final PointCut pointcut;
    
    public CFlowPointCut(TokenRange tokenRange, String type, PointCut pointcut){
        super(tokenRange, type);
        this.pointcut = pointcut;
    }
    
    public PointCut getPointCut() { return pointcut; }
    
}
