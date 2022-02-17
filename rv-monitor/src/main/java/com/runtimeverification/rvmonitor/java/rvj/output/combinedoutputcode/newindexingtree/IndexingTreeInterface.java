package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmtCollection;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeGenerator;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.itf.WeakReferenceVariables;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.reftree.RefTree;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeImplementation.Access;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeImplementation.Entry;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeImplementation.EventKind;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeImplementation.StmtCollectionInserter;
import com.runtimeverification.rvmonitor.java.rvj.output.monitor.SuffixMonitor;
import com.runtimeverification.rvmonitor.java.rvj.output.monitorset.MonitorSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;

/**
 * This class represents an indexing tree, which may embed GWRT.
 *
 * As its name implies, an instance of this class represents an interface, not
 * an implementation (IndexingTreeImplementation). That is, two different
 * instances of this class can share the same implementation. By default, there
 * is one-to-one mapping between an instance of this class and an instance of
 * IndexingTreeImplementation, but this becomes no longer true when indexing
 * trees are combined. For example, suppose that an indexing tree for
 * [Collection, Iterator] and another tree for [Collection] are combined. Before
 * combining them, there are two different interfaces and they have their own
 * implementations; i.e., itf_[C,I] -> impl_[C,I] itf_[C] -> impl_[C]
 *
 * The combining procedure will merge two implementations and modify the
 * relation as follows: itf_[C,I] -> impl_[C,I] itf_[C] /
 *
 * In the generated code, only one indexing tree will be instantiated because
 * two interfaces share the same implementation. However, two distinct caches
 * will be generated because there are two different ways to access the shared
 * implementation; e.g., there are a cache for [C,I] and another for [C].
 *
 * Besides, the separation of interfaces and implementations makes the code
 * generator simpler, I think.
 *
 * This class is similar to indexingtree.IndexingTree and others, but has been
 * separated in order to generate safer, more readable and more efficient code.
 * I also tried to avoid redundant code that exists in the old classes.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 * @see IndexingTreeImplementation
 */
public class IndexingTreeInterface implements ICodeGenerator {
    private final String name;
    private final RVMParameters specParams;
    private final RVMParameters queryParams;
    private final RVMParameters contentParams;
    private IndexingTreeImplementation impl;
    private IndexingCacheNew cache;

    public String getName() {
        return this.name;
    }

    public RVMParameters getSpecParams() {
        return this.specParams;
    }

    public RVMParameters getQueryParams() {
        return this.queryParams;
    }

    public String getPrettyName() {
        return IndexingTreeNameMangler.prettyName(this.queryParams,
                this.contentParams);
    }

    public IndexingTreeImplementation getImplementation() {
        return this.impl;
    }

    public boolean isMasterTree() {
        return this == this.impl.getMasterInterface();
    }

    public IndexingCacheNew getCache() {
        return this.cache;
    }

    public boolean isFullyFledgedTree() {
        // If an indexing tree does not take any parameter, then the code
        // generator
        // generates a zero-level object (such as a set, monitor or tuple) for
        // the
        // indexing tree, instead of an actual indexing tree.
        // It turned out that it's more consistent and easier to treat such
        // trees as
        // indexing trees in most cases. However, if one needs to make
        // distinction,
        // this method can tell the difference.
        RVMParameters params = this.getQueryParams();
        return params.size() > 0;
    }

    public IndexingTreeInterface(String outputName, RVMParameters specParams,
            RVMParameters queryParams, RVMParameters contentParams) {
        this.specParams = specParams;
        this.queryParams = queryParams;
        this.contentParams = contentParams;

        this.name = IndexingTreeNameMangler.fieldName(outputName, queryParams,
                contentParams);
    }

    public void initializeImplementation(String outputName, MonitorSet set,
            SuffixMonitor monitor, EventKind evttype, boolean timetrack) {
        IndexingTreeImplementation impl = new IndexingTreeImplementation(this,
                outputName, specParams, queryParams, contentParams, set,
                monitor, evttype, timetrack);
        this.switchImplementation(impl);
    }

    void switchImplementation(IndexingTreeImplementation newimpl) {
        this.impl = newimpl;
        this.cache = IndexingCacheNew.fromTree(this.name, this);
    }

