// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import javamop.parser.ast.visitor.GenericVisitor;
import javamop.parser.ast.visitor.PointcutVisitor;
import javamop.parser.ast.visitor.VoidVisitor;

/**
 * 
 * ThreadBlocked point cut used to check whether a thread is blocked
 * 
 * */
public class ThreadBlockedPointCut extends PointCut {
    
    private final String id;
    
    public ThreadBlockedPointCut(int line, int column, String id){
        super(line, column, "threadBlocked");
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
