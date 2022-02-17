package com.runtimeverification.rvmonitor.java.rvj.output.monitor;

import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;

public class VarInOutermostMonitor {

    // RVMVariable tau = new RVMVariable("tau");
    // private final RVMVariable disable;

    public VarInOutermostMonitor(String name, RVMonitorSpec rvmSpec,
            List<EventDefinition> events) {

        // If weak-reference interning is disabled, we cannot use
        // weak-reference's
        // 'disable' flag because there can be multiple weak references for the
        // same
        // monitor. For that reason, the 'disable' field is added to the
        // monitor.
        // this.disable = Main.useWeakRefInterning ? null : new
        // RVMVariable("disable");
    }

    @Override
    public String toString() {
        return "";
    }
}
