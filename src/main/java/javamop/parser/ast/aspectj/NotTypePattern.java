// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import com.github.javaparser.TokenRange;

public class NotTypePattern extends BaseTypePattern {
    
    private final TypePattern type;
    
    public NotTypePattern(TokenRange tokenRange, TypePattern type){
        super(tokenRange, "!");
        this.type = type;
    }
    
    public TypePattern getType() { return type; }

}
