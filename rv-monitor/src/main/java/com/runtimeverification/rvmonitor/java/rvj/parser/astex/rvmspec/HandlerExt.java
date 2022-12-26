package com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec;

import com.runtimeverification.rvmonitor.java.rvj.parser.astex.ExtNode;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.visitor.VoidVisitor;

public class HandlerExt extends ExtNode {

    private final String state;
    private final String blockStmt;
    private ReferenceSpec r;

    public HandlerExt(int line, int column, String state, String blockStmt,
            String specReference, String propertyReference) {
        super(line, column);
        this.state = state;
        this.blockStmt = blockStmt;
        this.r = new ReferenceSpec(line, column, specReference,
                propertyReference, "property");
    }

    public String getState() {
        return state;
    }

    public String getBlockStmt() {
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
