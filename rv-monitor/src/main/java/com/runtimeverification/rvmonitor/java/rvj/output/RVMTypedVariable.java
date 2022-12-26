package com.runtimeverification.rvmonitor.java.rvj.output;

import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.IndexingTree.IndexingTreeType;

/*
 * This class is similar to RVMVariable, but this class has a type. This was introduced
 * while making the generated code type-safe.
 */
public class RVMTypedVariable {
    private final String name;
    private final IndexingTreeType type;

    public String getName() {
        return this.name;
    }

    public IndexingTreeType getType() {
        return this.type;
    }

    private RVMTypedVariable(String name, IndexingTreeType type) {
        this.name = name;
        this.type = type;
    }

    public static RVMTypedVariable fromIndexingTree(String name,
            IndexingTreeType type) {
        return new RVMTypedVariable(name, type);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static class Pair {
        private final RVMTypedVariable var;
        private final boolean created;

        public RVMTypedVariable getVariable() {
            return this.var;
        }

        public boolean isCreated() {
            return this.created;
        }

        public Pair(RVMTypedVariable var, boolean created) {
            this.var = var;
            this.created = created;
        }
    }
}
