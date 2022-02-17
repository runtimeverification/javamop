package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.decentralized;

import java.util.HashMap;

import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.advice.LocalVariables;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.IndexingCache;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.IndexingTree;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.reftree.RefTree;
import com.runtimeverification.rvmonitor.java.rvj.output.monitor.SuffixMonitor;
import com.runtimeverification.rvmonitor.java.rvj.output.monitorset.MonitorSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;
import com.runtimeverification.rvmonitor.util.RVMException;

public class FullParamIndexingTree extends IndexingTree {
    private final RVMParameter firstKey;

    public FullParamIndexingTree(String outputName, RVMParameters queryParam,
            RVMParameters contentParam, RVMParameters fullParam,
            MonitorSet monitorSet, SuffixMonitor monitor,
            HashMap<String, RefTree> refTrees, boolean perthread,
            boolean isGeneral) throws RVMException {
        super(outputName, queryParam, contentParam, fullParam, monitorSet,
                monitor, refTrees, perthread, isGeneral);

        if (!isFullParam)
            throw new RVMException(
                    "FullParamIndexingTree can be created only when queryParam equals to fullParam.");

        if (queryParam.size() <= 1)
            throw new RVMException(
                    "Decentralized FullParamIndexingTree should contain at least two parameter.");

        if (anycontent) {
            this.name = new RVMVariable(outputName + "_"
                    + queryParam.parameterStringUnderscore() + "_Map");

            this.cache = new IndexingCache(this.name, this.queryParam,
                    this.fullParam, this.monitorClass, this.monitorSet,
                    refTrees, perthread, isGeneral);
            // this.cache = new LocalityIndexingCache(this.name,
            // this.queryParam, this.fullParam, this.monitorClass,
            // this.monitorSet, refTrees, perthread, isGeneral);
        } else {
            if (!contentParam.contains(queryParam))
                throw new RVMException(
                        "[Internal] contentParam should contain queryParam");

            this.name = new RVMVariable(outputName + "_"
                    + queryParam.parameterStringUnderscore() + "__To__"
                    + contentParam.parameterStringUnderscore() + "_Map");
        }

        this.firstKey = queryParam.get(0);
    }

    public RVMParameter getLastParam() {
        return queryParam.get(queryParam.size() - 1);
    }

    protected String lookupIntermediateCreative(LocalVariables localVars,
            RVMVariable monitor, RVMVariable lastMap, RVMVariable lastSet, int i)
                    throws RVMException {
        String ret = "";

        RVMVariable obj = localVars.get("obj");
        RVMVariable tempMap = localVars.get("tempMap");

        RVMParameter p = queryParam.get(i);
        RVMVariable tempRef = localVars.getTempRef(p);

        ret += obj + " = " + tempMap + ".getMap(" + tempRef + ");\n";

        ret += "if (" + obj + " == null) {\n";

        ret += createNewMap(i + 1) + ";\n";

        ret += tempMap + ".putMap(" + tempRef + ", " + obj + ");\n";
        ret += "}\n";

        if (i == queryParam.size() - 2) {
            ret += lastMap
                    + " = (com.runtimeverification.rvmonitor.java.rt.map.RVMAbstractMap)"
                    + obj + ";\n";
            ret += lookupNodeLast(localVars, monitor, lastMap, lastSet, i + 1,
                    true);
        } else {
            ret += tempMap
                    + " = (com.runtimeverification.rvmonitor.java.rt.map.RVMAbstractMap)"
                    + obj + ";\n";
            ret += lookupIntermediateCreative(localVars, monitor, lastMap,
                    lastSet, i + 1);
        }

        return ret;
    }

