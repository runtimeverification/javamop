package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode;

import java.util.HashMap;
import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;
import com.runtimeverification.rvmonitor.util.RVMException;

public class TimestampManager {

    private final HashMap<RVMonitorSpec, RVMVariable> timestamps = new HashMap<RVMonitorSpec, RVMVariable>();

    public TimestampManager(String name, List<RVMonitorSpec> specs)
            throws RVMException {
        for (RVMonitorSpec spec : specs) {
            if (spec.isGeneral())
                timestamps.put(spec, new RVMVariable(spec.getName()
                        + "_timestamp"));
        }
    }

    public RVMVariable getTimestamp(RVMonitorSpec spec) {
        return timestamps.get(spec);
    }

    public String decl() {
        String ret = "";

        if (timestamps.size() <= 0)
            return ret;

        ret += "// Declarations for Timestamps \n";
        for (RVMVariable timestamp : timestamps.values()) {
            if (Main.useFineGrainedLock)
                ret += "private static final AtomicLong " + timestamp
                + " = new AtomicLong(1);\n";
            else
                ret += "private static long " + timestamp + " = 1;\n";
        }
        ret += "\n";

        return ret;
    }

    public String reset() {
        String ret = "";

        if (timestamps.size() <= 0)
            return ret;

        for (RVMVariable timestamp : timestamps.values()) {
            if (Main.useFineGrainedLock)
                ret += timestamp + ".set(1);\n";
            else
                ret += timestamp + " = 1;\n";
        }
        ret += "\n";

        return ret;
    }

}
