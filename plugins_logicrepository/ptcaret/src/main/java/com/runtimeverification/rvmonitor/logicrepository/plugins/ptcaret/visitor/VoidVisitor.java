package com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor;

import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_BinaryFormula;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_False;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_Formula;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_Id;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_True;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_UnaryFormula;

public interface VoidVisitor<A> {

	public void visit(PTCARET_True n, A arg);

	public void visit(PTCARET_False n, A arg);

	public void visit(PTCARET_Id n, A arg);

	public void visit(PTCARET_UnaryFormula n, A arg);

	public void visit(PTCARET_BinaryFormula n, A arg);

	public void visit(PTCARET_Formula n, A arg);
}