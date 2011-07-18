package javamop.logicpluginshells.javaptcaret.visitor;

import java.util.ArrayList;

import javamop.logicpluginshells.javaptcaret.ast.PseudoCode;
import javamop.logicpluginshells.javaptcaret.ast.PseudoCode_Assignment;
import javamop.logicpluginshells.javaptcaret.ast.PseudoCode_Assignments;
import javamop.logicpluginshells.javaptcaret.ast.PseudoCode_BinExpr;
import javamop.logicpluginshells.javaptcaret.ast.PseudoCode_EventExpr;
import javamop.logicpluginshells.javaptcaret.ast.PseudoCode_Expr;
import javamop.logicpluginshells.javaptcaret.ast.PseudoCode_FalseExpr;
import javamop.logicpluginshells.javaptcaret.ast.PseudoCode_Node;
import javamop.logicpluginshells.javaptcaret.ast.PseudoCode_NotExpr;
import javamop.logicpluginshells.javaptcaret.ast.PseudoCode_Output;
import javamop.logicpluginshells.javaptcaret.ast.PseudoCode_TrueExpr;
import javamop.logicpluginshells.javaptcaret.ast.PseudoCode_VarExpr;

public class DumpVisitor implements GenericVisitor<String, Object> {

	public String visit(PseudoCode_Expr n, Object arg) {
		String ret = "";
		
		return ret;
	}

	public String visit(PseudoCode_TrueExpr n, Object arg) {
		String ret = "true";
		
		return ret;
	}

	public String visit(PseudoCode_FalseExpr n, Object arg) {
		String ret = "false";
		
		return ret;
	}
	
	public String visit(PseudoCode_VarExpr n, Object arg) {
		String ret = "";
		
		if(n.getType() == PseudoCode_VarExpr.Type.alpha){
			ret += "$alpha$";
		} else {
			ret += "$beta$";
		}
		
		ret += "[" + n.getIndex() + "]";
		
		return ret;
	}
	
	public String visit(PseudoCode_EventExpr n, Object arg) {
		String ret = "";
		
		ret += n.getName();
		
		return ret;
	}
	
	public String visit(PseudoCode_BinExpr n, Object arg) {
		String ret = "";
	
		ret += "(";
		ret += n.getLeft().accept(this, arg);
		switch(n.getOperator()){
		case or:
			ret += " || ";
			break;
		case xor:
			ret += " ^ ";
			break;
		case and:
			ret += " && ";
			break;
		}
		ret += n.getRight().accept(this, arg);
		ret += ")";
		
		return ret;
	}
	
	public String visit(PseudoCode_NotExpr n, Object arg) {
		String ret = "!" + n.getExpr().accept(this, arg);
		
		return ret;
	}

	public String visit(PseudoCode_Assignments n, Object arg){
		String ret = "";
		
		ArrayList<PseudoCode_Assignment> assignments = n.getAssignments();
		
		for(int i = 0; i < assignments.size(); i++){
			ret += assignments.get(i).accept(this, arg);
		}
		
		return ret;
	}
	
	public String visit(PseudoCode_Assignment n, Object arg){
		String ret = "";
		
		ret += n.getVar().accept(this, arg);
		ret += " := ";
		ret += n.getExpr().accept(this, arg);
		ret += ";\n";
		
		return ret;
	}
	
	public String visit(PseudoCode_Output n, Object arg){
		String ret = "";
		
		ret += "output(";
		ret += n.getExpr().accept(this, arg);
		ret += ")\n";
		
		return ret;
	}
	
	public String visit(PseudoCode n, Object arg){
		String ret = "";

		ret += n.getBefore().accept(this, arg);
		ret += n.getOutput().accept(this, arg);
		ret += n.getAfter().accept(this, arg);
		
		return ret;
	}
	
	public String visit(PseudoCode_Node n, Object arg) {
		String ret = "";
		
		return ret;
	}
}
