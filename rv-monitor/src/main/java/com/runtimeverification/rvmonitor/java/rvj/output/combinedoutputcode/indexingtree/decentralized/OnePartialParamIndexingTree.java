package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.decentralized;

import java.util.HashMap;

import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.advice.LocalVariables;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.IndexingTree;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.reftree.RefTree;
import com.runtimeverification.rvmonitor.java.rvj.output.monitor.SuffixMonitor;
import com.runtimeverification.rvmonitor.java.rvj.output.monitorset.MonitorSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;
import com.runtimeverification.rvmonitor.util.RVMException;

public class OnePartialParamIndexingTree extends IndexingTree {
    private RVMParameter firstKey;
    public RVMVariable oneParamNode;

    public OnePartialParamIndexingTree(String outputName,
            RVMParameters queryParam, RVMParameters contentParam,
            RVMParameters fullParam, MonitorSet monitorSet,
            SuffixMonitor monitor, HashMap<String, RefTree> refTrees,
            boolean perthread, boolean isGeneral) throws RVMException {
        super(outputName, queryParam, contentParam, fullParam, monitorSet,
                monitor, refTrees, perthread, isGeneral);

        if (anycontent) {
            this.name = new RVMVariable(outputName + "_Set");
            if (isGeneral)
                this.oneParamNode = new RVMVariable(outputName + "_Monitor");
        } else {
            this.name = new RVMVariable(outputName + "__To__"
                    + contentParam.parameterStringUnderscore() + "_Set");
        }

        this.firstKey = queryParam.get(0);
    }

    @Override
    public boolean containsSet() {
        return true;
    }

    @Override
    public String lookupNode(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr, boolean creative,
            String monitorType) {
        String ret = "";

        if (oneParamNode != null) {
            RVMVariable monitor = localVars.get(monitorStr);
            ret += monitor + " = " + retrieveOneParamMonitor() + ";\n";
        }

        return ret;
    }

    @Override
    public String lookupSet(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr, boolean creative) {
        String ret = "";

        RVMVariable lastSet = localVars.get(lastSetStr);

        if (creative) {
            ret += createTree();
        }

        ret += lastSet + " = " + retrieveTree() + ";\n";

        return ret;
    }

    @Override
    public String lookupNodeAndSet(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr, boolean creative,
            String monitorType) {
        String ret = "";

        RVMVariable lastSet = localVars.get(lastSetStr);

        if (creative) {
            ret += createTree();
        }

        ret += lastSet + " = " + retrieveTree() + ";\n";

        if (oneParamNode != null) {
            RVMVariable monitor = localVars.get(monitorStr);
            ret += monitor + " = " + retrieveOneParamMonitor() + ";\n";
        }

        return ret;
    }

    @Override
    public String attachNode(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr) {
        String ret = "";

        if (oneParamNode != null) {
            RVMVariable monitor = localVars.get(monitorStr);
            ret += retrieveOneParamMonitor() + " = " + monitor + ";\n";
        }

        return ret;
    }

    @Override
    public String attachSet(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr) {
        String ret = "";

        RVMVariable lastSet = localVars.get(lastSetStr);
        ret += retrieveTree() + " = " + lastSet + ";\n";

        return ret;
    }

    @Override
    public String addMonitor(LocalVariables localVars, String monitorStr,
            String tempMapStr, String tempSetStr) {
        String ret = "";

        RVMVariable monitor = localVars.get(monitorStr);

        ret += createTree();
        ret += retrieveTree() + ".add(" + monitor + ");\n";

        return ret;
    }

    @Override
    public String retrieveTree() {
        return firstKey.getName() + "." + name.toString();
    }

    public String retrieveOneParamMonitor() {
        if (oneParamNode == null)
            return "";

        return firstKey.getName() + "." + oneParamNode.toString();
    }

    protected String createTree() {
        String ret = "";

        ret += "if (" + retrieveTree() + " == null) {\n";
        ret += retrieveTree() + " = " + "new " + monitorSet.getName() + "()"
                + ";\n";
        ret += "}\n";

        return ret;
    }

    @Override
    public String getRefTreeType() {
        String ret = "";

        if (parentTree != null)
            return parentTree.getRefTreeType();

        return ret;
    }

    @Override
    public String toString() {
        String ret = "";

        ret += monitorSet.getName() + " " + firstKey.getType() + "." + name
                + " = null;\n";
        if (oneParamNode != null) {
            ret += monitorClass.getOutermostName() + " " + firstKey.getType()
                    + "." + oneParamNode + " = null;\n";
        }

        if (cache != null)
            ret += cache;

        return ret;
    }

    @Override
    public String reset() {
        String ret = "";

        ret += firstKey.getType() + "." + name + " = null;\n";
        if (oneParamNode != null) {
            ret += firstKey.getType() + "." + oneParamNode + " = null;\n";
        }

        if (cache != null)
            ret += cache.reset();

        return ret;
    }
}
