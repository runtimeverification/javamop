package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode;

import java.util.List;
import java.util.TreeMap;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;

public class ActivatorManager {

    private final TreeMap<RVMonitorSpec, RVMVariable> activators = new TreeMap<RVMonitorSpec, RVMVariable>();

    public ActivatorManager(String name, List<RVMonitorSpec> specs) {
        for (RVMonitorSpec spec : specs) {
            activators
            .put(spec, new RVMVariable(spec.getName() + "_activated"));
        }
    }

    public RVMVariable getActivator(RVMonitorSpec spec) {
        return activators.get(spec);
    }

    private String getValue(RVMVariable var) {
        StringBuilder s = new StringBuilder();
        s.append(var);
        if (Main.useFineGrainedLock)
            s.append(".get()");
        return s.toString();
    }

    public String getValue(RVMonitorSpec spec) {
        RVMVariable var = this.activators.get(spec);
        return this.getValue(var);
    }

    private String setValue(RVMVariable var, boolean value) {
        String valuestr = value ? "true" : "false";

        StringBuilder s = new StringBuilder();
        s.append(var);
        if (Main.useFineGrainedLock) {
            s.append(".set(");
            s.append(valuestr);
            s.append(')');
        } else {
            s.append(" = ");
            s.append(valuestr);
        }
        return s.toString();
    }

    public String setValue(RVMonitorSpec spec, boolean value) {
        RVMVariable var = this.activators.get(spec);
        return this.setValue(var, value);
    }

    public String decl() {
        boolean isfinal = false;
        String type = "boolean";
        String initvalue = "false";

        if (Main.useFineGrainedLock) {
            isfinal = true;
            type = "AtomicBoolean";
            initvalue = "new AtomicBoolean()";
        }

        String ret = "";

        for (RVMVariable activator : activators.values()) {
            ret += "private static ";
            if (isfinal)
                ret += "final ";
            ret += type + " " + activator + " = " + initvalue + ";\n";
        }

        if (activators.size() > 0)
            ret += "\n";

        return ret;
    }

    public String reset() {
        String ret = "";

        for (RVMVariable activator : activators.values()) {
            ret += this.setValue(activator, false);
            ret += ";\n";
        }

        if (activators.size() > 0)
            ret += "\n";

        return ret;
    }

}
