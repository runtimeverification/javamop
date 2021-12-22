// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import java.util.List;

import com.github.javaparser.TokenRange;

public class CombinedTypePattern extends BaseTypePattern {
    
    private final List<TypePattern> sub_types;
    
    public CombinedTypePattern(TokenRange tokenRange, String op, List<TypePattern> sub_types){
        super(tokenRange, op);
        this.sub_types = sub_types;
    }
    
    public List<TypePattern> getSubTypes() { return sub_types; }
    
}
