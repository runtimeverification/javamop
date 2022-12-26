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

    private final String totalMonitorMethodName = "getTotalMonitorCount";
    private final String collectedMonitorMethodName = "getCollectedMonitorCount";
    private final String terminatedMonitorMethodName = "getTerminatedMonitorCount";
    private final String eventCountersMethodName = "getEventCounters";
    private final String categoryCountersMethodName = "getCategoryCounters";

    private final HashMap<String, MOPVariable> eventVars = new HashMap<String, MOPVariable>();
    private final HashMap<PropertyAndHandlers, HashMap<String, MOPVariable>> categoryVars =
        new HashMap<PropertyAndHandlers, HashMap<String, MOPVariable>>();
    private final HashMap<MOPParameter, MOPVariable> paramVars =
        new HashMap<MOPParameter, MOPVariable>();

    private final String specName;
    private final boolean isRaw;

    /**
     * Construct statistics variables for a single property.
     * @param name The name of the property.
     * @param mopSpec The specification that has statistics being collected on it.
     */
    public MOPStatistics(final String name, final JavaMOPSpec mopSpec) {
        this.specName = mopSpec.getName();
        this.isRaw = mopSpec.isRaw();

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
     * AspectJ advice code to display the statistics after termination of the program.
     * @return AspectJ/Java source code displaying statistics.
     */
    public String advice() {
        String ret = "";
        if (!JavaMOPMain.options.statistics)
            return ret;

        // String monitorName = specName + (isRaw ? "Raw" : "") + "Monitor";
        String monitorName = specName + "Monitor";

        // ret += "after () : execution(* *.main(..)) {\n";
        ret += "after () : execution(* org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(..)) {\n";

        ret += "System.err.println(\"==start " + this.specName + " ==\");\n";

        ret += "System.err.println(\"#monitors: \" + " + monitorName + "." + this.totalMonitorMethodName + "());\n";
        ret += "System.err.println(\"#collected monitors: \" + " + monitorName + "."
                + this.collectedMonitorMethodName + "());\n";
        ret += "System.err.println(\"#terminated monitors: \" + " + monitorName + "."
                + this.terminatedMonitorMethodName + "());\n";

        for (String eventName : eventVars.keySet()) {
            ret += "System.err.println(\"#event - " + eventName + ": \" + " + monitorName + "."
                    + this.eventCountersMethodName + "()" + ".get(\""  + eventName + "\")" +");\n";
        }

        for (PropertyAndHandlers prop : categoryVars.keySet()) {
            HashMap<String, MOPVariable> categoryVarsforProp = categoryVars.get(prop);
            for (String categoryName : categoryVarsforProp.keySet()) {
                ret += "System.err.println(\"#category - prop " + prop.getPropertyId() + " - " +
                    categoryName + ": \" + " + monitorName + "."
                        + this.categoryCountersMethodName + "()" + ".get(\""  + categoryName + "\")" +");\n";
            }
        }

        ret += "System.err.println(\"==end " + this.specName + " ==\");\n";
        // for(MOPParameter param : paramVars.keySet()){
        // MOPVariable paramVar = paramVars.get(param);
        // ret += "System.err.println(\"#parameter - " + param.getName() +
        // ": \" + " + paramVar + ".size()" + ");\n";
        // }

        ret += "}\n";

        return ret;
    }

}