    /**
     * Checks whether the passed indexing tree is part of this indexing tree.
     * This method is used to check whether two indexing trees can be combined.
     * The condition is: 1. two indexing trees belong to the same specification
     * because combining trees among different specifications has not been
     * implemented and can be tricky. 2. two trees share the same prefixes;
     * e.g., [C,I] and [C] can be merged, but [C,I] and [I] cannot be.
     *
     * @param that
     *            the other indexing tree that can be possibly part of this tree
     * @return true if the passed tree can be part of this tree
     */
    public boolean subsumes(IndexingTreeInterface that) {
        if (this.specParams != that.specParams) {
            // Combining indexing trees among specifications is not supported.
            // According to the original paper, this does not improve
            // performance.
            throw new IllegalArgumentException();
        }

        return this.queryParams.subsumes(that.queryParams, this.specParams);
    }

    /**
     * Embeds the global weak reference table (GWRT) into this indexing tree.
     *
     * @param refTree
     *            unused
     */
    public void embedGWRT(RefTree refTree) {
        if (!this.isMasterTree())
            throw new IllegalArgumentException();

        this.impl.embedGWRT(refTree, this.specParams, this.queryParams);
    }

    public CodeStmtCollection generateFindOrCreateEntryCode(
            WeakReferenceVariables weakrefs,
            StmtCollectionInserter<CodeExpr> inserter) {
        return this.impl.generateFindOrCreateEntryCode(this.queryParams,
                this.specParams, weakrefs, inserter);
    }

    public CodeStmtCollection generateFindEntryWithStrongRefCode(
            WeakReferenceVariables weakrefs,
            StmtCollectionInserter<CodeExpr> inserter,
            boolean suppressLastNullCheck) {
        return this.impl.generateFindEntryWithStrongRefCode(this.queryParams,
                this.specParams, weakrefs, inserter, suppressLastNullCheck);
    }

    public CodeStmtCollection generateFindOrCreateCode(Access access,
            WeakReferenceVariables weakrefs,
            StmtCollectionInserter<CodeExpr> inserter) {
        return this.impl.generateFindOrCreateCode(this.queryParams, access,
                this.specParams, weakrefs, inserter);
    }

    public CodeStmtCollection generateFindCode(CodeExpr rootref, Access access,
            WeakReferenceVariables weakrefs,
            StmtCollectionInserter<CodeExpr> inserter) {
        return this.impl.generateFindCode(rootref, this.queryParams, access,
                this.specParams, weakrefs, inserter);
    }

    public CodeStmtCollection generateFindCode(Access access,
            WeakReferenceVariables weakrefs,
            StmtCollectionInserter<CodeExpr> inserter) {
        return this.impl.generateFindCode(this.queryParams, access,
                this.specParams, weakrefs, inserter);
    }

    public CodeStmtCollection generateInsertMonitorCode(
            WeakReferenceVariables weakrefs, final CodeVarRefExpr monitorref) {
        return this.impl.generateInsertMonitorCode(this.queryParams,
                this.specParams, weakrefs, monitorref);
    }

    /**
     * Retrieves the entry that results from accessing this indexing tree using
     * the provided parameters.
     *
     * @param params
     *            parameters for accessing this indexing tree
     * @return resulting entry
     */
    public Entry lookupEntry(RVMParameters params) {
        return this.impl.lookupEntry(params);
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        if (this.cache != null)
            this.cache.getCode(fmt);
    }

    @Override
    public String toString() {
        String q = this.queryParams.parameterString();
        String c = this.contentParams == null ? null : this.contentParams
                .parameterString();
        if (c == null)
            return q;
        return q + " -> " + c;
    }
}

class IndexingTreeNameMangler {
    public static String fieldName(String outputName,
            RVMParameters queryParams, RVMParameters contentParams) {
        String name = outputName + "_";
        if (contentParams == null)
            name += queryParams.parameterStringUnderscore();
        else
            name += queryParams.parameterStringUnderscore() + "__To__"
                    + contentParams.parameterStringUnderscore();
        name += "_Map";
        return name;
    }

    public static String prettyName(RVMParameters queryParams,
            RVMParameters contentParams) {
        String from = queryParams.parameterStringUnderscore().replace('_', ',');
        String to = null;
        if (contentParams != null)
            to = contentParams.parameterStringUnderscore().replace('_', ',');

        if (to == null)
            return "<" + from + ">";
        return "<{" + from + "}:{" + to + "}>";
    }
}
