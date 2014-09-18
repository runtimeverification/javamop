// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import javamop.parser.ast.visitor.GenericVisitor;
import javamop.parser.ast.visitor.PointcutVisitor;
import javamop.parser.ast.visitor.VoidVisitor;

import java.util.List;

public class IDPointCut extends PointCut {
    
    private final List<TypePattern> args;
    private final String id;
    
    public IDPointCut(int line, int column, String id, List<TypePattern> args){
        super(line, column, "id");
        this.args = args;
        this.id = id;
    }
    
    public List<TypePattern> getArgs() { return args; }
    public String getId() { return id; }
    
    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
    
    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }
    
    @Override
    public <R, A> R accept(PointcutVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }
}
