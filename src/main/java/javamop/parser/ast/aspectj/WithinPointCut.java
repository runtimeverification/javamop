// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import com.github.javaparser.TokenRange;

public class WithinPointCut extends PointCut {
    
    private final TypePattern pattern;
    
    public WithinPointCut(TokenRange tokenRange, String type, TypePattern pattern){
        super(tokenRange, type);
        this.pattern = pattern;
    }
    
    public TypePattern getPattern() { return pattern; }
}
