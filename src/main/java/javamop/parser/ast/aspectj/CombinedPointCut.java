// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;


import javamop.parser.ast.visitor.GenericVisitor;
import javamop.parser.ast.visitor.PointcutVisitor;
import javamop.parser.ast.visitor.VoidVisitor;

import java.util.List;

public class CombinedPointCut extends PointCut {
    
    private final List<PointCut> pointcuts;
    
    public CombinedPointCut(int line, int column, String type, List<PointCut> pointcuts){
        super(line, column, type);
        
        this.pointcuts = pointcuts;
    }
    
    public List<PointCut> getPointcuts() { return pointcuts; }
    
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
