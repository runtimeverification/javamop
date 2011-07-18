package javamop.logicpluginshells.javaptcaret.ast;

import java.util.ArrayList;

import javamop.logicpluginshells.javaptcaret.visitor.DumpVisitor;
import javamop.logicpluginshells.javaptcaret.visitor.GenericVisitor;
import javamop.logicpluginshells.javaptcaret.visitor.VoidVisitor;

public class PseudoCode_Assignments extends PseudoCode_Node{
	ArrayList<PseudoCode_Assignment> assignments = new ArrayList<PseudoCode_Assignment>(); 
	
	public PseudoCode_Assignments(){
	}

	public void add(PseudoCode_Assignment assignment){
		assignments.add(assignment);
	}
	
	public ArrayList<PseudoCode_Assignment> getAssignments() {
		return assignments;
	}

	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}

	public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
		return v.visit(this, arg);
	}

	@Override
	public String toString() {
		DumpVisitor visitor = new DumpVisitor();
		String formula = accept(visitor, null);
		return formula;
	}
}
