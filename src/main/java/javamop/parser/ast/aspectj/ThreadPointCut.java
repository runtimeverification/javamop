// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import com.github.javaparser.TokenRange;

public class ThreadPointCut extends PointCut {
    
    private final String id;
    
    public ThreadPointCut(TokenRange tokenRange, String id){
        super(tokenRange, "thread");
        this.id = id;
    }
    
    public String getId() { return id; }
    
}
