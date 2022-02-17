package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.reftree;

import java.util.ArrayList;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.output.NotImplementedException;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeInterface;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;

public class RefTree {
    private final RVMVariable name;

    public final String type;

    public final ArrayList<RVMonitorSpec> properties = new ArrayList<RVMonitorSpec>();
    public final ArrayList<RVMonitorSpec> generalProperties = new ArrayList<RVMonitorSpec>();

    private IndexingTreeInterface hostIndexingTree = null;

    public RefTree(String outputName, RVMParameter param) {
        this.type = param.getType().toString();

        String typeStr = type;
        int dim;

        if (typeStr.endsWith("]")) {
            int firstBracket = typeStr.indexOf("[");
            int lastBracket = typeStr.lastIndexOf("[");

            dim = lastBracket - firstBracket + 1;

            typeStr = typeStr.substring(0, firstBracket);

            typeStr += "Array";

            if (dim > 1)
                typeStr += dim;
        }

        this.name = new RVMVariable(outputName + "_" + typeStr + "_RefMap");
    }

    public void addProperty(RVMonitorSpec spec) {
        properties.add(spec);
        if (spec.isGeneral())
            generalProperties.add(spec);
    }

    public void setHost(IndexingTreeInterface indexingTree) {
        hostIndexingTree = indexingTree;
    }

    public IndexingTreeInterface getHost() {
        return this.hostIndexingTree;
    }

    public String get(RVMVariable tempRef, RVMParameter p) {
        String ret = "";
        ret += tempRef + " = ";
        ret += this.get(p);
        ret += ";\n";
        return ret;
    }

    public String get(RVMParameter p) {
        String ret = "";
        RVMVariable name;

        if (hostIndexingTree == null)
            name = this.name;
        else
            name = new RVMVariable(hostIndexingTree.getImplementation()
                    .getName());

        ret += name + ".findOrCreateWeakRef(" + p.getName();
        ret += ")";
        return ret;
    }

    public String getRefNonCreative(RVMVariable tempRef, RVMParameter p) {
        String ret = "";
        ret += tempRef + " = ";
        ret += this.getRefNonCreative(p);
        ret += ";\n";
        return ret;
    }

    public String getRefNonCreative(RVMParameter p) {
        String ret = "";
        RVMVariable name;

        if (hostIndexingTree == null)
            name = this.name;
        else
            name = new RVMVariable(hostIndexingTree.getImplementation()
                    .getName());

        ret += name + ".findWeakRef(" + p.getName();
        ret += ")";

        return ret;
    }

    private String createWeakReferenceInternal(String weakref,
            RVMParameter ref, boolean conditional) {
        String ret = "";
        RVMVariable name;

        if (hostIndexingTree == null)
            name = this.name;
        else
            name = new RVMVariable(hostIndexingTree.getImplementation()
                    .getName());

        String weakreftype = this.getResultType();

        if (conditional)
            ret += "if (" + weakref + " == null) {\n";

        ret += weakref + " = new ";
        ret += weakreftype;
        ret += "(";
        if (generalProperties.size() >= 2) {
            ret += name;
            ret += ".getTagLen(),";
        }
        ret += ref.getName();
        ret += ");\n";

        if (conditional)
            ret += "}\n";

        return ret;
    }

    public String createWeakReferenceConditional(String weakref,
            RVMParameter ref) {
        return this.createWeakReferenceInternal(weakref, ref, true);
    }

    public boolean isTagging() {
        return generalProperties.size() != 0;
    }

    public int getTagNumber(RVMonitorSpec spec) {
        if (generalProperties.size() <= 1)
            return -1;
        else
            return generalProperties.indexOf(spec);
    }

    public String getResultType() {
        String ret = "";

        ret += "Cached";
        /*
         * We no longer store 'disable' and 't' at GWRT. if
         * (Main.useWeakRefInterning) { if (generalProperties.size() == 0) ret
         * += ""; else if (generalProperties.size() == 1) ret += "Tag"; else ret
         * += "MultiTag"; }
         */
        ret += "WeakReference";

        return ret;
    }

    public CodeType getResultFQType() {
        String pkgname = "com.runtimeverification.rvmonitor.java.rt.ref";
        String klass = this.getResultType();
        return new CodeType(pkgname, klass);
    }

    public String getType() {
        String ret = "";

        if (hostIndexingTree == null) {
            if (Main.useWeakRefInterning) {
                // Now that we no longer keep 'disable' and 't' at weak
                // references,
                // I don't think we need any distinction.
                /*
                 * if (generalProperties.size() == 0) ret =
                 * "com.runtimeverification.rvmonitor.java.rt.table.BasicRefMap"
                 * ; else if (generalProperties.size() == 1) ret =
                 * "com.runtimeverification.rvmonitor.java.rt.table.TagRefMap";
                 * else ret =
                 * "com.runtimeverification.rvmonitor.java.rt.table.MultiTagRefMap"
                 * ;
                 */
                ret = "com.runtimeverification.rvmonitor.java.rt.table.BasicRefMap";
            } else {
                // RefTree should not be used when weak-reference interning is
                // disabled.
                throw new NotImplementedException();
            }
        } else {
            // ret = hostIndexingTree.getRefTreeType();
            ret = hostIndexingTree.getImplementation().getField().getType()
                    .toString();
            // ret = hostIndexingTree.getType().toString();
        }

        return ret;
    }

    public RVMVariable getName() {
        return name;
    }

    @Override
    public String toString() {
        String ret = "";

        ret += "private static final " + this.getType() + " " + name + " = ";
        if (hostIndexingTree == null) {
            // We no longer need to take care of the following case.
            /*
             * if(generalProperties.size() > 1) ret += "new " + getType() + "("
             * + generalProperties.size() + ");\n"; else ret += "new " +
             * getType() + "();\n";
             */
            ret += "new " + getType() + "();\n";
        } else {
            ret += hostIndexingTree.getImplementation().getName() + ";\n";
        }

        return ret;
    }

    public String reset() {
        String ret = "";

        ret += name;
        ret += " = ";
        if (hostIndexingTree == null) {
            // We no longer need to take care of the following case.
            /*
             * if(generalProperties.size() > 1) ret += "new " + getType() + "("
             * + generalProperties.size() + ");\n"; else ret += "new " +
             * getType() + "();\n";
             */
            ret += "new " + getType() + "();\n";
        } else {
            ret += hostIndexingTree.getImplementation().getName() + ";\n";
        }

        return ret;
    }

    public String getCleanUpCode(String accvar) {
        String ret = "";
        if (hostIndexingTree == null) {
            ret += accvar;
            ret += " += ";
            ret += this.name;
            ret += ".";
            ret += "cleanUpUnnecessaryMappings();\n";
        }
        return ret;
    }

}
