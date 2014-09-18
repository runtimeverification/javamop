// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import javamop.parser.ast.Node;

public abstract class TypePattern extends Node {
    
    private final String op;
    
    public TypePattern(int line, int column, String op) {
        super(line, column);
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
