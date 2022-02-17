package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.runtimeverification.rvmonitor.java.rt.observable.IInternalBehaviorObserver.LookupPurpose;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExprStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeFieldRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeLiteralExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMemberField;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMethodInvokeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeNewExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmtCollection;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeHelper;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.itf.WeakReferenceVariables;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingCacheNew;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeInterface;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;

public class InternalBehaviorObservableCodeGenerator {
    private final boolean enabled;
    private final CodeFieldRefExpr observerref;

    public CodeMemberField getField() {
        return this.observerref.getField();
    }

    public InternalBehaviorObservableCodeGenerator(boolean enabled) {
        this.enabled = enabled;

        if (enabled) {
            CodeType observertype = CodeHelper.RuntimeType
                    .getInternalBehaviorMultiplexer();
            CodeExpr init = new CodeNewExpr(observertype);
            CodeMemberField field = new CodeMemberField("observer", false,
                    true, true, observertype, init);
            this.observerref = new CodeFieldRefExpr(field);
        } else
            this.observerref = null;
    }

    private CodeStmtCollection generateCommon(String methodname,
            CodeExpr... args) {
        return this.generateCommon(methodname, Arrays.asList(args));
    }

    private CodeStmtCollection generateCommon(String methodname,
            List<CodeExpr> args) {
        if (!this.enabled)
            return CodeStmtCollection.empty();

        CodeExpr invoke = new CodeMethodInvokeExpr(CodeType.foid(),
                this.observerref, methodname, args);
        CodeStmt stmt = new CodeExprStmt(invoke);
        return new CodeStmtCollection(stmt);
    }

    public CodeStmtCollection generateEventMethodEnterCode(EventDefinition event) {
        List<CodeExpr> args = new ArrayList<CodeExpr>();
        args.add(CodeLiteralExpr.string(event.getId()));
        for (RVMParameter param : event.getRVMParametersOnSpec()) {
            CodeVariable var = new CodeVariable(CodeType.object(),
                    param.getName());
            args.add(new CodeVarRefExpr(var));
        }
        return this.generateCommon("onEventMethodEnter", args);
    }

    public CodeStmtCollection generateEventMethodLeaveCode(EventDefinition event) {
        return this.generateCommon("onEventMethodLeave");
    }

    public CodeStmtCollection generateIndexingTreeCacheHitCode(
            IndexingCacheNew cache, CodeVarRefExpr valueref) {
        CodeExpr cacheref = CodeLiteralExpr.string(cache.getValueField()
                .getName());
        return this
                .generateCommon("onIndexingTreeCacheHit", cacheref, valueref);
    }

    public CodeStmtCollection generateIndexingTreeCacheMissedCode(
            IndexingCacheNew cache) {
        CodeExpr cacheref = CodeLiteralExpr.string(cache.getValueField()
                .getName());
        return this.generateCommon("onIndexingTreeCacheMissed", cacheref);
    }

    public CodeStmtCollection generateIndexingTreeCacheUpdatedCode(
            IndexingCacheNew cache, CodeVarRefExpr valueref) {
        CodeExpr cacheref = CodeLiteralExpr.string(cache.getValueField()
                .getName());
        return this.generateCommon("onIndexingTreeCacheUpdated", cacheref,
                valueref);
    }

    private boolean isFullyFledgedIndexingTree(
            IndexingTreeInterface indexingtree) {
        // Since only an indexing tree can have information for dumping,
        // the current implementation does not invoke observable objects for
        // zero-level objects.
        return indexingtree.isFullyFledgedTree();
    }

    public CodeStmtCollection generateIndexingTreeLookupCode(
            IndexingTreeInterface indexingtree, LookupPurpose purpose,
            WeakReferenceVariables weakrefs, boolean usestrongref,
            CodeVarRefExpr retrieved) {
        if (!this.isFullyFledgedIndexingTree(indexingtree))
            return CodeStmtCollection.empty();

        List<CodeExpr> args = new ArrayList<CodeExpr>();
        args.add(new CodeFieldRefExpr(indexingtree.getImplementation()
                .getField()));
        args.add(CodeLiteralExpr.enumValue(purpose));
        args.add(retrieved);
        for (RVMParameter param : indexingtree.getQueryParams()) {
            CodeVariable key;
            if (usestrongref)
                key = new CodeVariable(CodeType.object(), param.getName());
            else
                key = weakrefs.getWeakRef(param);
            args.add(new CodeVarRefExpr(key));
        }

        return this.generateCommon("onIndexingTreeLookup", args);
    }

    public CodeStmtCollection generateTimeCheckedCode(
            IndexingTreeInterface indexingtree,
            WeakReferenceVariables weakrefs, CodeExpr source,
            CodeExpr candidate, CodeVarRefExpr definable) {
        List<CodeExpr> args = new ArrayList<CodeExpr>();
        args.add(new CodeFieldRefExpr(indexingtree.getImplementation()
                .getField()));
        args.add(source);
        args.add(candidate);
        args.add(definable);
        for (RVMParameter param : indexingtree.getQueryParams()) {
            CodeVariable var = weakrefs.getWeakRef(param);
            args.add(new CodeVarRefExpr(var));
        }

        return this.generateCommon("onTimeCheck", args);
    }

    public CodeStmtCollection generateIndexingTreeNodeInsertedCode(
            IndexingTreeInterface indexingtree,
            WeakReferenceVariables weakrefs, CodeVarRefExpr inserted) {
        if (!this.isFullyFledgedIndexingTree(indexingtree))
            return CodeStmtCollection.empty();

        List<CodeExpr> args = new ArrayList<CodeExpr>();
        args.add(new CodeFieldRefExpr(indexingtree.getImplementation()
                .getField()));
        args.add(inserted);
        for (RVMParameter param : indexingtree.getQueryParams()) {
            CodeVariable var = weakrefs.getWeakRef(param);
            args.add(new CodeVarRefExpr(var));
        }

        return this.generateCommon("onIndexingTreeNodeInserted", args);
    }

    public CodeStmtCollection generateNewMonitorCreatedCode(
            CodeVarRefExpr monitorref) {
        return this.generateCommon("onNewMonitorCreated", monitorref);
    }

    public CodeStmtCollection generateMonitorClonedCode(
            CodeVarRefExpr sourcemonref, CodeVarRefExpr monitorref) {
        return this.generateCommon("onMonitorCloned", sourcemonref, monitorref);
    }

    public CodeStmtCollection generateDisableFieldUpdatedCode(
            CodeVarRefExpr holderref) {
        return this.generateCommon("onDisableFieldUpdated", holderref);
    }

    public CodeStmtCollection generateMonitorTransitionedCode(
            CodeVarRefExpr affectedref) {
        return this.generateCommon("onMonitorTransitioned", affectedref);
    }
}
