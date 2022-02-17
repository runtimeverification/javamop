// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.mopspec;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import javamop.parser.ast.aspectj.TypePattern;
import javamop.parser.ast.visitor.BaseVisitor;
import javamop.parser.ast.visitor.MOPVoidVisitor;
import javamop.parser.astex.ExtNode;

public class MOPParameter extends ExtNode {
    private final TypePattern type;
    private final String name;
    
    public MOPParameter (TokenRange tokenRange, TypePattern type, String name){
        super(tokenRange);
        this.type = type;
        this.name = name;
    }
    
    public TypePattern getType() {return type;}
    public String getName() {return name;}
    
    public boolean equals(MOPParameter param){
        return type.equals(param.getType()) && name.equals(param.getName());
    }

    public <A> void accept(VoidVisitor<A> v, A arg) {
        if (v instanceof javamop.parser.ast.visitor.MOPVoidVisitor) {
            ((MOPVoidVisitor)v).visit(this, arg);
        }
    }

    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        if (v instanceof BaseVisitor) {
            return ((BaseVisitor<R, A>) v).visit(this, arg);
        } else {
            return null;
        }
    }
    
}
