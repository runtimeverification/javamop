package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree;

import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeLiteralExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmtCollection;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarDeclStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodePair;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper.CodeVariable;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.rvapi.GeneratedCodeAPI;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeRVType;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.event.itf.WeakReferenceVariables;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeImplementation.Access;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeImplementation.Entry;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeImplementation.Level;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;

/**
 * This class represents one indexing tree and many other information that can
 * be useful to use the indexing tree. This class was written to avoid repeated
 * code.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public class IndexingTreeQueryResult {
    private final IndexingTreeInterface indexingTree;
    private final WeakReferenceVariables weakrefs;
    private final RVMParameters params;
    private final Access access;
    private final CodeVarRefExpr secondLastMapRef;
    private final Entry matchedEntry;
    private final CodeVarRefExpr matchedEntryRef;
    private final CodeVarRefExpr matchedSetRef;
    private final CodeVarRefExpr matchedLeafRef;

    public WeakReferenceVariables getWeakRefs() {
        return this.weakrefs;
    }

    public RVMParameters getParams() {
        return this.params;
    }

    public CodeVarRefExpr getLastMapRef() {
        return this.secondLastMapRef;
    }

    public Entry getEntry() {
        return this.matchedEntry;
    }

    public CodeVarRefExpr getEntryRef() {
        return this.matchedEntryRef;
    }

    public CodeVarRefExpr getSetRef() {
        return this.matchedSetRef;
    }

    public CodeVarRefExpr getLeafRef() {
        return this.matchedLeafRef;
    }

    public CodeVarRefExpr getSetOrLeafRef() {
        switch (this.access) {
        case Set:
            return this.matchedSetRef;
        case Leaf:
            return this.matchedLeafRef;
        default:
            throw new IllegalArgumentException();
        }
    }

    public IndexingTreeQueryResult(IndexingTreeInterface tree,
            WeakReferenceVariables weakrefs, RVMParameters params,
            Access access, String prefix) {
        this.indexingTree = tree;
        this.weakrefs = weakrefs;
        this.params = params;
        this.access = access;

        if (params.size() > 0) {
            RVMParameters heads = new RVMParameters();
            for (int i = 0; i < params.size() - 1; ++i)
                heads.add(params.get(i));

            Entry secondlastentry = this.indexingTree.lookupEntry(heads);
            Level secondlastmap = secondlastentry.getMap();
            if (secondlastmap == null)
                throw new IllegalArgumentException();
            String varname = prefix + "LastMap";
            CodeVariable var = new CodeVariable(secondlastmap.getCodeType(),
                    varname);
            this.secondLastMapRef = new CodeVarRefExpr(var);
        } else
            this.secondLastMapRef = null;

        this.matchedEntry = this.indexingTree.lookupEntry(params);

        {
            String varname = prefix + "Entry";
            CodeVariable var = new CodeVariable(
                    this.matchedEntry.getCodeType(), varname);
            this.matchedEntryRef = new CodeVarRefExpr(var);
        }

        if (this.matchedEntry.getSet() != null) {
            String varname = prefix + "Set";
            CodeVariable var = new CodeVariable(this.matchedEntry.getSet(),
                    varname);
            this.matchedSetRef = new CodeVarRefExpr(var);
        } else
            this.matchedSetRef = null;

        if (this.matchedEntry.getLeaf() != null) {
            String varname = prefix + "Leaf";
            CodeVariable var = new CodeVariable(this.matchedEntry.getLeaf(),
                    varname);
            this.matchedLeafRef = new CodeVarRefExpr(var);
        } else
            this.matchedLeafRef = null;
    }

    public CodeStmtCollection generateDeclarationCode() {
        CodeStmtCollection stmts = new CodeStmtCollection();

        if (this.secondLastMapRef != null)
            stmts.add(new CodeVarDeclStmt(this.secondLastMapRef.getVariable(),
                    CodeLiteralExpr.nul()));

        stmts.add(new CodeVarDeclStmt(this.matchedEntryRef.getVariable(),
                CodeLiteralExpr.nul()));

        if (this.access == Access.Set && this.matchedSetRef != null)
            stmts.add(new CodeVarDeclStmt(this.matchedSetRef.getVariable(),
                    CodeLiteralExpr.nul()));

        if (this.access == Access.Leaf && this.matchedLeafRef != null)
            stmts.add(new CodeVarDeclStmt(this.matchedLeafRef.getVariable(),
                    CodeLiteralExpr.nul()));

        return stmts;
    }

    public CodePair<CodeVarRefExpr> generateFieldGetCode(Access access,
            String varnameprefix) {
        return this.matchedEntry.generateFieldGetCode(this.matchedEntryRef,
                access, varnameprefix);
    }

    public CodeStmtCollection generateFieldSetCode(Access access, CodeExpr value) {
        return this.matchedEntry.generateFieldSetCode(this.matchedEntryRef,
                access, value);
    }

    public CodeStmtCollection generateLeafUpdateCode(CodeVarRefExpr valueref,
            boolean forceleafupdate) {
        CodeStmtCollection stmts = new CodeStmtCollection();

        boolean setfield = false;
        boolean putentry = false;

        CodeType entrytype = this.matchedEntry.getCodeType();
        if (entrytype instanceof CodeRVType.Tuple)
            setfield = true;
        else
            putentry = true;

        if (forceleafupdate)
            setfield = true;

        if (setfield)
            stmts.add(this.generateFieldSetCode(Access.Leaf, valueref));

        if (putentry) {
            RVMParameter lastprm = this.params.getLast();
            // Although a map is used in most cases, a single entry data
            // structure
            // (which can be also thought of as 0-level map) can be used.
            if (lastprm != null) {
                CodeVarRefExpr keyref = new CodeVarRefExpr(
                        weakrefs.getWeakRef(lastprm));
                stmts.add(GeneratedCodeAPI.generatePutNode(
                        this.secondLastMapRef, keyref, valueref));
            }
        }

        return stmts;
    }
}
