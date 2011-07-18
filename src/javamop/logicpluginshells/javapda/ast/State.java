package javamop.logicpluginshells.javapda.ast;

import java.util.ArrayList;

import javamop.logicpluginshells.javapda.visitor.DumpVisitor;
import javamop.logicpluginshells.javapda.visitor.GenericVisitor;
import javamop.logicpluginshells.javapda.visitor.VoidVisitor;


public class State {
	String state;
	ArrayList<StackSymbol> queue;

	public State(String state, ArrayList<StackSymbol> queue) {
		this.state = state;
		this.queue = queue;
	}

	public String getState() {
		return state;
	}
	
	public ArrayList<StackSymbol> getQueue(){
		return queue;
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
	public boolean equals(Object o) {
		if (!(o instanceof State))
			return false;
		if (!state.equals(((State) o).getState()))
			return false;
		
		return queue.equals(((State) o).getQueue());
	}

}
