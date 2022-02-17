package com.runtimeverification.rvmonitor.logicrepository.plugins.fsm.parser.ast;

import java.util.HashMap;

/**
 * A Finite State Machine state. Accessed using {@link #get(String s)} to limit
 * the State instances, so they can be compared with == and !=.
 */
public class State {
    private static HashMap<String, State> stringRef;
    private static HashMap<State, String> refString;
    
    static {
        stringRef = new HashMap<String, State>();
        refString = new HashMap<State, String>();
    }
    
    /**
     * Retrieve the state instance associated with the given name.
     * @param s The name of the state.
     * @return The unique state instance with that name.
     */
    public static State get(String s) {
        if(stringRef.containsKey(s)) {
            return stringRef.get(s);
        }
        else {
            State ret = new State();
            stringRef.put(s, ret);
            refString.put(ret, s);
            return ret;
        }
    }
    
    /**
     * The name of the state.
     * @return The name the state was initialized with.
     */
    @Override
    public String toString() {
        return refString.get(this);
    }
}
