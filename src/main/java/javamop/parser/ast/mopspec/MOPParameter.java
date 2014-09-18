// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.mopspec;

import javamop.parser.ast.Node;
import javamop.parser.ast.visitor.GenericVisitor;
import javamop.parser.ast.visitor.VoidVisitor;
import javamop.parser.ast.aspectj.*;

public class MOPParameter extends Node {
    private final TypePattern type;
    private final String name;
    
    public MOPParameter (int line, int column, TypePattern type, String name){
        super(line, column);
        this.type = type;
        this.name = name;
    }
    
    public TypePattern getType() {return type;}
    public String getName() {return name;}
    
    public boolean equals(MOPParameter param){
        return type.equals(param.getType()) && name.equals(param.getName());
    }
    
    @Override
    public int hashCode(){
        return name.hashCode();
    }
    
    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
    
    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }
}
