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

public interface GenericVisitor<R, A> {

    public R visit(PseudoCode_Expr n, A arg);

    public R visit(PseudoCode_TrueExpr n, A arg);

    public R visit(PseudoCode_FalseExpr n, A arg);

    public R visit(PseudoCode_VarExpr n, A arg);

    public R visit(PseudoCode_EventExpr n, A arg);

    public R visit(PseudoCode_BinExpr n, A arg);

    public R visit(PseudoCode_NotExpr n, A arg);

    public R visit(PseudoCode_Assignments n, A arg);

    public R visit(PseudoCode_Assignment n, A arg);

    public R visit(PseudoCode_Output n, A arg);

    public R visit(PseudoCode n, A arg);

    public R visit(PseudoCode_Node n, A arg);
}