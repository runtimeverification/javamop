// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.output.combinedaspect;

import java.util.HashMap;

import javamop.JavaMOPMain;
import javamop.output.MOPVariable;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.PropertyAndHandlers;

/**
 * Statistics for a single property.
 */
public class MOPStatistics {
    private final String aspectName;
    
    private final MOPVariable numMonitor;
    private final MOPVariable collectedMonitor;
    private final MOPVariable terminatedMonitor;
    private final HashMap<String, MOPVariable> eventVars = new HashMap<String, MOPVariable>();
    private final HashMap<PropertyAndHandlers, HashMap<String, MOPVariable>> categoryVars = 
        new HashMap<PropertyAndHandlers, HashMap<String, MOPVariable>>();
    private final HashMap<MOPParameter, MOPVariable> paramVars = 
        new HashMap<MOPParameter, MOPVariable>();
    
    private final String specName;

    /**
     * Construct statistics variables for a single property.
     * @param name The name of the property.
     * @param mopSpec The specification that has statistics being collected on it.
     */
    public MOPStatistics(final String name, final JavaMOPSpec mopSpec) {
        this.aspectName = name + "MonitorAspect";
        this.specName = mopSpec.getName();
        this.numMonitor = new MOPVariable(mopSpec.getName() + "_Monitor_num");
        this.collectedMonitor = new MOPVariable(mopSpec.getName() + "_CollectedMonitor_num");
        this.terminatedMonitor = new MOPVariable(mopSpec.getName() + "_TerminatedMonitor_num");

        for (EventDefinition event : mopSpec.getEvents()) {
            MOPVariable eventVar = new MOPVariable(mopSpec.getName() + "_" + event.getId() + 
                "_num");
            this.eventVars.put(event.getId(), eventVar);
        }
        
        for (PropertyAndHandlers prop : mopSpec.getPropertiesAndHandlers()) {
            HashMap<String, MOPVariable> categoryVarsforProp = new HashMap<String, MOPVariable>();
            for (String key : prop.getHandlers().keySet()) {
                MOPVariable categoryVar = new MOPVariable(mopSpec.getName() + "_" + 
                    prop.getPropertyId() + "_" + key + "_num");
                categoryVarsforProp.put(key, categoryVar);
            }
            this.categoryVars.put(prop, categoryVarsforProp);
        }
        
        for (MOPParameter param : mopSpec.getParameters()) {
            MOPVariable paramVar = new MOPVariable(mopSpec.getName() + "_" + param.getName() + 
                "_set");
            this.paramVars.put(param, paramVar);
        }
    }
    
    /**
     * Fields used in maintaining statistics for this property.
     * @return Java source code declarations for maintaining statistics.
     */
    public String fieldDecl() {
        String ret = "";
        if (!JavaMOPMain.options.statistics)
            return ret;
        
        ret += "static long " + numMonitor + " = 0;\n";
        ret += "static long " + collectedMonitor + " = 0;\n";
        ret += "static long " + terminatedMonitor + " = 0;\n";
        
        for (MOPVariable eventVar : eventVars.values()) {
            ret += "static long " + eventVar + " = 0;\n";
        }
        
        for (HashMap<String, MOPVariable> categoryVarsforProp : categoryVars.values()) {
            for (MOPVariable categoryVar : categoryVarsforProp.values()) {
                ret += "static long " + categoryVar + " = 0;\n";
            }
        }
        
        return ret;
    }
    
    public String paramInc(final MOPParameter param) {
        return "";
    }
    
    /**
     * Code to increment the counter for an event.
     * @param eventName The name of the event that was incremented.
     * @return Java source code to increment the counter for receiving events.
     */
    public String eventInc(final String eventName) {
        String ret = "";
        if (!JavaMOPMain.options.statistics)
            return ret;
        
        MOPVariable eventVar = eventVars.get(eventName);
        
        ret += eventVar + "++;\n";
        
        return ret;
    }
    
    public String categoryInc(final PropertyAndHandlers prop, final String category) {
        String ret = "";
        if (!JavaMOPMain.options.statistics)
            return ret;
        
        MOPVariable categoryVar = categoryVars.get(prop).get(category);
        
        ret += aspectName + "." + categoryVar + "++;\n";
        
        return ret;
    }
    
    /**
     * Code to increment the counter for number of monitors generated.
     * @return Java code to increment the counter for a monitor being generated.
     */
    public String incNumMonitor() {
        String ret = "";
        if (!JavaMOPMain.options.statistics)
            return ret;
        
        ret += aspectName + "." + numMonitor + "++;\n";
        
        return ret;
    }
    
    /**
     * Code to increment the counter for collected monitors.
     * @return Java code to increment the counter for a monitor being collected.
     */
    public String incCollectedMonitor() {
        String ret = "";
        if (!JavaMOPMain.options.statistics)
            return ret;
        
        ret += aspectName + "." + collectedMonitor + "++;\n";
        
        return ret;
    }
    
    /**
     * Code to increment the counter for terminated monitors.
     * @return Java code to increment the counter for a monitor being terminated.
     */
    public String incTerminatedMonitor() {
        String ret = "";
        if (!JavaMOPMain.options.statistics)
            return ret;
        
        ret += aspectName + "." + terminatedMonitor + "++;\n";
        
        return ret;
    }
    
    /**
     * AspectJ advice code to display the statistics after termination of the program.
     * @return AspectJ/Java source code displaying statistics.
     */
    public String advice() {
        String ret = "";
        if (!JavaMOPMain.options.statistics)
            return ret;
        
        ret += "after () : execution(* *.main(..)) {\n";
        
        ret += "System.err.println(\"== " + this.specName + " ==\");\n";
        ret += "System.err.println(\"#monitors: \" + " + numMonitor + ");\n";
        
        for (String eventName : eventVars.keySet()) {
            MOPVariable eventVar = eventVars.get(eventName);
            ret += "System.err.println(\"#event - " + eventName + ": \" + " + eventVar + ");\n";
        }
        
        for (PropertyAndHandlers prop : categoryVars.keySet()) {
            HashMap<String, MOPVariable> categoryVarsforProp = categoryVars.get(prop);
            for (String categoryName : categoryVarsforProp.keySet()) {
                MOPVariable categoryVar = categoryVarsforProp.get(categoryName);
                ret += "System.err.println(\"#category - prop " + prop.getPropertyId() + " - " + 
                    categoryName + ": \" + " + categoryVar + ");\n";
            }
        }
        
        // for(MOPParameter param : paramVars.keySet()){
        // MOPVariable paramVar = paramVars.get(param);
        // ret += "System.err.println(\"#parameter - " + param.getName() +
        // ": \" + " + paramVar + ".size()" + ");\n";
        // }
        
        ret += "}\n";
        
        return ret;
    }
    
}
