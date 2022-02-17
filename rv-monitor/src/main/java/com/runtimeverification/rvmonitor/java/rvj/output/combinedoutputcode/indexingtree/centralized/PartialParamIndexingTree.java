package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.centralized;

import java.util.HashMap;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMTypedVariable;
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

public class PartialParamIndexingTree extends IndexingTree {

    private final static int NODEONLY = 0;
    private final static int SETONLY = 1;
    private final static int NODEANDSET = 2;

    public PartialParamIndexingTree(String outputName,
            RVMParameters queryParam, RVMParameters contentParam,
            RVMParameters fullParam, MonitorSet monitorSet,
            SuffixMonitor monitor, HashMap<String, RefTree> refTrees,
            boolean perthread, boolean isGeneral) throws RVMException {
        super(outputName, queryParam, contentParam, fullParam, monitorSet,
                monitor, refTrees, perthread, isGeneral);

        if (isFullParam)
            throw new RVMException(
                    "PartialParamIndexingTree can be created only when queryParam does not equal to fullParam.");

        if (queryParam.size() == 0)
            throw new RVMException(
                    "PartialParamIndexingTree should contain at least one parameter.");

        if (anycontent) {
            this.name = new RVMVariable(outputName + "_"
                    + queryParam.parameterStringUnderscore() + "_Map");
        } else {
            if (!contentParam.contains(queryParam))
                throw new RVMException(
                        "[Internal] contentParam should contain queryParam");
            if (contentParam.size() <= queryParam.size())
                throw new RVMException(
                        "[Internal] contentParam should be larger than queryParam");

            this.name = new RVMVariable(outputName + "_"
                    + queryParam.parameterStringUnderscore() + "__To__"
                    + contentParam.parameterStringUnderscore() + "_Map");
        }

        if (anycontent) {
            this.cache = new IndexingCache(this.name, this.queryParam,
                    this.fullParam, this.monitorClass, this.monitorSet,
                    refTrees, perthread, isGeneral);
            // this.cache = new LocalityIndexingCache(this.name,
            // this.queryParam, this.fullParam, this.monitorClass,
            // this.monitorSet, refTrees, perthread, isGeneral);
        }
    }

    public RVMParameter getLastParam() {
        return queryParam.get(queryParam.size() - 1);
    }

    protected String lookupIntermediateCreative(LocalVariables localVars,
            RVMVariable monitor, String lastMapStr, RVMVariable lastSet, int i,
            int target) {
        String ret = "";

        RVMTypedVariable tempMap = localVars.getTempMap();
        RVMTypedVariable obj = localVars.createObj(tempMap,
                IndexingTreeInterface.Map);

        RVMParameter p = queryParam.get(i);
        RVMVariable tempRef = localVars.getTempRef(p);

        if (Main.useWeakRefInterning)
            ret += obj.getType() + " " + obj.getName() + " = " + tempMap
            + ".getMap(" + tempRef + ");\n";
        else
            ret += this.getGetMapStrongRefCode(p, tempRef, tempMap, obj);

        RVMParameter nextP = queryParam.get(i + 1);

        ret += "if (" + obj + " == null) {\n";

        ret += obj + " = new " + obj.getType() + "("
                + fullParam.getIdnum(nextP) + ");\n";

        ret += tempMap + ".putMap(" + tempRef + ", " + obj + ");\n";
        ret += "}\n";

        if (i == queryParam.size() - 2) {
            RVMTypedVariable.Pair pair = localVars.createOrFindMap(lastMapStr,
                    obj.getType());
            if (pair.isCreated())
                ret += pair.getVariable().getType() + " ";
            ret += pair.getVariable() + " = " + obj + ";\n";
            if (target == NODEONLY)
                ret += lookupNodeLast(localVars, monitor, lastMapStr, lastSet,
                        i + 1, true);
            else if (target == SETONLY)
                ret += lookupSetLast(localVars, monitor, lastMapStr, lastSet,
                        i + 1, true);
            else if (target == NODEANDSET)
                ret += lookupNodeAndSetLast(localVars, monitor, lastMapStr,
                        lastSet, i + 1, true);
        } else {
            RVMTypedVariable var = localVars.createTempMap(obj.getType());
            ret += var.getType() + " " + var.getName() + " = " + obj + ";\n";
            ret += lookupIntermediateCreative(localVars, monitor, lastMapStr,
                    lastSet, i + 1, target);
        }

        return ret;
    }

