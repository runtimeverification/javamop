package com.runtimeverification.rvmonitor.java.rvj.output;

import java.util.HashMap;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.PropertyAndHandlers;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;

public class RVMonitorStatistics {
    private final RVMVariable numMonitor;
    private final RVMVariable collectedMonitor;
    private final RVMVariable terminatedMonitor;
    private final HashMap<String, RVMVariable> eventVars = new HashMap<String, RVMVariable>();
    private final HashMap<PropertyAndHandlers, HashMap<String, RVMVariable>> categoryVars = new HashMap<PropertyAndHandlers, HashMap<String, RVMVariable>>();
    private final HashMap<RVMParameter, RVMVariable> paramVars = new HashMap<RVMParameter, RVMVariable>();

    private final String specName;

    public RVMonitorStatistics(String name, RVMonitorSpec rvmSpec) {
        this.specName = rvmSpec.getName();
        this.numMonitor = new RVMVariable(rvmSpec.getName() + "_Monitor_num");
        this.collectedMonitor = new RVMVariable(rvmSpec.getName()
                + "_CollectedMonitor_num");
        this.terminatedMonitor = new RVMVariable(rvmSpec.getName()
                + "_TerminatedMonitor_num");

        for (EventDefinition event : rvmSpec.getEvents()) {
            RVMVariable eventVar = new RVMVariable(rvmSpec.getName() + "_"
                    + event.getId() + "_num");
            this.eventVars.put(event.getId(), eventVar);
        }

        for (PropertyAndHandlers prop : rvmSpec.getPropertiesAndHandlers()) {
            HashMap<String, RVMVariable> categoryVarsforProp = new HashMap<String, RVMVariable>();
            for (String key : prop.getHandlers().keySet()) {
                RVMVariable categoryVar = new RVMVariable(rvmSpec.getName()
                        + "_" + prop.getPropertyId() + "_" + key + "_num");
                categoryVarsforProp.put(key, categoryVar);
            }
            this.categoryVars.put(prop, categoryVarsforProp);
        }

        for (RVMParameter param : rvmSpec.getParameters()) {
            RVMVariable paramVar = new RVMVariable(rvmSpec.getName() + "_"
                    + param.getName() + "_set");
            this.paramVars.put(param, paramVar);
        }
    }

    public String fieldDecl() {
        String ret = "";
        if (!Main.statistics)
            return ret;

        ret += "protected static long " + numMonitor + " = 0;\n";
        ret += "protected static long " + collectedMonitor + " = 0;\n";
        ret += "protected static long " + terminatedMonitor + " = 0;\n";

        for (RVMVariable eventVar : eventVars.values()) {
            ret += "protected static long " + eventVar + " = 0;\n";
        }

        for (HashMap<String, RVMVariable> categoryVarsforProp : categoryVars
                .values()) {
            for (RVMVariable categoryVar : categoryVarsforProp.values()) {
                ret += "protected static long " + categoryVar + " = 0;\n";
            }
        }

        /* removed for buggy behavior */
        // for(RVMVariable paramVar : paramVars.values()){
        // ret += "static HashSet " + paramVar + " = new HashSet();\n";
        // }

        return ret;
    }

