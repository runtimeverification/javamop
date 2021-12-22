// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import com.github.javaparser.TokenRange;

public class NotPointCut extends PointCut {
    
    private final PointCut pointcut;
    
    public NotPointCut(TokenRange tokenRange, PointCut pointcut) {
        super(tokenRange, "!");
        this.pointcut = pointcut;
    }
    
    public PointCut getPointCut() { return pointcut; }

}
