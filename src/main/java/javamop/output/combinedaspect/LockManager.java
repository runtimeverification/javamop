// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.output.combinedaspect;

import java.util.List;

import javamop.util.MOPException;
import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.JavaMOPSpec;

/**
 * A manager for a global lock.
 */
public class LockManager {
    private final GlobalLock lock;
    
    /**
     * Construct a lock manager with a named over some specifications.
     * @param name The name of the lock.
     * @param specs The specifications managed by the lock.
     */
    public LockManager(final String name, final List<JavaMOPSpec> specs) throws MOPException {
        lock = new GlobalLock(new MOPVariable(name + "_MOPLock"));
    }
    
    /**
     * The lock managed by this LockManager.
     * @return The lock.
     */
    public GlobalLock getLock(){
        return lock;
    }
    
    /**
     * Declarations for the lock.
     * @return Java source code with declarations.
     */
    public String decl() {
        String ret = "";
        
        ret += "// Declarations for the Lock \n";
        ret += lock;
        ret += "\n";
        
        return ret;
    }
    
}
