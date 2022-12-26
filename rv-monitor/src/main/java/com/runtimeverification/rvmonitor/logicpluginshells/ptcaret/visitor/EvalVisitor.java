package com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.visitor;

import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.ast.PseudoCode;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.ast.PseudoCode_Assignment;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.ast.PseudoCode_Assignments;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.ast.PseudoCode_BinExpr;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.ast.PseudoCode_EventExpr;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.ast.PseudoCode_Expr;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.ast.PseudoCode_FalseExpr;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.ast.PseudoCode_Node;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.ast.PseudoCode_NotExpr;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.ast.PseudoCode_Output;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.ast.PseudoCode_TrueExpr;
import com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.ast.PseudoCode_VarExpr;

public class EvalVisitor implements GenericVisitor<PseudoCode_Node, String> {

    @Override
    public PseudoCode_Node visit(PseudoCode_Expr n, String arg) {
        return n;
    }

    @Override
    public PseudoCode_Node visit(PseudoCode_TrueExpr n, String arg) {
        return n;
    }

    @Override
    public PseudoCode_Node visit(PseudoCode_FalseExpr n, String arg) {
        return n;
    }

    @Override
    public PseudoCode_Node visit(PseudoCode_VarExpr n, String arg) {
        return n;
    }

    @Override
    public PseudoCode_Node visit(PseudoCode_EventExpr n, String arg) {
        if (n.getName().equals(arg))
            return new PseudoCode_TrueExpr();
        else
            return new PseudoCode_FalseExpr();
    }

    @Override
    public PseudoCode_Node visit(PseudoCode_BinExpr n, String arg) {
        PseudoCode_Expr left = (PseudoCode_Expr) n.getLeft().accept(this, arg);
        PseudoCode_Expr right = (PseudoCode_Expr) n.getRight()
                .accept(this, arg);
        PseudoCode_Node ret = new PseudoCode_BinExpr(left, n.getOperator(),
                right);

        switch (n.getOperator()) {
        case or:
            if (left instanceof PseudoCode_TrueExpr
                    || right instanceof PseudoCode_TrueExpr)
                ret = new PseudoCode_TrueExpr();
            else if (left instanceof PseudoCode_FalseExpr
                    && right instanceof PseudoCode_FalseExpr)
                ret = new PseudoCode_FalseExpr();
            else if (left instanceof PseudoCode_FalseExpr)
                ret = right;
            else if (right instanceof PseudoCode_FalseExpr)
                ret = left;
            break;
        case xor:
            if (left instanceof PseudoCode_TrueExpr
                    && right instanceof PseudoCode_TrueExpr)
                ret = new PseudoCode_FalseExpr();
            else if (left instanceof PseudoCode_FalseExpr
                    && right instanceof PseudoCode_FalseExpr)
                ret = new PseudoCode_FalseExpr();
            else if (left instanceof PseudoCode_TrueExpr
                    && right instanceof PseudoCode_FalseExpr)
                ret = new PseudoCode_TrueExpr();
            else if (left instanceof PseudoCode_FalseExpr
                    && right instanceof PseudoCode_TrueExpr)
                ret = new PseudoCode_TrueExpr();
            else if (left instanceof PseudoCode_FalseExpr)
                ret = right;
            else if (left instanceof PseudoCode_TrueExpr)
                ret = new PseudoCode_NotExpr(right).accept(this, arg);
            else if (right instanceof PseudoCode_FalseExpr)
                ret = left;
            else if (right instanceof PseudoCode_TrueExpr)
                ret = new PseudoCode_NotExpr(left).accept(this, arg);
            break;
        case and:
            if (left instanceof PseudoCode_TrueExpr
                    && right instanceof PseudoCode_TrueExpr)
                ret = new PseudoCode_TrueExpr();
            else if (left instanceof PseudoCode_FalseExpr
                    || right instanceof PseudoCode_FalseExpr)
                ret = new PseudoCode_FalseExpr();
            else if (left instanceof PseudoCode_TrueExpr)
                ret = right;
            else if (right instanceof PseudoCode_TrueExpr)
                ret = left;
            break;
        }

        return ret;
    }

    @Override
    public PseudoCode_Node visit(PseudoCode_NotExpr n, String arg) {
        PseudoCode_Expr sub = (PseudoCode_Expr) n.getExpr().accept(this, arg);

        if (sub instanceof PseudoCode_NotExpr) {
            return ((PseudoCode_NotExpr) sub).getExpr();
        } else if (sub instanceof PseudoCode_TrueExpr) {
            return new PseudoCode_FalseExpr();
        } else if (sub instanceof PseudoCode_FalseExpr) {
            return new PseudoCode_TrueExpr();
        } else {
            return new PseudoCode_NotExpr(sub);
        }
    }

    @Override
    public PseudoCode_Node visit(PseudoCode_Assignments n, String arg) {
        PseudoCode_Assignments ret = new PseudoCode_Assignments();

        for (int i = 0; i < n.getAssignments().size(); i++) {
            PseudoCode_Assignment assignment = n.getAssignments().get(i);
            assignment = (PseudoCode_Assignment) assignment.accept(this, arg);

            ret.add(assignment);
        }

        return ret;
    }

    @Override
    public PseudoCode_Node visit(PseudoCode_Assignment n, String arg) {
        PseudoCode_Expr value = (PseudoCode_Expr) n.getExpr().accept(this, arg);

        return new PseudoCode_Assignment(n.getVar(), value);
    }

    @Override
    public PseudoCode_Node visit(PseudoCode_Output n, String arg) {
        PseudoCode_Expr value = (PseudoCode_Expr) n.getExpr().accept(this, arg);

        return new PseudoCode_Output(value);
    }

    @Override
    public PseudoCode_Node visit(PseudoCode n, String arg) {
        PseudoCode_Assignments before = (PseudoCode_Assignments) n.getBefore()
                .accept(this, arg);
        PseudoCode_Assignments after = (PseudoCode_Assignments) n.getAfter()
                .accept(this, arg);
        PseudoCode_Output output = (PseudoCode_Output) n.getOutput().accept(
                this, arg);

        return new PseudoCode(before, output, after);
    }

    @Override
    public PseudoCode_Node visit(PseudoCode_Node n, String arg) {
        return n;
    }

}
