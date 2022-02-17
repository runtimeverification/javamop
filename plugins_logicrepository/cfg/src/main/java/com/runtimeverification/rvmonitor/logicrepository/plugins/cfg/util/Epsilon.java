package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util;

/**
 * A symbol representing the empty string.
 */
public class Epsilon extends Symbol {
    
    /**
     * Construct an Epsilon.
     */
    public Epsilon() {
        super("epsilon");
    }
    
    @Override
    public String toString() {
        return "epsilon";
    }
}
