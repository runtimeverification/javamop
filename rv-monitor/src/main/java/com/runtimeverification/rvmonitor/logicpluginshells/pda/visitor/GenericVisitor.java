package com.runtimeverification.rvmonitor.logicpluginshells.pda.visitor;

import com.runtimeverification.rvmonitor.logicpluginshells.pda.ast.Event;
import com.runtimeverification.rvmonitor.logicpluginshells.pda.ast.PDA;
import com.runtimeverification.rvmonitor.logicpluginshells.pda.ast.StackSymbol;
import com.runtimeverification.rvmonitor.logicpluginshells.pda.ast.State;

public interface GenericVisitor<R, A> {

    public R visit(PDA n, A arg);

    public R visit(State n, A arg);

    public R visit(StackSymbol n, A arg);

    public R visit(Event n, A arg);

}