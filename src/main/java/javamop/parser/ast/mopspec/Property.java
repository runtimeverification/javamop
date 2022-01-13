// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.mopspec;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import javamop.parser.ast.visitor.BaseVisitor;
import javamop.parser.ast.visitor.MOPVoidVisitor;

public class Property extends Node {
    
    private final String type;
    
    public Property (TokenRange tokenRange, String type){
        super(tokenRange);
        this.type = type;
    }
    
    public String getType() { return type; }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        if (v instanceof MOPVoidVisitor) {
            ((MOPVoidVisitor)v).visit(this, arg);
        }
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        if (v instanceof BaseVisitor) {
            return ((BaseVisitor<R, A>) v).visit(this, arg);
        } else {
            return null;
        }
    }
    
}