    protected String lookupIntermediateNonCreative(LocalVariables localVars,
            RVMVariable monitor, String lastMapStr, RVMVariable lastSet, int i,
            int target) {
        String ret = "";

        RVMTypedVariable tempMap = localVars.getTempMap();
        RVMTypedVariable obj = localVars.createObj(tempMap,
                IndexingTreeInterface.Map);

        RVMParameter p = queryParam.get(i);
        RVMVariable tempRef = localVars.getTempRef(p);

        if (Main.useWeakRefInterning)
            ret += obj.getType() + " " + obj.getName() + " = " + tempMap
            + ".getMap(" + tempRef + ");\n";
        else
            ret += this.getGetMapStrongRefCode(p, tempRef, tempMap, obj);

        ret += "if (" + obj + " != null) {\n";

        if (i == queryParam.size() - 2) {
            RVMTypedVariable.Pair pair = localVars.createOrFindMap(lastMapStr,
                    obj.getType());
            if (pair.isCreated())
                ret += pair.getVariable().getType() + " ";
            ret += pair.getVariable() + " = " + obj + ";\n";
            if (target == NODEONLY)
                ret += lookupNodeLast(localVars, monitor, lastMapStr, lastSet,
                        i + 1, false);
            else if (target == SETONLY)
                ret += lookupSetLast(localVars, monitor, lastMapStr, lastSet,
                        i + 1, false);
            else if (target == NODEANDSET)
                ret += lookupNodeAndSetLast(localVars, monitor, lastMapStr,
                        lastSet, i + 1, false);
        } else {
            RVMTypedVariable var = localVars.createTempMap(obj.getType());
            ret += var.getType() + " " + var.getName() + " = " + obj + ";\n";
            ret += lookupIntermediateNonCreative(localVars, monitor,
                    lastMapStr, lastSet, i + 1, target);
        }

        ret += "}\n";

        if (!Main.useWeakRefInterning) {
            if (i + 1 < queryParam.size()) {
                RVMParameter q = queryParam.get(i + 1);
                RVMVariable wrq = localVars.getTempRef(q);

                RefTree tree = this.refTrees.get(q.getType().getOp());
                ret += "else {\n";
                ret += tree.createWeakReferenceConditional(wrq.getVarName(), q);
                ret += "}\n";
            }
        }

        return ret;
    }

    protected String lookupNodeLast(LocalVariables localVars,
            RVMVariable monitor, String lastMapStr, RVMVariable lastSet, int i,
            boolean creative) {
        String ret = "";

        RVMParameter p = queryParam.get(i);
        RVMVariable tempRef = localVars.getTempRef(p);
        RVMTypedVariable lastMap = localVars.getMap(lastMapStr);

        ret += monitor + " = " + lastMap + ".getLeaf(" + tempRef + ");\n";

        return ret;
    }

    @Override
    public String lookupNode(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr, boolean creative,
            String monitorType) {
        String ret = "";

        RVMVariable monitor = localVars.get(monitorStr);

        if (queryParam.size() == 1) {
            RVMTypedVariable.Pair pair = localVars.createOrFindMap(lastMapStr,
                    this.getTreeType());
            if (pair.isCreated())
                ret += pair.getVariable().getType() + " ";
            ret += pair.getVariable() + " = " + retrieveTree() + ";\n";

            ret += lookupNodeLast(localVars, monitor, lastMapStr, null, 0,
                    creative);
        } else {
            RVMTypedVariable tempMap = localVars.createTempMap(this
                    .getTreeType());
            ret += tempMap.getType().toString() + " " + tempMap.getName()
                    + " = " + retrieveTree() + ";\n";

            if (creative) {
                ret += lookupIntermediateCreative(localVars, monitor,
                        lastMapStr, null, 0, NODEONLY);
            } else {
                ret += lookupIntermediateNonCreative(localVars, monitor,
                        lastMapStr, null, 0, NODEONLY);
            }
        }

        return ret;
    }

