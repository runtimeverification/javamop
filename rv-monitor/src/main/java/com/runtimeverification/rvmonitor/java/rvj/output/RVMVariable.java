package com.runtimeverification.rvmonitor.java.rvj.output;

import com.runtimeverification.rvmonitor.java.rvj.RVMNameSpace;

public class RVMVariable {
    private RVMVariable pred;
    private final String varName;

    public RVMVariable(String varName) {
        this.varName = varName;
    }

    public RVMVariable(RVMVariable pred, String varName) {
        this.pred = pred;
        this.varName = varName;
    }

    public String getVarName() {
        return varName;
    }

    @Override
    public String toString() {
        if (pred != null)
            return pred.toString() + "." + RVMNameSpace.getRVMVar(varName);
        else
            return RVMNameSpace.getRVMVar(varName);
    }

}
