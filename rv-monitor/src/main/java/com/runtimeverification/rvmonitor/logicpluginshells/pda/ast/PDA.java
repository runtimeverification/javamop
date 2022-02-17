package com.runtimeverification.rvmonitor.logicpluginshells.pda.ast;

import java.util.HashMap;

import com.runtimeverification.rvmonitor.logicpluginshells.pda.visitor.DumpVisitor;
import com.runtimeverification.rvmonitor.logicpluginshells.pda.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.logicpluginshells.pda.visitor.VoidVisitor;

public class PDA {
    State firstState = null;
    HashMap<State, HashMap<Event, State>> transitions;

    public PDA() {
        this.transitions = new HashMap<State, HashMap<Event, State>>();
    }

    public void put(State state, HashMap<Event, State> transition) {
        if (firstState == null)
            firstState = state;

        if (this.transitions.containsKey(state)) {
            HashMap<Event, State> oldTran = this.transitions.get(state);

            oldTran.putAll(transition);
        } else {
            this.transitions.put(state, transition);
        }
    }

    public State getFirstState() {
        return firstState;
    }

    public HashMap<State, HashMap<Event, State>> getTransitions() {
        return transitions;
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
}