    /**
     * Get public method declarations required for RV-Monitor statistics code
     *
     * @return method declaration bodies
     **/
    public String methodDecl() {
        String ret = "";
        if (!Main.statistics)
            return ret;

        // Generate getters for main statistics (total, collected, terminated
        // monitor count)
        ret += "\n\npublic static long getTotalMonitorCount() {\n" + "return "
                + numMonitor + ";\n}\n";
        ret += "public static long getCollectedMonitorCount() {\n" + "return "
                + collectedMonitor + ";\n}\n";
        ret += "public static long getTerminatedMonitorCount() {\n" + "return "
                + terminatedMonitor + ";\n}\n";

        // Create a getter for statistics on each event's occurences as
        // HashMap from event name to number of occurences
        ret += "public static Map<String, Long> getEventCounters() {\n";
        ret += "HashMap<String, Long> eventCounters = new HashMap<String, Long>();\n";
        for (String eventId : eventVars.keySet()) {
            ret += "eventCounters.put(\"" + eventId + "\", "
                    + eventVars.get(eventId) + ");\n";
        }
        ret += "return eventCounters;\n}\n";

        // Same as above for categories rather than events
        ret += "public static Map<String, Long> getCategoryCounters() {\n";
        ret += "HashMap<String, Long> categoryCounters = new HashMap<String, Long>();\n";
        for (HashMap<String, RVMVariable> categoryVarsforProp : categoryVars
                .values()) {
            for (String categoryId : categoryVarsforProp.keySet()) {
                ret += "categoryCounters.put(\"" + categoryId + "\", "
                        + categoryVarsforProp.get(categoryId) + ");\n";
            }
        }
        ret += "return categoryCounters;\n}\n";

        return ret;
    }

    public String paramInc(RVMParameter param) {
        String ret = "";
        if (!Main.statistics)
            return ret;

        /* removed for buggy behavior */
        // RVMVariable paramVar = null;
        //
        // for(RVMParameter p : paramVars.keySet()){
        // if(p.getName().equals(param.getName()))
        // paramVar = paramVars.get(p);
        // }
        //
        // if(paramVar != null)
        // ret += paramVar + ".add(" + param.getName() + ");\n";

        return ret;
    }

    public String eventInc(String eventName) {
        String ret = "";
        if (!Main.statistics)
            return ret;

        RVMVariable eventVar = eventVars.get(eventName);

        ret += specName + "Monitor." + eventVar + "++;\n";

        return ret;
    }

    public String categoryInc(PropertyAndHandlers prop, String category) {
        String ret = "";
        if (!Main.statistics)
            return ret;

        RVMVariable categoryVar = categoryVars.get(prop).get(category);

        ret += categoryVar + "++;\n";

        return ret;
    }

    public String incNumMonitor() {
        String ret = "";
        if (!Main.statistics)
            return ret;

        ret += numMonitor + "++;\n";

        return ret;
    }

    public String incCollectedMonitor() {
        String ret = "";
        if (!Main.statistics)
            return ret;

        ret += collectedMonitor + "++;\n";

        return ret;
    }

    public String incTerminatedMonitor() {
        String ret = "";
        if (!Main.statistics)
            return ret;

        ret += terminatedMonitor + "++;\n";

        return ret;
    }

    public String advice() {
        String ret = "";
        if (!Main.statistics)
            return ret;

        ret += "after () : execution(* *.main(..)) {\n";

        ret += "System.err.println(\"== " + this.specName + " ==\");\n";
        ret += "System.err.println(\"#monitors: \" + " + numMonitor + ");\n";

        for (String eventName : eventVars.keySet()) {
            RVMVariable eventVar = eventVars.get(eventName);
            ret += "System.err.println(\"#event - " + eventName + ": \" + "
                    + eventVar + ");\n";
        }

        for (PropertyAndHandlers prop : categoryVars.keySet()) {
            HashMap<String, RVMVariable> categoryVarsforProp = categoryVars
                    .get(prop);
            for (String categoryName : categoryVarsforProp.keySet()) {
                RVMVariable categoryVar = categoryVarsforProp.get(categoryName);
                ret += "System.err.println(\"#category - prop "
                        + prop.getPropertyId() + " - " + categoryName
                        + ": \" + " + categoryVar + ");\n";
            }
        }

        // for(RVMParameter param : paramVars.keySet()){
        // RVMVariable paramVar = paramVars.get(param);
        // ret += "System.err.println(\"#parameter - " + param.getName() +
        // ": \" + " + paramVar + ".size()" + ");\n";
        // }

        ret += "}\n";

        return ret;
    }

}