    protected String lookupSetLast(LocalVariables localVars,
            RVMVariable monitor, String lastMapStr, RVMVariable lastSet, int i,
            boolean creative) {
        String ret = "";

        RVMParameter p = queryParam.get(i);
        RVMVariable tempRef = localVars.getTempRef(p);
        RVMTypedVariable lastMap = localVars.getMap(lastMapStr);

        if (Main.useWeakRefInterning) {
            ret += lastSet + " = " + lastMap + ".getSet(" + tempRef + ");\n";

            if (creative) {
                ret += "if (" + lastSet + " == null){\n";
                ret += lastSet + " = new " + monitorSet.getName() + "();\n";
                ret += lastMap + ".putSet(" + tempRef + ", " + lastSet + ");\n";
                ret += "}\n";
            }
        } else {
            ret += this.getGetAndSetWithStrongRefCode(localVars, lastMap, p,
                    lastSet.getVarName(), tempRef.getVarName(), creative);
            /*
             * ret += "{\n"; ret +=
             * "rvmonitorrt.map.hashentry.EntryPair pair = " + lastMap +
             * ".getSetStrong(" + p.getName() + ");\n"; ret +=
             * "if (pair == null) {\n"; if (creative) { ret += lastSet +
             * " = new " + monitorSet.getName() + "();\n"; ret += tempRef +
             * " = new " + localVars.getTempRefType(p) + "(" + p.getName() +
             * ");\n"; ret += lastMap + ".putSet(" + tempRef + ", " + lastSet +
             * ");\n"; } else { ret += lastSet + " = null;\n"; } ret += "}\n";
             * ret += "else {\n"; ret += lastSet + " = pair.getValue();\n"; ret
             * += tempRef + " = pair.getWeakRef();\n"; ret += "}\n"; ret +=
             * "}\n";
             */
        }

        return ret;
    }

    @Override
    public String lookupSet(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr, boolean creative) {
        String ret = "";

        RVMVariable lastSet = localVars.get(lastSetStr);

        if (queryParam.size() == 1) {
            RVMTypedVariable.Pair pair = localVars.createOrFindMap(lastMapStr,
                    this.getTreeType());
            if (pair.isCreated())
                ret += pair.getVariable().getType() + " ";
            ret += pair.getVariable() + " = " + retrieveTree() + ";\n";

            ret += lookupSetLast(localVars, null, lastMapStr, lastSet, 0,
                    creative);
        } else {
            RVMTypedVariable tempMap = localVars.createTempMap(this
                    .getTreeType());
            ret += tempMap.getType().toString() + " " + tempMap.getName()
                    + " = " + retrieveTree() + ";\n";

            if (creative) {
                ret += lookupIntermediateCreative(localVars, null, lastMapStr,
                        lastSet, 0, SETONLY);
            } else {
                ret += lookupIntermediateNonCreative(localVars, null,
                        lastMapStr, lastSet, 0, SETONLY);
            }
        }

        return ret;
    }