    protected String lookupIntermediateNonCreative(LocalVariables localVars,
            RVMVariable monitor, RVMVariable lastMap, RVMVariable lastSet, int i)
                    throws RVMException {
        String ret = "";

        RVMVariable obj = localVars.get("obj");
        RVMVariable tempMap = localVars.get("tempMap");

        RVMParameter p = queryParam.get(i);
        RVMVariable tempRef = localVars.getTempRef(p);

        ret += obj + " = " + tempMap + ".getMap(" + tempRef + ");\n";

        ret += "if (" + obj + " != null) {\n";

        if (i == queryParam.size() - 2) {
            ret += lastMap
                    + " = (com.runtimeverification.rvmonitor.java.rt.map.RVMAbstractMap)"
                    + obj + ";\n";
            ret += lookupNodeLast(localVars, monitor, lastMap, lastSet, i + 1,
                    false);
        } else {
            ret += tempMap
                    + " = (com.runtimeverification.rvmonitor.java.rt.map.RVMAbstractMap)"
                    + obj + ";\n";
            ret += lookupIntermediateNonCreative(localVars, monitor, lastMap,
                    lastSet, i + 1);
        }

        ret += "}\n";

        return ret;
    }

    protected String lookupNodeLast(LocalVariables localVars,
            RVMVariable monitor, RVMVariable lastMap, RVMVariable lastSet,
            int i, boolean creative) {
        String ret = "";

        RVMParameter p = queryParam.get(i);
        RVMVariable tempRef = localVars.getTempRef(p);

        ret += monitor + " = " + "(" + monitorClass.getOutermostName() + ")"
                + lastMap + ".getNode(" + tempRef + ");\n";

        return ret;
    }

    @Override
    public String lookupNode(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr, boolean creative,
            String monitorType) throws RVMException {
        String ret = "";

        RVMVariable monitor = localVars.get(monitorStr);
        RVMVariable lastMap = localVars.get(lastMapStr);

        if (creative) {
            ret += createTree();
        }

        if (queryParam.size() == 2) {
            ret += lastMap + " = " + retrieveTree() + ";\n";
            if (creative) {
                ret += lookupNodeLast(localVars, monitor, lastMap, null, 1,
                        creative);
            } else {
                ret += "if (" + lastMap + " != null) {\n";
                ret += lookupNodeLast(localVars, monitor, lastMap, null, 1,
                        creative);
                ret += "}\n";
            }
        } else {
            RVMVariable tempMap = localVars.get("tempMap");
            ret += tempMap + " = " + retrieveTree() + ";\n";

            if (creative) {
                ret += lookupIntermediateCreative(localVars, monitor, lastMap,
                        null, 1);
            } else {
                ret += "if (" + lastMap + " != null) {\n";
                ret += lookupIntermediateNonCreative(localVars, monitor,
                        lastMap, null, 1);
                ret += "}\n";
            }
        }

        return ret;
    }

    @Override
    public String lookupSet(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr, boolean creative) {
        return "";
    }

    @Override
    public String lookupNodeAndSet(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr, boolean creative,
            String monitorType) throws RVMException {
        return lookupNode(localVars, monitorStr, lastMapStr, lastSetStr,
                creative, monitorType);
    }

    @Override
    public String attachNode(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr) {
        String ret = "";

        RVMVariable monitor = localVars.get(monitorStr);

        RVMVariable tempRef = localVars.getTempRef(getLastParam());

        if (queryParam.size() == 2) {
            ret += retrieveTree() + ".putNode(" + tempRef + ", " + monitor
                    + ");\n";
        } else {
            RVMVariable lastMap = localVars.get(lastMapStr);

            ret += lastMap + ".putNode(" + tempRef + ", " + monitor + ");\n";
        }

        return ret;
    }

    @Override
    public String attachSet(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr) {
        return "";
    }

