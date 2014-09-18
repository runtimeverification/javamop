// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.mopspec;

import javamop.parser.ast.stmt.BlockStmt;
import javamop.parser.astex.visitor.GenericVisitor;
import javamop.parser.astex.visitor.VoidVisitor;
import javamop.parser.astex.ExtNode;

public class HandlerExt extends ExtNode {
    
    private final String state;
    private final BlockStmt blockStmt;
    private ReferenceSpec r;
    
    public HandlerExt(int line, int column, String state, BlockStmt blockStmt, String specReference, String propertyReference) {
        super(line, column);
        this.state = state;
        this.blockStmt = blockStmt;
        this.r = new ReferenceSpec(line, column, specReference, propertyReference, "property");
    }
    
    public String getState() {
        return state;
    }
    
    public BlockStmt getBlockStmt() {
        return blockStmt;
    }
    
    public ReferenceSpec getReferenceSpec() {
        return r;
    }
    
    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
    
    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }
    
    public void setNewReference(ReferenceSpec r2) {
        this.r = r2;
        
    }
}
