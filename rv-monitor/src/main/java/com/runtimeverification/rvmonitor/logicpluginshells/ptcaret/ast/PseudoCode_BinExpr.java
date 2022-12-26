package com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.ast;

import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.visitor.DumpVisitor;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.visitor.VoidVisitor;

public class PseudoCode_BinExpr extends PseudoCode_Expr {
    public static enum Operator {
        and, or, xor
    }

    Operator operator;
    PseudoCode_Expr left;
    PseudoCode_Expr right;

    public PseudoCode_BinExpr(PseudoCode_Expr left, Operator operator,
            PseudoCode_Expr right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public Operator getOperator() {
        return operator;
    }

    public PseudoCode_Expr getLeft() {
        return left;
    }

    public PseudoCode_Expr getRight() {
        return right;
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
