package logicrepository.plugins.ptcaret.visitor;

import logicrepository.plugins.ptcaret.ast.PTCARET_BinaryFormula;
import logicrepository.plugins.ptcaret.ast.PTCARET_False;
import logicrepository.plugins.ptcaret.ast.PTCARET_Formula;
import logicrepository.plugins.ptcaret.ast.PTCARET_Id;
import logicrepository.plugins.ptcaret.ast.PTCARET_True;
import logicrepository.plugins.ptcaret.ast.PTCARET_UnaryFormula;

public class NumberingVisitor implements VoidVisitor<Object> {
	public int alpha_counter = 0;
	public int beta_counter = 0;
	
	public NumberingVisitor(){
		alpha_counter = 0;
		beta_counter = 0;
	}

	public void visit(PTCARET_True n, Object arg) {
		return;
	}

	public void visit(PTCARET_False n, Object arg) {
		return;
	}

	public void visit(PTCARET_Id n, Object arg) {
		return;
	}

	public void visit(PTCARET_UnaryFormula n, Object arg) {
		n.getFormula().accept(this, arg);
		
		switch (n.getOp()) {
		case not:
			break;
		case prev:
			n.alpha_index = alpha_counter++;
			break;
		case ab_prev:
			n.beta_index = beta_counter++;
			break;
		}
		return;
	}

	public void visit(PTCARET_BinaryFormula n, Object arg) {
		String op = "";

		n.getLeft().accept(this, arg);
		
		n.getRight().accept(this, arg);

		switch (n.getOp()) {
		case ab_since:
			n.beta_index = beta_counter++;
			break;
		case since:
			n.alpha_index = alpha_counter++;
			break;
		}
		return;
	}

	public void visit(PTCARET_Formula n, Object arg) {
		return ;
	}

}