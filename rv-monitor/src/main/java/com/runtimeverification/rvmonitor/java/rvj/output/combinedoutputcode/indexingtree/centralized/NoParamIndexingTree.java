package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.centralized;

import java.util.HashMap;

import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.advice.LocalVariables;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.IndexingTree;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.reftree.RefTree;
import com.runtimeverification.rvmonitor.java.rvj.output.monitor.SuffixMonitor;
import com.runtimeverification.rvmonitor.java.rvj.output.monitorset.MonitorSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;
import com.runtimeverification.rvmonitor.util.RVMException;

public class NoParamIndexingTree extends IndexingTree {
    public RVMVariable globalNode;

    public NoParamIndexingTree(String outputName, RVMParameters queryParam,
            RVMParameters contentParam, RVMParameters fullParam,
            MonitorSet monitorSet, SuffixMonitor monitor,
            HashMap<String, RefTree> refTrees, boolean perthread,
            boolean isGeneral) throws RVMException {
        super(outputName, queryParam, contentParam, fullParam, monitorSet,
                monitor, refTrees, perthread, isGeneral);

        if (anycontent) {
            if (fullParam.size() == 0) {
                this.name = new RVMVariable(outputName + "_Monitor");
            } else {
                this.name = new RVMVariable(outputName + "_Set");
                if (isGeneral)
                    this.globalNode = new RVMVariable(outputName + "_Monitor");
            }
        } else {
            if (contentParam.size() == 0) {
                this.name = new RVMVariable(outputName + "_Monitor");
            } else {
                this.name = new RVMVariable(outputName + "__To__"
                        + contentParam.parameterStringUnderscore() + "_Set");
            }
        }
    }

    @Override
    public String lookupNode(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr, boolean creative,
            String monitorType) {
        String ret = "";

        if (isFullParam || globalNode != null) {
            RVMVariable monitor = localVars.get(monitorStr);

            if (isFullParam) {
                ret += monitor + " = " + retrieveTree() + ";\n";
            } else if (globalNode != null) {
                ret += monitor + " = " + retrieveGlobalMonitor() + ";\n";
            }
        }

        return ret;
    }

    @Override
    public String lookupSet(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr, boolean creative) {
        String ret = "";

        if (!isFullParam) {
            RVMVariable lastSet = localVars.get(lastSetStr);

            ret += lastSet + " = " + retrieveTree() + ";\n";
        }

        return ret;
    }

    @Override
    public String lookupNodeAndSet(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr, boolean creative,
            String monitorType) {
        String ret = "";

        if (isFullParam) {
            RVMVariable monitor = localVars.get(monitorStr);

            ret += monitor + " = " + retrieveTree() + ";\n";
        } else {
            RVMVariable lastSet = localVars.get(lastSetStr);

            ret += lastSet + " = " + retrieveTree() + ";\n";
            if (globalNode != null) {
                RVMVariable monitor = localVars.get(monitorStr);

                ret += monitor + " = " + retrieveGlobalMonitor() + ";\n";
            }
        }

        return ret;
    }

    @Override
    public String attachNode(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr) {
        String ret = "";

        if (isFullParam || globalNode != null) {
            RVMVariable monitor = localVars.get(monitorStr);

            if (isFullParam) {
                ret += retrieveTree() + " = " + monitor + ";\n";
            } else if (globalNode != null) {
                ret += retrieveGlobalMonitor() + " = " + monitor + ";\n";
                ret += retrieveTree() + ".add(" + monitor + ");\n";
            }
        }

        return ret;
    }

    @Override
    public String attachSet(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr) {
        String ret = "";

        if (!isFullParam) {
            RVMVariable lastSet = localVars.get(lastSetStr);

            ret += retrieveTree() + " = " + lastSet + ";\n";
        }

        return ret;
    }

    @Override
    public String addMonitor(LocalVariables localVars, String monitorStr,
            String tempMapStr, String tempSetStr) {
        String ret = "";
        RVMVariable monitor = localVars.get(monitorStr);

        if (isFullParam) {
            ret += retrieveTree() + " = " + monitor + ";\n";
        } else {
            ret += retrieveTree() + ".add(" + monitor + ");\n";
        }

        return ret;
    }

    @Override
    public boolean containsSet() {
        return fullParam.size() != 0;
    }

    @Override
    public String retrieveTree() {
        if (perthread) {
            String ret = "";

            ret += "(";

            ret += "(" + monitorClass.getOutermostName() + ")";

            ret += name + ".get()";
            ret += ")";

            return ret;

        } else {
            return name.toString();
        }
    }

    public String retrieveGlobalMonitor() {
        if (globalNode == null)
            return "";

        if (perthread) {
            String ret = "";

            ret += "(";
            ret += "(" + monitorClass.getOutermostName() + ")";
            ret += globalNode + ".get()";
            ret += ")";

            return ret;
        } else {
            return globalNode.toString();
        }
    }

    @Override
    public String getRefTreeType() {
        return "";
    }

    @Override
    public String toString() {
        String ret = "";

        if (perthread) {
            ret += "static final ThreadLocal " + name
                    + " = new ThreadLocal() {\n";
            ret += "protected Object initialValue(){\n";
            ret += "return ";

            if (isFullParam) {
                ret += "new " + monitorClass.getOutermostName() + "();\n";
            } else {
                ret += "new " + monitorSet.getName() + "();\n";
            }

            ret += "}\n";
            ret += "};\n";

            if (globalNode != null) {
                ret += "static final ThreadLocal " + globalNode
                        + " = new ThreadLocal() {\n";
                ret += "protected Object initialValue(){\n";
                ret += "return null;\n";

                ret += "}\n";
                ret += "};\n";
            }
        } else {
            if (isFullParam) {
                ret += "static " + monitorClass.getOutermostName() + " " + name
                        + " = new " + monitorClass.getOutermostName() + "();\n";
            } else {
                ret += "static " + monitorSet.getName() + " " + name
                        + " = new " + monitorSet.getName() + "();\n";
                if (globalNode != null) {
                    ret += "static " + monitorClass.getOutermostName() + " "
                            + globalNode + " = null;\n";
                }
            }
        }

        return ret;
    }

    @Override
    public String reset() {
        String ret = "";

        if (perthread) {
        } else {
            if (isFullParam) {
                ret += name + " = new " + monitorClass.getOutermostName()
                        + "();\n";
            } else {
                // ret += "System.err.println(\""+ name + " size: \" + " + name
                // + ".size" + ");\n";

                ret += name + " = new " + monitorSet.getName() + "();\n";
                if (globalNode != null) {
                    ret += globalNode + " = null;\n";
                }
            }
        }

        return ret;
    }

}
