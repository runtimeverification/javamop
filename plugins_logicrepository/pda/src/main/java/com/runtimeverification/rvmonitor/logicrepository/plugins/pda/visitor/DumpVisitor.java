package com.runtimeverification.rvmonitor.logicrepository.plugins.pda.visitor;

import java.util.HashMap;
import java.util.Set;

import com.runtimeverification.rvmonitor.logicrepository.plugins.pda.ast.Event;
import com.runtimeverification.rvmonitor.logicrepository.plugins.pda.ast.PDA;
import com.runtimeverification.rvmonitor.logicrepository.plugins.pda.ast.StackSymbol;
import com.runtimeverification.rvmonitor.logicrepository.plugins.pda.ast.State;

public class DumpVisitor implements GenericVisitor<String, Object> {

	private String printTransition(HashMap<Event, State> tran, Object arg){
		String ret = "";
		
		ret += "[\n";
		
		State def = null;
		for (Event event : tran.keySet()) {
			State state = tran.get(event);
			
			if(event.isDefault()){
				if(def == null)
					def = state;
			} else {
				ret += event.accept(this, arg) + " -> " + state.accept(this, arg);
				ret += ",\n";
			}
		}
		
		if(def != null){
			ret += "default " + def.accept(this, arg);
			ret += "\n";
		}
		
		ret += "]\n";
		
		return ret;
	}
	
	public String visit(PDA n, Object arg) {
		String ret = "";
		Set<State> allState = n.getTransitions().keySet(); 

		ret += n.getFirstState().accept(this, arg);
		ret += printTransition(n.getTransitions().get(n.getFirstState()), arg);
		
		for (State state : allState){
			if(state.equals(n.getFirstState()))
				continue;
			
			ret += state.accept(this, arg);
			ret += printTransition(n.getTransitions().get(state), arg);
		}

		return ret;
	}

	public String visit(State n, Object arg) {
		String ret = "";
		
		ret += n.getState();
		
		if(n.getQueue().size() != 0){
			ret += " * ";
			for(StackSymbol s : n.getQueue()){
				ret += s.accept(this, arg);
			}
		}
		
		return ret;
	}

	public String visit(StackSymbol n, Object arg) {
		return n.getSymbol();
	}

	public String visit(Event n, Object arg) {
		return n.getName();
	}

}