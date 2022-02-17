package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.itf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.runtimeverification.rvmonitor.java.rt.observable.IInternalBehaviorObserver.LookupPurpose;
import com.runtimeverification.rvmonitor.java.rvj.Main;
import com.runtimeverification.rvmonitor.java.rvj.output.CodeGenerationOption;
import com.runtimeverification.rvmonitor.java.rvj.output.NotImplementedException;
import com.runtimeverification.rvmonitor.java.rvj.output.RVMVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeAssignStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeBinOpExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeBlockStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeCastExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeConditionStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExprStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeFieldRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeForStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeInstanceOfExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeLazyStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeLiteralExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMemberField;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMethodInvokeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeNegExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeNewExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodePrePostfixExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmtCollection;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarDeclStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.analysis.CodeSimplifier;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeFormatters;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeHelper;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodePair;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeFormatter;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.ICodeGenerator;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeRVType;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.CombinedOutput;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.GlobalLock;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.InternalBehaviorObservableCodeGenerator;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.advice.AdviceBody;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.indexingtree.reftree.RefTree;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingCacheNew;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeImplementation.Access;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeImplementation.Entry;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeImplementation.StmtCollectionInserter;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeInterface;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeQueryResult;
import com.runtimeverification.rvmonitor.java.rvj.output.monitor.MonitorFeatures;
import com.runtimeverification.rvmonitor.java.rvj.output.monitor.MonitorInfo;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorParameterPair;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;

