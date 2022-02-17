package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.output.NotImplementedException;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeAssignStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeBlockStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExprStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeFieldRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeLiteralExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMemberField;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMethodInvokeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeNewExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeObject;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmtCollection;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarDeclStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeHelper;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodePair;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeGenerator;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeRVType;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.itf.WeakReferenceVariables;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.reftree.RefTree;
import com.runtimeverification.rvmonitor.java.rvj.output.monitor.SuffixMonitor;
import com.runtimeverification.rvmonitor.java.rvj.output.monitorset.MonitorSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;

/**
 * This class represents the implementation of an indexing tree. One instance of
 * this class can be shared by multiple indexing tree interfaces
 * (IndexingTreeInterface). For more information, see IndexingTreeInterface.
 *
 * It is totally possible an instance of this class is designed to serve
 * [Collection,Iterator] but tied with an interface for serving [Collection], if
 * that interface is combined with one for serving [Collection,Iterator]. When
 * used by the [Collection] interface, the traversal simply stops after
 * accessing the first level.
 *
 * In this example, the interface for serving [Collection,Iterator] is specially
 * called the master interface, because that parameter list matches with this
 * instance's parameter list.
 *
 * An instance of this class will eventually generate an indexing tree, as a
 * static field, in the generated Java class.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 * @see IndexingTreeInterface
 */
public class IndexingTreeImplementation implements ICodeGenerator {
    private final IndexingTreeInterface master;
    /**
     * Represents the top entry of the indexing tree. If this instance is for
     * [Collection,Iterator], the entry will have a valid 'map' field. If this
     * instance is used for the case where no parameters are bound, the entry
     * will have the null 'map' field; instead, it would have a valid 'set'
     * field.
     */
    private final Entry topEntry;
    /**
     * Represents the field for holding the generated indexing tree.
     */
    private CodeMemberField field;

    public IndexingTreeInterface getMasterInterface() {
        return this.master;
    }

    public Entry getTopEntry() {
        return this.topEntry;
    }

    public String getName() {
        return this.field.getName();
    }

    public CodeMemberField getField() {
        return this.field;
    }

    public String getPrettyName() {
        return this.master.getPrettyName();
    }

    /**
     * Creates an instance of IndexingTreeImplementation.
     *
     * @param master
     *            master interface
     * @param outputName
     *            output name
     * @param specParams
     *            parameters defined in the specification
     * @param queryParams
     *            parameters used for query
     * @param contentParams
     *            parameters provided by this tree
     * @param set
     *            monitor set class
     * @param monitor
     *            monitor class
     * @param evttype
     *            specifies whether this event always, possibly or never creates
     *            a monitor
     * @param timetrack
     *            specifies whether time tracking (keeping the 'disable' and 't'
     *            fields) is necessary
     */
    IndexingTreeImplementation(IndexingTreeInterface master, String outputName,
            RVMParameters specParams, RVMParameters queryParams,
            RVMParameters contentParams, MonitorSet set, SuffixMonitor monitor,
            EventKind evttype, boolean timetrack) {
        this.master = master;

        {
            CodeRVType.MonitorSet settype = set.getCodeType();
            CodeRVType.Monitor monitortype = monitor.getRuntimeType();
            boolean fullbinding = specParams.equals(queryParams);
            this.topEntry = Entry.determine(queryParams, settype, monitortype,
                    fullbinding, evttype, timetrack);
        }

        String name = IndexingTreeNameMangler.fieldName(outputName,
                queryParams, contentParams);
        this.field = this.topEntry.generateField(name, specParams, queryParams);
    }

    private IndexingTreeImplementation(IndexingTreeInterface master,
            Entry topEntry, String treename) {
        this.master = master;
        this.topEntry = topEntry;
        this.field = this.topEntry.generateField(treename,
                master.getSpecParams(), master.getQueryParams());
    }

    /**
     * Creates a new implementation by combining two existing implementations.
     * The caller should guarantee that the two implementations are combined.
     *
     * @param superimpl
     *            implementation that subsumes the subimpl
     * @param subimpl
     *            implementation that is subsumed by the superimpl
     * @return combined implementation
     */
    static IndexingTreeImplementation combine(
            IndexingTreeImplementation superimpl,
            IndexingTreeImplementation subimpl) {
        Entry combined = Entry.join(superimpl.topEntry, subimpl.topEntry);
        return new IndexingTreeImplementation(superimpl.master, combined,
                superimpl.getName());
    }

    /**
     * Tells this implementation that global weak reference table (GWRT) should
     * be embeded.
     *
     * @param refTree
     *            unused
     * @param specParams
     *            parameters defined in the specification
     * @param queryParams
     *            parameters for query
     */
    void embedGWRT(RefTree refTree, RVMParameters specParams,
            RVMParameters queryParams) {
        // Since storing 'disable' and 't' at weak references turned out to be
        // incorrect,
        // indexing trees with multiple tags will not be used any longer.
        // Therefore, there
        // are only two cases: one without GWRT embedding, and the other with
        // GWRT
        // embedding (which has the BasicRef- prefix).
        Level embedding = this.topEntry.getMap();
        if (embedding == null)
            throw new IllegalArgumentException();

        embedding.embedGWRT();

        String fieldname = this.getName();
        this.field = this.topEntry.generateField(fieldname, specParams,
                queryParams);
    }

