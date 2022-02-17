package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode;

import java.util.HashMap;
import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMonitorStatistics;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;
import com.runtimeverification.rvmonitor.util.RVMException;

public class RVMonitorStatManager {

    private final HashMap<RVMonitorSpec, RVMonitorStatistics> stats = new HashMap<RVMonitorSpec, RVMonitorStatistics>();

    private final RVMVariable statClass;
    private final RVMVariable statObject;

    public RVMonitorStatManager(String name, List<RVMonitorSpec> specs)
            throws RVMException {
        for (RVMonitorSpec spec : specs) {
            stats.put(spec, new RVMonitorStatistics(name, spec));
        }

        statClass = new RVMVariable(name + "_Statistics");
        statObject = new RVMVariable(name + "_StatisticsInstance");
    }

    public RVMonitorStatistics getStat(RVMonitorSpec spec) {
        return stats.get(spec);
    }

    public String statClass() {
        String ret = "";

        if (!Main.statistics2)
            return ret;

        ret = "class "
                + statClass
                + " extends Thread implements com.runtimeverification.rvmonitor.java.rt.RVMObject {\n";

        ret += "static public long numTotalEvents = 0;\n";
        ret += "static public long numTotalMonitors = 0;\n";

        ret += "public void run() {\n";
        {
            ret += "System.err.println(\"# of total events: \" + " + statClass
                    + ".numTotalEvents);\n";
            ret += "System.err.println(\"# of total monitors: \" + "
                    + statClass + ".numTotalMonitors);\n";
        }
        ret += "}\n";

        ret += "}\n";

        return ret;
    }

    public String incEvent(RVMonitorSpec spec, EventDefinition event) {
        String ret = "";

        if (!Main.statistics2)
            return ret;

        ret += statClass + ".numTotalEvents++;\n";

        return ret;
    }

    public String incMonitor(RVMonitorSpec spec) {
        String ret = "";

        if (!Main.statistics2)
            return ret;

        ret += statClass + ".numTotalMonitors++;\n";

        return ret;
    }

    public String fieldDecl2() {
        String ret = "";

        if (!Main.statistics2)
            return ret;

        ret += "private static " + statClass + " " + statObject + ";\n";

        return ret;
    }

    public String constructor() {
        String ret = "";

        if (!Main.statistics2)
            return ret;

        ret += statObject + " = new " + statClass + "();\n";
        ret += "Runtime.getRuntime().addShutdownHook(" + statObject + ");\n";

        return ret;
    }

    public String fieldDecl() {
        String ret = "";

        if (!Main.statistics)
            return ret;

        ret += "// Declarations for Statistics \n";
        for (RVMonitorStatistics stat : stats.values()) {
            ret += stat.fieldDecl();
        }
        ret += "\n";

        return ret;
    }

    public String advice() {
        String ret = "";

        if (!Main.statistics)
            return ret;

        ret += "\n";
        ret += "// advices for Statistics \n";
        for (RVMonitorStatistics stat : stats.values()) {
            ret += stat.advice();
        }

        return ret;
    }

}
