package javamop.logicpluginshells.javaptcaret.ast;

import javamop.logicpluginshells.javaptcaret.visitor.DumpVisitor;
import javamop.logicpluginshells.javaptcaret.visitor.GenericVisitor;
import javamop.logicpluginshells.javaptcaret.visitor.VoidVisitor;


public class PseudoCode_VarExpr extends PseudoCode_Expr{
	public static enum Type{
		alpha, beta 
	}
	
	Type type;
	int index;
	
	public PseudoCode_VarExpr(Type type, int index){
		this.type = type;
		this.index = index;
	}
	
	public Type getType(){
		return type;
	}
	
	public int getIndex(){
		return index;
	}
	
	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}

	@Override
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
