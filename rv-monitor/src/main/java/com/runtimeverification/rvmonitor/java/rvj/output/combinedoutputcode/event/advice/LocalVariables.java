package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.advice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import com.runtimeverification.rvmonitor.java.rvj.output.RVMTypedVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.CombinedOutput;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.IndexingTree.IndexingTreeInterface;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.IndexingTree.IndexingTreeType;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.reftree.RefTree;
import com.runtimeverification.rvmonitor.java.rvj.output.monitor.SuffixMonitor;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;

public class LocalVariables {
    private final TreeMap<String, RefTree> refTrees;

    private final ArrayList<Variable> variables = new ArrayList<Variable>();

    private final HashMap<String, Variable> varMap = new HashMap<String, Variable>();
    private final HashMap<String, Variable> tempRefs = new HashMap<String, Variable>();

    private final ArrayList<RVMTypedVariable> varTempMaps = new ArrayList<RVMTypedVariable>();
    private final ArrayList<RVMTypedVariable> varObjs = new ArrayList<RVMTypedVariable>();
    private final HashMap<String, RVMTypedVariable> varMaps = new HashMap<String, RVMTypedVariable>();
    private final HashMap<String, RVMTypedVariable> varUniqueIdMaps = new HashMap<String, RVMTypedVariable>();

    public LocalVariables(RVMonitorSpec rvmSpec, EventDefinition event,
            CombinedOutput combinedOutput) {
        this.refTrees = combinedOutput.indexingTreeManager.refTrees;

        SuffixMonitor monitorClass = combinedOutput.monitors.get(rvmSpec);
        String monitorName = monitorClass.getOutermostName().toString();
        String monitorSetName = combinedOutput.monitorSets.get(rvmSpec)
                .getName().toString();

        // default variables
        addVar("boolean", "cacheHit", "true");

        addVar(monitorName, "mainMonitor", "null");
        addVar(monitorName, "origMonitor", "null");
        addVar(monitorName, "lastMonitor", "null");
        addVar(monitorName, "monitor", "null");

        // The following variables were removed in favor of uses of ephemeral
        // variables.
        // These old variables are bad, because they don't have types and,
        // consequently,
        // cause the generated code to down-cast, which not only may do
        // unnecessary operation
        // but also prevent type-checking. If someone touches this code or
        // others, please take
        // types into consideration, please...
        // addVar("Object", "obj");
        // addVar("com.runtimeverification.rvmonitor.java.rt.map.RVMMap",
        // "tempMap");
        // addVar("com.runtimeverification.rvmonitor.java.rt.map.RVMMap",
        // "mainMap", "null");
        // addVar("com.runtimeverification.rvmonitor.java.rt.map.RVMMap",
        // "origMap", "null");
        // addVar("com.runtimeverification.rvmonitor.java.rt.map.RVMMap",
        // "lastMap", "null");

        addVar(monitorSetName, "mainSet", "null");
        addVar(monitorSetName, "origSet", "null");
        addVar(monitorSetName, "lastSet", "null");
        addVar(monitorSetName, "monitors", "null");

        for (RVMParameter p : rvmSpec.getParameters()) {
            addTempRef(p.getName(), getRefTree(p).getResultType(), "TempRef_"
                    + p.getName());
        }
    }

    public RefTree getRefTree(RVMParameter p) {
        return refTrees.get(p.getType().toString());
    }

    public void addVar(String type, String mopVarName) {
        RVMVariable mopVar = new RVMVariable(mopVarName);

        if (varMap.get(mopVarName) == null) {
            Variable var = new Variable(type, mopVar);

            variables.add(var);
            varMap.put(mopVarName, var);
        }
    }

    public void addVar(String type, String mopVarName, String init) {
        RVMVariable mopVar = new RVMVariable(mopVarName);

        if (varMap.get(mopVarName) == null) {
            Variable var = new Variable(type, mopVar, init);

            variables.add(var);
            varMap.put(mopVarName, var);
        }
    }

