package com.runtimeverification.rvmonitor.java.rvj.output.codedom.type;

import java.util.List;
import java.util.Map;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeHelper;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeInterface;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;

/**
 * This class (or its subclasses) represents a specialized CodeType for two
 * purposes. 1. a marker: having a specialized type for the generated Monitor
 * enables the code generator to make distinction between a Monitor instance and
 * a DisableHolder instance, using RTTI; i.e., the 'instanceof' operator. 2.
 * associated data: a subclass of this class may define additional information,
 * which is similar to a tag, in the sense that that is associated with the
 * core, the CodeType object in this context. That information is used solely
 * during code generation; i.e., the resulting code is not affected. For
 * example, a Tuple class, a subclass of this class, represents Tuple2 or
 * Tuple3, and it keeps the elements of the tuple.
 *
 * As its package name implies, this class is used solely during code
 * generation. The runtime library and the generated code never refers to any of
 * it or its subclass.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class CodeRVType extends CodeType {
    protected CodeRVType(CodeType t) {
        super(t);
    }

    public static CodeRVType.Interface forInterface(CodeType monitortype) {
        CodeType i = CodeHelper.RuntimeType
                .getMonitorInterfaceFromMonitor(monitortype);
        CodeType d = CodeHelper.RuntimeType
                .getDisableHolderFromMonitor(monitortype);
        return new Interface(i, d, monitortype);
    }

    public static CodeRVType.DisableHolder forDisableHolder(CodeType monitortype) {
        CodeType t = CodeHelper.RuntimeType
                .getDisableHolderFromMonitor(monitortype);
        return new DisableHolder(t);
    }

    public static CodeRVType.Monitor forMonitor(CodeType monitortype) {
        return new Monitor(monitortype);
    }

    public static CodeRVType.BasicMonitorSet forBasicMonitorSet(String typename) {
        return new BasicMonitorSet(new CodeType(typename));
    }

    public static CodeRVType.PartitionedMonitorSet forPartitionedMonitorSet(
            String typename,
            Map<RVMParameter, List<IndexingTreeInterface>> ctorargsmap) {
        return new PartitionedMonitorSet(new CodeType(typename), ctorargsmap);
    }

    public static CodeRVType.Tuple forTuple(CodeType tupletype,
            List<CodeRVType> elems) {
        return new Tuple(tupletype, elems);
    }

    public static CodeRVType.IndexingTree forIndexingTree(CodeType treetype) {
        return new IndexingTree(treetype);
    }

    /**
     * This class represents an object that can be used as a leaf.
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     */
    public static abstract class Leaf extends CodeRVType {
        protected Leaf(CodeType t) {
            super(t);
        }
    }

    /**
     * This class corresponds to an interface generated when a leaf can hold
     * either a Monitor instance or a DisableHolder instance.
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     */
    public static class Interface extends Leaf {
        private final DisableHolder disholder;
        private final Monitor monitor;

        public DisableHolder getDisableHolderType() {
            return this.disholder;
        }

        public Monitor getMonitorType() {
            return this.monitor;
        }

        Interface(CodeType itf, CodeType disholder, CodeType monitor) {
            super(itf);

            this.disholder = new DisableHolder(disholder);
            this.monitor = new Monitor(monitor);
        }
    }

    /**
     * This class corresponds the DisableHolder class in the runtime library.
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     * @see com.runtimeverification.rvmonitor.java.rt.tablebase.DisableHolder
     */
    public static class DisableHolder extends Leaf {
        DisableHolder(CodeType t) {
            super(t);
        }
    }

    /**
     * This class represents a generated monitor class. The generated monitor
     * code implements the IMonitor interface in the runtime library.
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     * @see com.runtimeverification.rvmonitor.java.rt.tablebase.IMonitor
     */
    public static class Monitor extends Leaf {
        Monitor(CodeType t) {
            super(t);
        }
    }

    /**
     * This class represents a generated monitor set class. e
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     */
    public static abstract class MonitorSet extends CodeRVType {
        protected MonitorSet(CodeType t) {
            super(t);
        }
    }

    /**
     * This class represents a basic set implementation. The generated set will
     * be a subclass of the AbstractMonitorSet class.
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     * @see com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractMonitorSet
     */
    public static class BasicMonitorSet extends MonitorSet {
        BasicMonitorSet(CodeType t) {
            super(t);
        }
    }

    /**
     * This class represents a partition set implementation. The generated set
     * will be a subclass of the AbstractPartitionedMonitorSet class.
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     * @see com.runtimeverification.rvmonitor.java.rt.tablebase.AbstractPartitionedMonitorSet
     */
    public static class PartitionedMonitorSet extends MonitorSet {
        private final Map<RVMParameter, List<IndexingTreeInterface>> ctorargsmap;

        PartitionedMonitorSet(CodeType t,
                Map<RVMParameter, List<IndexingTreeInterface>> ctorargsmap) {
            super(t);

            this.ctorargsmap = ctorargsmap;
        }

        public List<IndexingTreeInterface> getConstructorArguments(
                RVMParameter prm) {
            List<IndexingTreeInterface> args = this.ctorargsmap.get(prm);
            if (args == null)
                throw new IllegalArgumentException();
            return args;
        }
    }

    /**
     * This class represents a tuple. An instance of this class corresponds to
     * Tuple2 or Tuple3 in the runtime library.
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     * @see com.runtimeverification.rvmonitor.java.rt.tablebase.TableAdopter.Tuple2
     * @see com.runtimeverification.rvmonitor.java.rt.tablebase.TableAdopter.Tuple3
     */
    public static class Tuple extends CodeRVType {
        private final List<CodeRVType> elems;

        public List<CodeRVType> getElements() {
            return this.elems;
        }

        Tuple(CodeType t, List<CodeRVType> elems) {
            super(t);

            this.elems = elems;
        }
    }

    /**
     * This class represents an indexing tree.
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     */
    public static class IndexingTree extends CodeRVType {
        IndexingTree(CodeType t) {
            super(t);
        }
    }
}
