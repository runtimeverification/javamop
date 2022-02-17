package com.runtimeverification.rvmonitor.java.rvj.output.monitor;

import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;

public class MonitorInfo {
    private final RVMParameters parameters;
    private final RVMVariable monitorInfo = new RVMVariable("monitorInfo");

    private final boolean isFullBinding;
    private final boolean isConnected;

    public MonitorInfo(RVMonitorSpec rvmSpec) {
        this.parameters = rvmSpec.getParameters();
        this.isFullBinding = rvmSpec.isFullBinding();
        this.isConnected = rvmSpec.isConnected();
    }

    public String newInfo(RVMVariable monitorVar, RVMParameters vars) {
        String ret = "";

        ret += monitorVar
                + "."
                + monitorInfo
                + " = "
                + "new com.runtimeverification.rvmonitor.java.rt.RVMMonitorInfo();\n";

        if (isFullBinding) {
            if (vars.size() == parameters.size())
                ret += monitorVar + "." + monitorInfo + ".isFullParam" + " = "
                        + "true;\n";
            else
                ret += monitorVar + "." + monitorInfo + ".isFullParam" + " = "
                        + "false;\n";
        }

        if (isConnected) {
            ret += monitorVar + "." + monitorInfo + ".connected" + " = "
                    + "new int[" + parameters.size() + "];\n";

            for (int i = 0; i < parameters.size(); i++) {
                RVMParameter p = parameters.get(i);
                if (vars.contains(p))
                    ret += monitorVar + "." + monitorInfo + ".connected" + "["
                            + i + "] = -1;\n";
                else
                    ret += monitorVar + "." + monitorInfo + ".connected" + "["
                            + i + "] = -2;\n";
            }
        }

        return ret;
    }

    public String initConnected() {
        String ret = "";

        if (isConnected) {
            for (int i = 0; i < parameters.size(); i++) {
                ret += "this" + "." + monitorInfo + ".connected" + "[" + i
                        + "]" + " = ";
                ret += "(" + "this" + "." + monitorInfo + ".connected" + "["
                        + i + "]" + " == " + "-2" + ")" + "?";
                ret += "-2" + ":" + "-1";
                ret += ";\n";
            }
        }

        return ret;
    }

    public String copy(RVMVariable monitorVar) {
        String ret = "";

        ret += monitorVar + "." + monitorInfo + " = "
                + "(com.runtimeverification.rvmonitor.java.rt.RVMMonitorInfo)"
                + "this." + monitorInfo + ".clone();\n";

        return ret;
    }

    public String copy(RVMVariable targetVar, RVMVariable origVar) {
        return copy(targetVar.toString(), origVar.toString());
    }

    public String copy(String targetVar, String origVar) {
        String ret = "";

        ret += targetVar + "." + monitorInfo + " = "
                + "(com.runtimeverification.rvmonitor.java.rt.RVMMonitorInfo)"
                + origVar + "." + monitorInfo + ".clone();\n";

        return ret;
    }

    public String expand(RVMVariable monitorVar, SuffixMonitor suffixMonitor,
            RVMParameters vars) {
        String ret = "";
        RVMVariable monitor = new RVMVariable("monitor");
        RVMVariable monitorList = new RVMVariable("monitorList");

        boolean isFullParam = vars.size() == parameters.size();

        if (isFullBinding)
            ret += monitorVar + "." + monitorInfo + ".isFullParam" + " = "
                    + isFullParam + ";\n";
        if (isConnected) {
            for (int i = 0; i < parameters.size(); i++) {
                RVMParameter p = parameters.get(i);
                if (vars.contains(p)) {
                    ret += monitorVar + "." + monitorInfo + ".connected" + "["
                            + i + "]" + " = ";
                    ret += "(" + monitorVar + "." + monitorInfo + ".connected"
                            + "[" + i + "]" + " == " + "-2" + ")" + "?";
                    ret += "-1" + ":";
                    ret += monitorVar + "." + monitorInfo + ".connected" + "["
                            + i + "]";
                    ret += ";\n";
                }
            }
        }

        if (suffixMonitor.isDefined) {
            Monitor innerMonitor = suffixMonitor.innerMonitor;

            ret += "for(" + innerMonitor.getOutermostName() + " " + monitor
                    + " : " + monitorVar + "." + monitorList + "){\n";
            if (isFullBinding)
                ret += monitor + "." + monitorInfo + ".isFullParam" + " = "
                        + isFullParam + ";\n";
            if (isConnected) {
                for (int i = 0; i < parameters.size(); i++) {
                    RVMParameter p = parameters.get(i);
                    if (vars.contains(p)) {
                        ret += monitor + "." + monitorInfo + ".connected" + "["
                                + i + "]" + " = ";
                        ret += "(" + monitor + "." + monitorInfo + ".connected"
                                + "[" + i + "]" + " == " + "-2" + ")" + "?";
                        ret += "-1" + ":";
                        ret += monitor + "." + monitorInfo + ".connected" + "["
                                + i + "]";
                        ret += ";\n";
                    }
                }
            }

            ret += "}\n";
        }

        return ret;
    }

