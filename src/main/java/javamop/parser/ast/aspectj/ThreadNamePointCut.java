// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import com.github.javaparser.TokenRange;

/**
 * 
 * ThreadName point cut used to specify the name of a thread
 * 
 * */
public class ThreadNamePointCut extends PointCut {
    
    private final String id;
    
    public ThreadNamePointCut(TokenRange tokenRange, String id){
        super(tokenRange, "threadName");
        this.id = id;
    }
    
    public String getId() { return id; }
}
