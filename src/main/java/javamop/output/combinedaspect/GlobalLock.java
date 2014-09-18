// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.output.combinedaspect;

import javamop.output.MOPVariable;

/**
 * A globally available lock for use in the generated code.
 */
public class GlobalLock {
    private final MOPVariable lock;
    
    /**
     * Assign a variable to be a global lock.
     * @param lock The variable to use.
     */
    public GlobalLock(final MOPVariable lock) {
        this.lock = lock;
    }
    
    /**
     * The variable being used as a lock.
     * @return The lock variable.
     */
    public MOPVariable getName() {
        return lock;
    }
    
    /**
     * Generate declarations for the global lock to be made.
     */
    @Override
    public String toString() {
        String ret = "";
        
        ret += "static ReentrantLock " + lock + " = new ReentrantLock();\n";
        ret += "static Condition " + lock + "_cond = " + lock + ".newCondition();\n";
        
        return ret;
    }
    
}