    public void addTempRef(String param, String type, String tempRefName) {
        RVMVariable tempRef = new RVMVariable(tempRefName);

        if (tempRefs.get(param.toString()) == null) {
            Variable var = new Variable(type, tempRef);

            variables.add(var);
            tempRefs.put(param.toString(), var);
        }
    }

    public void init() {
        for (Variable var : variables) {
            var.used = false;
        }
    }

    public RVMVariable get(String name) {
        Variable var = varMap.get(name);

        if (var == null)
            return null;

        var.used = true;

        return var.var;
    }

    public RVMVariable getTempRef(RVMParameter p) {
        return getTempRef(p.getName());
    }

    public String getTempRefType(RVMParameter p) {
        return getTempRefType(p.getName());
    }

    public RVMVariable getTempRef(String paramName) {
        Variable var = tempRefs.get(paramName);

        if (var == null)
            return null;

        var.used = true;
        return var.var;
    }

    public String getTempRefType(String paramName) {
        Variable var = tempRefs.get(paramName);
        if (var == null)
            return null;
        return var.type;
    }

    public RVMTypedVariable createTempMap(IndexingTreeType type) {
        int newid = this.varTempMaps.size() + 1;
        String name = "tempMap" + newid;

        RVMTypedVariable created = RVMTypedVariable
                .fromIndexingTree(name, type);
        this.varTempMaps.add(created);
        return created;
    }

    public RVMTypedVariable getTempMap() {
        int index = this.varTempMaps.size() - 1;
        return this.varTempMaps.get(index);
    }

    public RVMTypedVariable createObj(RVMTypedVariable tempMap,
            IndexingTreeInterface itf) {
        int newid = this.varObjs.size() + 1;
        String name = "obj" + newid;

        IndexingTreeType nestedtype = tempMap.getType().getPart(itf);
        RVMTypedVariable created = RVMTypedVariable.fromIndexingTree(name,
                nestedtype);
        this.varObjs.add(created);
        return created;
    }

    private boolean isUniqueMapNameRequired(String name) {
        boolean createUnique = false;

        if (name.equals("origMap"))
            createUnique = true;
        else if (name.equals("lastMap"))
            createUnique = true;

        return createUnique;
    }

    public RVMTypedVariable.Pair createOrFindMap(String name,
            IndexingTreeType type) {
        boolean createUnique = this.isUniqueMapNameRequired(name);

        RVMTypedVariable var = null;
        boolean created = false;

        if (createUnique) {
            RVMTypedVariable lastvar = this.varUniqueIdMaps.get(name);
            int lastsuffix = 0;
            if (lastvar != null)
                lastsuffix = Integer.parseInt(lastvar.getName().substring(
                        name.length()));
            int id = lastsuffix + 1;

            String uniquename = name + id;
            var = RVMTypedVariable.fromIndexingTree(uniquename, type);
            this.varUniqueIdMaps.put(name, var);
            created = true;
        } else {
            var = this.varMaps.get(name);
            if (var == null) {
                var = RVMTypedVariable.fromIndexingTree(name, type);
                this.varMaps.put(name, var);
                created = true;
            }
        }

        return new RVMTypedVariable.Pair(var, created);
    }

    public RVMTypedVariable getMap(String name) {
        boolean createUnique = this.isUniqueMapNameRequired(name);

        if (createUnique)
            return this.varUniqueIdMaps.get(name);
        else
            return this.varMaps.get(name);
    }

    public String varDecl() {
        String ret = "";

        for (Variable var : variables) {
            if (var.used) {
                ret += var.type + " " + var.var;
                if (var.init != null)
                    ret += " = " + var.init;
                ret += ";\n";
            }
        }

        if (ret.length() != 0)
            ret += "\n";

        return ret;
    }

    class Variable {
        String type;
        RVMVariable var;
        boolean used = false;
        String init = null;

        Variable(String type, RVMVariable var) {
            this.type = type;
            this.var = var;
        }

        Variable(String type, RVMVariable var, String init) {
            this.type = type;
            this.var = var;
            this.init = init;
        }
    }
}
