// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import com.github.javaparser.TokenRange;

public class EndObjectPointCut extends PointCut {
    
    private final TypePattern targetType;
    private final String id;
    
    public EndObjectPointCut(TokenRange tokenRange, TypePattern targetType, String id){
        super(tokenRange, "endObject");
        this.targetType = targetType;
        this.id = id;
    }
    public TypePattern getTargetType() { return targetType; }
    
    public String getId() { return id; }

}