    /**
     * This method creates an empty block and puts the provided body into it, in
     * order to avoid potential name clashes.
     *
     * @param body
     *            the body that will be put inside of the empty block
     * @param comment
     *            optional comment for debugging
     * @return the resulting block
     */
    private CodeStmtCollection promoteSeparateBlock(CodeStmtCollection body,
            String comment) {
        if (body == null)
            return null;

        if (comment != null) {
            CodeStmtCollection newbody = new CodeStmtCollection();
            newbody.comment(comment);
            newbody.add(body);
            body = newbody;
        }

        CodeBlockStmt block = new CodeBlockStmt(body);
        return new CodeStmtCollection(block);
    }

    CodeStmtCollection generateFindOrCreateEntryCode(RVMParameters queryprms,
            RVMParameters specprms, WeakReferenceVariables weakrefs,
            StmtCollectionInserter<CodeExpr> inserter) {
        CodeFieldRefExpr root = new CodeFieldRefExpr(this.field);
        CodeStmtCollection stmts = this.topEntry.generateFindOrCreateCode(
                queryprms, Access.Entry, specprms, weakrefs, inserter, root);
        return this.promoteSeparateBlock(stmts, "FindOrCreateEntry");
    }

    CodeStmtCollection generateFindEntryWithStrongRefCode(
            RVMParameters queryprms, RVMParameters specprms,
            WeakReferenceVariables weakrefs,
            StmtCollectionInserter<CodeExpr> inserter,
            boolean suppressLastNullCheck) {
        CodeFieldRefExpr root = new CodeFieldRefExpr(this.field);
        CodeStmtCollection stmts = this.topEntry.generateFindWithStrongRefCode(
                queryprms, Access.Entry, specprms, weakrefs, inserter, root,
                suppressLastNullCheck);
        return this.promoteSeparateBlock(stmts, "FindEntry");
    }

    CodeStmtCollection generateFindOrCreateCode(RVMParameters queryprms,
            Access access, RVMParameters specprms,
            WeakReferenceVariables weakrefs,
            StmtCollectionInserter<CodeExpr> inserter) {
        CodeFieldRefExpr root = new CodeFieldRefExpr(this.field);
        CodeStmtCollection stmts = this.topEntry.generateFindOrCreateCode(
                queryprms, access, specprms, weakrefs, inserter, root);
        return this.promoteSeparateBlock(stmts, "FindOrCreate");
    }

    CodeStmtCollection generateFindCode(RVMParameters queryprms, Access access,
            RVMParameters specprms, WeakReferenceVariables weakrefs,
            StmtCollectionInserter<CodeExpr> inserter) {
        CodeFieldRefExpr root = new CodeFieldRefExpr(this.field);
        return this.generateFindCode(root, queryprms, access, specprms,
                weakrefs, inserter);
    }

    CodeStmtCollection generateFindCode(CodeExpr root, RVMParameters queryprms,
            Access access, RVMParameters specprms,
            WeakReferenceVariables weakrefs,
            StmtCollectionInserter<CodeExpr> inserter) {
        CodeStmtCollection stmts = this.topEntry.generateFindCode(queryprms,
                access, specprms, weakrefs, inserter, root);
        return this.promoteSeparateBlock(stmts, "FindCode");
    }

    CodeStmtCollection generateInsertMonitorCode(RVMParameters queryprms,
            RVMParameters specprms, WeakReferenceVariables weakrefs,
            final CodeVarRefExpr monitorref) {
        CodeFieldRefExpr root = new CodeFieldRefExpr(this.field);
        StmtCollectionInserter<CodeExpr> inserter = new StmtCollectionInserter<CodeExpr>() {
            @Override
            public CodeStmtCollection insertLastEntry(Entry entry,
                    CodeExpr entryref) {
                CodeStmtCollection stmts = new CodeStmtCollection();
                CodePair<CodeVarRefExpr> codepair = entry.generateFieldGetCode(
                        entryref, Access.Set, "target");
                stmts.add(codepair.getGeneratedCode());

                CodeVarRefExpr fieldref = codepair.getLogicalReturn();
                CodeMethodInvokeExpr invoke = new CodeMethodInvokeExpr(
                        CodeType.foid(), fieldref, "add", monitorref);
                stmts.add(new CodeExprStmt(invoke));
                return stmts;
            }
        };
        CodeStmtCollection stmts = this.topEntry.generateFindOrCreateCode(
                queryprms, Access.Entry, specprms, weakrefs, inserter, root);
        return this.promoteSeparateBlock(stmts, "InsertMonitor");
    }

    /**
     * Returns the exact type of this indexing tree. The returned type can be
     * used to declare this tree in the generated code.
     *
     * @return the type of this indexing tree
     */
    public CodeType getCodeType() {
        return this.topEntry.getCodeType();
    }