    protected String lookupNodeAndSetLast(LocalVariables localVars,
            RVMVariable monitor, String lastMapStr, RVMVariable lastSet, int i,
            boolean creative) {
        String ret = "";

        RVMParameter p = queryParam.get(i);
        RVMVariable tempRef = localVars.getTempRef(p);
        RVMTypedVariable lastMap = localVars.getMap(lastMapStr);

        if (Main.useWeakRefInterning) {
            ret += monitor + " = " + lastMap + ".getLeaf(" + tempRef + ");\n";
            ret += lastSet + " = " + lastMap + ".getSet(" + tempRef + ");\n";
        } else {
            ret += this.getGetWithStrongRefCode(lastMap, p,
                    IndexingTreeInterface.Leaf, monitor.getVarName(),
                    tempRef.getVarName(), true);
            /*
             * RefTree tree = this.refTrees.get(p.getType().getOp()); ret +=
             * "{\n"; ret += "rvmonitorrt.map.hashentry.EntryPair pair = " +
             * lastMap + ".getNodeStrong(" + p.getName() + ");\n"; ret +=
             * "if (pair == null) {\n"; ret += monitor + " = null;\n"; ret +=
             * tree.createWeakReferenceConditional(tempRef.getVarName(), p); ret
             * += "}\n"; ret += "else {\n"; ret += monitor +
             * " = pair.getValue();\n"; ret += tempRef +
             * " = pair.getWeakRef();\n"; ret += "}\n"; ret += "}\n";
             */

            ret += this.getGetWithStrongRefCode(lastMap, p,
                    IndexingTreeInterface.Set, lastSet.getVarName(), null,
                    false);
            /*
             * ret += "{\n"; ret +=
             * "rvmonitorrt.map.hashentry.EntryPair pair = " + lastMap +
             * ".getSetStrong(" + p.getName() + ");\n"; ret +=
             * "if (pair == null) {\n"; ret += lastSet + " = null;\n"; ret +=
             * "}\n"; ret += "else {\n"; ret += lastSet +
             * " = pair.getValue();\n"; ret += "}\n"; ret += "}\n";
             */
        }

        if (creative) {
            ret += "if (" + lastSet + " == null){\n";
            ret += lastSet + " = new " + monitorSet.getName() + "();\n";
            ret += lastMap + ".putSet(" + tempRef + ", " + lastSet + ");\n";
            ret += "}\n";
        }

        return ret;
    }

    @Override
    public String lookupNodeAndSet(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr, boolean creative,
            String monitorType) {
        String ret = "";

        RVMVariable monitor = localVars.get(monitorStr);
        RVMVariable lastSet = localVars.get(lastSetStr);

        if (queryParam.size() == 1) {
            RVMTypedVariable.Pair pair = localVars.createOrFindMap(lastMapStr,
                    this.getTreeType());
            if (pair.isCreated())
                ret += pair.getVariable().getType() + " ";
            ret += pair.getVariable() + " = " + retrieveTree() + ";\n";

            ret += lookupNodeAndSetLast(localVars, monitor, lastMapStr,
                    lastSet, 0, creative);
        } else {
            RVMTypedVariable tempMap = localVars.createTempMap(this
                    .getTreeType());
            ret += tempMap.getType().toString() + " " + tempMap.getName()
                    + " = " + retrieveTree() + ";\n";

            if (creative) {
                ret += lookupIntermediateCreative(localVars, monitor,
                        lastMapStr, lastSet, 0, NODEANDSET);
            } else {
                ret += lookupIntermediateNonCreative(localVars, monitor,
                        lastMapStr, lastSet, 0, NODEANDSET);
            }
        }

        return ret;
    }

    @Override
    public String attachNode(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr) {
        String ret = "";

        RVMVariable monitor = localVars.get(monitorStr);
        RVMVariable lastSet = localVars.get(lastSetStr);

        RVMVariable tempRef = localVars.getTempRef(getLastParam());

        if (queryParam.size() == 1) {
            ret += retrieveTree() + ".putLeaf(" + tempRef + ", " + monitor
                    + ");\n";
        } else {
            RVMTypedVariable var = localVars.getMap(lastMapStr);
            ret += var + ".putLeaf(" + tempRef + ", " + monitor + ");\n";
        }

        ret += lastSet + ".add(" + monitor + ");\n";

        return ret;
    }

    @Override
    public String attachSet(LocalVariables localVars, String monitorStr,
            String lastMapStr, String lastSetStr) {
        String ret = "";

        RVMVariable lastSet = localVars.get(lastSetStr);

        RVMVariable tempRef = localVars.getTempRef(getLastParam());

        if (queryParam.size() == 1) {
            ret += retrieveTree() + ".putSet(" + tempRef + ", " + lastSet
                    + ");\n";
        } else {
            RVMTypedVariable lastMap = localVars.getMap(lastMapStr);

            ret += lastMap + ".putSet(" + tempRef + ", " + lastSet + ");\n";
        }

        return ret;
    }

