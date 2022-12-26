package com.runtimeverification.rvmonitor.java.rvj.output;

import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.PropertyAndHandlers;

public class RVMJavaCodeNoNewLine extends RVMJavaCode {

    public RVMJavaCodeNoNewLine(String code) {
        super(code);
    }

    public RVMJavaCodeNoNewLine(String code, RVMVariable monitorName) {
        super(code, monitorName);
    }

    public RVMJavaCodeNoNewLine(PropertyAndHandlers prop, String code,
            RVMVariable monitorName) {
        super(prop, code, monitorName);
    }

    @Override
    public String toString() {
        String ret = super.toString();
        ret = ret.trim();

        if (ret.length() != 0 && ret.endsWith("\n"))
            ret = ret.substring(0, ret.length() - 1);

        return ret;
    }

}