    @Override
    public String addMonitor(LocalVariables localVars, String monitorStr,
            String tempMapStr, String tempSetStr) throws RVMException {
        String ret = "";

        RVMVariable obj = localVars.get("obj");
        RVMVariable tempMap = localVars.get(tempMapStr);
        RVMVariable monitor = localVars.get(monitorStr);

        ret += createTree();

        ret += tempMap + " = " + retrieveTree() + ";\n";

        for (int i = 1; i < queryParam.size() - 1; i++) {
            RVMParameter p = queryParam.get(i);
            RVMVariable tempRef = localVars.getTempRef(p);

            ret += obj + " = " + tempMap + ".getMap(" + tempRef + ");\n";

            ret += "if (" + obj + " == null) {\n";

            ret += createNewMap(i + 1) + ";\n";

            ret += tempMap + ".putMap(" + tempRef + ", " + obj + ");\n";
            ret += "}\n";

            ret += tempMap
                    + " = (com.runtimeverification.rvmonitor.java.rt.map.RVMAbstractMap)"
                    + obj + ";\n";
        }

        RVMParameter p = getLastParam();
        RVMVariable tempRef = localVars.getTempRef(p);

        ret += tempMap + ".putNode(" + tempRef + ", " + monitor + ");\n";

        return ret;
    }

    @Override
    public boolean containsSet() {
        return false;
    }

    @Override
    public String retrieveTree() {
        if (parentTree != null)
            return parentTree.retrieveTree();

        return firstKey.getName() + "." + name.toString();
    }

    protected String createTree() throws RVMException {
        String ret = "";

        ret += "if (" + retrieveTree() + " == null) {\n";
        ret += retrieveTree() + " = " + createNewMap(1) + ";\n";
        ret += "}\n";

        return ret;
    }

    protected String createNewMap(int paramIndex) throws RVMException {
        String ret = "";

        if (paramIndex < 1)
            throw new RVMException(
                    "The first parameter cannot use getMapType(int).");

        if (isGeneral) {
            if (paramIndex == queryParam.size() - 1) {
                ret += "new com.runtimeverification.rvmonitor.java.rt.map.RVMMapOfMonitor("
                        + fullParam.getIdnum(queryParam.get(paramIndex)) + ")";
            } else {
                ret += "new com.runtimeverification.rvmonitor.java.rt.map.RVMMapOfAll("
                        + fullParam.getIdnum(queryParam.get(paramIndex)) + ")";
            }
        } else {
            if (paramIndex == queryParam.size() - 1) {
                ret += "new com.runtimeverification.rvmonitor.java.rt.map.RVMMapOfMonitor("
                        + fullParam.getIdnum(queryParam.get(paramIndex)) + ")";
            } else {
                ret += "new com.runtimeverification.rvmonitor.java.rt.map.RVMMapOfMapSet("
                        + fullParam.getIdnum(queryParam.get(paramIndex)) + ")";
            }
        }

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

        if (parentTree == null) {
            if (isGeneral) {
                if (queryParam.size() == 1) {
                    ret += "static com.runtimeverification.rvmonitor.java.rt.map.RVMAbstractMap "
                            + name + " = null;\n";
                } else {
                    ret += "static com.runtimeverification.rvmonitor.java.rt.map.RVMAbstractMap "
                            + name + " = null;\n";
                }
            } else {
                if (queryParam.size() == 1) {
                    ret += "static com.runtimeverification.rvmonitor.java.rt.map.RVMAbstractMap "
                            + name + " = null;\n";
                } else {
                    ret += "static com.runtimeverification.rvmonitor.java.rt.map.RVMAbstractMap "
                            + name + " = null;\n";
                }
            }
        }

        if (cache != null)
            ret += cache;

        return ret;
    }

    @Override
    public String reset() {
        String ret = "";

        if (parentTree == null) {
            if (isGeneral) {
                if (queryParam.size() == 1) {
                    ret += name + " = null;\n";
                } else {
                    ret += name + " = null;\n";
                }
            } else {
                if (queryParam.size() == 1) {
                    ret += name + " = null;\n";
                } else {
                    ret += name + " = null;\n";
                }
            }
        }

        if (cache != null)
            ret += cache.reset();

        return ret;
    }

}