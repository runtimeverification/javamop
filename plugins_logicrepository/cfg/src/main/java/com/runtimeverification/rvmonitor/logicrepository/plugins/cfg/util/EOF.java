package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util;

/**
 * A special terminal describing the end of file.
 */
public class EOF extends Terminal {
    
    /**
     * Construct an EOF terminal.
     */
    public EOF() { 
        super("@EOF");
    }
}
