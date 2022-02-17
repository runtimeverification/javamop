package com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast;

import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor.DumpVisitor;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor.VoidVisitor;

public class PTCARET_Id extends PTCARET_Formula{
	String id;
	
	public PTCARET_Id(String id){
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
    public final String toString() {
        DumpVisitor visitor = new DumpVisitor();
        String formula = accept(visitor, null);
        return formula;
    }

}

