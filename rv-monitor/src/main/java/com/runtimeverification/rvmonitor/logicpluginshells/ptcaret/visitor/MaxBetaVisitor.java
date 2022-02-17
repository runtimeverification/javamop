package com.runtimeverification.rvmonitor.logicpluginshells.ptcaret.visitor;

import java.util.ArrayList;

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

public class MaxBetaVisitor implements GenericVisitor<Integer, Object> {

    @Override
    public Integer visit(PseudoCode_Expr n, Object arg) {
        return new Integer(0);
    }

    @Override
    public Integer visit(PseudoCode_TrueExpr n, Object arg) {
        return new Integer(0);
    }

    @Override
    public Integer visit(PseudoCode_FalseExpr n, Object arg) {
        return new Integer(0);
    }

    @Override
    public Integer visit(PseudoCode_VarExpr n, Object arg) {
        Integer ret = new Integer(0);

        if (n.getType() == PseudoCode_VarExpr.Type.beta) {
            ret = new Integer(n.getIndex() + 1);
        }

        return ret;
    }

    @Override
    public Integer visit(PseudoCode_EventExpr n, Object arg) {
        return new Integer(0);
    }

    @Override
    public Integer visit(PseudoCode_BinExpr n, Object arg) {
        Integer ret = n.getLeft().accept(this, arg);

        Integer temp = n.getRight().accept(this, arg);
        if (temp > ret)
            ret = temp;

        return ret;
    }

    @Override
    public Integer visit(PseudoCode_NotExpr n, Object arg) {
        return n.getExpr().accept(this, arg);
    }

    @Override
    public Integer visit(PseudoCode_Assignments n, Object arg) {
        Integer ret = new Integer(0);

        ArrayList<PseudoCode_Assignment> assignments = n.getAssignments();

        for (int i = 0; i < assignments.size(); i++) {
            Integer temp = assignments.get(i).accept(this, arg);

            if (temp > ret)
                ret = temp;
        }

        return ret;
    }

    @Override
    public Integer visit(PseudoCode_Assignment n, Object arg) {
        Integer ret = n.getVar().accept(this, arg);

        Integer temp = n.getExpr().accept(this, arg);

        if (temp > ret)
            ret = temp;

        return ret;
    }

    @Override
    public Integer visit(PseudoCode_Output n, Object arg) {
        return n.getExpr().accept(this, arg);
    }

    @Override
    public Integer visit(PseudoCode n, Object arg) {
        Integer ret = n.getOutput().accept(this, arg);

        Integer temp = n.getBefore().accept(this, arg);
        if (temp > ret)
            ret = temp;

        temp = n.getAfter().accept(this, arg);
        if (temp > ret)
            ret = temp;

        return ret;
    }

    @Override
    public Integer visit(PseudoCode_Node n, Object arg) {
        return new Integer(0);
    }
}
