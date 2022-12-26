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

public interface VoidVisitor<A> {

    public void visit(PseudoCode_Expr n, A arg);

    public void visit(PseudoCode_TrueExpr n, A arg);

    public void visit(PseudoCode_FalseExpr n, A arg);

    public void visit(PseudoCode_VarExpr n, A arg);

    public void visit(PseudoCode_EventExpr n, A arg);

    public void visit(PseudoCode_BinExpr n, A arg);

    public void visit(PseudoCode_NotExpr n, A arg);

    public void visit(PseudoCode_Assignments n, A arg);

    public void visit(PseudoCode_Assignment n, A arg);

    public void visit(PseudoCode_Output n, A arg);

    public void visit(PseudoCode n, A arg);

    public void visit(PseudoCode_Node n, A arg);
}