// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import com.github.javaparser.TokenRange;
import javamop.parser.astex.ExtNode;

public abstract class TypePattern extends ExtNode {
    
    private final String op;
    
    public TypePattern(TokenRange tokenRange, String op) {
        super(tokenRange);
        this.op = op;
    }
    
    public String getOp() { return op; }
    
    public boolean equals(Object o){
        if(!(o instanceof TypePattern)){
            return false;
        }
        
        TypePattern t2 = (TypePattern) o;
        
        return op.equals(t2.getOp());
    }
    
}
