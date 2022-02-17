package com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree;

import java.util.ArrayList;
import java.util.List;

import com.runtimeverification.rvmonitor.java.rvj.output.NotImplementedException;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeAssignStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeBinOpExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeConditionStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeExprStmt;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeFieldRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeLiteralExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMemberField;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeMethodInvokeExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeNewExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeStmtCollection;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.CodeVarRefExpr;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeRVType;
import com.runtimeverification.rvmonitor.java.rvj.output.codedom.type.CodeType;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeImplementation.Access;
import com.runtimeverification.rvmonitor.java.rvj.output.combinedoutputcode.newindexingtree.IndexingTreeImplementation.Entry;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;

/**
 * This interface defines operations that any indexing tree visitor should
 * implement, such as what the visitor should do when it reaches the last entry
 * and what it should do before/after it reaches each entry. A visitor, an
 * implementation of this interface, is used to generate code.
 *
 * visitPreNode() and visitPostNode() are to be invoked in the following manner:
 * <code>
 * generateRecursively() {
 *   ...
 *   visitor.visitPreNode(...);
 *   nested = generateRecursively(...);
 *   visitor.visitPostNode(..., nested);
 *   ...
 * }
 * </code>
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public interface IIndexingTreeVisitor {
    /**
     * Invoked when a node is about to be visited.
     *
     * @param entry
     *            entry that is about to be visited
     * @param parentref
     *            reference to the parent entry
     * @param weakref
     *            reference to the weak reference that directs to this entry
     * @param entryref
     *            reference to the entry
     * @param nextparam
     *            next parameter in the indexing tree
     * @return generated code
     */
    public CodeStmtCollection visitPreNode(Entry entry, CodeExpr parentref,
            CodeVarRefExpr weakref, CodeExpr entryref, RVMParameter nextparam);

    /**
     * Invoked when a node has been visited.
     *
     * @param entry
     *            entry that has been visited
     * @param entryref
     *            reference to the entry
     * @param nextparam
     *            next parameter in the indexing tree
     * @param nested
     *            code generated while visiting the node
     * @return generated code
     */
    public CodeStmtCollection visitPostNode(Entry entry,
            CodeVarRefExpr entryref, RVMParameter nextparam,
            CodeStmtCollection nested);

    /**
     * Invoked when the second-last node is reached. This moment may be
     * important to insert a new monitor into a set or map because the last
     * entry would be the monitor itself.
     *
     * @param entry
     *            the second-last node
     * @param entryref
     *            reference to the entry
     * @return generated code
     */
    public CodeStmtCollection visitSecondLast(Entry entry, CodeExpr entryref);

    /**
     * Invoked when the last node is reached. This moment may be important to
     * insert a new monitor into a tuple at the leaf.
     *
     * @param entry
     *            the second-last node
     * @param entryref
     *            reference to the entry
     * @return generated code
     */
    public CodeStmtCollection visitLast(Entry entry, CodeExpr entryref);
}