    /**
     * Retrieves the entry that results from accessing this indexing tree using
     * the provided parameters.
     *
     * @param params
     *            parameters for accessing this indexing tree
     * @return resulting entry
     */
    Entry lookupEntry(RVMParameters params) {
        return this.topEntry.lookupEntry(params);
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        this.field.getCode(fmt);
    }

    /**
     * This class is used to insert some code while visiting a node in the
     * indexing tree. What the user typically needs to do is to create an
     * anonymous class from this class and overrides methods if some code should
     * be inserted at that particular moment.
     *
     * In RV-Monitor, this class (or its subclass) is invoked by an
     * implementation of IIndexingTreeVisitor while it is visiting nodes. The
     * separation of the visitor and the inserter was made because visiting
     * patterns are only a few whereas inserting patterns are various.
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     *
     * @param <T>
     *            currently it is always CodeExpr
     */
    public static class StmtCollectionInserter<T extends CodeObject> {
        /**
         * Allows the user to insert code at the second last map. By the second
         * last map, we mean the map that lies right before the leaf node; i.e.,
         * there is no more map below this.
         *
         * @param mapref
         *            reference to the second last map
         * @return code to be added
         */
        public CodeStmtCollection insertSecondLastMap(T mapref) {
            return null;
        }

        /**
         * Allows the user to insert code at the last entry. By the last entry,
         * we mean there is no entry below this. This also implies that this
         * entry does not contain any map.
         *
         * @param entry
         *            the last entry
         * @param entryref
         *            reference to the last entry
         * @return code to be added
         */
        public CodeStmtCollection insertLastEntry(Entry entry, T entryref) {
            return null;
        }

        /**
         * Allows the user to insert code at the last field (such as map, set or
         * leaf).
         *
         * @param entry
         *            the last entry that includes the provided field
         * @param contextref
         *            reference to the field
         * @return code to be added
         */
        public CodeStmtCollection insertLastField(Entry entry, T fieldref) {
            return null;
        }
    }

    /**
     * This enumeration represents the kind of an access in an indexing tree.
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     * @see Entry
     */
    public enum Access {
        Entry, Map, Set, Leaf,
    }

    /**
     * This enumeration represents the kind of a leaf.
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     * @see Entry
     */
    public enum LeafKind {
        Monitor, Holder,
    }

    /**
     * This enumeration indiciates whether an event always, possibly or never
     * creates a monitor.
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     * @see Entry
     */
    public enum EventKind {
        AlwaysCreate, MayCreate, NeverCreate,
    }

    /**
     * This class represents an entry in an indexing tree. An entry is
     * compile-time object that corresponds to the type of values in an indexing
     * tree at runtime. An entry can hold a map, set, and/or leaf. For example,
     * suppose that an entry holds both a map and a set. During code generation,
     * an instance of this class will be created, where both the 'map' and 'set'
     * fields are set. From this entry, the generated code will use Tuple2<Map,
     * Set> to plug two things into the value in an indexing tree.
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     * @see Level
     */
    public static class Entry {
        private final Level map;
        private final CodeRVType.MonitorSet set;
        private final CodeRVType.Leaf leaf;
        private final Map<LeafKind, CodeRVType.Leaf> leafTypes;

        public final Level getMap() {
            return this.map;
        }

        public final CodeRVType.MonitorSet getSet() {
            return this.set;
        }

        public final CodeRVType getLeaf() {
            return this.leaf;
        }

        public final CodeRVType getLeafType(LeafKind leaf) {
            CodeRVType t = this.leafTypes.get(leaf);
            if (t == null)
                throw new IllegalArgumentException();
            return t;
        }

        private Entry(Level map, CodeRVType.MonitorSet set,
                CodeRVType.Leaf leaf, Map<LeafKind, CodeRVType.Leaf> leaftypes) {
            this.map = map;
            this.set = set;
            this.leaf = leaf;
            this.leafTypes = leaftypes;
        }

        private static Entry fromCodeType(Level map, CodeRVType.MonitorSet set,
                CodeRVType.Monitor monitor, Set<LeafKind> leafflags) {
            CodeRVType.Leaf leaftype = null;
            Map<LeafKind, CodeRVType.Leaf> typemap = new HashMap<LeafKind, CodeRVType.Leaf>();
            if (leafflags != null) {
                int numtypes = 0;
                for (LeafKind l : leafflags) {
                    switch (l) {
                    case Monitor:
                        leaftype = monitor;
                        typemap.put(l, leaftype);
                        break;
                    case Holder:
                        leaftype = CodeRVType.forDisableHolder(monitor);
                        typemap.put(l, leaftype);
                        break;
                    default:
                        throw new NotImplementedException();
                    }
                    ++numtypes;
                }

                if (numtypes > 1)
                    leaftype = CodeRVType.forInterface(monitor);
            }

            return new Entry(map, set, leaftype, typemap);
        }

