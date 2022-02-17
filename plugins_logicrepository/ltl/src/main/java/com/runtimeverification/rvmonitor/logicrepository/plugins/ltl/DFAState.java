package com.runtimeverification.rvmonitor.logicrepository.plugins.ltl;

import java.util.HashMap;

/**
 * A single state in a Deterministic Finite Automaton. Interned for consistency in string names.
 */
public class DFAState{
    private static HashMap<String, DFAState> stringRef = new HashMap();
    private static HashMap<DFAState, String> refString = new HashMap();
    
    /**
     * Private constructor to allow for interning.
     */
    private DFAState(){}
    
    /**
     * Returns the interned instance for the given name. Constructs a new instance if none exists.
     * @param name The name of the state.
     * @return The DFAState instance matching the name.
     */
    public static DFAState get(String name){
        if(stringRef.containsKey(name)) return stringRef.get(name);
        DFAState s = new DFAState();
        stringRef.put(name, s);
        refString.put(s, name);
        return s;
    }
    
    /**
     * Retrieve the string associated with this state.
     * @return The name of the state.
     */
    public String toString(){
        return refString.get(this);
    }
}
