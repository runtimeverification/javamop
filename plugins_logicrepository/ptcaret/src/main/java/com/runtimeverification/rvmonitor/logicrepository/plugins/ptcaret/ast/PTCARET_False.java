package com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast;

import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor.DumpVisitor;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor.VoidVisitor;

public class PTCARET_False extends PTCARET_Formula {

	
    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

    @Override
    public final String toString() {
        DumpVisitor visitor = new DumpVisitor();
        String formula = accept(visitor, null);
        return formula;
    }

}
