package com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor;

import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_BinaryFormula;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_False;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_Formula;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_Id;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_True;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_UnaryFormula;

public class InitVisitor implements VoidVisitor<Object> {

	public void visit(PTCARET_True n, Object arg) {
		n.init_value = true;
		return;
	}

	public void visit(PTCARET_False n, Object arg) {
		n.init_value = false;
		return;
	}

	public void visit(PTCARET_Id n, Object arg) {
		n.init_value = false;
		return;
	}

	public void visit(PTCARET_UnaryFormula n, Object arg) {
		n.getFormula().accept(this, arg);
		
		switch (n.getOp()) {
		case not:
			n.init_value = !n.getFormula().init_value; 
			break;
		case prev:
			n.init_value = n.getFormula().init_value;
			break;
		case ab_prev:
			n.init_value = n.getFormula().init_value;
			break;
		}
		return;
	}

	public void visit(PTCARET_BinaryFormula n, Object arg) {
		String op = "";

		PTCARET_Formula left = n.getLeft();
		PTCARET_Formula right = n.getRight();
		
		left.accept(this, arg);
		right.accept(this, arg);

		switch (n.getOp()) {
		case or:
			n.init_value = left.init_value || right.init_value;
			break;
		case xor:
			n.init_value = left.init_value ^ right.init_value;
			break;
		case and:
			n.init_value = left.init_value && right.init_value;
			break;
		case ab_since:
			n.init_value = false;
			break;
		case since:
			n.init_value = false;
			break;
		}
		return;
	}

	public void visit(PTCARET_Formula n, Object arg) {
		return ;
	}

}