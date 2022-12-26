package com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor;

import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.Code;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_BinaryFormula;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_False;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_Formula;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_Id;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_True;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast.PTCARET_UnaryFormula;

public class CodeGenVisitor implements GenericVisitor<Code, Object> {

	public Code visit(PTCARET_True n, Object arg) {
		Code ret = new Code();
		ret.output = "true";
		return ret;
	}

	public Code visit(PTCARET_False n, Object arg) {
		Code ret = new Code();
		ret.output = "false";
		return ret;
	}

	public Code visit(PTCARET_Id n, Object arg) {
		Code ret = new Code();
		ret.output = n.getId();
		return ret;
	}

	public Code visit(PTCARET_UnaryFormula n, Object arg) {
		Code ret = new Code();
		Code sub = n.getFormula().accept(this, arg);
		
		ret.beforeCode = sub.beforeCode;
		ret.afterCode = sub.afterCode;

		try {
			switch (n.getOp()) {
			case not:
				ret.output += "!" + sub.output;
				break;
			case prev:
				if(n.alpha_index == -1)
					throw new Exception("Numbering Error");
					
				ret.afterCode = "$alpha$[" + n.alpha_index + "] := " + sub.output + ";\n" + ret.afterCode; 
				ret.output += "$alpha$[" + n.alpha_index + "]";
				break;
			case ab_prev:
				if(n.beta_index == -1)
					throw new Exception("Numbering Error");
				
				ret.afterCode = "$beta$[" + n.beta_index + "] := " + sub.output + ";\n" + ret.afterCode; 
				ret.output += "$beta$[" + n.beta_index + "]";
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	public Code visit(PTCARET_BinaryFormula n, Object arg) {
		Code ret = new Code();
		Code left = n.getLeft().accept(this, arg);
		Code right = n.getRight().accept(this, arg);

		ret.beforeCode += left.beforeCode + right.beforeCode;
		ret.afterCode += right.afterCode + left.afterCode;
		
		try {
			switch (n.getOp()) {
			case or:
				ret.output += "(" + left.output + " || " + right.output + ")";
				break;
			case xor:
				ret.output += "(" + left.output + " ^ " + right.output + ")";
				break;
			case and:
				ret.output += "(" + left.output + " && " + right.output + ")";
				break;
			case ab_since:
				if(n.beta_index == -1)
					throw new Exception("Numbering Error");

				ret.beforeCode += "$beta$[" + n.beta_index + "] := (" + right.output + " || " + left.output + " && $beta$[" + n.beta_index + "]);\n";
				ret.output += "$beta$[" + n.beta_index + "]";
				break;
			case since:
				if(n.alpha_index == -1)
					throw new Exception("Numbering Error");
				
				ret.beforeCode += "$alpha$[" + n.alpha_index + "] := (" + right.output + " || " + left.output + " && $alpha$[" + n.alpha_index + "]);\n"; 
				ret.output += "$alpha$[" + n.alpha_index + "]";
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	public Code visit(PTCARET_Formula n, Object arg) {
		return new Code();
	}

}