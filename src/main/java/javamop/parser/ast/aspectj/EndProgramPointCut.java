// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import com.github.javaparser.TokenRange;

public class EndProgramPointCut extends PointCut {
    
    public EndProgramPointCut(TokenRange tokenRange){
        super(tokenRange, "endProgram");
    }

}
