package com.runtimeverification.rvmonitor.logicrepository.plugins.fsm.parser.ast;

import java.util.HashMap;

/**
 * A Finite State Machine alphabet symbol. Accessed using {@link #get(String s)} 
 * to limit the Symbol instances, so they can be compared with == and !=.
 */
public class Symbol {
    private static HashMap<String, Symbol> stringRef;
    private static HashMap<Symbol, String> refString;
    
    static {
        stringRef = new HashMap<String, Symbol>();
        refString = new HashMap<Symbol, String>();
    }
    
    /**
     * Retrieve the symbol instance associated with the given name.
     * @param s The name of the symbol.
     * @return The unique instance with that name.
     */
    public static Symbol get(String s) {
        if(stringRef.containsKey(s)) {
            return stringRef.get(s);
        }
        else {
            Symbol ret = new Symbol();
            stringRef.put(s, ret);
            refString.put(ret, s);
            return ret;
        }
    }
    
    /**
     * The name of the symbol.
     * @return The name the symbol was initialized with.
     */
    @Override
    public String toString() {
        return refString.get(this);
    }
}