/**
 * This class represents the body of each event method, a static Java method
 * that a client can invoke to inform RV-monitor of an event. The code that this
 * class generates is equivalent to main() in Algorithm D(X). For optimization
 * purposes, however, a few things are newly introduced, and complicated loops
 * in the algorithm were replaced by simpler ones.
 *
 * This class originates from GeneralAdviceBody. While fixing a bug and
 * improving performance, it seemed necessary to significantly modify
 * IndexingTree and GeneralAdviceBody. Except the bug, the behavior of this
 * class should be equivalent to that of the originating class.
 *
 * That being said, this class somewhat changes the behavior for the purposes of
 * generating 1. type-safe code (i.e., do not generate casting expressions,
 * unless it must exist); 2. precisely-scoped code (i.e., the life span of a
 * variable is precisely set); 3. offensive code (i.e., do not null-check unless
 * it is required); and 4. non-redundant code (i.e., do not perform expensive
 * operations redundantly).
 *
 * When writing this class, the performance during code generation was not my
 * concern at all; only the performance of the generated code and the
 * readability of this class were the only concern I had. I'd be happy to do 10
 * times slower operation if that can improve readability or runtime
 * performance.
 *
 * Not to interfere with the old interfaces and classes, this new class was
 * created. This class and indexing trees will eventually replace the old
 * counterparts.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class EventMethodBody extends AdviceBody implements ICodeGenerator {
    private final Map<RVMonitorParameterPair, IndexingTreeInterface> indexingTreesForCopy;
    private final List<RVMonitorParameterPair> paramPairsForCopy;
    private final GlobalLock enforceLock;

    private CodeStmtCollection generatedCode;
    private final Strategy strategy;

    static class Strategy {
        private final boolean needsWeakReferenceLookup;
        private final boolean shouldCreateIndexingTreeIntemediateNodes;
        private final boolean needsTimeTracking;
        private final boolean mayCreateMonitors;
        private final boolean mayCopyFromOtherMonitors;
        private final boolean needsNullCheckForTransition;

        Strategy(boolean isCreationEvent, boolean isGeneral,
                boolean fullybound, RVMParameters eventParams,
                HashSet<RVMParameter> disableParams) {
            // In general, weak references should be retrieved according to the
            // strong references
            // carried by the event. In the special case, however, such lookup
            // is unnecessary
            // considering that weak references are needed only for inserting
            // nodes into indexing trees.
            this.needsWeakReferenceLookup = isGeneral || isCreationEvent;

            // In general, all the intermediate nodes should be created while
            // looking up
            // the entry corresponding to the parameters that the event carries.
            // However,
            // for the special case, only creation events need such behavior,
            // because
            // non-creation events would not create any monitors and, therefore,
            // the
            // intermediate nodes are unnecessary.
            this.shouldCreateIndexingTreeIntemediateNodes = isGeneral
                    || isCreationEvent;

            // This field tells whether or not keeping track of the 'disable'
            // field is needed.
            {
                // The following is similar to the 'doDisable' field in JavaMOP
                // 3.0,
                // but it keeps the 'disable' field regardless of this field.
                // So, I'm
                // not using this logic.
                /*
                 * boolean disable = false; for (RVMParameter prm : eventParams)
                 * { if (disableParams.contains(prm)) { disable = true; break; }
                 * } this.needsTimeTracking = disable;
                 */
                this.needsTimeTracking = isGeneral;
            }

            // This field tells whether anything related to monitor creation is
            // ever needed.
            // If this field is false, the routine for lines 2--5 in 'main' can
            // be skipped.
            this.mayCreateMonitors = isGeneral || isCreationEvent;

            // This field tells whether this event may need to copy-construct a
            // monitor,
            // which is defined as the 'defineTo' function in D(X). If this
            // field is false,
            // invoking 'createNewMonitorState' on line 2 in 'main' is
            // unnecessary.
            this.mayCopyFromOtherMonitors = isGeneral;

            // This field tells whether null-check is necessary for state
            // transitions, described
            // on lines 8--9 in 'main'.
            {
                boolean needed = false;
                if (!isCreationEvent) {
                    // If the event is not a creation one, then the matched
                    // entry can be null.
                    // However, if the matched entry is not a set, it would
                    // never be null because
                    // the generated code always creates a set.
                    if (isGeneral) {
                        if (fullybound)
                            needed = true;
                    } else
                        needed = true;
                }
                this.needsNullCheckForTransition = needed;
            }

            this.validate();
        }

        private void validate() {
            // The former should be no stricter than the latter. If this is not
            // the case,
            // the latter cannot be reached, which is wrong.
            if (!this.mayCreateMonitors && this.mayCopyFromOtherMonitors)
                throw new NotImplementedException();

            // At this moment, it is believed that the following two fields are
            // related.
            // Also, copying from other monitors seems impossible without time
            // tracking
            // because of time checking on line 2 in 'defineTo'.
            if (this.needsTimeTracking != this.mayCopyFromOtherMonitors)
                throw new NotImplementedException();
        }
    }

    private CodeType getMonitorType() {
        return new CodeType(this.monitorClass.getOutermostName().getVarName());
    }

    /**
     * Returns the monitor features, which tell what features of the monitor
     * implement and expect.
     *
     * @return a MonitorFeatures object, owned by the monitor class
     * @see com.runtimeverification.rvmonitor.java.rvj.output.monitor.MonitorFeatures
     */
    private MonitorFeatures getMonitorFeatures() {
        return this.monitorClass.getFeatures();
    }

    private MonitorInfo getMonitorInfo() {
        return this.monitorClass.getMonitorInfo();
    }

    @SuppressWarnings("unused")
    private CodeType getMonitorSetType() {
        return new CodeType(this.monitorSet.getName().getVarName());
    }

    private Timestamp getTimestamp() {
        RVMVariable var = this.output.timestampManager.getTimestamp(rvmSpec);
        return Timestamp.create(var.getVarName());
    }

    private InternalBehaviorObservableCodeGenerator getBehaviorObserver() {
        return this.output.getInternalBehaviorObservableGenerator();
    }

    /**
     * @return the indexing tree that exactly matches with the parameters that
     *         the event carries.
     */
    private IndexingTreeInterface getMatchedIndexingTree() {
        return this.indexingTrees.get(this.eventParams);
    }

    /**
     * @return true if this event carries all the necessary parameters and,
     *         consequently, affects exactly one monitor instance rather than a
     *         set of monitors.
     */
    private boolean isFullyBound() {
        // The following is the definition of 'isFullParam' in AdviceBody,
        // which seems little bit suspicious.
        // this.isFullParam = eventParams.equals(rvmSpec.getParameters());

        // Instead, I'm using the following.
        return this.event.getParameters()
                .contains(this.rvmSpec.getParameters());
    }

    private IndexingCacheNew getIndexingTreeCache() {
        return this.getMatchedIndexingTree().getCache();
    }

    public EventMethodBody(RVMonitorSpec rvmSpec, EventDefinition event,
            CombinedOutput combinedOutput) {
        super(rvmSpec, event, combinedOutput);

        this.indexingTreesForCopy = indexingDecl.getIndexingTreesForCopy();
        this.paramPairsForCopy = indexingDecl.getCopyParamForEvent(event);

        {
            // Only enforcing case requires a global lock. Here we do not create
            // any lock in
            // other cases, so that any wrong assumption results in a visible
            // failure.
            GlobalLock lock = null;
            if (rvmSpec.isEnforce())
                lock = new GlobalLock(new RVMVariable(combinedOutput.getName()
                        + "." + combinedOutput.lockManager.getLock().getName()));
            this.enforceLock = lock;
        }

        {
            HashSet<RVMParameter> disableprms = this.output.setOfParametersForDisable
                    .get(rvmSpec);
            this.strategy = new Strategy(this.event.isStartEvent(),
                    this.isGeneral, this.isFullyBound(), this.eventParams,
                    disableprms);
        }

        MonitorFeatures features = this.getMonitorFeatures();
        features.setTimeTracking(this.strategy.needsTimeTracking);
        features.setDisableHolder(this.strategy.needsTimeTracking);
    }

    /**
     * Generates code for looking up the indexing tree cache. If the cache hits,
     * the corresponding node can be retrieved without accessing the indexing
     * tree, which can be relatively expensive.
     *
     * @param weakrefs
     *            weak references, each of which corresponds to one parameter in
     *            the event
     * @param destref
     *            reference to the variable for holding the cached value
     * @param cachehitref
     *            reference to the variable for holding the result of the cache
     *            lookup
     * @return generated code
     */
    private CodeConditionStmt generateCacheLookup(
            WeakReferenceVariables weakrefs, CodeVarRefExpr destref,
            CodeVarRefExpr cachehitref) {
        IndexingCacheNew cache = this.getIndexingTreeCache();
        if (cache == null)
            return null;

        CodeExpr ifmatch = cache.getKeyCompareCode();
        CodeStmtCollection body = cache
                .getCacheRetrievalCode(weakrefs, destref);
        body.add(this.getBehaviorObserver().generateIndexingTreeCacheHitCode(
                cache, destref));
        body.add(new CodeAssignStmt(cachehitref, CodeLiteralExpr.bool(true)));
        return new CodeConditionStmt(ifmatch, body);
    }

    private RefTree getGWRT(RVMParameter param) {
        return this.output.indexingTreeManager.refTrees.get(param.getType()
                .toString());
    }

    /**
     * Generates code for creating or retrieving weak references.
     *
     * @param weakrefs
     *            weak references, each of which corresponds to one parameter in
     *            the event
     * @param conditional
     *            true if the generated code should create a weak reference only
     *            if it is null
     * @return generated code
     */
    private CodeStmtCollection generateWeakReferenceLookup(
            WeakReferenceVariables weakrefs, boolean conditional) {
        CodeStmtCollection stmts = new CodeStmtCollection();

        for (RVMParameter param : this.eventParams) {
            CodeVarRefExpr weakref = new CodeVarRefExpr(
                    weakrefs.getWeakRef(param));

            CodeExpr condition = null;
            if (conditional)
                condition = CodeBinOpExpr.isNull(weakref);

            // The following was the original condition for preventing creation
            // of a weak reference, which
            // cannot be directly applied; e.g., noncreative :=
            // (!isGeneral && !event.isStartEvent()) ||
            // (isGeneral && !event.isStartEvent() && paramPairsForCopy.size()
            // == 0 && !doDisable)

            boolean create = true;
            if (CodeGenerationOption.isCacheKeyWeakReference())
                throw new NotImplementedException();

            CodeExpr initval = null;
            if (Main.useWeakRefInterning) {
                String rhs;
                RefTree gwrt = this.getGWRT(param);
                if (create)
                    rhs = gwrt.get(param);
                else
                    rhs = gwrt.getRefNonCreative(param);
                initval = CodeExpr.fromLegacy(weakref.getType(), rhs);
            } else {
                // When the weak reference interning is disabled, a weak
                // reference needs to be created.
                CodeType weakreftype = CodeHelper.RuntimeType
                        .getWeakReference();
                CodeVarRefExpr strongref = new CodeVarRefExpr(new CodeVariable(
                        CodeType.object(), param.getName()));
                initval = new CodeNewExpr(weakreftype, strongref);
            }

            CodeAssignStmt assign = new CodeAssignStmt(weakref, initval);
            if (condition == null)
                stmts.add(assign);
            else
                stmts.add(new CodeConditionStmt(condition, assign));
        }

        return stmts;
    }

    /**
     * Generates code for updating the indexing tree cache. The generated code
     * should be reached, at runtime, only if cache misses.
     *
     * @param matched
     *            information for accessing indexing trees, weak references and
     *            so forth
     * @param cachehitref
     *            reference to the variable for holding the result of the cache
     *            lookup
     * @return generated code
     */
    private CodeStmtCollection generateCacheUpdate(CodeVarRefExpr matched,
            CodeVarRefExpr cachehitref) {
        IndexingCacheNew cache = this.getIndexingTreeCache();
        if (cache == null)
            return null;

        CodeStmtCollection stmts = new CodeStmtCollection();
        CodeStmt cacheupdate = new CodeConditionStmt(CodeBinOpExpr.identical(
                cachehitref, CodeLiteralExpr.bool(false)), stmts);

        stmts.add(cache.getCacheUpdateCode(matched));
        stmts.add(this.getBehaviorObserver()
                .generateIndexingTreeCacheUpdatedCode(cache, matched));

        return new CodeStmtCollection(cacheupdate);
    }

    /**
     * Generates code for looking up the indexing tree. The generated code
     * should be reached, at runtime, only if there is no indexing tree cache or
     * the cache does not hit.
     *
     * @param matched
     *            information for accessing indexing trees, weak references and
     *            so forth
     * @return generated code
     */
    private CodeStmtCollection generateIndexingTreeLookup(
            final IndexingTreeQueryResult matched) {
        CodeStmtCollection stmts = new CodeStmtCollection();

        {
            IndexingCacheNew cache = this.getIndexingTreeCache();
            if (cache != null)
                stmts.add(this.getBehaviorObserver()
                        .generateIndexingTreeCacheMissedCode(cache));
        }

        if (this.strategy.needsWeakReferenceLookup)
            stmts.add(this.generateWeakReferenceLookup(matched.getWeakRefs(),
                    false));

        {
            StmtCollectionInserter<CodeExpr> inserter = new StmtCollectionInserter<CodeExpr>() {
                @Override
                public CodeStmtCollection insertSecondLastMap(CodeExpr mapref) {
                    if (matched.getLastMapRef() == null)
                        return null;
                    CodeStmt assign = new CodeAssignStmt(
                            matched.getLastMapRef(), mapref);
                    return new CodeStmtCollection(assign);
                }

                @Override
                public CodeStmtCollection insertLastEntry(Entry entry,
                        CodeExpr entryref) {
                    CodeStmt assign = new CodeAssignStmt(matched.getEntryRef(),
                            entryref);
                    return new CodeStmtCollection(assign);
                }
            };
            IndexingTreeInterface indexingtree = this.getMatchedIndexingTree();
            CodeStmtCollection code;
            if (this.strategy.shouldCreateIndexingTreeIntemediateNodes)
                code = indexingtree.generateFindOrCreateEntryCode(
                        matched.getWeakRefs(), inserter);
            else
                code = indexingtree.generateFindEntryWithStrongRefCode(
                        matched.getWeakRefs(), inserter, true);
            stmts.add(code);
            stmts.add(this.getBehaviorObserver()
                    .generateIndexingTreeLookupCode(indexingtree,
                            LookupPurpose.TransitionedMonitor,
                            matched.getWeakRefs(),
                            !this.strategy.needsWeakReferenceLookup,
                            matched.getEntryRef()));
        }

        return stmts;
    }

    private IndexingTreeInterface findIndexingTreeForCopy(
            RVMonitorParameterPair needle) {
        for (RVMonitorParameterPair pair : indexingTreesForCopy.keySet()) {
            if (needle.equals(pair))
                return this.indexingTreesForCopy.get(pair);
        }
        return null;
    }

    private IndexingTreeInterface findIndexingTree(RVMonitorParameterPair needle) {
        for (RVMParameters params : this.indexingTrees.keySet()) {
            if (params.equals(needle.getParam2()))
                return this.indexingTrees.get(params);
        }
        return null;
    }

    private IndexingTreeInterface findIndexingTree(RVMParameters needle) {
        for (RVMParameters params : this.indexingTrees.keySet()) {
            if (params.equals(needle))
                return this.indexingTrees.get(params);
        }
        return null;
    }

    /**
     * Generates code that corresponds to either defineTo:7 or defineNew:5 in
     * D(X). The implementation is slightly different from the algorithm
     * description. Unlike the description, the implementation does not have the
     * map U. Instead, the implementation keeps multiple indexing trees, one for
     * a distinct parameter list, and one can achieve accessing U by iterating
     * over multiple indexing trees. the implementation enables access to more
     * informative monitors by accessing different indexing trees. For this
     * reason, this implementation simply inserts the newly created monitor to
     * all the indexing trees corresponding to parameters less informative than
     * monitor's.
     *
     * @param combined
     *            indexing tree information for the parameter list of the
     *            created monitor
     * @param monitorref
     *            the monitor to be inserted into
     * @param isDefineTo
     *            true if invoked for handling 'defineTo', rather than
     *            'defineNew'
     * @return generated code
     */
    private CodeStmtCollection generateInsertMonitorToAllCompatibleTreesCode(
            IndexingTreeQueryResult combined, CodeVarRefExpr monitorref,
            boolean isDefineTo) {
        CodeStmtCollection stmts = new CodeStmtCollection();

        String ctxmsg = "D(X) " + (isDefineTo ? "defineTo:7" : "defineNew:5");

        RVMParameters targetprms = combined.getParams();
        WeakReferenceVariables weakrefs = combined.getWeakRefs();

        for (RVMParameters param : this.indexingTrees.keySet()) {
            if (targetprms.contains(param) && !targetprms.equals(param)) {
                stmts.comment(ctxmsg + " for <" + param.parameterString() + ">");
                IndexingTreeInterface srctree = this.indexingTrees.get(param);
                CodeStmtCollection insert = srctree.generateInsertMonitorCode(
                        weakrefs, monitorref);
                stmts.add(insert);
                stmts.add(this.getBehaviorObserver()
                        .generateIndexingTreeNodeInsertedCode(srctree,
                                weakrefs, monitorref));
            }
        }

        for (RVMonitorParameterPair pair : this.indexingTreesForCopy.keySet()) {
            if (targetprms.equals(pair.getParam2())) {
                stmts.comment(ctxmsg + " for <"
                        + pair.getParam1().parameterString() + "-"
                        + pair.getParam2().parameterString() + ">");
                IndexingTreeInterface srctree = this.indexingTreesForCopy
                        .get(pair);
                CodeStmtCollection insert = srctree.generateInsertMonitorCode(
                        weakrefs, monitorref);
                stmts.add(insert);
                stmts.add(this.getBehaviorObserver()
                        .generateIndexingTreeNodeInsertedCode(srctree,
                                weakrefs, monitorref));
            }
        }

        return stmts;
    }

    /**
     * Generates the code for comparing the timestamp.
     *
     * @param source
     *            theta' in 'defineTo' in Algorithm D(X)
     * @param candidate
     *            theta'' in 'definTo' in Algorithm D(X)
     * @return generated code
     */
    private CodeExpr generateTimeCheckCode(CodeVarRefExpr source,
            CodeExpr candidate) {
        CodeExpr t1 = new CodeMethodInvokeExpr(CodeType.integer(), source,
                "getTau");
        CodeExpr t2 = new CodeMethodInvokeExpr(CodeType.integer(), candidate,
                "getTau");
        CodeExpr disable2 = new CodeMethodInvokeExpr(CodeType.integer(),
                candidate, "getDisable");

        CodeExpr cond1 = CodeBinOpExpr.greater(disable2, t1);

        CodeExpr cond2 = CodeBinOpExpr.less(t2, t1);
        CodeExpr cond2extra = CodeBinOpExpr.greater(t2,
                CodeLiteralExpr.integer(0));
        cond2 = CodeBinOpExpr.logicalAnd(cond2extra, cond2);

        CodeExpr cond = CodeBinOpExpr.logicalOr(cond1, cond2);
        return cond;
    }

    /**
     * This class is used to lazily generate code for setting the value of the
     * weak references in a monitor instance. That code should be lazily
     * generated because what parameters need to be kept in a monitor instance
     * is determined during the first stage of code generation.
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     */
    static class MonitorWeakRefSetLazyCode extends CodeLazyStmt {
        private final MonitorFeatures features;
        private final RVMParameters sourceprms;
        private final RVMParameters targetprms;
        private final WeakReferenceVariables weakrefs;
        private final CodeVarRefExpr monitorref;

        public MonitorWeakRefSetLazyCode(MonitorFeatures features,
                RVMParameters sourceprms, RVMParameters targetprms,
                WeakReferenceVariables weakrefs, CodeVarRefExpr monitorref) {
            this.features = features;
            this.sourceprms = sourceprms;
            this.targetprms = targetprms;
            this.weakrefs = weakrefs;
            this.monitorref = monitorref;
        }

        @Override
        protected CodeStmtCollection evaluate() {
            // The code can be generated only after the first pass has
            // completed.
            if (!this.features.isStabilized())
                return null;

            CodeStmtCollection stmts = new CodeStmtCollection();

            if (this.features.isNonFinalWeakRefsInMonitorNeeded()
                    || this.features.isFinalWeakRefsInMonitorNeeded()) {
                for (RVMParameter prm : targetprms.setDiff(sourceprms)) {
                    CodeExpr rhs = new CodeVarRefExpr(weakrefs.getWeakRef(prm));
                    CodeExpr lhs = new CodeFieldRefExpr(monitorref,
                            CodeHelper.VariableName.getWeakRefInMonitor(prm,
                                    rhs.getType()));
                    CodeStmt assign = new CodeAssignStmt(lhs, rhs);
                    stmts.add(assign);
                }
            }

            return stmts;
        }
    }

    /**
     * Generates code that corresponds to createNewMonitorStates:6 in D(X).
     *
     * @param sourceprms
     *            theta'' in createNewMonitorStates, and theta' in defineTo
     * @param targetprms
     *            theta'' cup theta in createNewMonitorStates, and theta in
     *            defineTo
     * @param dest
     *            indexing tree and other information on targetprms
     * @param sourcemonref
     *            reference to the source monitor, from which the new monitor is
     *            copied
     * @param transition
     *            indexing tree and other information on theta in
     *            createNewMonitorStates; i.e., the parameters carried by the
     *            event
     * @param isFromMonitor
     *            true if this method is being invoked for copying from a
     *            monitor, rather than a set
     * @return generated code
     */
    private CodeStmtCollection generateCopyStateInternalCode(
            RVMParameters sourceprms, RVMParameters targetprms,
            final IndexingTreeQueryResult dest,
            final CodeVarRefExpr sourcemonref,
            IndexingTreeQueryResult transition, boolean isFromMonitor) {
        CodeStmtCollection stmts = new CodeStmtCollection();

        CodeVariable definable;
        {
            definable = new CodeVariable(CodeType.bool(), "definable");
            CodeVarDeclStmt decl = new CodeVarDeclStmt(definable,
                    CodeLiteralExpr.bool(true));
            stmts.add(decl);
        }
        final CodeVarRefExpr definableref = new CodeVarRefExpr(definable);

        // param := theta'' in defineTo in D(X)
        for (RVMParameters param : this.indexingTrees.keySet()) {
            if (targetprms.contains(param) && !sourceprms.contains(param)) {
                stmts.comment("D(X) defineTo:1--5 for <"
                        + param.parameterString() + ">");
                CodeExpr ifcond = definableref;
                CodeStmtCollection ifbody = new CodeStmtCollection();
                {
                    final IndexingTreeInterface srctree = this.indexingTrees
                            .get(param);

                    StmtCollectionInserter<CodeExpr> inserter = new StmtCollectionInserter<CodeExpr>() {
                        @Override
                        public CodeStmtCollection insertLastField(Entry entry,
                                CodeExpr leafref) {
                            CodeStmtCollection stmts = new CodeStmtCollection();
                            CodeStmtCollection guarded = stmts;

                            if (entry.getCodeType() instanceof CodeRVType.Tuple) {
                                // If this is of tuple type, the corresponding
                                // field should be additionally checked.
                                CodeExpr ifextracond = CodeBinOpExpr
                                        .isNotNull(leafref);
                                guarded = new CodeStmtCollection();
                                stmts.add(new CodeConditionStmt(ifextracond,
                                        guarded));
                            }

                            CodeStmt check = new CodeConditionStmt(
                                    generateTimeCheckCode(sourcemonref, leafref),
                                    new CodeAssignStmt(definableref,
                                            CodeLiteralExpr.bool(false)));
                            guarded.add(check);
                            guarded.add(getBehaviorObserver()
                                    .generateTimeCheckedCode(srctree,
                                            dest.getWeakRefs(), sourcemonref,
                                            leafref, definableref));

                            return stmts;
                        }
                    };
                    ifbody.add(srctree.generateFindCode(Access.Leaf,
                            dest.getWeakRefs(), inserter));
                }
                stmts.add(new CodeConditionStmt(ifcond, ifbody));
            }
        }

        {
            CodeExpr ifcond = definableref;
            CodeStmtCollection ifbody = new CodeStmtCollection();
            stmts.add(new CodeConditionStmt(ifcond, ifbody));

            CodeVarRefExpr monitorref;
            {
                ifbody.comment("D(X) defineTo:6");
                CodeVarDeclStmt decl = new CodeVarDeclStmt(new CodeVariable(
                        this.getMonitorType(), "created"), new CodeCastExpr(
                                this.getMonitorType(), new CodeMethodInvokeExpr(
                                        CodeType.object(), sourcemonref, "clone")));
                ifbody.add(decl);
                monitorref = new CodeVarRefExpr(decl.getVariable());

                MonitorWeakRefSetLazyCode weakrefset = new MonitorWeakRefSetLazyCode(
                        this.getMonitorFeatures(), sourceprms, targetprms,
                        dest.getWeakRefs(), monitorref);
                ifbody.add(weakrefset);
            }
            ifbody.add(this.getBehaviorObserver().generateMonitorClonedCode(
                    sourcemonref, monitorref));

            {
                MonitorInfo moninfo = this.getMonitorInfo();
                if (moninfo != null) {
                    String legacycode = moninfo.expand(monitorref.getVariable()
                            .toLegacy(), this.monitorClass, targetprms);
                    ifbody.add(CodeStmtCollection.fromLegacy(legacycode));
                }
            }

            ifbody.add(this.generateInsertMonitorCode(dest, transition,
                    monitorref, true, isFromMonitor));
        }

        return stmts;
    }

    /**
     * Generates code that corresponds to createNewMonitorStates:6 in D(X) when
     * one or multiple new monitors should be created from a set of existing
     * monitors.
     *
     * @param sourceprms
     *            theta'' in createNewMonitorStates, and theta' in defineTo
     * @param targetprms
     *            theta'' cup theta in createNewMonitorStates, and theta in
     *            defineTo
     * @param transition
     *            information on theta
     * @param source
     *            information on the indexing tree for sourceprms
     * @return generated code
     */
    private CodeStmtCollection generateCopyStateFromListCode(
            RVMParameters sourceprms, RVMParameters targetprms,
            IndexingTreeQueryResult transition, IndexingTreeQueryResult source) {
        CodeStmtCollection stmts = new CodeStmtCollection();

        IndexingTreeInterface targettree = this.findIndexingTree(targetprms);

        WeakReferenceVariables borrowedweakrefs;
        {
            RVMParameters borrowedprms = new RVMParameters();
            for (RVMParameter prm : sourceprms) {
                if (!this.eventParams.contains(prm)) {
                    borrowedprms.add(prm);
                    this.getMonitorFeatures().addRememberedParameters(prm);
                }
            }
            borrowedweakrefs = new WeakReferenceVariables(
                    this.output.indexingTreeManager, borrowedprms);
        }

        {
            CodeVarDeclStmt declnumalive = new CodeVarDeclStmt(
                    new CodeVariable(CodeType.integer(), "numalive"),
                    CodeLiteralExpr.integer(0));
            CodeVarRefExpr numaliveref = new CodeVarRefExpr(
                    declnumalive.getVariable());
            stmts.add(declnumalive);

            CodeVarDeclStmt declsize = new CodeVarDeclStmt(new CodeVariable(
                    CodeType.integer(), "setlen"), new CodeMethodInvokeExpr(
                            CodeType.integer(), source.getSetRef(), "getSize"));
            stmts.add(declsize);
            CodeVarRefExpr sizeref = new CodeVarRefExpr(declsize.getVariable());
            CodeVarDeclStmt decli = new CodeVarDeclStmt(new CodeVariable(
                    CodeType.integer(), "ielem"), CodeLiteralExpr.integer(0));
            CodeVarRefExpr iref = new CodeVarRefExpr(decli.getVariable());
            CodeExpr cond = CodeBinOpExpr.less(iref, sizeref);
            CodeStmt incri = new CodeExprStmt(CodePrePostfixExpr.prefix(iref,
                    true));
            CodeStmtCollection loopbody = new CodeStmtCollection();
            CodeForStmt loop = new CodeForStmt(decli, cond, incri, loopbody);
            stmts.add(loop);

            {
                CodeVarDeclStmt declsrcmon = new CodeVarDeclStmt(
                        new CodeVariable(this.getMonitorType(),
                                "sourceMonitor", "theta''"),
                                new CodeMethodInvokeExpr(this.getMonitorType(), source
                                        .getSetRef(), "get", iref));
                loopbody.add(declsrcmon);
                CodeVarRefExpr srcmonref = new CodeVarRefExpr(
                        declsrcmon.getVariable());

                CodeExpr ifalive = new CodeNegExpr(new CodeMethodInvokeExpr(
                        CodeType.bool(), srcmonref, "isTerminated"));
                for (Map.Entry<RVMParameter, CodeVariable> pair : borrowedweakrefs
                        .getMapping().entrySet()) {
                    CodeType wrtype = pair.getValue().getType();
                    RVMVariable wrfieldname = this.monitorClass
                            .getRVMonitorRef(pair.getKey());
                    CodeMemberField wrfield = new CodeMemberField(
                            wrfieldname.getVarName(), false, false, false,
                            wrtype);
                    CodeFieldRefExpr wrfieldref = new CodeFieldRefExpr(
                            srcmonref, wrfield);
                    CodeMethodInvokeExpr getref = new CodeMethodInvokeExpr(
                            CodeType.object(), wrfieldref, "get");
                    CodeExpr notnull = CodeBinOpExpr.isNotNull(getref);
                    ifalive = CodeBinOpExpr.logicalAnd(ifalive, notnull);
                }
                CodeStmtCollection alivebody = new CodeStmtCollection();
                loopbody.add(new CodeConditionStmt(ifalive, alivebody));

                {
                    CodeExpr incrnumalive = CodePrePostfixExpr.postfix(
                            numaliveref, true);
                    CodeMethodInvokeExpr move = new CodeMethodInvokeExpr(
                            CodeType.foid(), source.getSetRef(), "set",
                            incrnumalive, srcmonref);
                    alivebody.add(new CodeExprStmt(move));

                    alivebody.add(borrowedweakrefs.getDeclarationCode(
                            this.monitorClass, srcmonref));
                    WeakReferenceVariables mergedweakrefs = WeakReferenceVariables
                            .merge(source.getWeakRefs(), borrowedweakrefs);

                    final IndexingTreeQueryResult dest = new IndexingTreeQueryResult(
                            targettree, mergedweakrefs, targetprms,
                            Access.Leaf, "dest");
                    alivebody.add(dest.generateDeclarationCode());

                    StmtCollectionInserter<CodeExpr> inserter = new StmtCollectionInserter<CodeExpr>() {
                        @Override
                        public CodeStmtCollection insertSecondLastMap(
                                CodeExpr mapref) {
                            CodeStmt assign = new CodeAssignStmt(
                                    dest.getLastMapRef(), mapref);
                            return new CodeStmtCollection(assign);
                        }

                        @Override
                        public CodeStmtCollection insertLastEntry(Entry entry,
                                CodeExpr entryref) {
                            CodeStmt assign = new CodeAssignStmt(
                                    dest.getEntryRef(), entryref);
                            return new CodeStmtCollection(assign);
                        }

                        @Override
                        public CodeStmtCollection insertLastField(Entry entry,
                                CodeExpr leafref) {
                            CodeStmt assign = new CodeAssignStmt(
                                    dest.getLeafRef(), leafref);
                            return new CodeStmtCollection(assign);
                        }
                    };
                    alivebody.add(targettree.generateFindOrCreateCode(
                            Access.Leaf, mergedweakrefs, inserter));
                    alivebody.add(this.getBehaviorObserver()
                            .generateIndexingTreeLookupCode(targettree,
                                    LookupPurpose.CombinedMonitor,
                                    mergedweakrefs, false, dest.getLeafRef()));

                    CodeExpr ifcond = CodeBinOpExpr.isNull(dest.getLeafRef());
                    if (dest.getLeafRef().getType() instanceof CodeRVType.Interface) {
                        // Additional check is needed because the destination
                        // leaf may refer to a DisableHolder.
                        CodeRVType.Interface itftype = (CodeRVType.Interface) dest
                                .getLeafRef().getType();
                        CodeRVType.DisableHolder dhtype = itftype
                                .getDisableHolderType();
                        CodeExpr extra = new CodeInstanceOfExpr(
                                dest.getLeafRef(), dhtype);
                        ifcond = CodeBinOpExpr.logicalOr(ifcond, extra);
                    }
                    CodeStmtCollection ifbody = this
                            .generateCopyStateInternalCode(sourceprms,
                                    targetprms, dest, srcmonref, transition,
                                    false);
                    alivebody.add(new CodeConditionStmt(ifcond, ifbody));
                }
            }

            CodeExpr erase = new CodeMethodInvokeExpr(CodeType.foid(),
                    source.getSetRef(), "eraseRange", numaliveref);
            stmts.add(new CodeExprStmt(erase));
        }

        return stmts;
    }

    /**
     * Generates code that corresponds to createNewMonitorStates:6 in D(X) when
     * one new monitor should be created from an existing monitor.
     *
     * @param sourceprms
     *            theta'' in createNewMonitorStates, and theta' in defineTo
     * @param targetprms
     *            theta'' cup theta in createNewMonitorStates, and theta in
     *            defineTo
     * @param transition
     *            information on theta
     * @param source
     *            information on the indexing tree for sourceprms
     * @return generated code
     */
    private CodeStmtCollection generateCopyStateFromMonitorCode(
            RVMParameters sourceprms, RVMParameters targetprms,
            IndexingTreeQueryResult transition, IndexingTreeQueryResult source) {
        return this.generateCopyStateInternalCode(sourceprms, targetprms,
                transition, source.getLeafRef(), transition, true);
    }

    /**
     * Generates code that corresponds to createNewMonitorState in D(X).
     *
     * @param transition
     *            information for accessing indexing trees, weak references and
     *            so forth
     * @return generated code
     */
    private CodeStmtCollection generateCreateNewMonitorStateCode(
            IndexingTreeQueryResult transition) {
        CodeStmtCollection stmts = new CodeStmtCollection();

        for (RVMonitorParameterPair pair : this.paramPairsForCopy) {
            boolean frommonitor = this.event.getRVMParametersOnSpec().contains(
                    pair.getParam2());
            IndexingTreeInterface sourcetree = this
                    .findIndexingTreeForCopy(pair);
            if (frommonitor && sourcetree == null)
                sourcetree = this.findIndexingTree(pair);
            Access access = frommonitor ? Access.Leaf : Access.Set;

            CodeStmtCollection nested = new CodeStmtCollection();
            nested.comment("D(X) createNewMonitorStates:4 when Dom(theta'') = <"
                    + sourcetree.getQueryParams().parameterString() + ">");

            final IndexingTreeQueryResult sourceresult = new IndexingTreeQueryResult(
                    sourcetree, transition.getWeakRefs(),
                    sourcetree.getQueryParams(), access, "source");
            nested.add(sourceresult.generateDeclarationCode());

            StmtCollectionInserter<CodeExpr> inserter = new StmtCollectionInserter<CodeExpr>() {
                @Override
                public CodeStmtCollection insertLastField(Entry entry,
                        CodeExpr leafref) {
                    CodeAssignStmt assign = new CodeAssignStmt(
                            sourceresult.getSetOrLeafRef(), leafref);
                    return new CodeStmtCollection(assign);
                }
            };
            nested.add(sourcetree.generateFindCode(access,
                    sourceresult.getWeakRefs(), inserter));
            nested.add(this.getBehaviorObserver()
                    .generateIndexingTreeLookupCode(sourcetree,
                            LookupPurpose.ClonedMonitor,
                            sourceresult.getWeakRefs(), false,
                            sourceresult.getSetOrLeafRef()));

            {
                RVMParameters sourceprms = pair.getParam2();
                RVMParameters targetprms = this.rvmSpec.getParameters()
                        .sortParam(
                                RVMParameters.unionSet(sourceprms,
                                        this.eventParams));

                CodeExpr ifcond = CodeBinOpExpr.isNotNull(sourceresult
                        .getSetOrLeafRef());
                CodeStmtCollection ifbody;
                if (frommonitor)
                    ifbody = this.generateCopyStateFromMonitorCode(sourceprms,
                            targetprms, transition, sourceresult);
                else
                    ifbody = this.generateCopyStateFromListCode(sourceprms,
                            targetprms, transition, sourceresult);
                nested.add(new CodeConditionStmt(ifcond, ifbody));
            }

            stmts.add(new CodeBlockStmt(nested));
        }

        return stmts;
    }

    /**
     * This class is used to lazily generate code for creating a monitor. That
     * code should be lazily generated because what parameters are expected by
     * the constructor is determined during the first stage of code generation.
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     */
    static class MonitorCreationLazyCode extends CodeLazyStmt {
        private final MonitorFeatures features;
        private final RVMParameters specParams;
        private final WeakReferenceVariables weakrefs;
        private final CodeType type;
        private final List<CodeExpr> fixedarguments;
        private final CodeVariable createdvar;

        public MonitorCreationLazyCode(MonitorFeatures features,
                RVMParameters specprms, WeakReferenceVariables weakrefs,
                CodeType type, CodeExpr... fixedarguments) {
            this.features = features;
            this.specParams = specprms;
            this.weakrefs = weakrefs;
            this.type = type;
            this.fixedarguments = new ArrayList<CodeExpr>();
            for (CodeExpr a : fixedarguments) {
                if (a == null)
                    continue;
                this.fixedarguments.add(a);
            }
            this.createdvar = new CodeVariable(this.type, "created");
        }

        public CodeVarRefExpr getDeclaredMonitorRef() {
            return new CodeVarRefExpr(this.createdvar);
        }

        @Override
        protected CodeStmtCollection evaluate() {
            // The code can be generated only after the first pass has
            // completed.
            if (!this.features.isStabilized())
                return null;

            CodeStmtCollection stmts = new CodeStmtCollection();

            List<CodeExpr> args = new ArrayList<CodeExpr>();
            args.addAll(this.fixedarguments);
            if (this.features.isNonFinalWeakRefsInMonitorNeeded()
                    || this.features.isFinalWeakRefsInMonitorNeeded()) {
                // The constructor will expect one parameter for each. It's okay
                // to pass
                // null if that parameter is unavailable.
                for (RVMParameter param : this.specParams) {
                    CodeVariable wrvar = this.weakrefs.getWeakRef(param);
                    if (wrvar == null)
                        args.add(CodeLiteralExpr.nul());
                    else
                        args.add(new CodeVarRefExpr(wrvar));
                }
            } else {
                for (RVMParameter param : this.features
                        .getRememberedParameters()) {
                    CodeVariable wrvar = this.weakrefs.getWeakRef(param);
                    args.add(new CodeVarRefExpr(wrvar));
                }
            }

            CodeVarDeclStmt decl = new CodeVarDeclStmt(this.createdvar,
                    new CodeNewExpr(this.type, args));
            stmts.add(decl);

            return stmts;
        }
    }

    /**
     * Generates code that corresponds to defineNew in Algorithm D(X).
     *
     * @param transition
     *            information for accessing indexing trees, weak references and
     *            so forth
     * @return generated code
     */
    private CodeStmtCollection generateDefineNewCode(
            IndexingTreeQueryResult transition) {
        CodeStmtCollection stmts = new CodeStmtCollection();

        // It seems the original code assumes that defineNew:1--3 is not needed.
        // I hope that is correct assumption.

        CodeVarRefExpr monitorref;
        {
            CodeExpr arg = null;
            if (this.strategy.needsTimeTracking)
                arg = this.getTimestamp().generateGetAndIncrementCode();
            MonitorCreationLazyCode create = new MonitorCreationLazyCode(
                    this.getMonitorFeatures(), this.rvmSpec.getParameters(),
                    transition.getWeakRefs(), this.getMonitorType(), arg);
            monitorref = create.getDeclaredMonitorRef();
            stmts.add(create);

            this.getMonitorFeatures().addRelatedEvent(this);
        }
        stmts.add(this.getBehaviorObserver().generateNewMonitorCreatedCode(
                monitorref));

        {
            MonitorInfo moninfo = this.getMonitorInfo();
            if (moninfo != null) {
                String legacycode = moninfo.newInfo(monitorref.getVariable()
                        .toLegacy(), this.eventParams);
                stmts.add(CodeStmtCollection.fromLegacy(legacycode));
            }
        }

        stmts.add(this.generateInsertMonitorCode(transition, transition,
                monitorref, false, false));
        return stmts;
    }

    /**
     * Generates code that inserts the newly created monitor into all the
     * affected sets and indexing trees.
     *
     * @param created
     *            indexing tree information for the parameter list of the
     *            created monitor
     * @param transition
     *            indexing tree information for the parameter list carried by
     *            the event
     * @param monitorref
     *            reference to the newly created monitor
     * @param isDefineTo
     *            true if invoked for handling 'defineTo', rather than
     *            'defineNew'
     * @param isFromMonitor
     *            true if this method is being invoked for copying from a
     *            monitor, rather than a set
     * @return generated code
     */
    private CodeStmtCollection generateInsertMonitorCode(
            IndexingTreeQueryResult created,
            IndexingTreeQueryResult transition, CodeVarRefExpr monitorref,
            boolean isDefineTo, boolean isFromMonitor) {
        CodeStmtCollection stmts = new CodeStmtCollection();

        boolean forceleafupdate = false;
        if (!isDefineTo || isFromMonitor)
            forceleafupdate = true;
        stmts.add(created.generateLeafUpdateCode(monitorref, forceleafupdate));

        if (isFromMonitor) {
            if (transition.getEntry().getCodeType() instanceof CodeRVType.Tuple) {
                // If the type is not a tuple, it must be a leaf. In such case,
                // 'entry' is
                // the same as 'leaf', and assigning has been already done.
                stmts.add(new CodeAssignStmt(transition.getLeafRef(),
                        monitorref));
            }

            if (transition.getEntry().getSet() != null) {
                CodePair<CodeVarRefExpr> codepair = transition
                        .generateFieldGetCode(Access.Set, "enclosing");
                stmts.add(codepair.getGeneratedCode());

                CodeVarRefExpr fieldref = codepair.getLogicalReturn();
                CodeMethodInvokeExpr invoke = new CodeMethodInvokeExpr(
                        CodeType.foid(), fieldref, "add", monitorref);
                stmts.add(new CodeExprStmt(invoke));
            }
        }

        if (!isDefineTo) {
            // When this method is reached for cloning monitors, this branch is
            // not taken. This is because
            // the matched entry (which is referred to by 'entry') has nothing
            // to do with the created monitor.
            if (created.getEntry().getSet() != null) {
                CodePair<CodeVarRefExpr> codepair = created
                        .generateFieldGetCode(Access.Set, "enclosing");
                stmts.add(codepair.getGeneratedCode());

                CodeVarRefExpr fieldref = codepair.getLogicalReturn();
                CodeMethodInvokeExpr invoke = new CodeMethodInvokeExpr(
                        CodeType.foid(), fieldref, "add", monitorref);
                stmts.add(new CodeExprStmt(invoke));
            }
        }

        stmts.add(this.generateInsertMonitorToAllCompatibleTreesCode(created,
                monitorref, isDefineTo));

        return stmts;
    }

    /**
     * Generated code that corresponds to main:1--6 in Algorithm D(X).
     *
     * @param transition
     *            information for accessing indexing trees, weak references and
     *            so forth
     * @return generated code
     */
    private CodeStmtCollection generateMonitorCreation(
            IndexingTreeQueryResult transition) {
        CodeStmtCollection stmts = new CodeStmtCollection();

        stmts.comment("D(X) main:1");
        CodePair<CodeVarRefExpr> codepair = transition.generateFieldGetCode(
                Access.Leaf, "matched");
        stmts.add(codepair.getGeneratedCode());
        CodeVarRefExpr leafref = codepair.getLogicalReturn();

        CodeBinOpExpr if1cond = CodeBinOpExpr.isNull(leafref);
        CodeStmtCollection if1body = new CodeStmtCollection();
        stmts.add(new CodeConditionStmt(if1cond, if1body));

        if (!CodeGenerationOption.isCacheKeyWeakReference())
            if1body.add(this.generateWeakReferenceLookup(
                    transition.getWeakRefs(), true));

        boolean maynotnull = false;
        if (this.strategy.mayCopyFromOtherMonitors) {
            if1body.add(this.generateCreateNewMonitorStateCode(transition));
            maynotnull = true;
        }

        if (this.event.isStartEvent()) {
            CodeStmtCollection nested = new CodeStmtCollection();
            nested.comment("D(X) main:4");
            nested.add(this.generateDefineNewCode(transition));
            if (maynotnull)
                if1body.add(new CodeConditionStmt(if1cond, nested));
            else
                if1body.add(nested);
        }

        if (this.strategy.needsTimeTracking) {
            if1body.comment("D(X) main:6");
            if1body.add(this.generateDisableUpdateCode(transition));
        }

        return stmts;
    }

    /**
     * Generates code that corresponds to main:6 in Algorithm D(X).
     *
     * @param transition
     *            information for accessing indexing trees, weak references and
     *            so forth
     * @return generated code
     */
    private CodeStmtCollection generateDisableUpdateCode(
            IndexingTreeQueryResult transition) {
        CodeStmtCollection stmts = new CodeStmtCollection();

        CodeVarRefExpr holderref;
        {
            CodePair<CodeVarRefExpr> codepair = transition
                    .generateFieldGetCode(Access.Leaf, "disableUpdated");
            stmts.add(codepair.getGeneratedCode());
            holderref = codepair.getLogicalReturn();
        }

        if (!this.event.isStartEvent()) {
            // If this event is not a creation event, the matched entry can be
            // still null.
            // If that's the case, a DisableHolder should be inserted to keep
            // the 'disable' value.
            CodeExpr ifnull = CodeBinOpExpr.isNull(holderref);
            CodeStmtCollection ifbody = new CodeStmtCollection();
            stmts.add(new CodeConditionStmt(ifnull, ifbody));

            CodeRVType.DisableHolder dhtype;
            {
                CodeRVType leaftype = transition.getEntry().getLeaf();
                if (leaftype instanceof CodeRVType.DisableHolder)
                    dhtype = (CodeRVType.DisableHolder) leaftype;
                else if (leaftype instanceof CodeRVType.Interface) {
                    CodeRVType.Interface itf = (CodeRVType.Interface) leaftype;
                    dhtype = itf.getDisableHolderType();
                } else
                    throw new NotImplementedException();
            }
            CodeVarDeclStmt create = new CodeVarDeclStmt(new CodeVariable(
                    dhtype, "holder"), new CodeNewExpr(dhtype,
                            CodeLiteralExpr.integer(-1)));
            CodeVarRefExpr createdref = new CodeVarRefExpr(create.getVariable());
            ifbody.add(create);
            ifbody.add(transition.generateLeafUpdateCode(createdref, false));

            CodeAssignStmt assign = new CodeAssignStmt(holderref, createdref);
            ifbody.add(assign);
        }

        CodeExpr ts = this.getTimestamp().generateGetAndIncrementCode();
        CodeMethodInvokeExpr invoke = new CodeMethodInvokeExpr(CodeType.foid(),
                holderref, "setDisable", ts);
        stmts.add(new CodeExprStmt(invoke));
        stmts.add(this.getBehaviorObserver().generateDisableFieldUpdatedCode(
                holderref));

        return stmts;
    }

    /**
     * Generates code that corresponds to main:8--9 in Algorithm D(X).
     *
     * @param matched
     *            information for accessing indexing trees, weak references and
     *            so forth
     * @param cacheupdatecode
     *            code for updating the indexing tree cache
     * @return generates code
     */
    private CodeStmtCollection generateMonitorTransition(
            IndexingTreeQueryResult matched, CodeStmtCollection cacheupdatecode) {
        CodeStmtCollection stmts = new CodeStmtCollection();
        stmts.comment("D(X) main:8--9");

        boolean single = this.isFullyBound();

        CodeExpr guard1cond = null;
        if (this.strategy.needsNullCheckForTransition) {
            // If the entry is of tuple type, we first need to check whether or
            // not
            // the entry itself is non-null, which is addressed in 'guard1cond'.
            // Then,
            // we also need to check whether or not the specific field in the
            // entry
            // is non-null, which will be addressed in 'guard2cond'.
            if (matched.getEntryRef().getType() instanceof CodeRVType.Tuple)
                guard1cond = CodeBinOpExpr.isNotNull(matched.getEntryRef());
        }
        CodeStmtCollection guard1body = new CodeStmtCollection();

        CodeVarRefExpr affectedref;
        {
            Access access = single ? Access.Leaf : Access.Set;
            CodePair<CodeVarRefExpr> pair = matched.generateFieldGetCode(
                    access, "stateTransitioned");
            guard1body.add(pair.getGeneratedCode());
            affectedref = pair.getLogicalReturn();
        }

        CodeExpr guard2cond = null;
        if (this.strategy.needsNullCheckForTransition)
            guard2cond = CodeBinOpExpr.isNotNull(affectedref);
        CodeStmtCollection guard2body = new CodeStmtCollection();

        if (single) {
            // The type of the leaf may not be a subclass of AbstractMonitor,
            // which implies that
            // the referent is a DisableHolder instance. Since such instances
            // cannot transition, it
            // is required to check the type and cast the reference.
            CodeType leaftype = affectedref.getVariable().getType();
            if (leaftype instanceof CodeRVType.DisableHolder)
                throw new NotImplementedException();
            else if (leaftype instanceof CodeRVType.Interface) {
                CodeRVType.Interface itftype = (CodeRVType.Interface) leaftype;
                CodeRVType.Monitor monitortype = itftype.getMonitorType();
                guard2cond = new CodeInstanceOfExpr(affectedref, monitortype);
                {
                    CodeVariable monitor = new CodeVariable(monitortype,
                            "monitor");
                    CodeVarDeclStmt decl = new CodeVarDeclStmt(monitor,
                            new CodeCastExpr(monitortype, affectedref));
                    guard2body.add(decl);
                    affectedref = new CodeVarRefExpr(monitor);
                }
            }

            guard2body.add(this.monitorClass.generateMonitorTransitionedCode(
                    affectedref, this.event, this.enforceLock));
        } else
            guard2body.add(this.monitorSet.generateMonitoringCode(affectedref,
                    this.event, this.enforceLock));
        guard2body.add(this.getBehaviorObserver()
                .generateMonitorTransitionedCode(affectedref));

        guard2body.add(cacheupdatecode);

        if (guard2cond != null)
            guard1body.add(new CodeConditionStmt(guard2cond, guard2body));
        else
            guard1body.add(guard2body);
        if (guard1cond != null)
            stmts.add(new CodeConditionStmt(guard1cond, guard1body));
        else
            stmts.add(guard1body);
        return stmts;
    }

    /**
     * Generates the body of an event handling routine, equivalent to 'main' in
     * Algorithm D(X). This method generates the code and maintains the
     * constructed AST in a field, so that getCode() finally prints out the Java
     * code, using a code formatter.
     */
    @Override
    public void generateCode() {
        if (this.generatedCode != null)
            return;

        CodeStmtCollection stmts = new CodeStmtCollection();

        WeakReferenceVariables weakrefs = new WeakReferenceVariables(
                this.output.indexingTreeManager, this.eventParams);
        stmts.add(weakrefs.getDeclarationCode());

        IndexingTreeQueryResult transition = new IndexingTreeQueryResult(
                this.getMatchedIndexingTree(), weakrefs, this.eventParams,
                Access.Entry, "matched");
        stmts.add(transition.generateDeclarationCode());

        CodeVarRefExpr cachehitref;
        {
            CodeVariable cachehit = new CodeVariable(CodeType.bool(),
                    "cachehit");
            stmts.add(new CodeVarDeclStmt(cachehit, CodeLiteralExpr.bool(false)));
            cachehitref = new CodeVarRefExpr(cachehit);
        }

        CodeConditionStmt cacheLookup = this.generateCacheLookup(weakrefs,
                transition.getEntryRef(), cachehitref);
        CodeStmtCollection treeLookup = this
                .generateIndexingTreeLookup(transition);
        if (cacheLookup == null)
            stmts.add(treeLookup);
        else {
            stmts.add(cacheLookup);
            cacheLookup.setElse(treeLookup);
        }

        if (this.strategy.mayCreateMonitors)
            stmts.add(this.generateMonitorCreation(transition));

        CodeStmtCollection cacheupdate = this.generateCacheUpdate(
                transition.getEntryRef(), cachehitref);
        stmts.add(this.generateMonitorTransition(transition, cacheupdate));

        // Due to lazily generated code, the code simplifier cannot be used
        // until
        // everything is stabilized. To complete code generation, this method
        // should be (and actually will be) invoked again. To prevent the next
        // call
        // to this method from skipping code generation, the 'generatedCode'
        // field
        // is not written at the first time.
        if (this.getMonitorFeatures().isStabilized()) {
            CodeSimplifier simplifier = new CodeSimplifier(stmts);
            simplifier.simplify();
            this.generatedCode = stmts;
        }
    }

    /**
     * This is for debugging only.
     */
    @Override
    public String toString() {
        ICodeFormatter fmt = CodeFormatters.getDefault();
        this.getCode(fmt);
        return fmt.getCode();
    }

    @Override
    public void getCode(ICodeFormatter fmt) {
        this.generateCode();
        this.generatedCode.getCode(fmt);
    }

    /**
     * This class represents the timestamp field in a monitor class. There are
     * two concrete implementations: 1. OrdinaryTimestamp: previously, one
     * global lock was used to synchronize every single event and, therefore, a
     * timestamp does not need to be synchronized. 2. AtomicTimestamp: now that
     * fine-grained locking is used, an atomically increased timestamp
     * implementation was needed.
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     */
    static abstract class Timestamp {
        protected CodeMemberField field;

        public static Timestamp create(String fieldname) {
            if (Main.useFineGrainedLock)
                return new AtomicTimestamp(fieldname);
            return new OrdinaryTimestamp(fieldname);
        }

        public abstract CodeExpr generateGetAndIncrementCode();
    }

    /**
     * This class is used when synchronization is guaranteed by callers and,
     * therfore, no additional synchronization is needed.
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     */
    static class OrdinaryTimestamp extends Timestamp {
        OrdinaryTimestamp(String fieldname) {
            this.field = new CodeMemberField(fieldname, false, true, false,
                    CodeType.rong());
        }

        @Override
        public CodeExpr generateGetAndIncrementCode() {
            CodeFieldRefExpr fieldref = new CodeFieldRefExpr(this.field);
            return CodePrePostfixExpr.postfix(fieldref, true);
        }
    }

    /**
     * This class is used when the caller does not guarantee serial access of
     * the timestamp.
     *
     * @author Choonghwan Lee <clee83@illinois.edu>
     */
    static class AtomicTimestamp extends Timestamp {
        AtomicTimestamp(String fieldname) {
            CodeType fieldtype = CodeType.AtomicLong();
            CodeExpr init = new CodeNewExpr(fieldtype, CodeLiteralExpr.rong(1));
            this.field = new CodeMemberField(fieldname, false, true, true,
                    fieldtype, init);
        }

        @Override
        public CodeExpr generateGetAndIncrementCode() {
            CodeFieldRefExpr fieldref = new CodeFieldRefExpr(this.field);
            CodeExpr invoke = new CodeMethodInvokeExpr(CodeType.rong(),
                    fieldref, "getAndIncrement");
            return invoke;
        }
    }
}
