package com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor;

import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_BinaryFormula;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_False;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_Formula;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_Id;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_True;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_UnaryFormula;

public class DumpVisitor implements GenericVisitor<String, Object> {

	public String visit(PTCARET_True n, Object arg) {
		return "true";
	}

	public String visit(PTCARET_False n, Object arg) {
		return "false";
	}

	public String visit(PTCARET_Id n, Object arg) {
		return n.getId();
	}

	public String visit(PTCARET_UnaryFormula n, Object arg) {
		String op = "";

		switch (n.getOp()) {
		case not:
			op = "!";
			break;
		case prev:
			op = "(*)";
			break;
		case eventually:
			op = "<*>";
			break;
		case always:
			op = "[*]";
			break;
		case ab_prev:
			op = "(*a)";
			break;
		case ab_eventually:
			op = "<*a>";
			break;
		case ab_always:
			op = "[*a]";
			break;
		case at_begin:
			op = "@b ";
			break;
		case at_call:
			op = "@c ";
			break;
		case always_at_begin:
			op = "[*s@b]";
			break;
		case always_at_call:
			op = "[*s@c]";
			break;
		case always_at_begincall:
			op = "[*s@bc]";
			break;
		case eventually_at_begin:
			op = "<*s@b>";
			break;
		case eventually_at_call:
			op = "<*s@c>";
			break;
		case eventually_at_begincall:
			op = "<*s@bc>";
			break;
		}

		if(n.getFormula() instanceof PTCARET_BinaryFormula)
			return op + "(" + n.getFormula().accept(this, arg) + ")";
		else
			return op + n.getFormula().accept(this, arg);
	}

	public String visit(PTCARET_BinaryFormula n, Object arg) {
		String ret = "";
		String op = "";

		switch (n.getOp()) {
		case iff:
			op = "<->";
			break;
		case implies:
			op = "->";
			break;
		case or:
			op = "||";
			break;
		case xor:
			op = "^";
			break;
		case and:
			op = "&&";
			break;
		case since_at_b:
			op = "Ss@b";
			break;
		case since_at_c:
			op = "Ss@c";
			break;
		case since_at_bc:
			op = "Ss@bc";
			break;
		case ab_since:
			op = "Sa";
			break;
		case since:
			op = "S";
			break;
		}

		if(n.getLeft() instanceof PTCARET_BinaryFormula)
			ret += "(" + n.getLeft().accept(this, arg) + ")";
		else
			ret += n.getLeft().accept(this, arg);
		ret += " " + op + " ";
		
		if(n.getRight() instanceof PTCARET_BinaryFormula)
			ret += "(" + n.getRight().accept(this, arg) + ")";
		else
			ret += n.getRight().accept(this, arg);
		
		return ret;
	}

	public String visit(PTCARET_Formula n, Object arg) {
		return "";
	}

}