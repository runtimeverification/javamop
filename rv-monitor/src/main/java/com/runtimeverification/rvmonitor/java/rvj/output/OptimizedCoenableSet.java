package com.runtimeverification.rvmonitor.java.rvj.output;

import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameterSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;
import com.runtimeverification.rvmonitor.util.RVMBooleanSimplifier;

public class OptimizedCoenableSet extends CoEnableSet {
    private final RVMParameterSet parameterGroups = new RVMParameterSet();

    public OptimizedCoenableSet(CoEnableSet coenableSet) {
        super(coenableSet.events, coenableSet.specParameters);
        this.contents = coenableSet.contents;
        optimize();
    }

    private void optimize() {
        for (EventDefinition event : this.events) {
            RVMParameterSet enables = contents.get(event.getId());

            if (enables == null)
                enables = getFullEnable();

            RVMParameterSet simplifiedDNF = RVMBooleanSimplifier.simplify(
                    enables, this.specParameters);

            for (RVMParameters param : simplifiedDNF) {
                if (param.size() > 0)
                    parameterGroups.add(param);
            }

            contents.put(event.getId(), simplifiedDNF);
        }
    }

    public RVMParameterSet getParameterGroups() {
        return parameterGroups;
    }
}
