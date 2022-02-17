package com.runtimeverification.rvmonitor.logicrepository.plugins.srs;

public class ActionState {
    private int action;
    private State state;
    
    public int getAction() {
        return action;
    }
    
    public State getState() {
        return state;
    }
    
    public ActionState(int action, State state) {
        this.action = action;
        this.state = state;
    }
    
    @Override public String toString() {
        return "[" + action + "] " + state.toString();
    }
    
    @Override
    public int hashCode() {
        return action ^ state.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ActionState)) return false;
        ActionState as = (ActionState) o;
        return(as.action == action && state.equals(as.state));
    }
}