        /**
         * Creates an entry from settings such as whether parameters are fully
         * bound, event type and whether time tracking is needed. This method
         * calls Level.determine(), which can call this method recursively, to
         * construct all the necessary entries and levels.
         *
         * @param params
         *            parameter list used to query the indexing tree
         * @param set
         *            monitor set type
         * @param leaf
         *            monitor type
         * @param fullbinding
         *            true if parameters are fully bound
         * @param evttype
         *            whether an event always, possibly or never creates a
         *            monitor
         * @param timetrack
         *            whether time tracking is needed
         * @return constructed entry
         */
        public static Entry determine(RVMParameters params,
                CodeRVType.MonitorSet set, CodeRVType.Monitor leaf,
                boolean fullbinding, EventKind evttype, boolean timetrack) {
            if (params.size() == 0) {
                Set<LeafKind> leafflags;

                if (timetrack) {
                    switch (evttype) {
                    case AlwaysCreate:
                        leafflags = EnumSet.of(LeafKind.Monitor);
                        break;
                    case MayCreate:
                        leafflags = EnumSet.of(LeafKind.Monitor,
                                LeafKind.Holder);
                        break;
                    case NeverCreate:
                        if (fullbinding) {
                            // Even though the given event is not a creation
                            // event, a monitor instance can be
                            // created by combining existing parameters.
                            leafflags = EnumSet.of(LeafKind.Monitor,
                                    LeafKind.Holder);
                        } else {
                            // Otherwise, a monitor instance may not be created.
                            // This is based on conjecture; i.e.,
                            // this assumption might be wrong. Unfortunately,
                            // this assumption is wrong. At this
                            // moment, let's do conservatively.
                            // leafflags = EnumSet.of(LeafKind.Holder);
                            leafflags = EnumSet.of(LeafKind.Monitor,
                                    LeafKind.Holder);
                        }
                        break;
                    default:
                        throw new NotImplementedException();
                    }
                } else {
                    // If time tracking is unnecessary, a DisableHolder object
                    // will never be created.
                    leafflags = EnumSet.of(LeafKind.Monitor);
                }

                if (fullbinding)
                    return Entry.fromCodeType(null, null, leaf, leafflags);
                else {
                    // If time tracking is unnecessary, there is no need to have
                    // a field for
                    // a DisableHolder, which is merely for time tracking.
                    if (timetrack)
                        return Entry.fromCodeType(null, set, leaf, leafflags);
                    else
                        return Entry.fromCodeType(null, set, null, null);
                }
            } else {
                Level map = Level.determine(params, set, leaf, fullbinding,
                        evttype, timetrack);
                return new Entry(map, null, null, null);
            }
        }

        /**
         * Joins two entries. The caller should guarantee that two entries can
         * be joined. This method is used when two indexing trees are combined.
         *
         * @param entry1
         *            one entry
         * @param entry2
         *            the other entry
         * @return merged entry
         */
        public static Entry join(Entry entry1, Entry entry2) {
            boolean s1 = entry1.set != null;
            boolean s2 = entry2.set != null;
            boolean l1 = entry1.leaf != null;
            boolean l2 = entry2.leaf != null;
            if ((s1 && s2) || (l1 && l2)) {
                // Although both entries can share the subsequent levels (i.e.,
                // both of them
                // can have non-null 'map' fields), I assume their sets and
                // leaves are disjoint.
                throw new IllegalArgumentException();
            }

            CodeRVType.MonitorSet set = s1 ? entry1.set : entry2.set;
            CodeRVType.Leaf leaf = l1 ? entry1.leaf : entry2.leaf;
            Map<LeafKind, CodeRVType.Leaf> leaftypes = l1 ? entry1.leafTypes
                    : entry2.leafTypes;

            Level joinedmap = Level.join(entry1.map, entry2.map);

            return new Entry(joinedmap, set, leaf, leaftypes);
        }

        public Entry lookupEntry(RVMParameters params) {
            List<RVMParameter> list = params.toList();
            return this.lookupEntry(list, 0);
        }

        public Entry lookupEntry(RVMParameter... params) {
            List<RVMParameter> list = Arrays.asList(params);
            return this.lookupEntry(list, 0);
        }

        private Entry lookupEntry(List<RVMParameter> params, int index) {
            if (index == params.size())
                return this;

            if (this.map == null)
                throw new IllegalArgumentException();

            RVMParameter param = params.get(index);
            if (!this.map.key.equals(param))
                throw new IllegalArgumentException();

            return this.map.value.lookupEntry(params, index + 1);
        }

        private CodeType accessEntry(Access access) {
            switch (access) {
            case Entry:
                return this.getCodeType();
            case Map:
                if (this.map == null)
                    throw new IllegalArgumentException();
                return this.map.getCodeType();
            case Set:
                if (this.set == null)
                    throw new IllegalArgumentException();
                return this.set;
            case Leaf:
                if (this.leaf == null)
                    throw new IllegalArgumentException();
                return this.leaf;
            default:
                throw new IllegalArgumentException();
            }
        }

