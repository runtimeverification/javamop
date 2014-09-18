// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import javamop.parser.ast.visitor.GenericVisitor;
import javamop.parser.ast.visitor.VoidVisitor;

import java.util.List;

public class ArgsPointCut extends PointCut {
    
    private final List<TypePattern> args;
    
    public ArgsPointCut(int line, int column, String type, List<TypePattern> args){
        super(line, column, type);
        this.args = args;
    }
    
    public List<TypePattern> getArgs() { return args; }
    
    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
    
    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }
}
