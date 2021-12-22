// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import com.github.javaparser.TokenRange;

public class EndThreadPointCut extends PointCut {
    
    public EndThreadPointCut(TokenRange tokenRange){
        super(tokenRange, "endThread");
    }

}
