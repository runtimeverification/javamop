// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import com.github.javaparser.TokenRange;

/**
 * 
 * ThreadBlocked point cut used to check whether a thread is blocked
 * 
 * */
public class ThreadBlockedPointCut extends PointCut {
    
    private final String id;
    
    public ThreadBlockedPointCut(TokenRange tokenRange, String id){
        super(tokenRange, "threadBlocked");
        this.id = id;
    }
    
    public String getId() { return id; }
    
}