        public CodeType getCodeType() {
            ArrayList<CodeRVType> enabled = new ArrayList<CodeRVType>();

            if (this.map != null)
                enabled.add(this.map.getCodeType());
            if (this.set != null)
                enabled.add(this.set);
            if (this.leaf != null)
                enabled.add(this.leaf);

            if (enabled.size() == 1)
                return enabled.get(0);

            return CodeHelper.RuntimeType.getIndexingTreeTuple(enabled);
        }

        /**
         * Returns the index in the tuple for accessing the specified access
         * kind. The indexing tree can hold a single object as a value. This is
         * a design decision to have one unified implementation for all the
         * indexing trees; previously, about 20 implementations existed.
         * Instead, the new indexing tree implementation employs a tuple (such
         * as Tuple2 and Tuple3) to put multiple entities (such as both a map
         * and a set) as a value in an indexing tree. For example, suppose that
         * a Tuple2 instance is used for a map and a set. To extract a map from
         * this tuple, it is necessary to know which field corresponds to the
         * map. This method returns the index of the corresponding field. This
         * method can determine the index because a tuple holds a map, set
         * and/or leaf in the predefined order. When a tuple is used, the index
         * starts from 1, which corresponds to the getValue1() method. When a
         * tuple is not used, because the value holds a single object, this
         * method returns 0.
         *
         * @param access
         *            access kind
         * @return 0 if no tuples are used; or a non-zero index in a tuple for
         *         accessing the specified access kind
         */
        private int calculateFieldIndex(Access access) {
            boolean m = this.map != null;
            boolean s = this.set != null;
            boolean l = this.leaf != null;
            int count = (m ? 1 : 0) + (s ? 1 : 0) + (l ? 1 : 0);

            // No wrappers are created if the entry holds a single field.
            if (count == 1 || access == Access.Entry)
                return 0;

            int index = 0;
            switch (access) {
            case Entry:
                throw new NotImplementedException();
            case Map:
                index = 1;
                break;
            case Set:
                index = 2;
                break;
            case Leaf:
                index = 3;
                break;
            default:
                throw new NotImplementedException();
            }

            if (m) {
                if (access == Access.Map)
                    return index;
            } else
                --index;

            if (s) {
                if (access == Access.Set)
                    return index;
            } else
                --index;

            if (l) {
                if (access == Access.Leaf)
                    return index;
            } else
                --index;
            throw new IllegalArgumentException();
        }

        public CodeMemberField generateField(String name,
                RVMParameters specParams, RVMParameters queryParams) {
            CodeExpr init;
            CodeType type = this.getCodeType();
            {
                List<CodeExpr> args = new ArrayList<CodeExpr>();
                if (type instanceof CodeRVType.Tuple) {
                    CodeRVType.Tuple tuple = (CodeRVType.Tuple) type;
                    for (CodeRVType elem : tuple.getElements()) {
                        // A map or set in a tuple needs to be instantiated.
                        if (elem instanceof CodeRVType.IndexingTree) {
                            CodeExpr id = CodeLiteralExpr.integer(specParams
                                    .getIdnum(queryParams.get(0)));
                            CodeExpr createmap = new CodeNewExpr(elem, id);
                            args.add(createmap);
                        } else if (elem instanceof CodeRVType.MonitorSet) {
                            CodeExpr createset = new CodeNewExpr(elem);
                            args.add(createset);
                        } else
                            args.add(CodeLiteralExpr.nul());
                    }
                } else if (this.getMap() != null) {
                    // A map needs an argument, which represents the tree id.
                    args.add(CodeLiteralExpr.integer(specParams
                            .getIdnum(queryParams.get(0))));
                }
                init = new CodeNewExpr(type, args);
            }

            return new CodeMemberField(name, false, true, true, type, init);
        }

        /**
         * Generates code for accessing a specific field from the given entry.
         * If the entry has only a single field (i.e., the type is not Tuple2 or
         * Tuple3), this method does not do anything.
         *
         * @param entryref
         *            entry where the field is retrieved
         * @param access
         *            specifies which field is of interest
         * @param varnameprefix
         *            prefix of the variable where the retrieved field is
         *            assigned
         * @return pair of the generated code and the reference to either the
         *         created variable or the entry
         */
        public CodePair<CodeVarRefExpr> generateFieldGetCode(CodeExpr entryref,
                Access access, String varnameprefix) {
            CodeExpr result = this
                    .generateFieldGetInlinedCode(entryref, access);

            if (result instanceof CodeVarRefExpr)
                return new CodePair<CodeVarRefExpr>((CodeVarRefExpr) result);

            String varname;
            if (varnameprefix == null)
                varname = access.toString().toLowerCase();
            else
                varname = varnameprefix + access.toString();
            CodeVariable var = new CodeVariable(result.getType(), varname);

            CodeVarDeclStmt decl = new CodeVarDeclStmt(var, result);
            return new CodePair<CodeVarRefExpr>(decl, new CodeVarRefExpr(var));
        }

