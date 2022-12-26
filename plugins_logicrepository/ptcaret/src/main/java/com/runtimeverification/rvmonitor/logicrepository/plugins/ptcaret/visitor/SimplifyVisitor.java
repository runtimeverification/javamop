package com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor;

import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_BinaryFormula;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_False;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_Formula;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_Id;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_True;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_UnaryFormula;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.parser.PTCARETParser;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.parser.ParseException;

public class SimplifyVisitor implements GenericVisitor<PTCARET_Formula, Object> {

	public PTCARET_Formula visit(PTCARET_True n, Object arg) {
		return n;
	}

	public PTCARET_Formula visit(PTCARET_False n, Object arg) {
		return n;
	}

	public PTCARET_Formula visit(PTCARET_Id n, Object arg) {
		return n;
	}

	public PTCARET_Formula visit(PTCARET_UnaryFormula n, Object arg) {
		PTCARET_Formula ret = n;
		PTCARET_Formula sub = n.getFormula().accept(this, arg);

		try {
			switch (n.getOp()) {
			case not:
				if (sub instanceof PTCARET_UnaryFormula && ((PTCARET_UnaryFormula) sub).getOp() == PTCARET_UnaryFormula.Operator.not) {
					ret = ((PTCARET_UnaryFormula) sub).getFormula();
				} else if (sub instanceof PTCARET_BinaryFormula && ((PTCARET_BinaryFormula) sub).getOp() == PTCARET_BinaryFormula.Operator.and) {
					ret = PTCARETParser.parse("!(" + ((PTCARET_BinaryFormula) sub).getLeft() + ") || !(" + ((PTCARET_BinaryFormula) sub).getRight() + ")");
					ret = ret.accept(this, arg);
				} else if (sub instanceof PTCARET_BinaryFormula && ((PTCARET_BinaryFormula) sub).getOp() == PTCARET_BinaryFormula.Operator.or) {
					ret = PTCARETParser.parse("!(" + ((PTCARET_BinaryFormula) sub).getLeft() + ") && !(" + ((PTCARET_BinaryFormula) sub).getRight() + ")");
					ret = ret.accept(this, arg);
				} else
					ret = new PTCARET_UnaryFormula(sub, n.getOp());
				break;
			case prev:
				ret = new PTCARET_UnaryFormula(sub, n.getOp());
				break;
			case eventually:
				ret = PTCARETParser.parse("true S (" + sub + ")");
				break;
			case always:
				ret = PTCARETParser.parse("!<*>(!(" + sub + "))");
				ret = ret.accept(this, arg);
				break;
			case ab_prev:
				ret = new PTCARET_UnaryFormula(sub, n.getOp());
				break;
			case ab_eventually:
				ret = PTCARETParser.parse("true Sa (" + sub + ")");
				break;
			case ab_always:
				ret = PTCARETParser.parse("!<*a>(!(" + sub + "))");
				ret = ret.accept(this, arg);
				break;
			case at_begin:
				ret = PTCARETParser.parse("(begin -> (" + sub + ")) /\\ (!begin -> ((*)(begin -> (" + sub + "))) Sa begin)");
				break;
			case at_call:
				ret = PTCARETParser.parse("@b (*)(" + sub + ")");
				ret = ret.accept(this, arg);
				break;
			case always_at_begin:
				ret = PTCARETParser.parse("!<*s@b>!(" + sub + ")");
				ret = ret.accept(this, arg);
				break;
			case always_at_call:
				ret = PTCARETParser.parse("!<*s@c>!(" + sub + ")");
				ret = ret.accept(this, arg);
				break;
			case always_at_begincall:
				ret = PTCARETParser.parse("!<*s@bc>!(" + sub + ")");
				ret = ret.accept(this, arg);
				break;
			case eventually_at_begin:
				ret = PTCARETParser.parse("true Ss@b (" + sub + ")");
				ret = ret.accept(this, arg);
				break;
			case eventually_at_call:
				ret = PTCARETParser.parse("true Ss@c (" + sub + ")");
				ret = ret.accept(this, arg);
				break;
			case eventually_at_begincall:
				ret = PTCARETParser.parse("true Ss@bc (" + sub + ")");
				ret = ret.accept(this, arg);
				break;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return ret;
	}

	public PTCARET_Formula visit(PTCARET_BinaryFormula n, Object arg) {
		PTCARET_Formula ret = n;
		PTCARET_Formula left = n.getLeft().accept(this, arg);
		PTCARET_Formula right = n.getRight().accept(this, arg);

		try {
			switch (n.getOp()) {
			case iff:
				ret = PTCARETParser.parse("((" + left + ") && (" + right + ")) || (" + "!(" + left + ") && " + "!(" + right + "))");
				ret = ret.accept(this, arg);
				break;
			case implies:
				ret = PTCARETParser.parse("!(" + left + ") || (" + right + ")");
				ret = ret.accept(this, arg);
				break;
			case or:
				if (left instanceof PTCARET_True || right instanceof PTCARET_True)
					ret = new PTCARET_True();
				else
					ret = new PTCARET_BinaryFormula(left, right, PTCARET_BinaryFormula.Operator.or);
				break;
			case xor:
				ret = new PTCARET_BinaryFormula(left, right, PTCARET_BinaryFormula.Operator.xor);
				break;
			case and:
				if (left instanceof PTCARET_True)
					ret = right;
				else if (right instanceof PTCARET_True)
					ret = left;
				else
					ret = new PTCARET_BinaryFormula(left, right, PTCARET_BinaryFormula.Operator.and);
				break;
			case since_at_b:
				ret = PTCARETParser.parse("(begin -> (" + left + ")) Sa (begin /\\ (" + right + "))");
				ret = ret.accept(this, arg);
				break;
			case since_at_c:
				ret = PTCARETParser.parse("(call -> (" + left + ")) Sa (begin /\\ (*)(" + right + "))");
				ret = ret.accept(this, arg);
				break;
			case since_at_bc:
				ret = PTCARETParser.parse("((begin || call) -> (" + left + ")) Sa ((begin || call) /\\ (" + right + "))");
				ret = ret.accept(this, arg);
				break;
			case ab_since:
				ret = new PTCARET_BinaryFormula(left, right, PTCARET_BinaryFormula.Operator.ab_since);
				break;
			case since:
				ret = new PTCARET_BinaryFormula(left, right, PTCARET_BinaryFormula.Operator.since);
				break;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return ret;
	}

	public PTCARET_Formula visit(PTCARET_Formula n, Object arg) {
		return n;
	}

}