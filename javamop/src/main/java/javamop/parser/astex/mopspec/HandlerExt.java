// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.mopspec;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import javamop.parser.ast.visitor.BaseVisitor;
import javamop.parser.ast.visitor.MOPVoidVisitor;
import javamop.parser.astex.ExtNode;

public class HandlerExt extends ExtNode {
    
    private final String state;
    private final BlockStmt blockStmt;
    private ReferenceSpec r;
    
    public HandlerExt(TokenRange tokenRange, String state, BlockStmt blockStmt, String specReference, String propertyReference) {
        super(tokenRange);
        this.state = state;
        this.blockStmt = blockStmt;
        this.r = new ReferenceSpec(tokenRange, specReference, propertyReference, "property");
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
    
    public void setNewReference(ReferenceSpec r2) {
        this.r = r2;
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
