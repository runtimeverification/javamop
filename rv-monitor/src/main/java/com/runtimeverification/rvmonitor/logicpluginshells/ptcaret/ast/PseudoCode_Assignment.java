package com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.ast;

import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.visitor.DumpVisitor;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.visitor.VoidVisitor;

public class PseudoCode_Assignment extends PseudoCode_Node {
    PseudoCode_VarExpr var;
    PseudoCode_Expr expr;

    public PseudoCode_Assignment(PseudoCode_VarExpr var, PseudoCode_Expr expr) {
        this.var = var;
        this.expr = expr;
    }

    public PseudoCode_VarExpr getVar() {
        return var;
    }

    public PseudoCode_Expr getExpr() {
        return expr;
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
