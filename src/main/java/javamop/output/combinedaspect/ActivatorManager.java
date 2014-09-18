// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.output.combinedaspect;

import java.util.List;
import java.util.TreeMap;

import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.JavaMOPSpec;

/**
 * Generates code that allows activating/deactivating properties.
 */
public class ActivatorManager {
    
    private final List<JavaMOPSpec> specs;
    private final TreeMap<JavaMOPSpec, MOPVariable> activators = 
        new TreeMap<JavaMOPSpec, MOPVariable>();
    
    /**
     * Construct an ActivatorManager managing the given specifications.
     * @param specs The specifications to manage.
     */
    public ActivatorManager(final List<JavaMOPSpec> specs) {
        this.specs = specs;
        for (JavaMOPSpec spec : specs) {
            activators.put(spec, new MOPVariable(spec.getName() + "_activated"));
        }
    }
    
    /**
     * The variable managing the activation of a particular specification.
     * @param spec The specification.
     * @return The variable managing {@code spec}.
     */
    public MOPVariable getActivator(final JavaMOPSpec spec) {
        return activators.get(spec);
    }
    
    /**
     * The code for the declarations that the ActivationManager needs.
     * @return The Java source code for the declarations.
     */
    public String decl() {
        String ret = "";
        
        for (MOPVariable activator : activators.values()) {
            ret += "static boolean " + activator + " = false;\n";
        }
        
        if (activators.size() > 0)
            ret += "\n";
        
        return ret;
    }
    
    /**
     * Code to reset all the activation variables.
     * @return The Java source code to reset the declarations.
     */
    public String reset() {
        String ret = "";
        
        for (MOPVariable activator : activators.values()) {
            ret += activator + " = false;\n";
        }
        
        if (activators.size() > 0)
            ret += "\n";
        
        return ret;
    }
    
}
