package com.runtimeverification.rvmonitor.java.rvj.output.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.RVMNameSpace;
import com.runtimeverification.rvmonitor.java.rvj.output.OptimizedCoenableSet;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMonitorStatistics;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.reftree.RefTree;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameterSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;

public class MonitorTermination {

    private final RVMParameters parameters;
    private final List<EventDefinition> events;
    private final OptimizedCoenableSet coenableSet;

    private final RVMonitorStatistics stat;

    final HashMap<RVMParameter, RVMVariable> references = new HashMap<RVMParameter, RVMVariable>();
    private TreeMap<String, RefTree> refTrees;

    public MonitorTermination(String name, RVMonitorSpec rvmSpec,
            List<EventDefinition> events, OptimizedCoenableSet coenableSet) {
        this.parameters = rvmSpec.getParameters();
        this.events = events;
        this.coenableSet = coenableSet;

        this.stat = new RVMonitorStatistics(name, rvmSpec);
    }

    public String getRefType(RVMParameter p) {
        if (refTrees != null) {
            RefTree refTree = refTrees.get(p.getType().toString());
            return refTree.getResultType();
        }
        return p.getType().toString();
    }

    public void setRefTrees(TreeMap<String, RefTree> refTrees) {
        this.refTrees = refTrees;

        for (RVMParameter param : parameters) {
            references.put(param, new RVMVariable("RVMRef_" + param.getName()));
        }
    }

    public String copyAliveParameters(RVMVariable toMonitor,
            RVMVariable fromMonitor) {
        String ret = "";

        for (int j = 0; j < coenableSet.getParameterGroups().size(); j++) {
            RVMVariable alive_parameter = new RVMVariable("alive_parameters_"
                    + j);

            ret += toMonitor + "." + alive_parameter + " = " + fromMonitor
                    + "." + alive_parameter + ";\n";
        }

        return ret;
    }

    public String getCode(MonitorFeatures features, String decl,
            String lastEventVar) {
        if (lastEventVar == null)
            lastEventVar = "RVM_lastevent";

        String synch = features.isSelfSynchronizationNeeded() ? " synchronized "
                : " ";
        String ret = "";

        {
            boolean generalcase = features.isNonFinalWeakRefsInMonitorNeeded()
                    || features.isFinalWeakRefsInMonitorNeeded();
            RVMParameters needed = features.getRememberedParameters();
            for (RVMParameter param : parameters) {
                if (generalcase || needed.contains(param)) {
                    if (!features.isNonFinalWeakRefsInMonitorNeeded())
                        ret += "final ";
                    ret += getRefType(param) + " " + references.get(param)
                            + ";";
                } else
                    ret += "// " + references.get(param)
                    + " was suppressed to reduce memory overhead";
                ret += "\n";
            }
        }
        ret += "\n";

        for (int j = 0; j < coenableSet.getParameterGroups().size(); j++) {
            ret += "//alive_parameters_" + j + " = "
                    + coenableSet.getParameterGroups().get(j) + "\n";
            ret += "boolean " + new RVMVariable("alive_parameters_" + j)
            + " = true;\n";
        }
        ret += "\n";

        ret += "@Override\n";
        ret += "protected" + synch
                + "final void terminateInternal(int idnum) {\n";

        if (decl != null)
            ret += decl + "\n";

        ret += "switch(idnum){\n";
        for (int i = 0; i < parameters.size(); i++) {
            ret += "case " + i + ":\n";

            for (int j = 0; j < coenableSet.getParameterGroups().size(); j++) {
                if (coenableSet.getParameterGroups().get(j)
                        .contains(parameters.get(i)))
                    ret += RVMNameSpace.getRVMVar("alive_parameters_" + j)
                    + " = false;\n";
            }

            ret += "break;\n";
        }
        ret += "}\n";

        // do endObject event
        ret += "switch(" + lastEventVar + ") {\n";
        ret += "case -1:\n";
        ret += "return;\n";
        for (EventDefinition event : this.events) {
            ret += "case " + event.getIdNum() + ":\n";
            ret += "//" + event.getId() + "\n";

            RVMParameterSet simplifiedDNF = coenableSet
                    .getEnable(event.getId());
            if (simplifiedDNF.size() == 1 && simplifiedDNF.get(0).size() == 0) {
                ret += "return;\n";
            } else {
                boolean firstFlag = true;

                ret += "//";
                for (RVMParameters param : simplifiedDNF) {
                    if (firstFlag) {
                        firstFlag = false;
                    } else {
                        ret += " || ";
                    }
                    boolean firstFlag2 = true;
                    for (RVMParameter s : param) {
                        if (firstFlag2) {
                            firstFlag2 = false;
                        } else {
                            ret += " && ";
                        }

                        ret += "alive_" + s.getName();
                    }
                }
                ret += "\n";

                ret += "if(!(";
                firstFlag = true;
                for (RVMParameters param : simplifiedDNF) {
                    if (firstFlag) {
                        firstFlag = false;
                    } else {
                        ret += " || ";
                    }
                    ret += "alive_parameters_"
                            + coenableSet.getParameterGroups().getIdnum(param);
                }
                ret += ")){\n";
                ret += "RVM_terminated = true;\n";

                if (Main.statistics) {
                    ret += stat.incTerminatedMonitor();
                }

                ret += "return;\n";
                ret += "}\n";
                ret += "break;\n";
                ret += "\n";
            }
        }
        ret += "}\n";

        ret += "return;\n";

        ret += "}\n";
        ret += "\n";

        if (Main.statistics) {
            ret += "protected void finalize() throws Throwable {\n";
            ret += "try {\n";
            ret += stat.incCollectedMonitor();
            ret += "} finally {\n";
            ret += "super.finalize();\n";
            ret += "}\n";
            ret += "}\n";
        }

        return ret;
    }
}
