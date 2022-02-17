package com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.ast;

import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.visitor.DumpVisitor;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.visitor.VoidVisitor;

public class PseudoCode extends PseudoCode_Node {
    PseudoCode_Assignments before;
    PseudoCode_Assignments after;
    PseudoCode_Output output;

    public PseudoCode(PseudoCode_Assignments before, PseudoCode_Output output,
            PseudoCode_Assignments after) {
        this.before = before;
        this.output = output;
        this.after = after;
    }

    public PseudoCode_Assignments getBefore() {
        return before;
    }

    public PseudoCode_Assignments getAfter() {
        return after;
    }

    public PseudoCode_Output getOutput() {
        return output;
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

    @Override
    public String toString() {
        DumpVisitor visitor = new DumpVisitor();
        String formula = accept(visitor, null);
        return formula;
    }
}
