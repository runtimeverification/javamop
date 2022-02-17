package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util;

/**
 * A special symbol representing the current index into the context-free grammar.
 */
public class Cursor extends Symbol {
    
    /**
     * Construct a Cursor object.
     */
    public Cursor() { 
        super("@@@");
    } 
    
    @Override
    public String toString() { 
        return "@@@";
    }
}