        /**
         * Generates code for accessing a specific field from the given entry
         * without introducing any variable.
         *
         * @param entryref
         *            entry where the field is retrieved
         * @param access
         *            specifies which field is of interest
         * @return generated code
         */
        public CodeExpr generateFieldGetInlinedCode(CodeExpr entryref,
                Access access) {
            CodeExpr result;

            int index = this.calculateFieldIndex(access);
            if (index == 0)
                result = entryref;
            else {
                String getter = "getValue" + index;
                CodeType type = this.accessEntry(access);
                result = new CodeMethodInvokeExpr(type, entryref, getter);
            }

            return result;
        }

        /**
         * Generates code for setting a value in the field.
         *
         * @param entryref
         *            entry where the field is retrieved
         * @param access
         *            specifies which field is of interest
         * @param value
         *            value to be assigned
         * @return generated code
         */
        public CodeStmtCollection generateFieldSetCode(CodeExpr entryref,
                Access access, CodeExpr value) {
            CodeStmt assign;

            int index = this.calculateFieldIndex(access);
            if (index == 0) {
                assign = new CodeAssignStmt(entryref, value);
            } else {
                String setter = "setValue" + index;
                CodeType type = this.accessEntry(access);
                CodeExpr invoke = new CodeMethodInvokeExpr(type, entryref,
                        setter, value);
                assign = new CodeExprStmt(invoke);
            }

            return new CodeStmtCollection(assign);
        }

        /**
         * Generates code for retrieving the node that corresponds to the given
         * parameter in the indexing tree.
         *
         * @param usestrongref
         *            true if one wants to look up using a strong reference
         *            rather than a weak reference
         * @param child
         *            entry
         * @param mapref
         *            reference to the map where the lookup is made
         * @param param
         *            parameter for the lookup, used if 'usestrongref' is true
         * @param weakref
         *            weak reference for the lookup, used if 'usestrongref' is
         *            false
         * @return generated code
         */
        private CodeMethodInvokeExpr generateGetNodeCode(boolean usestrongref,
                Entry child, CodeExpr mapref, RVMParameter param,
                CodeVarRefExpr weakref) {
            CodeType type = child.accessEntry(Access.Entry);

            CodeMethodInvokeExpr invoke;
            if (usestrongref) {
                CodeVarRefExpr strongref = new CodeVarRefExpr(new CodeVariable(
                        CodeType.object(), param.getName()));
                invoke = new CodeMethodInvokeExpr(type, mapref,
                        "getNodeWithStrongRef", strongref);
            } else {
                if (Main.useWeakRefInterning)
                    invoke = new CodeMethodInvokeExpr(type, mapref, "getNode",
                            weakref);
                else
                    invoke = new CodeMethodInvokeExpr(type, mapref,
                            "getNodeEquivalent", weakref);
            }

            return invoke;
        }

        /**
         * Recursively traverses all the nodes, one-by-one, in the indexing
         * tree. Typically, this method is used to generate code by visiting
         * each node. One can customize the generated code by providing
         * different visitors.
         *
         * @param queryprms
         *            parameters for the query
         * @param index
         *            the index of parameters that this method is handling
         * @param specprms
         *            parameters defined in the specification
         * @param weakrefs
         *            weak references
         * @param usestrongref
         *            true if the strong reference, rather than the weak
         *            reference, should be used for lookup
         * @param entryref
         *            reference to the entry
         * @param visitor
         *            visitor
         * @return generated code
         */
        private CodeStmtCollection traverseNodeInternal(
                RVMParameters queryprms, int index, RVMParameters specprms,
                WeakReferenceVariables weakrefs, boolean usestrongref,
                CodeExpr entryref, IIndexingTreeVisitor visitor) {
            CodeStmtCollection stmts = new CodeStmtCollection();
            if (index == queryprms.size())
                stmts.add(visitor.visitLast(this, entryref));
            else {
                if (index + 1 == queryprms.size())
                    stmts.add(visitor.visitSecondLast(this, entryref));

                RVMParameter head = queryprms.get(index);
                if (this.map == null)
                    throw new IllegalArgumentException();
                if (!this.map.key.equals(head))
                    throw new IllegalArgumentException();
                Entry child = this.map.value;
                CodeVarRefExpr weakref = new CodeVarRefExpr(
                        weakrefs.getWeakRef(head));

                CodeVarRefExpr resultref;
                {
                    CodeExpr mapref = this.generateFieldGetInlinedCode(
                            entryref, Access.Map);
                    CodeMethodInvokeExpr invoke = this.generateGetNodeCode(
                            usestrongref, child, mapref, head, weakref);
                    CodeVariable result = CodeHelper.VariableName
                            .getInternalNode(invoke.getType(), queryprms, index);
                    resultref = new CodeVarRefExpr(result);
                    CodeVarDeclStmt stmt = new CodeVarDeclStmt(result, invoke);
                    stmts.add(stmt);
                }

                RVMParameter nextprm = specprms.get(index + 1);
                stmts.add(visitor.visitPreNode(this, entryref, weakref,
                        resultref, nextprm));

                CodeStmtCollection nested = child.traverseNodeInternal(
                        queryprms, index + 1, specprms, weakrefs, usestrongref,
                        resultref, visitor);
                stmts.add(visitor.visitPostNode(this, resultref, nextprm,
                        nested));
            }
            return stmts;
        }

