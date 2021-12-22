// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import com.github.javaparser.TokenRange;

public class FieldPointCut extends PointCut {
    
    private final FieldPattern field;
    
    public FieldPointCut(TokenRange tokenRange, String type, FieldPattern field){
        super(tokenRange, type);
        this.field = field;
    }
    
    public FieldPattern getField() { return field; }
    
}
