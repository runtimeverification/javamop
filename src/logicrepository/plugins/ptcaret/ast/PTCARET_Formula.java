package logicrepository.plugins.ptcaret.ast;

import logicrepository.plugins.ptcaret.visitor.GenericVisitor;
import logicrepository.plugins.ptcaret.visitor.VoidVisitor;

public abstract class PTCARET_Formula {
	public int alpha_index = -1;
	public int beta_index = -1;
	public boolean init_value = false;	
	
	public PTCARET_Formula(){
		
	}
	
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

    @Override
    abstract public String toString();
}
