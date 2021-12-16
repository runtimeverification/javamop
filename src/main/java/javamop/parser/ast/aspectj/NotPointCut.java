// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import javamop.parser.ast.visitor.PointcutVisitor;

public class NotPointCut extends PointCut {
    
    private final PointCut pointcut;
    
    public NotPointCut(int line, int column, PointCut pointcut) {
        super(line, column, "!");
        this.pointcut = pointcut;
    }
    
    public PointCut getPointCut() { return pointcut; }
    
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