        /**
         * Recursively traverses all the nodes, one-by-one, in the indexing
         * tree. Typically, this method is used to generate code by visiting
         * each node. One can customize the generated code by providing
         * different visitors.
         *
         * @param queryprms
         *            parameters for the query
         * @param specprms
         *            parameters defined in the specification
         * @param weakrefs
         *            weak references
         * @param usestrongref
         *            true if the strong reference, rather than the weak
         *            reference, should be used for lookup
         * @param entryref
         *            reference to the entry
         * @param visitor
         *            visitor
         * @return generated code
         */
        private CodeStmtCollection traverseNode(RVMParameters queryprms,
                RVMParameters specprms, WeakReferenceVariables weakrefs,
                boolean usestrongref, CodeExpr entryref,
                IIndexingTreeVisitor visitor) {
            assert specprms.subsumes(queryprms, specprms);

            return this.traverseNodeInternal(queryprms, 0, specprms, weakrefs,
                    usestrongref, entryref, visitor);
        }

        static CodeStmtCollection callInsertSecondLastMap(
                StmtCollectionInserter<CodeExpr> inserter, Entry entry,
                CodeExpr entryref) {
            CodeStmtCollection stmts = new CodeStmtCollection();

            CodePair<CodeVarRefExpr> codepair = entry.generateFieldGetCode(
                    entryref, Access.Map, "itmd");
            stmts.add(codepair.getGeneratedCode());
            CodeVarRefExpr mapref = codepair.getLogicalReturn();

            CodeStmtCollection usercode = inserter.insertSecondLastMap(mapref);
            if (usercode != null) {
                stmts.add(usercode);
                return stmts;
            }

            return null;
        }

        static CodeStmtCollection callInsertLastEntryAndField(Access access,
                StmtCollectionInserter<CodeExpr> inserter, Entry entry,
                CodeExpr entryref) {
            CodeStmtCollection stmts = new CodeStmtCollection();

            CodeStmtCollection usercode1 = inserter.insertLastEntry(entry,
                    entryref);
            stmts.add(usercode1);

            if (access != Access.Entry) {
                CodeStmtCollection stmts2 = new CodeStmtCollection();
                CodePair<CodeVarRefExpr> codepair = entry.generateFieldGetCode(
                        entryref, access, "itmd");
                stmts2.add(codepair.getGeneratedCode());
                CodeVarRefExpr fieldref = codepair.getLogicalReturn();

                CodeStmtCollection usercode2 = inserter.insertLastField(entry,
                        fieldref);
                if (usercode2 != null) {
                    stmts2.add(usercode2);
                    stmts.add(stmts2);
                }
            }

            return stmts;
        }

        static CodeStmtCollection generateFieldGetCodeInVisitList(
                Access access, StmtCollectionInserter<CodeExpr> inserter,
                Entry entry, CodeExpr entryref) {
            CodeStmtCollection stmts = new CodeStmtCollection();
            CodePair<CodeVarRefExpr> codepair = entry.generateFieldGetCode(
                    entryref, access, "itmd");
            stmts.add(codepair.getGeneratedCode());
            CodeVarRefExpr fieldref = codepair.getLogicalReturn();

            CodeStmtCollection usercode = inserter.insertLastField(entry,
                    fieldref);
            if (usercode == null)
                return null;
            stmts.add(usercode);
            return stmts;
        }

        CodeStmtCollection generateFindOrCreateCode(RVMParameters queryprms,
                final Access access, RVMParameters specprms,
                WeakReferenceVariables weakrefs,
                final StmtCollectionInserter<CodeExpr> inserter,
                CodeExpr entryref) {
            IIndexingTreeVisitor visitor = new GenerativeIndexingTreeVisitor(
                    specprms) {
                @Override
                public CodeStmtCollection visitSecondLast(Entry entry,
                        CodeExpr entryref) {
                    return Entry.callInsertSecondLastMap(inserter, entry,
                            entryref);
                }

                @Override
                public CodeStmtCollection visitLast(Entry entry,
                        CodeExpr entryref) {
                    return Entry.callInsertLastEntryAndField(access, inserter,
                            entry, entryref);
                }
            };

            return this.traverseNode(queryprms, specprms, weakrefs, false,
                    entryref, visitor);
        }

        CodeStmtCollection generateFindWithStrongRefCode(
                RVMParameters queryprms, final Access access,
                RVMParameters specprms, WeakReferenceVariables weakrefs,
                final StmtCollectionInserter<CodeExpr> inserter,
                CodeExpr entryref, boolean suppressLastNullCheck) {
            IIndexingTreeVisitor visitor = new NonGenerativeIndexingTreeVisitor(
                    suppressLastNullCheck) {
                @Override
                public CodeStmtCollection visitSecondLast(Entry entry,
                        CodeExpr entryref) {
                    return null;
                }

                @Override
                public CodeStmtCollection visitLast(Entry entry,
                        CodeExpr entryref) {
                    return Entry.callInsertLastEntryAndField(access, inserter,
                            entry, entryref);
                }
            };

            return this.traverseNode(queryprms, specprms, weakrefs, true,
                    entryref, visitor);
        }

