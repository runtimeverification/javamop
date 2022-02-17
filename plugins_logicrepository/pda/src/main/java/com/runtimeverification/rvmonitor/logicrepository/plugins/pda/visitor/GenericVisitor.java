package com.runtimeverification.rvmonitor.logicrepository.plugins.pda.visitor;

import com.runtimeverification.rvmonitor.logicrepository.plugins.pda.ast.Event;
import com.runtimeverification.rvmonitor.logicrepository.plugins.pda.ast.PDA;
import com.runtimeverification.rvmonitor.logicrepository.plugins.pda.ast.StackSymbol;
import com.runtimeverification.rvmonitor.logicrepository.plugins.pda.ast.State;

public interface GenericVisitor<R, A> {

	public R visit(PDA n, A arg);

	public R visit(State n, A arg);

	public R visit(StackSymbol n, A arg);

	public R visit(Event n, A arg);

}