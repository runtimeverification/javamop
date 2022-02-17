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

public class OneFullParamIndexingTree extends IndexingTree {
    private final RVMParameter firstKey;

    public OneFullParamIndexingTree(String outputName,
            RVMParameters queryParam, RVMParameters contentParam,
            RVMParameters fullParam, MonitorSet monitorSet,
            SuffixMonitor monitor, HashMap<String, RefTree> refTrees,
            boolean perthread, boolean isGeneral) throws RVMException {
        super(outputName, queryParam, contentParam, fullParam, monitorSet,
                monitor, refTrees, perthread, isGeneral);

        this.name = new RVMVariable(outputName + "_Monitor");
        this.firstKey = queryParam.get(0);
    }

    @Override
    public boolean containsSet() {
        return false;
    }

    @Override
    public String lookupNode(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr, boolean creative,
            String monitorType) {
        String ret = "";

        RVMVariable monitor = localVars.get(monitorStr);
        ret += monitor + " = " + retrieveTree() + ";\n";

        return ret;
    }

    @Override
    public String lookupSet(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr, boolean creative) {
        String ret = "";

        // do nothing

        return ret;
    }

    @Override
    public String lookupNodeAndSet(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr, boolean creative,
            String monitorType) {
        return lookupNode(localVars, monitorStr, lastMapStr, lastSetStr,
                creative, monitorType);
    }

    @Override
    public String attachNode(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr) {
        String ret = "";

        RVMVariable monitor = localVars.get(monitorStr);

        ret += retrieveTree() + " = " + monitor + ";\n";

        return ret;
    }

    @Override
    public String attachSet(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr) {
        String ret = "";

        // do nothing

        return ret;
    }

    @Override
    public String addMonitor(LocalVariables localVars, String monitorStr,
            String tempMapStr, String tempSetStr) {
        String ret = "";

        RVMVariable monitor = localVars.get(monitorStr);
        ret += retrieveTree() + " = " + monitor + ";\n";

        return ret;
    }

    @Override
    public String retrieveTree() {
        return firstKey.getName() + "." + name.toString();
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

        ret += monitorClass.getOutermostName() + " " + firstKey.getType() + "."
                + name + " = null;\n";

        if (cache != null)
            ret += cache;

        return ret;
    }

    @Override
    public String reset() {
        String ret = "";

        ret += firstKey.getType() + "." + name + " = null;\n";

        if (cache != null)
            ret += cache.reset();

        return ret;
    }

}