        CodeStmtCollection generateFindCode(RVMParameters queryprms,
                final Access access, RVMParameters specprms,
                WeakReferenceVariables weakrefs,
                final StmtCollectionInserter<CodeExpr> inserter,
                CodeExpr entryref) {
            IIndexingTreeVisitor visitor = new NonGenerativeIndexingTreeVisitor(
                    false) {
                @Override
                public CodeStmtCollection visitSecondLast(Entry entry,
                        CodeExpr entryref) {
                    // return Entry.callInsertSecondLastMap(inserter, entry,
                    // entryref);
                    return null;
                }

                @Override
                public CodeStmtCollection visitLast(Entry entry,
                        CodeExpr entryref) {
                    return Entry.callInsertLastEntryAndField(access, inserter,
                            entry, entryref);
                }
            };

            return this.traverseNode(queryprms, specprms, weakrefs, false,
                    entryref, visitor);
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();

            List<String> nested = new ArrayList<String>();
            if (this.map != null)
                nested.add(this.map.toString());
            if (this.set != null)
                nested.add("S");
            if (this.leaf != null)
                nested.add("L");

            if (nested.size() > 1)
                s.append("<");
            for (int i = 0; i < nested.size(); ++i) {
                if (i > 0)
                    s.append(',');
                s.append(nested.get(i));
            }
            if (nested.size() > 1)
                s.append(">");

            return s.toString();
        }
    }

    /**
     * This class represents a level in the indexing tree. Since an indexing
     * tree can have multiple levels, an instance of this class can have nested
     * levels. For example, consider a two-level indexing map: the first level
     * being [Collection -> {Map, Set}] and the second level being [Iterator ->
     * Monitor]. Then, two levels will be created as follows:
     *
     * <pre>
     *              Level
     * +-----------------+
     * | key: Collection |
     * | value: --+      |
     * +----------+------+
     *            |                 Entry                 Level
     *            |    +-----------------+     +---------------+
     *            +--> | map:  ----------+---> | key: Iterator |
     *                 | set: MonitorSet |     | value: --+    |
     *                 | leaf: none      |     +----------+----+
     *                 +-----------------+                |               Entry
     *                                                    |    +---------------+
     *                                                    +--> | map: none     |
     *                                                         | set: none     |
     *                                                         | leaf: Monitor |
     *                                                         +---------------+
     * </pre>
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     * @see Entry
     */
    public static class Level {
        private final RVMParameter key;
        private final Entry value;
        private boolean embedGWRT;

        public final RVMParameter getKey() {
            return this.key;
        }

        public final Entry getValue() {
            return this.value;
        }

        private Level(RVMParameter key, Entry value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Creates a level from settings such as whether parameters are fully
         * bound, event type and whether time tracking is needed. This method
         * calls Entry.determine(), which can call this method recursively, to
         * construct all the necessary levels and entries.
         *
         * @param params
         *            parameter list used to query the indexing tree
         * @param set
         *            monitor set type
         * @param leaf
         *            monitor type
         * @param fullbinding
         *            true if parameters are fully bound
         * @param evttype
         *            whether an event always, possibly or never creates a
         *            monitor
         * @param timetrack
         *            whether time tracking is needed
         * @return constructed entry
         */
        public static Level determine(RVMParameters params,
                CodeRVType.MonitorSet set, CodeRVType.Monitor leaf,
                boolean fullbinding, EventKind evttype, boolean timetrack) {
            if (params.size() < 1)
                throw new IllegalArgumentException();

            RVMParameter key = params.get(0);
            Entry value = Entry.determine(params.tail(), set, leaf,
                    fullbinding, evttype, timetrack);
            return new Level(key, value);
        }

        /**
         * Joins two levels. The caller should guarantee that two levels can be
         * joined. This method is used when two indexing trees are combined.
         *
         * @param level1
         *            one level
         * @param level2
         *            the other level
         * @return merged level
         */
        public static Level join(Level level1, Level level2) {
            if (level1 == null)
                return level2;
            if (level2 == null)
                return level1;

            if (!level1.key.equals(level2.key))
                throw new IllegalArgumentException();

            RVMParameter key = level1.key;

            Entry joinedentry = Entry.join(level1.value, level2.value);
            return new Level(key, joinedentry);
        }

        public void embedGWRT() {
            this.embedGWRT = true;
        }

        public CodeRVType getCodeType() {
            return CodeHelper.RuntimeType.getIndexingTree(this.value.map,
                    this.value.set, this.value.leaf, this.embedGWRT);
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            s.append('[');
            s.append(this.key.getName());
            s.append(":");
            s.append(this.value.toString());
            s.append("]");
            return s.toString();
        }
    }
}