/**
 * This visitor is for generative traversal. The resulting code will look like
 * the following: <code>
 *   if (... == null) {
 *     create a node
 *     insert the node into the map (or set)
 *   }
 *   if (... == null) {
 *     create a node
 *     insert the node into the map (or set)
 *   }
 * </code> That is, the resulting code will not have nesting structure.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
abstract class GenerativeIndexingTreeVisitor implements IIndexingTreeVisitor {
    private final RVMParameters specParams;

    protected GenerativeIndexingTreeVisitor(RVMParameters specParams) {
        this.specParams = specParams;
    }

    private CodeNewExpr createSet(Entry nextentry, RVMParameter curparam) {
        CodeRVType.MonitorSet settype = nextentry.getSet();
        List<CodeExpr> args = new ArrayList<CodeExpr>();

        if (settype instanceof CodeRVType.PartitionedMonitorSet) {
            CodeRVType.PartitionedMonitorSet pset = (CodeRVType.PartitionedMonitorSet) settype;
            List<IndexingTreeInterface> trees = pset
                    .getConstructorArguments(curparam);
            for (IndexingTreeInterface itf : trees) {
                CodeExpr arg = CodeLiteralExpr.nul();
                if (itf != null) {
                    CodeMemberField field = itf.getImplementation().getField();
                    arg = new CodeFieldRefExpr(field);
                }
                args.add(arg);
            }
        }

        return new CodeNewExpr(settype, args);
    }

    private CodeNewExpr createMap(Entry nextentry, RVMParameter nextparam) {
        CodeExpr arg = null;
        if (this.specParams != null) {
            int id = this.specParams.getIdnum(nextparam);
            arg = CodeLiteralExpr.integer(id);
        }
        if (arg == null)
            return new CodeNewExpr(nextentry.getMap().getCodeType());
        else
            return new CodeNewExpr(nextentry.getMap().getCodeType(), arg);
    }

    @Override
    public CodeStmtCollection visitPreNode(Entry entry, CodeExpr parentref,
            CodeVarRefExpr weakref, CodeExpr entryref, RVMParameter nextparam) {
        CodeExpr ifnull = CodeBinOpExpr.isNull(entryref);
        CodeStmtCollection ifbody = new CodeStmtCollection();

        RVMParameter curparam = entry.getMap().getKey();
        Entry nextentry = entry.getMap().getValue();
        if (nextentry == null)
            throw new NotImplementedException();
        boolean m = nextentry.getMap() != null;
        boolean s = nextentry.getSet() != null;
        boolean l = nextentry.getLeaf() != null;
        boolean tuple = (m && s) || (s && l) || (l && m);

        CodeNewExpr createentry;
        if (tuple) {
            // A tuple is created when multiple fields need to be stored.
            createentry = new CodeNewExpr(nextentry.getCodeType());
        } else {
            if (m)
                createentry = this.createMap(nextentry, nextparam);
            else if (s)
                createentry = this.createSet(nextentry, curparam);
            else {
                // A monitor cannot be simply created. This should be and will
                // be
                // created by the caller after some computation.
                createentry = null;
            }
        }

        if (createentry == null)
            return null;

        CodeAssignStmt assign = new CodeAssignStmt(entryref, createentry);
        ifbody.add(assign);

        CodeExpr mapref = entry.generateFieldGetInlinedCode(parentref,
                Access.Map);
        CodeMethodInvokeExpr insert = new CodeMethodInvokeExpr(CodeType.foid(),
                mapref, "putNode", weakref, entryref);
        CodeExprStmt insertstmt = new CodeExprStmt(insert);
        ifbody.add(insertstmt);

        if (tuple) {
            // The current policy is that, whenever a tuple is created, its map
            // and set are
            // instantiated.
            if (m) {
                CodeExpr createdmap = this.createMap(nextentry, nextparam);
                ifbody.add(nextentry.generateFieldSetCode(entryref, Access.Map,
                        createdmap));
            }
            if (s) {
                CodeExpr createdset = this.createSet(nextentry, curparam);
                ifbody.add(nextentry.generateFieldSetCode(entryref, Access.Set,
                        createdset));
            }
        }

        return new CodeStmtCollection(new CodeConditionStmt(ifnull, ifbody));
    }

    @Override
    public CodeStmtCollection visitPostNode(Entry entry,
            CodeVarRefExpr entryref, RVMParameter nextparam,
            CodeStmtCollection nested) {
        return nested;
    }
}

/**
 * This visitor is for non-generative traversal. The resulting code will look
 * like the following: <code>
 *   if (... != null) {
 *     ...
 *     if (... != null) {
 *       ...
 *     }
 *   }
 * </code> That is, the resulting code will have nesting structure.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
abstract class NonGenerativeIndexingTreeVisitor implements IIndexingTreeVisitor {
    private final boolean suppressLastNullCheck;

    protected NonGenerativeIndexingTreeVisitor(boolean suppressLastNullCheck) {
        this.suppressLastNullCheck = suppressLastNullCheck;
    }

    @Override
    public CodeStmtCollection visitPreNode(Entry entry, CodeExpr parentref,
            CodeVarRefExpr weakref, CodeExpr entryref, RVMParameter nextparam) {
        return null;
    }

    @Override
    public CodeStmtCollection visitPostNode(Entry entry,
            CodeVarRefExpr entryref, RVMParameter nextparam,
            CodeStmtCollection nested) {
        // If this is the last node and null-check is unnecessary, do not touch
        // it.
        if (nextparam == null && this.suppressLastNullCheck)
            return nested;

        CodeExpr ifnull = CodeBinOpExpr.isNotNull(entryref);
        CodeConditionStmt cond = new CodeConditionStmt(ifnull, nested);
        return new CodeStmtCollection(cond);
    }
}
