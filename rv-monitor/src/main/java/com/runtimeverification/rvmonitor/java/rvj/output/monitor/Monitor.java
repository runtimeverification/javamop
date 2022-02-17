package com.runtimeverification.rvmonitor.java.rvj.output.monitor;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import com.runtimeverification.rvmonitor.java.rvj.output.NotImplementedException;
import com.runtimeverification.rvmonitor.java.rvj.output.OptimizedCoenableSet;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMonitorStatistics;
import com.runtimeverification.rvmonitor.java.rvj.output.Util;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeRVType;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.GlobalLock;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.reftree.RefTree;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.PropertyAndHandlers;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;
import com.runtimeverification.rvmonitor.util.RVMException;

public abstract class Monitor {
    RVMVariable monitorName;
    private String outputName;

    boolean isDefined;
    final boolean isOutermost;

    final boolean has__ACTIVITY;
    final boolean has__SKIP;
    boolean handlersHave__SKIP;

    private final MonitorFeatures features;

    public MonitorFeatures getFeatures() {
        return this.features;
    }

    String defaultMessage = "\"Specification ";

    public String getDefaultMessage() {
        return defaultMessage;
    }

    MonitorTermination monitorTermination = null;
    MonitorInfo monitorInfo = null;

    final RVMonitorStatistics stat;

    VarInOutermostMonitor varInOutermostMonitor = null;

    private final HashMap<String, RVMVariable> mopRefs = new HashMap<String, RVMVariable>();

    TreeMap<String, RefTree> refTrees;

    public Monitor(String outputName, RVMonitorSpec rvmSpec,
            OptimizedCoenableSet coenableSet, boolean isOutermost)
                    throws RVMException {
        this.outputName = outputName;
        this.isOutermost = isOutermost;

        this.has__ACTIVITY = rvmSpec.has__ACTIVITY();
        this.has__SKIP = rvmSpec.has__SKIP();

        this.features = new MonitorFeatures(rvmSpec.getParameters());

        this.handlersHave__SKIP = false;
        for (PropertyAndHandlers prop : rvmSpec.getPropertiesAndHandlers()) {
            for (String handler : prop.getHandlers().values()) {
                if (handler.indexOf("__SKIP") != -1) {
                    this.handlersHave__SKIP = true;
                }
            }
        }

        this.stat = new RVMonitorStatistics(outputName, rvmSpec);

        this.defaultMessage += outputName + " has been violated on line \" + "
                + "__LOC"
                + " + \". Documentation for this property can be found at "
                + Util.packageAndNameToUrl(rvmSpec.getPackage(), outputName)
                + "\"";

        for (RVMParameter p : rvmSpec.getParameters()) {
            mopRefs.put(p.getName(), new RVMVariable("RVMRef_" + p.getName()));
        }

    }

    public RVMVariable getRVMonitorRef(RVMParameter p) {
        return mopRefs.get(p.getName());
    }

    RVMVariable activity = new RVMVariable("RVM_activity");

    String activityCode() {
        String ret = "";
        ret += "static android.app.Activity " + activity + ";\n";
        // activity
        ret += "static void setActivity(android.app.Activity a) {\n";
        ret += this.activity + " = a;\n";
        ret += "}\n";
        ret += "\n";
        return ret;
    }

    public boolean has__ACTIVITY() {
        return has__ACTIVITY;
    }

    public RVMVariable getActivityName() {
        return activity;
    }

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    public abstract void setRefTrees(TreeMap<String, RefTree> refTrees);

    public abstract RVMVariable getOutermostName();

    public abstract Set<String> getNames();

    public abstract Set<RVMVariable> getCategoryVars();

    public abstract String Monitoring(RVMVariable monitorVar,
            EventDefinition event, RVMVariable loc, GlobalLock l,
            String outputName, boolean inMonitorSet);

    public abstract MonitorInfo getMonitorInfo();

    @Override
    public abstract String toString();

    public CodeRVType.Monitor getRuntimeType() {
        throw new NotImplementedException();
    }
}