    RVMVariable RVMNum1 = new RVMVariable("RVMNum1");
    RVMVariable RVMNum2 = new RVMVariable("RVMNum2");
    RVMVariable RVMNum3 = new RVMVariable("RVMNum3");
    RVMVariable RVMNumTemp = new RVMVariable("RVMNumTemp");

    public String union(RVMParameters params) {
        String ret = "";

        if (isConnected) {
            if (params.size() > 1) {
                ret += "int " + RVMNum1 + ";\n";
                ret += "int " + RVMNum2 + ";\n";
                ret += "int " + RVMNum3 + ";\n";
                ret += "int " + RVMNumTemp + ";\n";
                ret += "\n";

                RVMParameter p = params.get(0);
                for (int i = 1; i < params.size(); i++)
                    ret += union(p, params.get(i));
            }
        }

        return ret;
    }

    protected String union(RVMParameter p1, RVMParameter p2) {
        return union(findNum(p1), findNum(p2));
    }

    protected String union(int i1, int i2) {
        String ret = "";

        if (i1 < 0 || i2 < 0)
            return ret;

        ret += RVMNum1 + " = " + i1 + ";\n";
        ret += "while(" + monitorInfo + ".connected[" + RVMNum1 + "]"
                + " >= 0){\n";
        ret += RVMNum1 + " = " + monitorInfo + ".connected[" + RVMNum1 + "]"
                + ";\n";
        ret += "}\n";

        ret += RVMNum3 + " = " + i1 + ";\n";
        ret += "while(" + monitorInfo + ".connected[" + RVMNum3 + "]"
                + " >= 0){\n";
        ret += RVMNumTemp + " = " + RVMNum3 + ";\n";
        ret += RVMNum3 + " = " + monitorInfo + ".connected[" + RVMNum3 + "]"
                + ";\n";
        ret += monitorInfo + ".connected[" + RVMNumTemp + "]" + " = " + RVMNum1
                + ";\n";
        ret += "}\n";

        ret += RVMNum2 + " = " + i2 + ";\n";
        ret += "while(" + monitorInfo + ".connected[" + RVMNum2 + "]"
                + " >= 0){\n";
        ret += RVMNum2 + " = " + monitorInfo + ".connected[" + RVMNum2 + "]"
                + ";\n";
        ret += "}\n";

        ret += RVMNum3 + " = " + i2 + ";\n";
        ret += "while(" + monitorInfo + ".connected[" + RVMNum3 + "]"
                + " >= 0){\n";
        ret += RVMNumTemp + " = " + RVMNum3 + ";\n";
        ret += RVMNum3 + " = " + monitorInfo + ".connected[" + RVMNum3 + "]"
                + ";\n";
        ret += monitorInfo + ".connected[" + RVMNumTemp + "]" + " = " + RVMNum2
                + ";\n";
        ret += "}\n";

        ret += "if(" + RVMNum1 + " < " + RVMNum2 + "){\n";
        ret += monitorInfo + ".connected[" + RVMNum2 + "]" + " = " + RVMNum1
                + ";\n";
        ret += "} else if (" + RVMNum1 + " > " + RVMNum2 + "){\n";
        ret += monitorInfo + ".connected[" + RVMNum1 + "]" + " = " + RVMNum2
                + ";\n";
        ret += "}\n";

        return ret;
    }

    protected int findNum(RVMParameter p) {
        for (int i = 0; i < parameters.size(); i++) {
            RVMParameter param = parameters.get(i);

            if (param.getName().equals(p.getName()))
                return i;
        }
        return -1;
    }

    public String computeCategory(String statements) {
        String ret = "";
        RVMVariable RVMFlag = new RVMVariable("RVMFlag");
        RVMVariable i = new RVMVariable("i");

        if (statements == null || statements.length() == 0)
            return ret;

        if (isFullBinding) {
            ret += "if(" + "this." + monitorInfo + ".isFullParam" + "){\n";
        }

        if (isConnected) {
            ret += "boolean " + RVMFlag + " = false;\n";

            ret += "for(int " + i + " = 0; " + i + " < " + parameters.size()
                    + "; " + i + "++){\n";
            ret += "if(" + monitorInfo + ".connected[" + i + "] == -1){\n";
            ret += RVMFlag + " = true;\n";
            ret += "}\n";
            ret += "}\n";

            ret += "if(" + "!" + RVMFlag + "){\n";
        }

        ret += statements;

        if (isConnected) {
            ret += "}\n";
        }

        if (isFullBinding) {
            ret += "}\n";
        }

        // do something for connected

        return ret;
    }

    public String monitorDecl() {
        String ret = "";

        ret += "com.runtimeverification.rvmonitor.java.rt.RVMMonitorInfo "
                + monitorInfo + ";\n";

        return ret;
    }
}
