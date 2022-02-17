package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.centralized;

import java.util.HashMap;

import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.IndexingTree;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.reftree.RefTree;
import com.runtimeverification.rvmonitor.java.rvj.output.monitor.SuffixMonitor;
import com.runtimeverification.rvmonitor.java.rvj.output.monitorset.MonitorSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;
import com.runtimeverification.rvmonitor.util.RVMException;

public class CentralizedIndexingTree {

    static public IndexingTree defineIndexingTree(String outputName,
            RVMParameters queryParam, RVMParameters contentParam,
            RVMParameters fullParam, MonitorSet monitorSet,
            SuffixMonitor monitor, HashMap<String, RefTree> refTrees,
            boolean perthread, boolean isGeneral) throws RVMException {

        if (queryParam.size() == 0)
            return new NoParamIndexingTree(outputName, queryParam,
                    contentParam, fullParam, monitorSet, monitor, refTrees,
                    perthread, isGeneral);

        if (queryParam.equals(fullParam) || queryParam.equals(contentParam))
            return new FullParamIndexingTree(outputName, queryParam,
                    contentParam, fullParam, monitorSet, monitor, refTrees,
                    perthread, isGeneral);

        return new PartialParamIndexingTree(outputName, queryParam,
                contentParam, fullParam, monitorSet, monitor, refTrees,
                perthread, isGeneral);
    }

}
