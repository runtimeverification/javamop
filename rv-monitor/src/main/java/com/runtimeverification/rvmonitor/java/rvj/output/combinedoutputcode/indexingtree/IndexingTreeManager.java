package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree;

import java.util.List;
import java.util.TreeMap;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.output.EnableSet;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.reftree.RefTree;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingDeclNew;
import com.runtimeverification.rvmonitor.java.rvj.output.monitor.SuffixMonitor;
import com.runtimeverification.rvmonitor.java.rvj.output.monitorset.MonitorSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;
import com.runtimeverification.rvmonitor.util.RVMException;

public class IndexingTreeManager {

    private final TreeMap<RVMonitorSpec, IndexingDeclNew> trees = new TreeMap<RVMonitorSpec, IndexingDeclNew>();

    public TreeMap<String, RefTree> refTrees = new TreeMap<String, RefTree>();

    public IndexingTreeManager(String name, List<RVMonitorSpec> specs,
            TreeMap<RVMonitorSpec, MonitorSet> monitorSets,
            TreeMap<RVMonitorSpec, SuffixMonitor> monitors,
            TreeMap<RVMonitorSpec, EnableSet> enableSets) throws RVMException {
        getRefTrees(name, specs);

        for (RVMonitorSpec spec : specs) {
            MonitorSet monitorSet = monitorSets.get(spec);
            SuffixMonitor monitor = monitors.get(spec);
            EnableSet enableSet = enableSets.get(spec);

            trees.put(spec, new IndexingDeclNew(spec, monitorSet, monitor,
                    enableSet, refTrees));
        }
    }

    protected void getRefTrees(String name, List<RVMonitorSpec> specs)
            throws RVMException {
        for (RVMonitorSpec spec : specs) {
            for (RVMParameter param : spec.getParameters()) {
                RefTree refTree = refTrees.get(param.getType().toString());

                if (refTree == null) {
                    refTree = new RefTree(name, param);

                    refTrees.put(param.getType().toString(), refTree);
                }

                refTree.addProperty(spec);
            }
        }
    }

    public IndexingDeclNew getIndexingDecl(RVMonitorSpec spec) {
        return trees.get(spec);
    }

    public String decl() {
        String ret = "";

        if (trees.size() <= 0)
            return ret;

        // int count = 0;
        // for (IndexingDecl indexDecl : trees.values()) {
        // for(IndexingTree tree : indexDecl.indexingTrees.values()){
        // //if(tree.parentTree == null && tree.queryParam.size() > 0)
        // if(tree.queryParam.size() > 0)
        // count++;
        // }
        //
        // for(IndexingTree tree : indexDecl.indexingTreesForCopy.values()){
        // //if(tree.parentTree == null && tree.queryParam.size() > 0)
        // if(tree.queryParam.size() > 0)
        // count++;
        // }
        // }
        // System.out.println(count);

        ret += "// Declarations for Indexing Trees \n";
        for (IndexingDeclNew indexDecl : trees.values()) {
            ret += indexDecl;
        }
        ret += "\n";

        if (Main.useWeakRefInterning) {
            ret += "// Trees for References\n";
            for (RefTree refTree : refTrees.values()) {
                ret += refTree;
            }
            ret += "\n";
        }

        if (Main.internalBehaviorObserving) {
            ret += "static {\n";
            for (IndexingDeclNew indexDecl : trees.values()) {
                ret += indexDecl.getObservableObjectDescriptionSetCode();
            }
            ret += "}\n\n";
        }

        {
            String var = "collected";
            ret += "public static int cleanUp() {\n";
            ret += "int " + var + " = 0;\n";
            ret += "// indexing trees\n";
            for (IndexingDeclNew decl : trees.values()) {
                ret += decl.getCleanUpCode(var);
            }

            if (Main.useWeakRefInterning) {
                ret += "// ref trees\n";
                for (RefTree refTree : refTrees.values()) {
                    ret += refTree.getCleanUpCode(var);
                }
            }
            ret += "return collected;\n";
            ret += "}\n\n";
        }

        return ret;
    }

    public String reset() {
        String ret = "";

        if (trees.size() <= 0)
            return ret;

        for (IndexingDeclNew indexDecl : trees.values()) {
            ret += indexDecl.reset();
        }
        ret += "\n";

        for (RefTree refTree : refTrees.values()) {
            ret += refTree.reset();
        }
        ret += "\n";

        return ret;
    }

}
