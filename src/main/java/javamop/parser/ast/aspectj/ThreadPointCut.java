// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import javamop.parser.ast.visitor.GenericVisitor;
import javamop.parser.ast.visitor.PointcutVisitor;
import javamop.parser.ast.visitor.VoidVisitor;

public class ThreadPointCut extends PointCut {
    
    private final String id;
    
    public ThreadPointCut(int line, int column, String id){
        super(line, column, "thread");
        this.id = id;
    }
    
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
