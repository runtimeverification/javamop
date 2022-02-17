package com.runtimeverification.rvmonitor.logicrepository.plugins.fsm.parser.ast;

import java.util.HashMap;
import java.util.Set;
import java.util.Collection;

/**
 * All the outgoing transitions from a single Finite State Machine state.
 * Contains a mapping from symbols read to state transitioned to.
 */
public class Transition {
    //Essentially this class is just a wrapper for a HashMap
    //in order to provide a different toString method
    
    private HashMap<Symbol, State> contents; 
    
    /**
     * Construct a Transition map with no transitions.
     */
    public Transition() {
        contents = new HashMap<Symbol, State>();
    }
    
    /**
     * Add a transition to the map.
     * @param e The symbol read in on the transition.
     * @param s The state transitioned to.
     */
    public void put(Symbol e, State s) {
        contents.put(e,s);
    }
    
    /**
     * Test if the map has an outgoing transition on the given symbol.
     * @param e The symbol to look for transitions on.
     * @return If there is an outgoing transition with that symbol.
     */
    public boolean containsSymbol(Symbol e) {
        return contents.containsKey(e);
    }
    
    /**
     * Retrieve the outgoing transition for a particular symbol.
     * @param e The symbol to get the transition for.
     * @return The state transitioned to after reading the symbol.
     */
    public State get(Symbol e) {
        return contents.get(e);
    }
    
    /**
     * Retrieve all symbols that have outgoing transitions.
     * @return A set of the symbols with transitions.
     */
    public Set<Symbol> keySet() {
        return contents.keySet();
    }
    
    /**
     * Retrieve all the states reachable in one step from this state.
     * Possibly contains duplicates.
     * @return All the states that can be transitioned to.
     */
    public Collection<State> values() {
        return contents.values();
    }
    
    /**
     * The number of transitions in the map.
     * @return 
     */
    public int size() {
        return contents.size();
    }
    
    /**
     * Whether the state has transitions or not.
     * @return {@code true} if no transitions, {@code false} if there are.
     */
    public boolean isEmpty() {
        return contents.size() == 0;
    }
    
    /**
     * A formatted String of all the state transitions.
     * @return A string representation of all the state transitions.
     */
    @Override
    public String toString() {
        if(contents.keySet().size() == 0) return "";
        String ret = "";
        for(Symbol event : contents.keySet()) {
            if(event == null) ret += "  default " + contents.get(null) + "\n";
            else ret += "  " + event + " -> " + contents.get(event) + "\n";
        }
        return ret.substring(0, ret.length() - 1);
    }
}