    private String getGetMapOrSetStrongRefCode(IndexingTreeInterface itf,
            RVMParameter key, RVMVariable weakref, RVMTypedVariable map,
            RVMTypedVariable value) {
        String ret = "";
        ret += value.getType() + " " + value.getName() + " = null;\n";
        ret += this.getGetWithStrongRefCode(map, key, itf, value.getName(),
                weakref.getVarName(), true);
        return ret;
        /*
         * RefTree tree = this.refTrees.get(p.getType().getOp());
         *
         * String ret = ""; ret += "{\n"; ret +=
         * "rvmonitorrt.map.hashentry.EntryPair pair = " + tempMap + "." +
         * methodname + "(" + p.getName() + ");\n"; ret +=
         * "if (pair == null) {\n"; ret += obj + " = null;\n"; ret +=
         * tree.createWeakReferenceConditional(tempRef.getVarName(), p); ret +=
         * "}\n"; ret += "else {\n"; ret += obj + " = pair.getValue();\n"; ret
         * += tempRef + " = pair.getWeakRef();\n"; ret += "}\n"; ret += "}\n";
         * return ret;
         */
    }

    private String getGetMapStrongRefCode(RVMParameter p, RVMVariable tempRef,
            RVMTypedVariable tempMap, RVMTypedVariable obj) {
        return this.getGetMapOrSetStrongRefCode(IndexingTreeInterface.Map, p,
                tempRef, tempMap, obj);
    }

    @Override
    public String addMonitor(LocalVariables localVars, String monitorStr,
            String tempMapStr, String tempSetStr) {
        String ret = "";

        RVMTypedVariable tempMap = localVars.createTempMap(this.getTreeType());

        RVMVariable monitor = localVars.get(monitorStr);
        RVMVariable monitors = localVars.get(tempSetStr);

        ret += tempMap.getType() + " " + tempMap.getName() + " = "
                + retrieveTree() + ";\n";

        RVMParameter p = queryParam.get(0);
        RVMVariable tempRef = localVars.getTempRef(p);

        RVMTypedVariable obj = localVars.createObj(tempMap,
                queryParam.size() == 1 ? IndexingTreeInterface.Set
                        : IndexingTreeInterface.Map);
        if (Main.useWeakRefInterning) {
            if (queryParam.size() == 1) {
                ret += obj.getType() + " " + obj.getName() + " = " + tempMap
                        + ".getSet(" + tempRef + ");\n";
            } else {
                ret += obj.getType() + " " + obj.getName() + " = " + tempMap
                        + ".getMap(" + tempRef + ");\n";
            }
        } else {
            // String methodname = queryParam.size() == 1 ? "getSetStrong" :
            // "getMapStrong";
            IndexingTreeInterface itf = queryParam.size() == 1 ? IndexingTreeInterface.Set
                    : IndexingTreeInterface.Map;
            ret += this.getGetMapOrSetStrongRefCode(itf, p, tempRef, tempMap,
                    obj);
        }

        for (int i = 1; i < queryParam.size(); i++) {

            // if (i != 0) {

            ret += "if (" + obj + " == null) {\n";

            ret += obj + " = new " + obj.getType() + "("
                    + fullParam.getIdnum(p) + ");\n";
            ret += tempMap + ".putMap(" + tempRef + ", " + obj + ");\n";
            ret += "}\n";

            tempMap = localVars.createTempMap(obj.getType());
            ret += tempMap.getType() + " " + tempMap.getName() + " = "
                    + obj.getName() + ";\n";
            // }

            p = queryParam.get(i);
            tempRef = localVars.getTempRef(p);

            obj = localVars.createObj(tempMap,
                    i != queryParam.size() - 1 ? IndexingTreeInterface.Map
                            : IndexingTreeInterface.Set);

            if (Main.useWeakRefInterning) {
                if (i != queryParam.size() - 1)
                    ret += obj.getType() + " " + obj.getName() + " = "
                            + tempMap + ".getMap(" + tempRef + ");\n";
                else
                    ret += obj.getType() + " " + obj.getName() + " = "
                            + tempMap + ".getSet(" + tempRef + ");\n";
            } else {
                IndexingTreeInterface itf = i != queryParam.size() - 1 ? IndexingTreeInterface.Map
                        : IndexingTreeInterface.Set;
                ret += this.getGetMapOrSetStrongRefCode(itf, p, tempRef,
                        tempMap, obj);
            }
        }
        ret += monitors + " = ";
        ret += obj + ";\n";
        ret += "if (" + monitors + " == null) {\n";
        ret += monitors + " = new " + monitorSet.getName() + "();\n";
        ret += tempMap + ".putSet(" + localVars.getTempRef(getLastParam())
                + ", " + monitors + ");\n";
        ret += "}\n";

        ret += monitors + ".add(" + monitor + ");\n";

        // if(cache != null){
        // ret += cache.setCacheKeys(localVars);
        //
        // if (containsSet()) {
        // ret += cache.setCacheSet(monitors);
        // }
        //
        // if (cache.hasNode){
        // ret += cache.setCacheNode(null);
        // }
        // }

        return ret;
    }

