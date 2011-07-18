package javamop.logicpluginshells.javaptcaret.visitor;

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