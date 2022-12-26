package com.runtimeverification.rvmonitor.logicrepository.plugins.pda.ast;

import com.runtimeverification.rvmonitor.logicrepository.plugins.pda.visitor.DumpVisitor;
import com.runtimeverification.rvmonitor.logicrepository.plugins.pda.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.logicrepository.plugins.pda.visitor.VoidVisitor;

public class DefaultEvent extends Event {
	public DefaultEvent(){
		super();
		this.isDefault = true;
	}

    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

    @Override
    public final String toString() {
        DumpVisitor visitor = new DumpVisitor();
        String formula = accept(visitor, null);
        return formula;
    }

	@Override
	public boolean equals(Object o){
		if(!(o instanceof DefaultEvent))
			return false;
		return this == o;
	}
}