    @Override
    public boolean containsSet() {
        return true;
    }

    @Override
    public String retrieveTree() {
        if (parentTree != null)
            return parentTree.retrieveTree();

        if (perthread) {
            String ret = "";
            ret += name + ".get()";
            return ret;

        } else {
            return name.toString();
        }
    }

    @Override
    public String getRefTreeType() {
        String ret = "";

        if (parentTree != null)
            return parentTree.getRefTreeType();

        if (parasiticRefTree == null)
            return ret;

        ret = this.getTreeType().toString();

        return ret;
    }

    @Override
    public String toString() {
        String ret = "";

        if (parentTree == null) {
            if (perthread) {
                String type = this.getTreeType().toString();
                ret += "static final ThreadLocal<" + type + ">" + name
                        + " = new ThreadLocal<" + type + ">() {\n";
                ret += "protected " + type + " initialValue(){\n";
                ret += "return new " + type + "("
                        + fullParam.getIdnum(queryParam.get(0)) + ");\n";

                ret += "}\n";
                ret += "};\n";
            } else {
                String type = this.getTreeType().toString();
                ret += "static " + type + " " + name + " = new " + type + "("
                        + fullParam.getIdnum(queryParam.get(0));
                if (parasiticRefTree != null
                        && parasiticRefTree.generalProperties.size() > 1)
                    ret += ", " + parasiticRefTree.generalProperties.size();
                ret += ");\n";
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
            if (perthread) {
            } else {
                // ret += "System.err.println(\""+ name + " size: \" + (" + name
                // + ".addedMappings - " + name + ".deletedMappings" + "));\n";

                if (parasiticRefTree == null) {
                    if (isGeneral) {
                        if (queryParam.size() == 1) {
                            ret += name
                                    + " = new com.runtimeverification.rvmonitor.java.rt.map.RVMMapOfSetMon("
                                    + fullParam.getIdnum(queryParam.get(0))
                                    + ");\n";
                        } else {
                            ret += name
                                    + " = new com.runtimeverification.rvmonitor.java.rt.map.RVMMapOfAll("
                                    + fullParam.getIdnum(queryParam.get(0))
                                    + ");\n";
                        }
                    } else {
                        if (queryParam.size() == 1) {
                            ret += name
                                    + " = new com.runtimeverification.rvmonitor.java.rt.map.RVMMapOfSet("
                                    + fullParam.getIdnum(queryParam.get(0))
                                    + ");\n";
                        } else {
                            ret += name
                                    + " = new com.runtimeverification.rvmonitor.java.rt.map.RVMMapOfMapSet("
                                    + fullParam.getIdnum(queryParam.get(0))
                                    + ");\n";
                        }
                    }
                } else {
                    if (parasiticRefTree.generalProperties.size() <= 1) {
                        ret += name + " = new " + getRefTreeType() + "("
                                + fullParam.getIdnum(queryParam.get(0))
                                + ");\n";
                    } else {
                        ret += name + " = new " + getRefTreeType() + "("
                                + fullParam.getIdnum(queryParam.get(0)) + ", "
                                + parasiticRefTree.generalProperties.size()
                                + ");\n";
                    }
                }
            }
        }

        if (cache != null)
            ret += cache.reset();

        return ret;
    }

}
