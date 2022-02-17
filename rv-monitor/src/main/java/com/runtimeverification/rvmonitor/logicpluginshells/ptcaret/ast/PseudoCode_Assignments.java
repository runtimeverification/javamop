package com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.ast;

import java.util.ArrayList;

import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.visitor.DumpVisitor;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.visitor.VoidVisitor;

public class PseudoCode_Assignments extends PseudoCode_Node {
    ArrayList<PseudoCode_Assignment> assignments = new ArrayList<PseudoCode_Assignment>();

    public PseudoCode_Assignments() {
    }

    public void add(PseudoCode_Assignment assignment) {
        assignments.add(assignment);
    }

    public ArrayList<PseudoCode_Assignment> getAssignments() {
        return assignments;
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
