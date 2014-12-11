// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.output.combinedaspect;

import java.util.List;

import javamop.util.MOPException;
import javamop.output.MOPVariable;
import javamop.output.combinedaspect.event.EventManager;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.mopspec.JavaMOPSpec;

/**
 * The combined aspects of all the properties in a specification file.
 */
public class CombinedAspect {
    private final String name;
    private final MOPVariable mapManager;
    private final boolean versionedStack;
    
    private final List<JavaMOPSpec> specs;
    public final MOPStatManager statManager;
    public final LockManager lockManager;
    private final EventManager eventManager;

    /**
     * Construct the combined aspect.
     * @param name The name of the combined monitoring aspects.
     * @param mopSpecFile The specifications to monitor.
     * @param versionedStack Whether or not to maintain extra information about the call stack.
     */
    public CombinedAspect(final String name, final MOPSpecFile mopSpecFile,
                          final boolean versionedStack) throws MOPException {
        this.name = name + "MonitorAspect";
        this.versionedStack = versionedStack;
        
        this.specs = mopSpecFile.getSpecs();
        this.statManager = new MOPStatManager(name, this.specs);
        this.lockManager = new LockManager(name, this.specs);

        this.eventManager = new EventManager(name, this.specs, this);
        
        this.mapManager = new MOPVariable(name + "MapManager");

    }
    
    /**
     * The name of the combined aspect.
     * @return The name.
     */
    public String getAspectName() {
        return name;
    }
    
    /**
     * The name of the file to put the combined aspect in.
     * @return The file name.
     */
    public String getFileName() {
        return name.substring(0, name.length() - "MonitorAspect".length());
    }
    
    /**
     * Code to use the combined aspect with the backing of RV-Montior.
     * @return The generated code.
     */
    @Override
    public String toString() {
        String ret = "";
        ret += this.statManager.statClass();
        ret += "public aspect " + this.name +
        " implements com.runtimeverification.rvmonitor.java.rt.RVMObject {\n";
        
        // Constructor
        ret += "public " + this.name + "(){\n";
        
        ret += this.eventManager.printConstructor();
        
        //ret += mapManager + " = " + "new javamoprt.map.MOPMapManager();\n";
        //ret += mapManager + ".start();\n";
        
        //ret += this.statManager.constructor();
        
        //ret += constructor();
        //ret += initCache();
        
        ret += "}\n";
        ret += "\n";
        
        ret += this.statManager.advice();
        
        ret += this.lockManager.decl();
        
        ret += this.eventManager.advices();
        
        
        ret += "}\n";
        return ret;
    }
}
